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
public class KFoldCrossValidator extends Validator {
  
  private int k;
  
  public KFoldCrossValidator(int k, ClassifierFactory factory, Instances testData) {
    super(factory, testData);
    this.k = k;
  }
  
  public boolean validate() {
    
    Instances validationData = new Instances(dataset, false);
    Instances trainingData   = new Instances(dataset, false);
    
    int sz = dataset.size();
    int samples = sz/k;
    int nClasses = dataset.numClasses();
    
    for (int it = 0; it < k; ++it) {
      trainingData.delete();
      validationData.delete();
        // -------------------------
        // Crear datos de validacion
        // -------------------------
      for (int i = it*samples, j = 0; j < samples; ++j) {
        validationData.add(dataset.get(i+j));
      }
        // ----------------------------
        // Crear datos de entrenamiento
        // ----------------------------
      for (int i = 0; i < it*samples; ++i) {
        trainingData.add(dataset.get(i));
      }
      for (int i = (it*samples)+samples; i < sz; ++i) {
        trainingData.add(dataset.get(i));
      }
      
      SupervisedClassifier target = factory.create(trainingData);
     
      ConfusionMatrix m = new ConfusionMatrix(dataset.classAttribute());
      
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
    return "*** "+k+"-FoldCross Validation ***\nModel: "+factory.summary();
  }
}
