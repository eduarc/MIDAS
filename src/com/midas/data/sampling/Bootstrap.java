/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.midas.data.sampling;

import com.midas.classify.data.Instances;
import java.util.TreeSet;

/**
 *
 * @author Eduar Castrillo (eduarc)
 */
public class Bootstrap {
  
  private static final Bootstrap defaultSampler = new Bootstrap();
  
  private Bootstrap() {}
  
  public static Bootstrap getDefaultSampler() {
    return defaultSampler;
  }
  
  public static Instances getUnweightedSampling(Instances src) {
    
    int n = src.size();
    Instances sample = new Instances(src, false);
    
    TreeSet<Integer> sampling = new TreeSet();
    
    for (int i = 0; i < n; i++) {
      int s = (int)(Math.random()*n);
      sampling.add(s);
    }
    for (Integer i : sampling) {
      sample.add(src.get(i));
    }
    return sample;
  }
  
  public static Instances getWeightedSampling(Instances src) {

    int n = src.size();
    Instances sample = new Instances(src, false);
    
    double[] low = new double[n];
    double[] high = new double[n];
    double acc = 0;
    
    for (int i = 0; i < n; i++) {
      low[i] = acc;
      acc += src.get(i).weight();
      high[i] = acc;
    }
    
    TreeSet<Integer> sampling = new TreeSet();
    
    for (int i = 0; i < n; i++) {
      double s = Math.random();
      int l = 0, h = n;
      while (l < h) {
        int m = (l+h)>>1;
        if (s >= low[m] && s < high[m]) {
          sampling.add(m);
          break;
        }
        else if (s >= high[m]) {
          l = m+1;
        }
        else if (s < low[m]) {
          h = m;
        }
      }
    }
    for (Integer i : sampling) {
      sample.add(src.get(i));
    }
    return sample;
  }
}
