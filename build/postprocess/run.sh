#!/bin/bash

if [ $# -ne 1 ]
then
  echo "Usage: `basename $0` <scoreband or scorebanddedup>"
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

jarfile=../../target/postprocessing.jar
xmlconf=../../conf/postprocess/conf.xml
run_hadoop=${HADOOP_HOME}/bin/hadoop

############################################################                                                                                        
# Run Postprocessing                                                                                                                                 
###########################################################                                                                                               
echo "*****************************************************************************"
echo "Run post processing for result of exact or lsh similarity search"
$run_hadoop jar $jarfile $1 -conf $xmlconf


