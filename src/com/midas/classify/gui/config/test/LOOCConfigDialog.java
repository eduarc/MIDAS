/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.midas.classify.gui.config.test;

import com.midas.classify.gui.ClassificationPanel;
import com.midas.classify.gui.PopupDialog;
import com.midas.classify.metrics.LOOCValidator;
import com.midas.classify.metrics.Validator;
import javax.swing.JOptionPane;

/**
 *
 * @author eduarc
 */
public class LOOCConfigDialog extends ValidatorConfigDialog {

  LOOCConfigPanel panel;
  Validator validator;
  int iters;
  
  public LOOCConfigDialog(ClassificationPanel cPanel) {
    super(cPanel);
    
    panel = new LOOCConfigPanel();
    panel.tfIterations.setText("1");
    iters = 1;
    setViewportView(panel);
  }

  @Override
  public int onCancel() {
    return DISPOSE;
  }

  @Override
  public int onOk() {
    
    String strK = panel.tfIterations.getText();
    int it;
    
    try {
      it = Integer.parseInt(strK);
      if (it <= 0) {
        throw new NumberFormatException();
      }
    } catch (NumberFormatException e) {
      JOptionPane.showMessageDialog(this, "Invalid number of iterations", "Iterations", JOptionPane.INFORMATION_MESSAGE);
      return PopupDialog.VISIBLE;
    }
    iters = it;
    return DISPOSE;
  }

  @Override
  public Validator getValidator() {
    return new LOOCValidator(iters, mainPanel.factoryManager.getFactory(), mainPanel.dataset);
  }
}
