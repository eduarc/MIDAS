/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.midas.classify.factory;

import com.midas.classify.algorithms.SupervisedClassifier;
import com.midas.classify.data.Instances;

/**
 *
 * @author eduarc (Eduar Castrillo)
 */
public abstract class ClassifierFactory {
  
  public abstract String summary();
  public abstract SupervisedClassifier create(Instances trData);
}
