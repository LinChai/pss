#!/bin/bash

############################################################
# Entry point of script to run with incremental data update
############################################################

# set up incremental data source under <newDataFolder>
# set up existing dataset under <oldDataFolder>
if [ $# -ne 2 ]
then
  echo "Usage: `basename $0` <oldDataFolder> <newDataFolder>"
  exit 1
fi

# build static partition for existing dataset
# (this step only need to run once)
rm -rf input_dir/*; cp -r $1 input_dir
cd partition
./run.sh `wc -l $1/*` 2

# run PSS between incremental update and existing dataset
rm -rf input_dir/*; cp -r $2 input_dir
cd hybrid
./run.sh `wc -l $2/*` 2 N

# merge update data into existing dataset
mv -r $2/* $1
