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

package edu.ucsb.cs.lsh.projection;

import java.io.UnsupportedEncodingException;

import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.util.GenericOptionsParser;

/**
 * 
 *         This class is a Hadoop task to write #bits randomly generated vectors
 *         (of size #features) to a SequenceFile. Uses identity mappers to pass
 *         all work to reducers. Enables multiple Reducers to write random
 *         vectors simultaneously for a total of (nReducers*nVectors) = total
 *         number of random projections.
 * 
 *         WARNING: code does not accept MD5 hashed docIds or features.
 */
@SuppressWarnings("deprecation")
public class ProjectionLshDriver {

	public static final String NAMESPACE = "lsh";
	public static final String LSH_NBITS_PROPERTY = NAMESPACE + ".num.bits";
	public static final int LSH_NBITS_VALUE = 50;
	public static final String LSH_NFEATURES_PROPERTY = NAMESPACE + ".num.features";
	public static final int LSH_NFEATURES_VALUE = 0;
	public static final String LSH_NREDUCER_PROPERTY = NAMESPACE + ".num.reducers";
	public static final int LSH_NREDUCER_VALUE = 2;
	public static final String LSH_NPERMUTATION_PROPERTY = NAMESPACE + ".num.permutations";
	public static final int LSH_NPERMUTATION_VALUE = 5;
	public static String LSH_OVERLAP_PROPERTY = ProjectionLshDriver.NAMESPACE
			+ ".overlap.percentage";
	public static final float LSH_OVERLAP_VALUE = 1.0f;
	//private static String DOT_THRESHOLD_PROPERTY = ProjectionLshDriver.NAMESPACE
	//		+ ".projection.dot.threshold";// instead of [0,0,..0]

	public static void main(String[] args) throws Exception {

		long d = System.currentTimeMillis();
		JobConf job = new JobConf(ProjectionLshDriver.class);
		
		unique(new GenericOptionsParser(job, args).getRemainingArgs());
		//new GenericOptionsParser(job, args).getRemainingArgs();
			// uncomment
		ProjectionsGenerator.main(job); // random projections saved in HDFS
		SignaturesGenerator.main(args); // vectors to bit signatures
		
		job = new JobConf(ProjectionLshDriver.class);
		new GenericOptionsParser(job, args).getRemainingArgs();
		BucketsGenerator.main(job);
		System.out.println("LSH three jobs took:" + (System.currentTimeMillis() - d) + " ms.");
	}

	public static void unique(String[] args) throws UnsupportedEncodingException {
		if (args.length != 0)
			throw new UnsupportedEncodingException(
					"Usage: <className> -conf <config file>");
		/*
		ProjectionsGenerator.INPUT_DIR += args[0];
		ProjectionsGenerator.OUTPUT_DIR += args[0];
		SignaturesGenerator.INPUT_DIR += args[0];
		SignaturesGenerator.OUTPUT_DIR += args[0];
		BucketsGenerator.OUTPUT_DIR += args[0];
		*/
	}
}
