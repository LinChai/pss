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

package edu.ucsb.cs.postprocessing;

import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.util.ProgramDriver;

import edu.ucsb.cs.utilities.JobSubmitter;
import edu.ucsb.cs.postprocessing.scoreband.ScoreBandDriver;
import edu.ucsb.cs.postprocessing.scoreband.ScoreBandDedupDriver;

/**
 * Post process for calculating lsh precison / recall.
 */
public class PostProcessDriver {


  public static void main(String argv[]) {
    int exitCode = -1;
    ProgramDriver pgd = new ProgramDriver();
    try {
      pgd.addClass("scoreband", ScoreBandDriver.class,
          "Partition pairs to bands based on their similarity scores for results of exact similarity search.");
      pgd.addClass("scorebanddedup", ScoreBandDedupDriver.class,
          "Partition pairs to bands based on their similarity scores for results of lsh similarity search.");
      pgd.driver(argv);
    } catch (Throwable e) {
      e.printStackTrace();
    }
    
    System.exit(exitCode);
  }
}
