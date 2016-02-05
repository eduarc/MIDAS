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
public class AdaBoost extends MetaClassifier {

  private SupervisedClassifier[] C;
  private double alpha[];
  
  public AdaBoost(Instances trset, ClassifierFactory[] F, int K) {
    
    super(trset, F, K);
    build();
  }

  private void build() {
    
    C = new SupervisedClassifier[K];
    alpha = new double[K];
    
    int n = trainingData.size();
    double[] e = new double[K];
    
    for (int i = 0; i < n; i++) {
      trainingData.get(i).setWeight(1./n);
    }
    
    for (int i = 0; i < K; i++) {
      Instances di = Bootstrap.getWeightedSampling(trainingData);
      SupervisedClassifier c = F[i].create(di);
      
      int[] classy = c.classify(trainingData);
      
      for (int j = 0; j < n; j++) {
        Instance ins = trainingData.get(j);
        if (classy[j] != ins.classValue()) {
          e[i] += ins.weight();
        }
      }
      e[i] /= n;
      
      if (e[i] > 0.5) {
        for (int j = 0; j < n; j++) {
          trainingData.get(j).setWeight(1./n);
        }
        i--;
        continue;
      }
      if (e[i] == 0) {
        alpha[i] = 1;
      } else {
        alpha[i] = 0.5*Math.log((1-e[i])/e[i]);
      }
      
      double z = 0;
      for (int j = 0; j < n; j++) {
        Instance ins = trainingData.get(j);
        double v = Math.exp(alpha[i]);
        
        if (classy[j] == ins.classValue()) {
          v = 1./v;
        }
        ins.setWeight(ins.weight()*v);
        z += ins.weight();
      }
      
      for (int j = 0; j < n; j++) {
        Instance ins = trainingData.get(j);
        ins.setWeight(ins.weight()/z);
      }
        // clasificador
      C[i] = c;
        // terminar
      if (e[i] == 0) {
        K = i+1;
        break;
      }
    }
  }

  @Override
  public int classify(Instance sample) {
    
    int numClasses = trainingData.numClasses();
    int classy = -1;
    double maxi = -1;
    
    for (int i = 0; i < numClasses; i++) {
      double p = 0;
      for (int j = 0; j < K; j++) {
        if (i == C[j].classify(sample)) {
          p += alpha[j];
        }
      }
      if (maxi < p) {
        maxi = p;
        classy = i;
      }
    }
    return classy;
  }

  @Override
  public String summary() {
    
    String out = "Model: "+K+"-AdaBoost\n";
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
    
    String out = "Model: "+K+"-AdaBoost\n";
    for (int i = 0; i < F.length; i++) {
      out += "\n  -Classifier #"+i+":\n"+C[i].briefSummary()+"\n";
    }
    return out;
  }
}
