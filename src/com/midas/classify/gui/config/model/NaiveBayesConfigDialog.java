/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.midas.classify.gui.config.model;

import com.midas.classify.factory.ClassifierFactory;
import com.midas.classify.factory.NaiveBayesFactory;
import com.midas.classify.gui.ClassificationPanel;
import com.midas.classify.gui.ModelChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 *
 * @author Eduar Castrillo (eduarc)
 */
public class NaiveBayesConfigDialog extends ModelConfigDialog {
  
  public NaiveBayesConfigDialog(ClassificationPanel cPanel) {
    super(cPanel);
    setTitle(ModelChooser.MODEL_NAME[ModelChooser.M_NAIVE_BAYES]);
    
    JPanel empty = new JPanel();
    empty.add(new JLabel("NO PARAMS"));
    setViewportView(empty);
  }
  
  @Override
  public ClassifierFactory getFactory() {
    return new NaiveBayesFactory(0);
  }

  @Override
  public int onCancel() {
    return DISPOSE;
  }

  @Override
  public int onOk() {
    return DISPOSE;
  }
}
