/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.midas.classify.factory;

import com.midas.classify.algorithms.SupervisedClassifier;
import com.midas.classify.algorithms.meta.AdaBoost;
import com.midas.classify.data.Instances;

/**
 *
 * @author eduarc
 */
public class AdaBoostFactory extends ClassifierFactory {

  int K;
  ClassifierFactory[] base;
  
  public AdaBoostFactory(ClassifierFactory[] base, int K) {
    
    this.K = K;
    this.base = base;
  }
  
  @Override
  public String summary() {
    
    String s = K+"-AdaBoost Factory\nBase Models:\n";
    for (ClassifierFactory b : base) {
      s += "  - "+b.summary()+"\n\n";
    }
    return s;
  }

  @Override
  public SupervisedClassifier create(Instances trData) {
    
    AdaBoost boost;
    try {
      boost = new AdaBoost(trData, base, K);
    } catch (IllegalArgumentException e) {
      throw new IllegalArgumentException("In AdaBoost\n"+e.getMessage());
    }
    return boost;
  }
}
