Partition-based Similarity Search (PSS)
=======================================
Release note: [June 10, 2015]
-----------------

Project overview:
-----------------

A Partition-based Similarity Search as described in [1][2][3]. The package takes an input of the format <DocID: word1 word2..> as bag of words and output the pairs of document IDs that have a similarity value >= threshold. The framework used is the Java-based MapReduce framework provided by Apache Hadoop. 
The code also incorporates a feature to support Locality Sensitive Hashing (LSH) and incremental computing.

Package overview:
-----------------

src/: source code files.

build/: build.xml file and run.sh for each component.

conf/: conf.xml for each component. 

conf/lib: external jar files, mostly Hadoop jars.

target/: compiled jar files.

data/: contains 500 sample records for each benchmark tested in our experiments. 

Quick start:
------------

1) Clone the repository as: git clone git@github.com:ucsb-similarity/pss.git

2) Install Java. Make sure 'ant','java','javac' are installed by typing them into the command line.

3) Install and set up Hadoop following [ http://hadoop.apache.org/docs/r0.19.0/quickstart.html#Download ].

4) Setup the following environment variables:
   - HADOOP_HOME: 

      the location of your hadoop directory. For example, export HADOOP_HOME=/home/$USER/hadoop-1.0.1
   
   - JAVA_HOME: 
   
      For Linux: export JAVA_HOME=/usr/lib/jvm/java-openjdk //this is just an example
      For MAC: export JAVA_HOME=/System/Library/Frameworks/JavaVM.framework/Versions/1.6/Home

   - JAVA_VERSION:
   
      For MAC: export JAVA_VERSION=`java -version 2>&1 | head -n 1 | cut -d\" -f 2 | cut -f1 -f2 -d"."`
      In linux: export JAVA_VERSION=`java -version 2>&1| head -n 1 | cut -d \" -f 2 | cut -d . -f1,2`

5) To compile:

cd build/lsh; ant build.xml

cd build/preprocess; ant build.xml

cd build/partition; ant build.xml

cd build/hybrid; ant build.xml

cd build/postprocess; ant build.xml

6) To run PSS separately: 

sh build/run.sh <numDocuments> <cluedata=1,tweets=2,emails=3,ymusic=4,gnews=5,,wiki=6,disease=7> <enable static partitioning? Y/N>

For example, to calculate similarity scores for 500 Twitter records with static partitioning enabled, the command is 

sh run.sh 500 2 Y 

7) To run PSS with LSH:

cd build/lsh; sh runt.sh <numDocuments>

This will run LSH-incorporated PSS for <numDocuments> Tweets. The number of permutations $nPerm$ and number of bits per signature $nBits$ are configurable in build/lsh/runt.sh file.

8) To run PSS with incremental computing: 

sh build/runinc.sh <existingDataFolder> <incrementalUpdateFolder>

This will run PSS between incremental update and existing dataset with minimal additional computation. 


References:
-----------

[1]  "Optimizing Parallel Algorithms for All Pairs Similarity Search". M.Alabduljalil, X.Tang, T.Yang. WSDM'13.

[2]  "Cache-Conscious Performance Optimization for Similarity Search". M.Alabduljalil, X.Tang, T.Yang. SIGIR'13.

[3]  "Load Balancing for Partition-based Similarity Search". X.Tang, M.Alabduljalil, T.Yang. SIGIR'14.
