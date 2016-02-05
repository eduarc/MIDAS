/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.midas.classify.gui.config.test;

import com.midas.classify.gui.ClassificationPanel;
import com.midas.classify.gui.PopupDialog;
import com.midas.classify.metrics.KFoldCrossValidator;
import com.midas.classify.metrics.Validator;
import javax.swing.JOptionPane;

/**
 *
 * @author eduarc
 */
public class KFoldCrossConfigDialog extends ValidatorConfigDialog {

  KFoldCrossConfigPanel panel;
  Validator validator;
  int k;
  
  public KFoldCrossConfigDialog(ClassificationPanel cPanel) {
    super(cPanel);
    
    panel = new KFoldCrossConfigPanel();
    panel.tfK.setText("1");
    k = 1;
    setViewportView(panel);
  }

  @Override
  public int onCancel() {
    return DISPOSE;
  }

  @Override
  public int onOk() {
    
    String strK = panel.tfK.getText();
    int tk;
    
    try {
      tk = Integer.parseInt(strK);
      if (tk <= 0) {
        throw new NumberFormatException();
      }
    } catch (NumberFormatException e) {
      JOptionPane.showMessageDialog(this, "Invalid K = ", "K", JOptionPane.INFORMATION_MESSAGE);
      return PopupDialog.VISIBLE;
    }
    k = tk;
    return DISPOSE;
  }

  @Override
  public Validator getValidator() {
    return new KFoldCrossValidator(k, mainPanel.factoryManager.getFactory(), mainPanel.dataset);
  }
}
