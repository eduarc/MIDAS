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
import java.util.TreeSet;

/**
 *
 * @author eduarc (Eduar Castrillo)
 */
public class BootstrapValidator extends Validator {
  
  private int iters;
  
  public BootstrapValidator(int iters, ClassifierFactory factory, Instances testData) {
    super(factory, testData);
    this.iters = iters;
  }
  
  @Override
  public boolean validate() {
    Instances validationData = new Instances(dataset, false);
    Instances trainingData   = new Instances(dataset, false);
    
    int n = dataset.size();
    int nClasses = dataset.numClasses();
    
    for (int it = 0; it <  iters; it++) {
      validationData.delete();
      trainingData.delete();
      TreeSet<Integer> sampling = new TreeSet();
      
      for (int i = 0; i < n; ++i) {
        int idx = (int)(Math.random()*n);
        sampling.add(idx);
      }
      for (int i = 0; i < n; ++i) {
        Instance ins = dataset.get(i);
        if (sampling.contains(i)) {
          trainingData.add(ins);
        } else {
          validationData.add(ins);
        }
      }
      
      SupervisedClassifier target = factory.create(trainingData);
      int sz = validationData.size();
      
      ConfusionMatrix m = new ConfusionMatrix(dataset.classAttribute());
      
      for (int i = 0; i < sz; ++i) {
        Instance one = validationData.get(i);
        int pred = target.classify(one);
        int real = one.classValue();
        m.inc(real, pred);
        avgCMatrix.inc(real, pred);
      }
      cMatrix.add(m);
    }
    computeAvg();
    return true;
  }

  @Override
  public String summary() {
    return "*** Bootstrap Validation ***\nIterations: "+iters+"\nModel: "+factory.summary();
  }
}
