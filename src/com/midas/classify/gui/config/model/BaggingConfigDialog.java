/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.midas.classify.gui.config.model;

import com.midas.classify.factory.AdaBoostFactory;
import com.midas.classify.factory.BaggingFactory;
import com.midas.classify.factory.ClassifierFactory;
import com.midas.classify.gui.ClassificationPanel;
import com.midas.classify.gui.ModelChooser;
import com.midas.classify.gui.PopupDialog;
import static com.midas.classify.gui.PopupDialog.DISPOSE;
import javax.swing.JOptionPane;

/**
 *
 * @author eduarc
 */
public class BaggingConfigDialog extends ModelConfigDialog {

  BaggingConfigPanel panel;
  ClassifierFactory factory;
  
  public BaggingConfigDialog(ClassificationPanel cPanel) {
    super(cPanel);
    setTitle(ModelChooser.MODEL_NAME[ModelChooser.M_BAGGING]);
    
    panel = new BaggingConfigPanel(cPanel);
    setViewportView(panel);
  }
  
  @Override
  public ClassifierFactory getFactory() {
    return factory;
  }

  @Override
  public int onCancel() {
    return DISPOSE;
  }

  @Override
  public int onOk() {
    
    ClassifierFactory base = panel.factoryManager.getFactory();
    
    if (base == null) {
      JOptionPane.showMessageDialog(this, "Configure the Base Model", "Info", JOptionPane.INFORMATION_MESSAGE);
      return PopupDialog.VISIBLE;
    }
    
    String strK = panel.tfK.getText();
    int k;
    
    try {
      k = Integer.parseInt(strK);
      if (k <= 0) {
        throw new NumberFormatException();
      }
    } catch (NumberFormatException e) {
      JOptionPane.showMessageDialog(this, "Invalid # Classifiers", "Info", JOptionPane.INFORMATION_MESSAGE);
      return PopupDialog.VISIBLE;
    }
    
    ClassifierFactory[] bb = new ClassifierFactory[k];
    for (int i = 0; i < k; i++) {
      bb[i] = base;
    }
    factory = new BaggingFactory(bb, k);
    
    return DISPOSE;
  }
}
