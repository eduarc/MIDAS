/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.midas.classify.gui;

import java.awt.Color;
import java.awt.Dimension;
import org.math.plot.*;

/**
 *
 * @author eduarc (Eduar Castrillo)
 */
public class ROCSpaceViewer extends Plot2DPanel {
  
  public ROCSpaceViewer(double[] tpr, double[] fpr, double[] avgTpr, double[] avgFpr) {
   
    addLegend(Plot2DPanel.SOUTH);
    setAxisLabels("FPR or (1 - specificity)", "TPR or sensitivity");
    addScatterPlot("Iteration", Color.BLUE, fpr, tpr);
    addScatterPlot("Average", Color.RED, avgFpr, avgTpr);
    setFixedBounds(0, 0, 1);
    setFixedBounds(1, 0, 1);
  }
  
  public Dimension getPreferredSize() {
    return new Dimension(800, 600);
  }
}
