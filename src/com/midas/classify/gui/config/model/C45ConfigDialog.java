/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.midas.classify.gui.config.model;

import com.midas.classify.factory.C45Factory;
import com.midas.classify.factory.ClassifierFactory;
import com.midas.classify.gui.ClassificationPanel;
import com.midas.classify.gui.ModelChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 *
 * @author Eduar Castrillo (eduarc)
 */
public class C45ConfigDialog extends ModelConfigDialog {
  
  public C45ConfigDialog(ClassificationPanel cPanel) {
    super(cPanel);
    setTitle(ModelChooser.MODEL_NAME[ModelChooser.M_C45]);
    
    JPanel empty = new JPanel();
    empty.add(new JLabel("NO PARAMS"));
    setViewportView(empty);
  }
  
  @Override
  public ClassifierFactory getFactory() {
    return new C45Factory();
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
