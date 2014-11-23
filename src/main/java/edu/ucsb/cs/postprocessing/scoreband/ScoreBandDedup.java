/**
 * Copyright 2012-2013 The Regents of the University of California
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on
 * an "AS IS"; BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations under
 * the License.
 * 
 */

package edu.ucsb.cs.postprocessing.scoreband;

import java.io.IOException;
import java.net.URI;
import java.util.Iterator;
import java.util.List;

import org.apache.hadoop.filecache.DistributedCache;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.FloatWritable;
import org.apache.hadoop.io.SequenceFile;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.FileInputFormat;
import org.apache.hadoop.mapred.TextInputFormat;
import org.apache.hadoop.mapred.FileOutputFormat;
import org.apache.hadoop.mapred.TextOutputFormat;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.Mapper;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Partitioner;
import org.apache.hadoop.mapred.Reducer;
import org.apache.hadoop.mapred.Reporter;
import org.apache.hadoop.mapred.SequenceFileInputFormat;
import org.apache.hadoop.mapred.SequenceFileOutputFormat;
import org.apache.hadoop.mapred.lib.MultipleSequenceFileOutputFormat;

import edu.ucsb.cs.utilities.JobSubmitter;
import edu.ucsb.cs.postprocessing.types.FloatPairWritable;

import edu.ucsb.cs.postprocessing.PostProcessDriver;

/**
* Partition the result of PSS (i.e.id pairs with their similarity scores) 
* into score bands. Similarity scores within a range will be put to the same band.
*
* This class deals with duplication of id pairs in the reducers to make sure the
* correct ratio of recall is calculated when permutation level is higher than 1.
* 
* For processing result of exact similarity search, please use ScoreBand
*/

public class ScoreBandDedup {

    protected static int numReducers = 10;

    public static void main(JobConf job) throws Exception {

        int numMappers = 
            job.getInt(PostProcessDriver.POSTPROCESS_MAPPER_PROPERTY, 2);

        String INPUT_DIR = "lshss";
        String OUTPUT_DIR = INPUT_DIR + "sb";

        job.setJobName(ScoreBandDedup.class.getSimpleName());
        Path inputPath = new Path(INPUT_DIR);
        Path outputPath = new Path(OUTPUT_DIR);
        
        FileSystem fs = FileSystem.get(job);
        if (fs.exists(outputPath))
            fs.delete(outputPath);
        
        job.setInputFormat(SequenceFileInputFormat.class);
        // for more than one level of input folder
        FileStatus[] status_list = fs.listStatus(inputPath);
        if(status_list != null){
            for(FileStatus status : status_list){
                //add each file to the list of inputs for the map-reduce job
                FileInputFormat.addInputPath(job, status.getPath());
            }
        }
        
        FileOutputFormat.setOutputPath(job, outputPath);
        job.setOutputFormat(SequenceFileOutputFormat.class);

        job.setNumMapTasks(numMappers);
        job.setMapperClass(BandDedupMapper.class);
        job.setMapOutputKeyClass(FloatPairWritable.class);
        job.setMapOutputValueClass(NullWritable.class);

        job.setPartitionerClass(BandDedupPartitioner.class);
        
        job.setNumReduceTasks(numReducers);
        job.setReducerClass(BandDedupReducer.class);
        job.setOutputKeyClass(FloatPairWritable.class);
        job.setOutputValueClass(Text.class);

        job.setInputFormat(TextInputFormat.class);
        job.setOutputFormat(TextOutputFormat.class);

        JobSubmitter.run(job,"POSTPROCESS",-1);
    }

    public static class BandDedupMapper extends MapReduceBase implements
    Mapper<LongWritable, Text, FloatPairWritable, NullWritable> {

        public void map(LongWritable key, Text value, 
                        OutputCollector<FloatPairWritable, NullWritable> output, Reporter reporter) 
        throws IOException {
            String[] fields = value.toString().split("\t");     // id1 <space> id2 <tab> score
            String[] ids = fields[0].split(" ");
            int id1 = Integer.valueOf(ids[0]);
            int id2 = Integer.valueOf(ids[1]);
            
            // make sure id1 < id2
            if (id1 > id2) { 
                int tmp = id1;
                id1 = id2;
                id2 = tmp;
            }
            output.collect(new FloatPairWritable(Float.valueOf(fields[1]), id1, id2), 
                           NullWritable.get());
        }
    }

    public static class BandDedupPartitioner implements Partitioner<FloatPairWritable, NullWritable> {
        public void configure(JobConf conf) {}

        public int getPartition(FloatPairWritable key, NullWritable value, int numReducers) {
            float score = key.getScore();
            return ((int)(score * numReducers)) % numReducers;
        }
    }

    public static class BandDedupReducer extends MapReduceBase implements
    Reducer<FloatPairWritable, NullWritable, FloatWritable, Text> {

        int count = 0;
        OutputCollector<FloatWritable, Text> collector;
        public void reduce(FloatPairWritable key, Iterator<NullWritable> values, 
                           OutputCollector<FloatWritable, Text> output, Reporter reporter) 
        throws IOException {
            count++;
            /*
            // could be commented out and replaced by an output of the count of this reducer
            output.collect(new FloatWritable(key.getScore()), 
                           new Text(Integer.toString(key.getId1()) + " " 
                                    + Integer.toString(key.getId2())));
            */
        }        

        @Override
        public void close() throws IOException {
            // output counter to stdout
            System.out.println(count);
        }
    }
}

