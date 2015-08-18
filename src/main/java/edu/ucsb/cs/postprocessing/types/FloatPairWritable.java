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

package edu.ucsb.cs.postprocessing.types;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.io.WritableComparable;

/**
 *
 */

public class FloatPairWritable implements WritableComparable<FloatPairWritable> {

  private float score;
  private int id1;
  private int id2;  // id1 < id2

  public FloatPairWritable() {
  }
  
  public FloatPairWritable(float s, int i1, int i2) {
    score = s;
    id1 = i1;
    id2 = i2;
  }

  public float getScore() {
    return score;
  }

  public int getId1() {
    return id1;
  }

  public int getId2() {
    return id2;
  }

  @Override
  public void readFields(DataInput in) throws IOException {
    score = in.readFloat();
    id1 = in.readInt();
    id2 = in.readInt();
  }
  
  @Override
  public void write(DataOutput out) throws IOException {
    out.writeFloat(score);
    out.writeInt(id1);
    out.writeInt(id2);
  }

  @Override
  public int compareTo(FloatPairWritable other) {
    float diff = this.score - other.score;
    int comp = 0;
    if (diff > 0)
      comp = 1;
    else if (diff < 0) 
      comp = -1;
    if (comp == 0) {
      comp = this.id1 - other.id1;
      if (comp == 0) 
        comp = this.id2 - other.id2;
    }

    // for debug. output in log/userlogs/maplog
    //System.out.println(this.score+" "+this.id1+" "+this.id2+" "+
    //                   other.score+" "+other.id1+" "+other.id2+"\t"+comp);
    return comp;
  }
}

