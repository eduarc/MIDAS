/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.midas.classify.distance;

import com.midas.classify.data.Attribute;
import com.midas.classify.data.Instance;
import java.util.List;

/**
 *
 * @author Eduar Castrillo (eduarc)
 */
public class EuclideanDistance extends DistanceMeasure<Instance> {

  List<Attribute> attributes;
  
  public EuclideanDistance(List<Attribute> attrs) {
    attributes = attrs;
  }
  
  @Override
  public double distance(Instance a, Instance b) {
    
    double s = 0;
    int n = attributes.size();
    
    for (int i = 0; i < n; i++) {
      Attribute att = attributes.get(i);
      double d = a.value(i)-b.value(i);
      if (att.isString() || att.isNominal()) {
        d = (d != 0) ? 1 : 0;
      }
      s += att.weight()*d*d;
    }
    return Math.sqrt(s);
  }

  @Override
  public String summary() {
    return "Euclidean";
  }
}
