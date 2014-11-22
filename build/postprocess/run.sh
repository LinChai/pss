#!/bin/bash


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

jarfile=../../target/postprocessing.jar
xmlconf=../../conf/postprocess/conf.xml
run_hadoop=${HADOOP_HOME}/bin/hadoop

############################################################                                                                                        
# Run Postprocessing                                                                                                                                 
###########################################################                                                                                               
echo "*****************************************************************************"
echo "Run post processing for result of exact similarity search"
$run_hadoop jar $jarfile scoreband -conf $xmlconf

#echo "Run post processing for result of lsh similarity search stored in lshss"
#$run_hadoop jar $jarfile scorebanddedup -conf $xmlconf


