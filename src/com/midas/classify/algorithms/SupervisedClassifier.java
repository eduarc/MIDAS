/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.midas.classify.algorithms;

import com.midas.classify.data.Instance;
import com.midas.classify.data.Instances;

/**
 *
 * @author eduarc (Eduar Castrillo)
 */
public abstract class SupervisedClassifier {
  
  protected Instances trainingData;
  protected int classIndex;
  
  public SupervisedClassifier(Instances trset) {
    trainingData = trset;
    classIndex = trset.classIndex();
  }
  
  public Instances getTrainingData() {
    return trainingData;
  }
  
  public int getClassIndex() {
    return classIndex;
  }
  
  public int[] classify(Instances dataSet) {
    
    int n = dataSet.size();
    int[] classy = new int[n];
    for (int i = 0; i < n; i++) {
      classy[i] = classify(dataSet.get(i));
    }
    return classy;
  }
  
  public abstract int classify(Instance sample);
  public abstract String summary();
  public abstract String briefSummary();
}
