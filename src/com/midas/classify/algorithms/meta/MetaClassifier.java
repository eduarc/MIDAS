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

/**
 *
 * @author Eduar Castrillo (eduarc)
 */
public abstract class MetaClassifier extends SupervisedClassifier {

  protected int K;
  protected ClassifierFactory[] F;
  
  public MetaClassifier(Instances trset, ClassifierFactory[] F, int K) {
    
    super(trset);
    this.F = F;
    this.K = K;
  }

  public int getK() {
    return K;
  }
  
  public ClassifierFactory[] getClassifiersFactory() {
    return F;
  }

  public ClassifierFactory getClassifierFactory(int i) {
    return F[i];
  }
  
  @Override
  public abstract int classify(Instance sample);

  @Override
  public abstract String briefSummary();
  
  @Override
  public abstract String summary();
}
