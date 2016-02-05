/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.midas.classify.algorithms.meta;

import com.midas.classify.algorithms.SupervisedClassifier;
import com.midas.classify.data.Instance;
import com.midas.classify.data.Instances;
import com.midas.classify.factory.ClassifierFactory;
import com.midas.data.sampling.Bootstrap;

/**
 *
 * @author Eduar Castrillo (eduarc)
 */
public class Bagging extends MetaClassifier {
  
  SupervisedClassifier[] C;

  public Bagging(Instances trset, ClassifierFactory[] C, int K) {
    
    super(trset, C, K);
    build();
  }
  
  private void build() {
    
    C = new SupervisedClassifier[K];
    for (int i = 0; i < K; i++) {
      Instances di = Bootstrap.getUnweightedSampling(trainingData);
      C[i] = F[i].create(di);
    }
  }
  
  @Override
  public int classify(Instance sample) {
    
    int numClasses = trainingData.numClasses();
    int[] freq = new int[numClasses];
    int classy = -1, maxi = -1;
    
    for (int j = 0; j < K; j++) {
      freq[C[j].classify(sample)]++;
    }
    for (int i = 0; i < numClasses; i++) {
      if (freq[i] > maxi) {
        maxi = freq[i];
        classy = i;
      }
    }
    return classy;
  }

  @Override
  public String summary() {
    
    String out = "Model: "+K+"-Bagging\n";
    for (int i = 0; i < F.length; i++) {
      out += "\n  -Classifier #"+i+":\n"+C[i].summary()+"\n";
    }
    return out;
  }

  public String toString() {
    return briefSummary();
  }
  
  @Override
  public String briefSummary() {
    
    String out = "Model: "+K+"-Bagging\n";
    for (int i = 0; i < F.length; i++) {
      out += "\n  -Classifier #"+i+":\n"+C[i].briefSummary()+"\n";
    }
    return out;
  }
}
