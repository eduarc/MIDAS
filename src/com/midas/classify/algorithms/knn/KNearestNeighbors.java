/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.midas.classify.algorithms.knn;

import com.midas.classify.algorithms.SupervisedClassifier;
import com.midas.classify.data.Instance;
import com.midas.classify.data.Instances;
import com.midas.classify.distance.DistanceMeasure;
import java.util.Arrays;
import util.Pair;

/**
 *
 * @author Eduar Castrillo (eduarc)
 */
public class KNearestNeighbors extends SupervisedClassifier {

  private int K;
  private DistanceMeasure measure;
  
  public KNearestNeighbors(Instances trset, int K, DistanceMeasure measure) {
    
    super(trset);
    this.K = K;
    this.measure = measure;
  }

  @Override
  public int classify(Instance sample) {
    
    int m = sample.numAttributes();
    for (int i = 0; i < m; i++) {
      if (i != classIndex && sample.isMissing(i)) {
        throw new IllegalArgumentException("Missing values not supported by KNearestNeighbors");
      }
    }
    
    double oldClassy = sample.classValue();
    int n = trainingData.size();
    Pair<Double, Integer>[] dist = new Pair[n];
    int[] classFreq = new int[trainingData.numClasses()];
    
    for (int i = 0; i < n; i++) {
      Instance ins = trainingData.get(i);
        // esta asignación es para obviar el atributo clase en la medida
      sample.setValue(classIndex, ins.classValue());
      double s = measure.distance(sample, ins);
      dist[i] = new Pair(s, ins.classValue());
    }
    Arrays.sort(dist);
    for (int i = 0; i < K; i++) {
      classFreq[dist[i].second]++;
    }
    int classy = -1, maxi = -1;
    for (int i = 0; i < classFreq.length; i++) {
      if (maxi < classFreq[i]) {
        maxi = classFreq[i];
        classy = i;
      }
    }
    sample.setClassValue(oldClassy);
    return classy;
  }

  @Override
  public String summary() {
    
    String s = "Model: "+K+"-Nearest Neighbors\n";
    s += "Distance: "+measure.summary();
    return s;
  }
  
  @Override
  public String toString() {
    return summary();
  }

  @Override
  public String briefSummary() {
    return summary();
  }
}
