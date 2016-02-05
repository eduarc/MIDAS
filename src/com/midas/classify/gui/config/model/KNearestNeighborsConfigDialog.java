/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.midas.classify.gui.config.model;

import com.midas.classify.distance.CosineDistance;
import com.midas.classify.distance.DistanceMeasure;
import com.midas.classify.distance.EuclideanDistance;
import com.midas.classify.distance.HammingDistance;
import com.midas.classify.factory.ClassifierFactory;
import com.midas.classify.factory.KNearestNeighborsFactory;
import com.midas.classify.gui.ClassificationPanel;
import com.midas.classify.gui.DistanceChooser;
import com.midas.classify.gui.ModelChooser;
import com.midas.classify.gui.PopupDialog;
import javax.swing.JOptionPane;

/**
 *
 * @author tkd
 */
public class KNearestNeighborsConfigDialog extends ModelConfigDialog {
  
  KNearestNeighborsConfigPanel panel;
  ClassifierFactory factory;
  
  public KNearestNeighborsConfigDialog(ClassificationPanel cPanel) {
    super(cPanel);
    setTitle(ModelChooser.MODEL_NAME[ModelChooser.M_KNN]);
    
    panel = new KNearestNeighborsConfigPanel();
    setViewportView(panel);
    
    factory = null;
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
    
    int dist = panel.cbDistances.getSelectedIndex();
    if (dist == -1) {
      JOptionPane.showMessageDialog(this, "Select a Distance", "K", JOptionPane.INFORMATION_MESSAGE);
      return PopupDialog.VISIBLE;
    }
    String strK = panel.tfK.getText();
    
    int k;
    DistanceMeasure d = null;
    
    try {
      k = Integer.parseInt(strK);
      if (k <= 0) {
        throw new NumberFormatException();
      }
    } catch (NumberFormatException e) {
      JOptionPane.showMessageDialog(this, "Invalid K", "K", JOptionPane.INFORMATION_MESSAGE);
      return PopupDialog.VISIBLE;
    }
    if (dist == DistanceChooser.D_EUCLIDEAN) {
      d = new EuclideanDistance(mainPanel.dataset.getAttributes());
    }
    else if (dist == DistanceChooser.D_COSINE) {
      d = new CosineDistance();
    }
    else if (dist == DistanceChooser.D_HAMMING) {
      d = new HammingDistance();
    }
    factory = new KNearestNeighborsFactory(k, d);
    
    return DISPOSE;
  }
}
