/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.midas.classify.distance;

import com.midas.classify.data.Instance;

/**
 *
 * @author Eduar Castrillo (eduarc)
 */
public class HammingDistance extends DistanceMeasure<Instance> {

  public HammingDistance() {
    
  }
  
  @Override
  public double distance(Instance a, Instance b) {
    
    double d = 0;
    int n = a.numAttributes();
    for (int i = 0; i < n; i++) {
      if (a.value(i) != b.value(i)) {
        d++;
      }
    }
    return d;
  }

  @Override
  public String summary() {
    return "Hamming";
  }
}
