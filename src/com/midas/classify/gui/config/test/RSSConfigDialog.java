/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.midas.classify.gui.config.test;

import com.midas.classify.gui.ClassificationPanel;
import com.midas.classify.gui.PopupDialog;
import com.midas.classify.metrics.RandomSubSamplingValidator;
import com.midas.classify.metrics.Validator;
import javax.swing.JOptionPane;

/**
 *
 * @author eduarc
 */
public class RSSConfigDialog extends ValidatorConfigDialog {

  RSSConfigPanel panel;
  Validator validator;
  int iters;
  int samples;
  
  public RSSConfigDialog(ClassificationPanel cPanel) {
    super(cPanel);
    
    panel = new RSSConfigPanel();
    panel.tfIterations.setText("1");
    panel.tfSamplingSize.setText("1");
    iters = 1;
    samples = 1;
    setViewportView(panel);
  }

  @Override
  public Validator getValidator() {
    return new RandomSubSamplingValidator(iters, samples, mainPanel.factoryManager.getFactory(), mainPanel.dataset);
  }

  @Override
  public int onCancel() {
    return DISPOSE;
  }

  @Override
  public int onOk() {
    
    String str = panel.tfIterations.getText();
    int it;
    
    try {
      it = Integer.parseInt(str);
      if (it <= 0) {
        throw new NumberFormatException();
      }
    } catch (NumberFormatException e) {
      JOptionPane.showMessageDialog(this, "Invalid number of iterations", "Iterations", JOptionPane.INFORMATION_MESSAGE);
      return PopupDialog.VISIBLE;
    }
    
    str = panel.tfSamplingSize.getText();
    int sz;
    
    try {
      sz = Integer.parseInt(str);
      if (sz <= 0) {
        throw new NumberFormatException();
      }
    } catch (NumberFormatException e) {
      JOptionPane.showMessageDialog(this, "Invalid sampling size", "Sampling size", JOptionPane.INFORMATION_MESSAGE);
      return PopupDialog.VISIBLE;
    }
    iters = it;
    samples = sz;
    return DISPOSE;
  }
}
