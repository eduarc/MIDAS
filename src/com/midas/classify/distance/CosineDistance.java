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
public class CosineDistance extends DistanceMeasure<Instance> {

  public CosineDistance() {
    
  }
  
  @Override
  public double distance(Instance a, Instance b) {
    
    double dot = 0, ma = 0, mb = 0;
    int n = a.numAttributes();
    
    for (int i = 0; i < n; i++) {
      double va = a.value(i);
      double vb = b.value(i);
      dot += va*vb;
      ma  += va*va;
      mb  += vb*vb;
    }
    return 1-dot/(Math.sqrt(ma)*Math.sqrt(mb));
  }
  
  @Override
  public String summary() {
    return "Cosine";
  }
}
