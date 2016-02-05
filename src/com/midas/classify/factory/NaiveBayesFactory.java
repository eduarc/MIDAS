/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.midas.classify.factory;

import com.midas.classify.algorithms.bayes.NaiveBayes;
import com.midas.classify.algorithms.SupervisedClassifier;
import com.midas.classify.data.Attribute;
import com.midas.classify.data.Instances;
import java.util.List;

/**
 *
 * @author eduarc (Eduar Castrillo)
 */
public class NaiveBayesFactory extends ClassifierFactory {

  int prD;
  
  public NaiveBayesFactory(int prD) {
    this.prD = prD;
  }
  
  @Override
  public String summary() {
    return "NaiveBayes";
  }
  
  @Override
  public SupervisedClassifier create(Instances trData) {
    
    String str = "Naive Bayes support:\n  - Missing Values: No.\n  - Attributes: NUMERIC, NOMIMAL\n";
    
    if (trData.hasMissingValue()) {
      throw new IllegalArgumentException(str+trData.relationName()+" has Missing Values");
    }
    List<Attribute> attrs = trData.getAttributes();
    for (Attribute a : attrs) {
      int t = a.type();
      if (t == Attribute.DATE) {
        throw new IllegalArgumentException(str+trData.relationName()+" has DATE attribute");
      }
      if (t == Attribute.STRING) {
        throw new IllegalArgumentException(str+trData.relationName()+" has STRING attribute");
      }
    }
    return new NaiveBayes(trData, prD);
  }
}
