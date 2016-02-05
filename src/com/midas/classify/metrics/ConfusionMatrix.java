/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.midas.classify.metrics;

import com.midas.classify.data.Attribute;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author eduarc (Eduar Castrillo)
 */
public class ConfusionMatrix {
  
  public static final int NUM_MEASURES = 9;
  public static final int TRUE_POSITIVE_RATE = 0;
  public static final int TRUE_NEGATIVE_RATE = 1;
  public static final int POSITIVE_PREDICTIVE_VALUE = 2;
  public static final int NEGATIVE_PREDICTIVE_VALUE = 3;
  public static final int FALSE_POSITIVE_RATE = 4;
  public static final int FALSE_NEGATIVE_RATE = 5;
  public static final int FALSE_DISCOVERY_RATE = 6;
  public static final int ACCURACY = 7;
  public static final int F1_SCORE = 8;
  
  public static final int SENSITIVITY = TRUE_POSITIVE_RATE;
  public static final int RECALL = TRUE_POSITIVE_RATE;
  public static final int SPECIFICITY = TRUE_NEGATIVE_RATE;
  public static final int PRECISION = POSITIVE_PREDICTIVE_VALUE;
  public static final int FALL_OUT = FALSE_POSITIVE_RATE;
  
  public static final String[] measureName;
  
  static {
    measureName = new String[NUM_MEASURES];
    measureName[TRUE_POSITIVE_RATE] = "True Positive Rate";
    measureName[TRUE_NEGATIVE_RATE] = "True Negative Rate";
    measureName[POSITIVE_PREDICTIVE_VALUE] = "Positive Predictive Value";
    measureName[NEGATIVE_PREDICTIVE_VALUE] = "Negative Predictive Value";
    measureName[FALSE_POSITIVE_RATE] = "False Positive Rate";
    measureName[FALSE_NEGATIVE_RATE] = "False Negative Rate";    
    measureName[FALSE_DISCOVERY_RATE] = "False Discovery Rate";
    measureName[ACCURACY] = "Accuracy";
    measureName[F1_SCORE] = "F1 Score";
  }
  
  private int nClasses;
  private Attribute attribute;
  private double[][] matrix;
  
  public ConfusionMatrix(Attribute classAttribute) {
    
    nClasses = classAttribute.numValues();
    matrix = new double[nClasses][nClasses];
    attribute = classAttribute;
  }
  
  public ConfusionMatrix(Attribute classAttribute, int real[], int pred[]) {
    
    nClasses = classAttribute.numValues();
    matrix = new double[nClasses][nClasses];
    
    for (int i = 0; i < real.length; i++) {
      inc(real[i], pred[i]);
    }
  }
  
  public Attribute getAttribute() {
    return attribute;
  }
  
  public int numClasses() {
    return nClasses;
  }
  
  public void inc(int i, int j) {
    matrix[i][j]++;
  }
  
  public void dec(int i, int j) {
    matrix[i][j]++;
  }
  
  public void set(int i, int j, double v) {
    matrix[i][j] = v;
  }
  
  public double get(int i, int j) {
    return matrix[i][j];
  }
  
  public double getClassError(int i) {
    
    double e = 0, t = 0;
    for (int j = 0; j < nClasses; j++) {
      t += matrix[i][j];
      if (i != j) {
        e += matrix[i][j];
      }
    }
    return e/t;
  }
  
  public double[] getClassError() {
    
    double[] e = new double[nClasses];
    for (int i = 0; i < nClasses; i++) {
      e[i] = getClassError(i);
    }
    return e;
  }
  
  public ConfusionMatrix getBinaryMatrix(int positive) {
    
    int n = attribute.numValues();
    List<String> vals = new ArrayList();
    vals.add(attribute.value(positive));
    vals.add("NOT "+attribute.value(positive));
    Attribute attr_bin = new Attribute(attribute.name(), vals);
    
    ConfusionMatrix bin = new ConfusionMatrix(attr_bin);
    double tp = matrix[positive][positive], fn = 0, fp = 0, tn = 0;
    
    for (int i = 0; i < n; i++) {
      if (i != positive) {
        fp += matrix[i][positive];
        fn += matrix[positive][i];
        for (int j = 0; j < n; j++) {
          if (j != positive) {
            tn += matrix[i][j];
          }
        }
      }
    }
    bin.set(0, 0, tp);
    bin.set(0, 1, fn);
    bin.set(1, 0, fp);
    bin.set(1, 1, tn);
    return bin;
  }
  
    // ----------------------------------------
    // Measures ONLY for Binary ConfusionMatrix
    // ----------------------------------------
  
  public double getTruePositiveRate() {
    
    double tp = matrix[0][0];
    double fn = matrix[0][1];
    return tp/(tp+fn);
  }
  
  public double getTrueNegativeRate() {
    
    double tn = matrix[1][1];
    double fp = matrix[1][0];
    return tn/(tn+fp);
  }
  
  public double getPositivePredictiveValue() {
    
    double tp = matrix[0][0];
    double fp = matrix[1][0];
    return tp/(tp+fp);
  }
  
  public double getNegativePredictiveValue() {
    
    double tn = matrix[1][1];
    double fn = matrix[0][1];
    return tn/(tn+fn);
  }
  
  public double getFalsePositiveRate() {
    
    double fp = matrix[1][0];
    double tn = matrix[1][1];
    return fp/(fp+tn);
  }
  
  public double getFalseNegativeRate() {
    
    double fn = matrix[0][1];
    double tp = matrix[0][0];
    return fn/(fn+tp);
  }
  
  public double getFalseDiscoveryRate() {
    
    double tp = matrix[0][0];
    double fp = matrix[1][0];
    return fp/(tp+fp);
  }
  
  public double getAccuracy() {
    
    double tp = matrix[0][0];
    double tn = matrix[1][1];
    double fp = matrix[1][0];
    double fn = matrix[0][1];
    return (tp+tn)/(tp+tn+fp+fn);
  }
  
  public double getF1Score() {
    
    double tp = matrix[0][0];
    double fp = matrix[1][0];
    double fn = matrix[0][1];
    return 2*tp/(2*tp+fp+fn);
  }
  
  public double getSensitivity() {
    return getTruePositiveRate();
  }
  
  public double getRecall() {
    return getTruePositiveRate();
  }
  
  public double getSpecificity() {
    return getTrueNegativeRate();
  }
  
  public double getPrecision() {
    return getPositivePredictiveValue();
  }
  
  public double getFallOut() {
    return getFalsePositiveRate();
  }
  
  public double getMissRate() {
    return getFalseNegativeRate();
  }
  
  public double getMeasure(int m) {
    
    switch (m) {
      case ConfusionMatrix.TRUE_POSITIVE_RATE: 
        return getTruePositiveRate();
      case ConfusionMatrix.TRUE_NEGATIVE_RATE: 
        return getTrueNegativeRate();
      case ConfusionMatrix.POSITIVE_PREDICTIVE_VALUE: 
        return getPositivePredictiveValue();
      case ConfusionMatrix.NEGATIVE_PREDICTIVE_VALUE: 
        return getNegativePredictiveValue();
      case ConfusionMatrix.FALSE_POSITIVE_RATE: 
        return getFalsePositiveRate();
      case ConfusionMatrix.FALSE_NEGATIVE_RATE: 
        return getFalseNegativeRate();
      case ConfusionMatrix.FALSE_DISCOVERY_RATE: 
        return getFalseDiscoveryRate();
      case ConfusionMatrix.ACCURACY: 
        return getAccuracy();
      case ConfusionMatrix.F1_SCORE:
        return getF1Score();
      default:
        return -1;
    }
  }
  
  public double[] getMeasures() {
    
    double[] m = new double[NUM_MEASURES];
    m[ConfusionMatrix.TRUE_POSITIVE_RATE] = getTruePositiveRate();
    m[ConfusionMatrix.TRUE_NEGATIVE_RATE] = getTrueNegativeRate();
    m[ConfusionMatrix.POSITIVE_PREDICTIVE_VALUE] = getPositivePredictiveValue();
    m[ConfusionMatrix.NEGATIVE_PREDICTIVE_VALUE] = getNegativePredictiveValue();
    m[ConfusionMatrix.FALSE_POSITIVE_RATE] = getFalsePositiveRate();
    m[ConfusionMatrix.TRUE_NEGATIVE_RATE] = getFalseNegativeRate();
    m[ConfusionMatrix.FALSE_DISCOVERY_RATE] = getFalseDiscoveryRate();
    m[ConfusionMatrix.ACCURACY] = getAccuracy();
    m[ConfusionMatrix.F1_SCORE] = getF1Score();
    return m;
  }
  
  @Override
  public String toString() {
    
    DecimalFormat df = new DecimalFormat();
    df.setMinimumFractionDigits(2);
    df.setMaximumFractionDigits(2);
    
    String out = "\t\t";
    for (int i = 0; i < nClasses; i++) {
      out += attribute.value(i) + "\t";
    }
    out += "\n";
    for (int i = 0; i < nClasses; i++) {
      out += attribute.value(i) + "\t";
      for (int j = 0; j < nClasses; j++) {
        out += df.format(matrix[i][j]) + " \t";
      }
      out += "\n";
    }
    out += "\n";
    
    return out;
  }
}
