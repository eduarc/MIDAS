/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.midas.classify.factory;

import com.midas.classify.algorithms.SupervisedClassifier;
import com.midas.classify.algorithms.knn.KNearestNeighbors;
import com.midas.classify.data.Instances;
import com.midas.classify.distance.DistanceMeasure;

/**
 *
 * @author tkd
 */
public class KNearestNeighborsFactory extends ClassifierFactory {

  int K;
  DistanceMeasure measure;
  
  public KNearestNeighborsFactory(int K, DistanceMeasure measure) {
    
    this.K = K;
    this.measure = measure;
  }
  
  @Override
  public String summary() {
    return K+"-NearestNeightbors\nDistance: "+measure.summary();
  }

  @Override
  public SupervisedClassifier create(Instances trData) {
    
    String str = "K-Nearest Neighbors support:\n  - Missing Values: No.\n  - Attributes: All\n";
    if (trData.hasMissingValue()) {
      throw new IllegalArgumentException(str+trData.relationName()+" has Missing Values");
    }
    return new KNearestNeighbors(trData, K, measure);
  }
}
