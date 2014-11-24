#!/bin/bash

if [ $# -ne 1 ]
then
  echo "Usage: `basename $0`"
  exit 1
fi


############################################################                                                                                                       
# Enironment Variables Set
############################################################
if [ -z ${HADOOP_HOME} ] || [ -z ${JAVA_VERSION} ]
then
    echo "ERROR: either HADOOP_HOME or JAVA_VERSION is not set."
    exit 0
fi

############################################################
# Configuration Set
############################################################

numdocs=$1
twitterdata=../../data/twitter/500-twitter	#500-twitter
jarfile=../../target/lsh.jar
xmlconf=../../conf/lsh/conf.xml
tmpdata=input_dir
run_hadoop=${HADOOP_HOME}/bin/hadoop

############################################################
# Copy n Documents to Input Folder
############################################################

#
rm -r $tmpdata 2>/dev/null
mkdir $tmpdata
head -n $numdocs $twitterdata > $tmpdata/input
if [ $? -ne 0 ]
then
  exit 2
fi

############################################################                                                                                        
# Run Preprocessing                                                                                                                                 
###########################################################                                                                                               

echo "*****************************************************************************"
echo "Load "$numdocs" vectors of Twitter data into HDFS"
$run_hadoop dfs -rmr textpages
$run_hadoop dfs -put $tmpdata textpages

#echo "Run minhash"
#$run_hadoop jar $jarfile minhashlsh -conf $xmlconf

# clean up lshss output dir
$run_hadoop dfs -rmr lshss
$run_hadoop dfs -mkdir lshss

for permno in {0..0}  
# hard coded, should be {0,nPerm-1}
do
  echo "Run randomlsh, buckets are stored in lshpartitions"
  $run_hadoop jar $jarfile randomlsh -conf $xmlconf 
  exit

  echo "Run cosine similarity search on each partition"
  for partno in {0..3}   
  # hard coded, should be {0,2^nBits-1}
  do
    $run_hadoop dfs -rmr staticpartitions
    $run_hadoop dfs -mkdir staticpartitions
    $run_hadoop dfs -cp lshpartitions/"$partno" staticpartitions
    $run_hadoop dfs -ls staticpartitions

    cd ../hybrid   # to hybrid folder
    sh run.sh $numdocs 2 N      # 2 stands for twitter data, hard coded
    cd ../lsh  # back to lsh folder

    $run_hadoop dfs -mv SimilarityScores lshss/"$permno"-"$partno"
  done  # loop of partno
done  # loop of permno

cd ../postprocess  
sh run.sh scorebanddedup

