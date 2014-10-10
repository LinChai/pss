Partition-based Similarity Search (PSS)
=======================================
  A Partition-based Similarity Search as described in [1][2]. The package takes an input of the format <DocID: word1 word2..> as bag of words and output the pairs of document IDs that have a similarity value >= threshold. The framework used is the Java-based MapReduce framework provided by Apache Hadoop. 

Package overview:
-----------------

src/: source code files.

build/: build.xml file and run.sh for each component.

conf/: conf.xml for each component. 

conf/lib: external jar files, mostly Hadoop jars.

target/: compiled jar files.

data/: contains six different benchmarks each with 500 sample records. 

Quick start:
------------

1) Clone the repository as: git clone git@github.com:mahaucsb/pss.git

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

cd build/preprocess; ant build.xml
cd build/partition; ant build.xml
cd build/hybrid; ant build.xml

6) To run: 

sh build/run.sh <numDocuments> <cluedata=1,tweets=2,emails=3,ymusic=4,gnews=5,,wiki=6,disease=7> <enable static partitioning? Y/N>

For example, to calculate similarity scores for 500 Twitter records with static partitioning enabled, the command is 

sh run.sh 500 2 Y 


References:
-----------

[1]  "Optimizing Parallel Algorithms for All Pairs Similarity Search".M.Alabduljalil,X.Tang,T.Yang.WSDM'13.

[2]  "Cache-Conscious Performance Optimization for Similarity Search".M.Alabduljalil,X.Tang,T.Yang.SIGIR'13.
