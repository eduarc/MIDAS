/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.midas.classify.metrics;

import com.midas.classify.factory.ClassifierFactory;
import com.midas.classify.algorithms.SupervisedClassifier;
import com.midas.classify.data.Instance;
import com.midas.classify.data.Instances;

/**
 *
 * @author eduarc (Eduar Castrillo)
 */
public class LOOCValidator extends Validator {
  
  private int iters;
  
  public LOOCValidator(int iters, ClassifierFactory factory, Instances testData) {
    super(factory, testData);
    System.out.println("IS NULL");
    this.iters = iters;
  }
  
  @Override
  public boolean validate() {
    
    int sz = dataset.size();
    int nClasses = dataset.numClasses();
    
    for (int i = sz-1; i+iters >= sz; --i) {
      Instance one = dataset.get(i);
      dataset.remove(i);
      
      SupervisedClassifier target = factory.create(dataset);
      
      ConfusionMatrix m = new ConfusionMatrix(dataset.classAttribute());
      
      int pred = target.classify(one);
      int real = one.classValue();
      m.inc(real, pred);
      avgCMatrix.inc(real, pred);
      
      cMatrix.add(m);
      dataset.add(one);
    }
    computeAvg();
    return true;
  }

  @Override
  public String summary() {
    return "*** LOOC Validation ***\nIterations: "+iters+"\nModel: "+factory.summary();
  }
}
