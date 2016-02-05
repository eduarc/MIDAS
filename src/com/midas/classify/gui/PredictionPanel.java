/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.midas.classify.gui;

import com.midas.classify.algorithms.SupervisedClassifier;
import com.midas.classify.algorithms.bayes.NaiveBayes;
import com.midas.classify.algorithms.dtrees.ID3;
import com.midas.classify.algorithms.knn.KNearestNeighbors;
import com.midas.classify.data.Attribute;
import com.midas.classify.data.Instance;
import com.midas.classify.data.Instances;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Position;

/**
 *
 * @author eduarc (Eduar Castrillo)
 */
class PredictionPanel extends JPanel {
  
  ClassificationPanel cPanel;
  
  SupervisedClassifier classifier;
  Instances dataset;
  List<Attribute> attributes;
  Attribute classAttribute;
  
  JButton bClassy;
  JLabel[] lAttributeName;
  JComponent[] input;
  JLabel lClassy;
  JLabel info;
  
  public PredictionPanel(SupervisedClassifier classifier, ClassificationPanel cp) {
    
    this.cPanel = cp;
    
    setLayout(new BorderLayout());
    
    info = new JLabel("Leave the field empty for missing value. The class attribute is disabled");
    
    this.classifier = classifier;
    dataset = classifier.getTrainingData();
    attributes = dataset.getAttributes();
    classAttribute = dataset.classAttribute();
    
    int n = dataset.numAttributes();
    
    lAttributeName = new JLabel[n];
    input = new JComponent[n];
    
    for (int i = 0; i < n; i++) {
      Attribute att = attributes.get(i);
      lAttributeName[i] = new JLabel(att.name());
      
      if (att.isNominal()) {
        String[] vals = att.values().toArray(new String[0]);
        String[] arr = new String[vals.length+1];
        arr[0] = "";
        for (int j = 0; j < vals.length; j++) {
          arr[j+1] = vals[j];
        }
        input[i] = new JComboBox(new DefaultComboBoxModel(arr));
      }
      else {
        input[i] = new JTextField();
      }
      if (i == dataset.classIndex()) {
        input[i].setEnabled(false);
      }
    }
    
    JPanel pInput = new JPanel(new GridLayout(n, 2));
    
    for (int i = 0; i < n; i++) {
      pInput.add(lAttributeName[i]);
      pInput.add(input[i]);
    }
    
    bClassy = new JButton("Classify");
    lClassy = new JLabel("Prediction: ");
    
    JPanel pBottom = new JPanel(new FlowLayout(FlowLayout.LEFT));
    pBottom.add(bClassy);
    pBottom.add(lClassy);
    
    add(info, BorderLayout.NORTH);
    add(pInput, BorderLayout.CENTER);
    add(pBottom, BorderLayout.SOUTH);
    
    bClassy.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        
        Instance s;
        try {
          s = getClassy();
        } catch (IllegalArgumentException ex) {
          JOptionPane.showMessageDialog(cPanel, ex.getMessage(), "Info", JOptionPane.INFORMATION_MESSAGE);
          return;
        }
        lClassy.setText("Prediction: "+s.stringValue(s.classIndex()));
        Document doc = cPanel.aOutput.getDocument();
        Position p = doc.getEndPosition();
        try {
          doc.insertString(p.getOffset(), s.toStringNoWeight()+"\n", null);
        } catch (BadLocationException ex) {
          System.err.println("BAD LOCATION");
        }
      }
    });
  }
  
  public Instance getClassy() {
    
    int n = dataset.numAttributes();    
    double[] values = new double[n];
    
    for (int i = 0; i < n; i++) {
      Attribute att = attributes.get(i);
      
      if (att.isNominal()) {
        String sv = (String)((JComboBox)input[i]).getSelectedItem();
        if (sv.length() == 0) {
          values[i] = Double.NaN;
        } else {
          values[i] = att.indexOfValue(sv);
        }
      }
      else if (att.isDate()) {
        String sv = ((JTextField)input[i]).getText();
        if (sv.length() == 0) {
          values[i] = Double.NaN;
        } else {
          values[i] = att.parseDate(sv);
        }
      }
      else if (att.isString()) {
        String sv = (String)((JTextField)input[i]).getText();
        if (sv.length() == 0) {
          values[i] = Double.NaN;
        } else {
          values[i] = att.addStringValue(sv);
        }
      }
      else if (att.isNumeric()) {
        String sv = (String)((JTextField)input[i]).getText();
        if (sv.length() == 0) {
          values[i] = Double.NaN;
        } else {
          try {
            values[i] = Double.parseDouble(sv);
          } catch(NumberFormatException ex) {
            throw new IllegalArgumentException("Invalid numeric value: "+sv);
          }
        }
      }
    }
    Instance s = new Instance(1, values);
    s.setDataset(dataset);
    try {
      int pred = classifier.classify(s);
      s.setClassValue(pred);
    } catch(IllegalArgumentException e) {
      throw new IllegalArgumentException(e.getMessage());
    }
    return s;
  }
  
  @Override
  public Dimension getPreferredSize() {
    return new Dimension(520, 250);
  }
}
