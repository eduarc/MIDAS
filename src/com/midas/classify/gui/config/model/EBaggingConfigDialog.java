/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.midas.classify.gui.config.model;

import com.midas.classify.factory.BaggingFactory;
import com.midas.classify.factory.ClassifierFactory;
import com.midas.classify.gui.ClassificationPanel;
import com.midas.classify.gui.FactoryManager;
import java.awt.Component;
import java.awt.GridLayout;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

/**
 *
 * @author tkd
 */
public class EBaggingConfigDialog extends ModelConfigDialog {

  ClassifierFactory factory;
  FactoryManager[] factoryManager;
  JPanel panel;
  int k;
  
  public EBaggingConfigDialog(ClassificationPanel cPanel) {
    super(cPanel);
    
    k = -1;
    setTitle("Extended Bagging");
  }

  @Override
  public int showDialog(Component parent) {
   
    String s;
    if (k == -1) {
      s = JOptionPane.showInputDialog(super.mainPanel, "# Classifier:", "Extended Bagging", JOptionPane.INFORMATION_MESSAGE);
    } else {
      s = JOptionPane.showInputDialog(super.mainPanel, "# Classifier (empty to continue):", "Extended Bagging", JOptionPane.INFORMATION_MESSAGE);
    }
    
    if (s == null) {
      return CANCEL;
    }
    if (k == -1 || s.length() != 0) {
      try {
        k = Integer.parseInt(s);
      } catch (NumberFormatException ex) {
        JOptionPane.showMessageDialog(super.mainPanel, "Invalid # Classifiers", "Extended Bagging", JOptionPane.INFORMATION_MESSAGE);
        return CANCEL;
      }
    }
    if (s.length() != 0) {
      if (panel != null) {
        getContentPane().remove(panel);
      }
      factoryManager = new FactoryManager[k];
      panel = new JPanel(new GridLayout(k, 2));
      for (int i = 0; i < k; i++) {
        factoryManager[i] = new FactoryManager(super.mainPanel);
        panel.add(factoryManager[i].getBChoose());
        panel.add(factoryManager[i].getBConfig());
      }
      setViewportView(panel);
      setSize(500, getContentPane().getSize().height);
    }
    return super.showDialog(parent);
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
    
    ClassifierFactory[] base = new ClassifierFactory[k];
    for (int i = 0; i < k; i++) {
      base[i] = factoryManager[i].getFactory();
      if (base[i] == null) {
        JOptionPane.showMessageDialog(mainPanel, "Configure the "+i+"-th model", "Extended Bagging", JOptionPane.INFORMATION_MESSAGE);
        return VISIBLE;
      }
    }
    factory = new BaggingFactory(base, k);
    return DISPOSE;
  }
}
