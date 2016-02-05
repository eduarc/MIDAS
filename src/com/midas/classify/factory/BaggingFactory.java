/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.midas.classify.factory;

import com.midas.classify.algorithms.SupervisedClassifier;
import com.midas.classify.algorithms.meta.Bagging;
import com.midas.classify.data.Instances;

/**
 *
 * @author eduarc
 */
public class BaggingFactory extends ClassifierFactory {

  int K;
  ClassifierFactory[] base;
  
  public BaggingFactory(ClassifierFactory[] base, int K) {
    
    this.K = K;
    this.base = base;
  }
  
  @Override
  public String summary() {
    
    String s = K+"-Bagging Factory\nBase Models:\n";
    for (ClassifierFactory b : base) {
      s += "  - "+b.summary()+"\n\n";
    }
    return s;
  }

  @Override
  public SupervisedClassifier create(Instances trData) {
    
    Bagging bag;
    try {
      bag = new Bagging(trData, base, K);
    } catch (IllegalArgumentException e) {
      throw new IllegalArgumentException("In Bagging\n"+e.getMessage());
    }
    return bag;
  }
}
