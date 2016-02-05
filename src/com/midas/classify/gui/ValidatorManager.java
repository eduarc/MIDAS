/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.midas.classify.gui;

import com.midas.classify.gui.config.test.BootstrapConfigDialog;
import com.midas.classify.gui.config.test.KFoldCrossConfigDialog;
import com.midas.classify.gui.config.test.LOOCConfigDialog;
import com.midas.classify.gui.config.test.RSSConfigDialog;
import com.midas.classify.gui.config.test.ValidatorConfigDialog;
import com.midas.classify.metrics.Validator;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JOptionPane;

/**
 *
 * @author eduarc
 */
public class ValidatorManager {
  
  ClassificationPanel cPanel;
  
  JButton bChoose;
  JButton bConfig;
  
  ValidatorChooser chooser;
  ValidatorConfigDialog config;
  
  Validator validator;
  
  public ValidatorManager(ClassificationPanel cPanel) {
  
    this.cPanel = cPanel;
    
    chooser = new ValidatorChooser();
    
    setBChoose(new JButton("Choose"));
    setBConfig(new JButton("Configure"));
    
    validator = null;
  }
  
  public final void setBChoose(JButton b) {
    
    bChoose = b;
    bChoose.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        bChooseActionPerformed(e);
      }
    });
  }
  
  public final void setBConfig(JButton b) {
    
    bConfig = b;
    bConfig.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        bConfigActionPerformed(e);
      }
    });
  }
  
  public JButton getBChoose() {
    return bChoose;
  }
  
  public JButton getBConfig() {
    return bConfig;
  }
  
  public Validator getValidator() {
    
    if (config == null) {
      return null;
    }
    return config.getValidator();
  }
  
  private void bChooseActionPerformed(java.awt.event.ActionEvent evt) {                                            
    
    int s = chooser.showDialog(bChoose);
    if (s != -1) {
      bConfig.setText(chooser.getSelectedName());
      validator = null;
    }
    
    if (s == ValidatorChooser.T_BOOTSTRAP) {
      config = new BootstrapConfigDialog(cPanel);
    }
    else if (s == ValidatorChooser.T_K_FOLD_CROSS) {
      config = new KFoldCrossConfigDialog(cPanel);
    }
    else if (s == ValidatorChooser.T_RANDOM_SUB_SAMPLING) {
      config = new RSSConfigDialog(cPanel);
    }
    else if (s == ValidatorChooser.T_LOOC) {
      config = new LOOCConfigDialog(cPanel);
    }
    
    if (config != null) {
      validator = config.getValidator();
    }
  }                                           

  private void bConfigActionPerformed(java.awt.event.ActionEvent evt) {                                               
    
    if (config == null) {
      JOptionPane.showMessageDialog(cPanel, "Select a Validator method", "Info", JOptionPane.INFORMATION_MESSAGE);
      return;
    }
    if (config.showDialog(bConfig) == PopupDialog.OK) {
      validator = config.getValidator();
    }
  } 
}
