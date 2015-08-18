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

import java.io.UnsupportedEncodingException;

import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.util.GenericOptionsParser;

/**
 * 
 */
@SuppressWarnings("deprecation")
public class ScoreBandDriver {

  public static void main(String[] args) throws Exception {

    long d = System.currentTimeMillis();
    JobConf job = new JobConf(ScoreBandDriver.class);
    new GenericOptionsParser(job, args).getRemainingArgs();
    ScoreBand.main(job);
    System.out.println("ScoreBandDriver job took:" + (System.currentTimeMillis() - d) + " ms.");
  }
}
