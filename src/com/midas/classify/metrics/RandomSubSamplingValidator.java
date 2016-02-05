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
public class RandomSubSamplingValidator extends Validator {
  
  private int iters;
  private int samples;
  
  public RandomSubSamplingValidator(int iters, int samples, ClassifierFactory factory, Instances testData) {
    super(factory, testData);
    this.iters = iters;
    this.samples = samples;
  }
  
  public boolean validate() {
    
    int sz = dataset.size();
    int nClasses = dataset.numClasses();
    
    Instances validationData = new Instances(dataset, false);
    Instances trainingData   = new Instances(dataset, false);
    
    for (int it = 0; it < iters; ++it) {
      validationData.delete();
      trainingData.delete();
      boolean[] used = new boolean[sz];
        // -------------------------
        // Crear datos de validacion
        // -------------------------
      for (int i = 0; i < samples; ++i) {
        int idx;
          // esta es una mala idea, pero resulta en una implementacion mas eficiente
        do {
          idx = (int)(Math.random()*sz);
        } while (used[idx]);
        used[idx] = true;
        validationData.add(dataset.get(idx));
      }
        // ----------------------------
        // Crear datos de entrenamiento
        // ----------------------------
      for (int i = 0; i < sz; ++i) {
        if (!used[i]) {
          trainingData.add(dataset.get(i));
        }
      }
      
      SupervisedClassifier target = factory.create(trainingData);
      
      ConfusionMatrix m = new ConfusionMatrix(dataset.classAttribute());
      double[] error = new double[nClasses];
      
      for (int i = 0; i < samples; ++i) {
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
    return "*** Bootstrap Validation ***\nIterations: "+iters+"\nSampling: "+samples+"\nModel: "+factory.summary();
  }
}
