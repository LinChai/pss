#!/bin/bash
#SBATCH --job-name="pssnew"
#SBATCH --output="pssnew.%j.%N.out"
#SBATCH --partition=compute
#SBATCH --nodes=1
#SBATCH --ntasks-per-node=1
#SBATCH -t 01:00:00
export HADOOP_CONF_DIR=/home/$USER/cometcluster
export WORKDIR=`pwd`
module load hadoop/1.2.1
#module load hadoop/2.6.0
myhadoop-configure.sh
start-all.sh

export JAVA_VERSION=`java -version 2>&1| head -n 1 | cut -d \" -f 2 | cut -d . -f1,2`
sh run.sh 500 2 Y

stop-all.sh
myhadoop-cleanup.sh
