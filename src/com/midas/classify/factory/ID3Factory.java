/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.midas.classify.factory;

import com.midas.classify.algorithms.dtrees.ID3;
import com.midas.classify.algorithms.SupervisedClassifier;
import com.midas.classify.data.Attribute;
import com.midas.classify.data.Instances;
import java.util.List;

/**
 *
 * @author eduarc (Eduar Castrillo)
 */
public class ID3Factory extends ClassifierFactory {

  public ID3Factory() {
  }

  @Override
  public String summary() {
    return "ID3";
  }
  
  @Override
  public SupervisedClassifier create(Instances trData) {
    
    String str = "ID3 support:\n  - Missing Values: No.\n  - Attributes: NOMIMAL\n";
    if (trData.hasMissingValue()) {
      throw new IllegalArgumentException(str+trData.relationName()+" has Missing Values");
    }
    List<Attribute> attrs = trData.getAttributes();
    for (Attribute a : attrs) {
      int t = a.type();
      if (t == Attribute.NUMERIC) {
        throw new IllegalArgumentException(str+trData.relationName()+" has NUMERIC attribute");
      }
      if (t == Attribute.DATE) {
        throw new IllegalArgumentException(str+trData.relationName()+" has DATE attribute");
      }
      if (t == Attribute.STRING) {
        throw new IllegalArgumentException(str+trData.relationName()+" has STRING attribute");
      }
    }
    return new ID3(trData);
  }
}
