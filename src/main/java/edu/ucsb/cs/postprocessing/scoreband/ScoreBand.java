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

/**
* Partition the result of PSS (i.e.id pairs with their similarity scores) 
* into score bands. Similarity scores within a range will be put to the same band.
*
* This class only deals with single level of input folder, so is aimed for
* the result of exact similarity search. 
* 
* For processing result of lsh similarity search, please use ScoreBandDedup
*/

public class ScoreBand {

    protected static int numReducers = 10;

    public static void main(JobConf job) throws Exception {

        String INPUT_DIR = "exactss";  
        String OUTPUT_DIR = INPUT_DIR + "sb";

        job.setJobName(ScoreBand.class.getSimpleName());
        Path inputPath = new Path(INPUT_DIR);
        Path outputPath = new Path(OUTPUT_DIR);
        
        FileSystem fs = FileSystem.get(job);
        if (fs.exists(outputPath))
            fs.delete(outputPath);
        
        job.setInputFormat(SequenceFileInputFormat.class);
        FileInputFormat.setInputPaths(job, inputPath);
        FileOutputFormat.setOutputPath(job, outputPath);
        job.setOutputFormat(SequenceFileOutputFormat.class);


        job.setMapperClass(BandMapper.class);
        job.setMapOutputKeyClass(FloatWritable.class);
        job.setMapOutputValueClass(Text.class);

        job.setPartitionerClass(BandPartitioner.class);
        
        job.setNumReduceTasks(numReducers);
        job.setReducerClass(BandReducer.class);
        job.setOutputKeyClass(FloatWritable.class);
        job.setOutputValueClass(Text.class);

        job.setInputFormat(TextInputFormat.class);
        job.setOutputFormat(TextOutputFormat.class);

        JobSubmitter.run(job,"POSTPROCESS",-1);
    }

    public static class BandMapper extends MapReduceBase implements
    Mapper<LongWritable, Text, FloatWritable, Text> {

        public void map(LongWritable key, Text value, 
                        OutputCollector<FloatWritable, Text> output, Reporter reporter) 
        throws IOException {
            String[] fields = value.toString().split("\t");     // id1 <space> id2 <tab> score
            String[] ids = fields[0].split(" ");
            int id1 = Integer.valueOf(ids[0]);
            int id2 = Integer.valueOf(ids[1]);
            if (id1 > id2) 
                output.collect(new FloatWritable(Float.valueOf(fields[1])), 
                new Text(Integer.toString(id2) + " " + Integer.toString(id1)));  // score, pair
            else
                output.collect(new FloatWritable(Float.valueOf(fields[1])), new Text(fields[0]));
        }
    }


    public static class BandPartitioner implements Partitioner<FloatWritable, Text> {
        public void configure(JobConf conf) {}

        public int getPartition(FloatWritable key, Text value, int numReducers) {
            float score = key.get();
            return ((int)(score * numReducers)) % numReducers;
        }
    }

    public static class BandReducer extends MapReduceBase implements
    Reducer<FloatWritable, Text, FloatWritable, Text> {

        int count = 0;

        public void reduce(FloatWritable key, Iterator<Text> values, 
                           OutputCollector<FloatWritable, Text> output, Reporter reporter) 
            throws IOException {

            while (values.hasNext()) {
                count++;
                
                values.next();
                /* // could be commented out and replaced by an output of the count of this reducer
                Text nextVal = values.next(); 
                output.collect(key, nextVal); // score, pair
                */
            }
        }

        @Override
        public void close() throws IOException {
            // output counter to stdout
            System.out.println(count);
        }
    }
}