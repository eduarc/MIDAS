/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.midas.classify.metrics;

import com.midas.classify.factory.ClassifierFactory;
import com.midas.classify.data.Instances;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author eduarc (Eduar Castrillo)
 */
public abstract class Validator {

  protected Instances dataset;
  protected ClassifierFactory factory;
  protected List<ConfusionMatrix> cMatrix;
  protected ConfusionMatrix avgCMatrix;
  
  public Validator(ClassifierFactory factory, Instances dataset) {
    
    this.dataset = dataset;
    this.factory = factory;
    
    int n = dataset.numClasses();
    cMatrix = new ArrayList();
    avgCMatrix = new ConfusionMatrix(dataset.classAttribute());
  }
  
  public void setDataset(Instances d) {
    dataset = d;
  }
  
  public Instances getDataset() {
    return dataset;
  }
  
  public void setFactory(ClassifierFactory f) {
    factory = f;
  }
  
  public ClassifierFactory getFactory() {
    return factory;
  }
  
  public abstract boolean validate();
  public abstract String summary();
  
  protected void computeAvg() {
    
    int n = avgCMatrix.numClasses();
    int t = cMatrix.size();
    
    for (int real = 0; real < n; real++) {
      for (int pred = 0; pred < n; pred++) {
        double old = avgCMatrix.get(real, pred);
        avgCMatrix.set(real, pred, old/t);
      }
    }
  }
  
  public ConfusionMatrix getConfusionMatrix(int i) {
    return cMatrix.get(i);
  }
  
  public ConfusionMatrix getAvgConfusionMatrix() {
    return avgCMatrix;
  }
  
  public int getTests() {
    return cMatrix.size();
  }
}
