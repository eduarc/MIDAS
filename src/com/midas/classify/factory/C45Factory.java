/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.midas.classify.factory;

import com.midas.classify.algorithms.SupervisedClassifier;
import com.midas.classify.algorithms.dtrees.C45;
import com.midas.classify.data.Attribute;
import com.midas.classify.data.Instances;
import java.util.List;

/**
 *
 * @author eduarc (Eduar Castrillo)
 */
public class C45Factory extends ClassifierFactory {

  public C45Factory() {
    
  }
  
  @Override
  public String summary() {
    return "C 4.5";
  }

  @Override
  public SupervisedClassifier create(Instances trData) {
    
    String str = "C 4.5 support:\n  - Missing Values: Yes.\n  - Attributes: NUMERIC, NOMIMAL\n";
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
    return new C45(trData);
  }
}
