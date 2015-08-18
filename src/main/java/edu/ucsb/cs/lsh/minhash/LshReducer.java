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
 */

package edu.ucsb.cs.lsh.minhash;

import java.io.IOException;
import java.util.Iterator;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reducer;
import org.apache.hadoop.mapred.Reporter;

import edu.ucsb.cs.lsh.types.IntArrayWritable;

/**
 * 
 */
public class LshReducer extends MapReduceBase implements
		Reducer<IntArrayWritable, LongWritable, IntWritable, Text> {

	private IntWritable key = new IntWritable();
	private Text value = new Text();
	
	/* // not sure the purpose of this implementation
	private int count = 0;
	public void reduce(IntArrayWritable sig, Iterator<LongWritable> docids,
			OutputCollector<IntWritable, Text> output, Reporter report) throws IOException {
		count++;
		key.set(count);
		while (docids.hasNext()) {
			value.set(docids.next().get() + "");
			output.collect(key, value);
		}
	}
	*/

	public void reduce(IntArrayWritable sig, Iterator<LongWritable> docids,
			OutputCollector<IntWritable, Text> output, Reporter report) throws IOException {
		int count = 0;
		String text = "";

		while (docids.hasNext()) {
			count++;
			text += docids.next().get() + " ";	// could be more efficient
		}
		if (count > 1) {
			
			key.set(count);
			value.set(text);
			output.collect(key, value);
		}
	}


}
