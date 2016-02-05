/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.midas.classify.gui;

import com.midas.classify.factory.ClassifierFactory;
import com.midas.classify.gui.config.model.AdaBoostConfigDialog;
import com.midas.classify.gui.config.model.BaggingConfigDialog;
import com.midas.classify.gui.config.model.C45ConfigDialog;
import com.midas.classify.gui.config.model.EAdaBoostConfigDialog;
import com.midas.classify.gui.config.model.EBaggingConfigDialog;
import com.midas.classify.gui.config.model.ID3ConfigDialog;
import com.midas.classify.gui.config.model.KNearestNeighborsConfigDialog;
import com.midas.classify.gui.config.model.ModelConfigDialog;
import com.midas.classify.gui.config.model.NaiveBayesConfigDialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JOptionPane;

/**
 *
 * @author eduarc
 */
public class FactoryManager {
  
  ClassificationPanel cPanel;
  
  JButton bChoose;
  JButton bConfig;
  
  ModelChooser chooser;
  ModelConfigDialog config;
  
  ClassifierFactory factory;
  
  public FactoryManager(ClassificationPanel cPanel) {
  
    this.cPanel = cPanel;
    
    chooser = new ModelChooser();
    
    bChoose = new JButton("", new ImageIcon(getClass().getResource("/com/midas/classify/gui/icons/edit-find.png")));
    bChoose.setToolTipText("Choose the model");
    setBChoose(bChoose);
    bConfig = new JButton("Configure", new ImageIcon(getClass().getResource("/com/midas/classify/gui/icons/edit.png")));
    bConfig.setToolTipText("Configure the model");
    setBConfig(bConfig);
    
    factory = null;
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
  
  public ClassifierFactory getFactory() {
    
    if (config == null) {
      return null;
    }
    return config.getFactory();
  }
  
  public void bChooseActionPerformed(ActionEvent e) {
    
    int s = chooser.showDialog(bChoose);
    if (s != -1) {
      bConfig.setText(chooser.getSelectedName());
      factory = null;
    }
    
    if (s == ModelChooser.M_ID3) {
      config = new ID3ConfigDialog(cPanel);
    }
    else if (s == ModelChooser.M_C45) {
      config = new C45ConfigDialog(cPanel);
    }
    else if (s == ModelChooser.M_KNN) {
      config = new KNearestNeighborsConfigDialog(cPanel);
    }
    else if (s == ModelChooser.M_NAIVE_BAYES) {
      config = new NaiveBayesConfigDialog(cPanel);
    }
    else if (s == ModelChooser.M_BAGGING) {
      config = new BaggingConfigDialog(cPanel);
    }
    else if (s == ModelChooser.M_ADABOOST) {
      config = new AdaBoostConfigDialog(cPanel);
    }
    else if (s == ModelChooser.M_EBAGGING) {
      config = new EBaggingConfigDialog(cPanel);
    }
    else if (s == ModelChooser.M_EADABOOST) {
      config = new EAdaBoostConfigDialog(cPanel);
    }
    
    if (config != null) {
      factory = config.getFactory();
    }
  }
  
  private void bConfigActionPerformed(ActionEvent e) {
    
    if (config == null) {
      JOptionPane.showMessageDialog(cPanel, "Select a Model", "Info", JOptionPane.INFORMATION_MESSAGE);
      return;
    }
    if (config.showDialog(bConfig) == PopupDialog.OK) {
      factory = config.getFactory();
    }
  }
}
