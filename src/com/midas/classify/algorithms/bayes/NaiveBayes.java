/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.midas.classify.algorithms.bayes;

import com.midas.classify.algorithms.SupervisedClassifier;
import com.midas.classify.data.Attribute;
import com.midas.classify.data.Instance;
import com.midas.classify.data.Instances;

/**
 *
 * @author Eduar Castrillo (eduarc)
 */
public class NaiveBayes extends SupervisedClassifier {
  
  public static final int UNIFORM_CLASS  = 0;
  public static final int SAMPLED_CLASS  = 1;
  
  private int nClasses;
  private double[] classP;
  private double[][][] frequency;
  private double[][] mean, samples, variance;
  
  private Attribute[] attributes;
  
  public NaiveBayes(Instances trData, int classDistribution) {
    
    super(trData);
    
    double n  = trData.numInstances();
    int nAttr = trData.numAttributes();
    
    nClasses = trData.numClasses();
    
    variance  = new double[nClasses][nAttr];
    mean      = new double[nClasses][nAttr];
    samples   = new double[nClasses][nAttr];
    frequency = new double[nClasses][nAttr][];
    
    classP = new double[nClasses];
    
    attributes = new Attribute[nAttr];
    for (int i = 0; i < nAttr; i++) {
      attributes[i] = trData.attribute(i);
    }
    
    for (int j = 0; j < nAttr; j++) {
      if (attributes[j].isNominal()) {
        int l = attributes[j].numValues();
        for (int i = 0; i < nClasses; i++) {
          frequency[i][j] = new double[l];
          for (int k = 0; k < l; k++) {
            frequency[i][j][k] = 1;
          }
        }
      }
    }
    for (int i = 0; i < n; i++) {
      Instance ins = trData.instance(i);
      int c = ins.classValue();
      
      for (int j = 0; j < nAttr; j++) {
        if (attributes[j].isNumeric()) {
          mean[c][j] += ins.value(j);
          samples[c][j]++;
        }
        else if (attributes[j].isNominal()) {
          frequency[c][j][(int)ins.value(j)]++;
        }
        else {
          throw new IllegalArgumentException("NaiveBayes: Attribute type not supported");
        }
      }
    }
    for (int i = 0; i < nClasses; ++i) {
      for (int j = 0; j < nAttr; ++j) {
        mean[i][j] /= samples[i][j];
      }
    }
    for (int i = 0; i < n; ++i) {
      Instance ins = trData.instance(i);
      int c = ins.classValue();
      
      for (int j = 0; j < nAttr; j++) {
        if (attributes[j].isNumeric()) {
          double m = mean[c][j];
          double v = ins.value(j);
          variance[c][j] += (v-m)*(v-m);
        }
      }
    }
    for (int i = 0; i < nClasses; ++i) {
      for (int j = 0; j < nAttr; ++j) {
        variance[i][j] /= samples[i][j]-1;
      }
    }
    
    for (int i = 0; i < nClasses; ++i) {
      for (int j = 0; j < nAttr; ++j) {
        if (j == classIndex) {
          continue;
        }
        if (attributes[j].isNominal()) {
          double s = 0;
          int l = frequency[i][j].length;
          for (int k = 0; k < l; k++) {
            s += frequency[i][j][k];
          }
          for (int k = 0; k < l; k++) {
            frequency[i][j][k] /= s;
          }
        }
      }
    }
    
    if (classDistribution == UNIFORM_CLASS) {
      for (int i = 0; i < nClasses; i++) {
        classP[i] = 1./nClasses;
      }
    }
    else if (classDistribution == SAMPLED_CLASS) {
      for (int i = 0; i < n; ++i) {
        int c = trData.instance(i).classValue();
        classP[c]++;
      }
      for (int i = 0; i < nClasses; i++) {
        classP[i] /= n;
      }
    }
    else {
      throw new IllegalArgumentException("NaiveBayes: Unknown predefined class Distribution: "+classDistribution);
    }
  }

  private double getAttrP(double val, int att, int c) {
    return frequency[c][att][(int)val];
  }
  
  private double getNormalP(double v, double u, double d) {
    return Math.exp(-((v-u)*(v-u)/(2*d)))/Math.sqrt(2*Math.PI*d);
  }
  
  @Override
  public int classify(Instance sample) {
    
    int m = sample.numAttributes();
    for (int i = 0; i < m; i++) {
      if (i != classIndex && sample.isMissing(i)) {
        throw new IllegalArgumentException("Missing values not supported by NaiveBayes");
      }
    }
    
    int nAttr = trainingData.numAttributes();
    double bestP = Double.MIN_VALUE;
    int classy = -1;
    
    for (int i = 0; i < nClasses; i++) {
      double p = classP[i];
      for (int j = 0; j < nAttr; j++) {
        if (j == classIndex) {
          continue;
        }
        if (attributes[j].isNominal()) {
          p *= getAttrP(sample.value(j), j, i);
        }
        else {
          p *= getNormalP(sample.value(j), mean[i][j], variance[i][j]);
        }
      }
      if (p > bestP) {
        bestP = p;
        classy = i;
      }
    }
    return classy;
  }

  @Override
  public String summary() {
    
    int n = trainingData.numAttributes();
    
    String out = "";
    out += "Model: NaiveBayes\n\n";
    out += "*** Class Probability ***\n";
    
    Attribute classAtt = trainingData.classAttribute();
    for (int c : classAtt.iterableValues()) {
      out += "    P("+classAtt.value(c)+") = "+classP[c]+"\n";
    }
    out += "\n";
    
    out += "*** Discrete Attributes (Probability per Class) ***\n";
    
    for (int i : classAtt.iterableValues()) {
      out += "\nClass: "+classAtt.value(i)+"\n";
      for (int j = 0; j < n; j++) {
        if (j == trainingData.classIndex()) {
          continue;
        }
        if (attributes[j].isNominal()) {
          out += "\n    - Attribute: "+attributes[j].name()+"\n\n";
          for (int v : attributes[j].iterableValues()) {
            String sv = attributes[j].value(v);
            out += "        P("+sv+") = "+getAttrP(v, j, i)+"\n";
          }
        }
      }
    }
    
    out += "\n\n";
    out += "*** Continuous Attributes (Normal Distribution Parameters per Class) ***\n";
    
    for (int i : classAtt.iterableValues()) {
      out += "\nClass: "+classAtt.value(i)+"\n";
      for (int j = 0; j < n; j++) {
        if (j == trainingData.classIndex()) {
          continue;
        }
        if (attributes[j].isNumeric()) {
          out += "    - Attribute: "+attributes[j].name()+"\n";
          out += "        Mean = "+mean[i][j]+"\n        Variance: "+Math.sqrt(variance[i][j])+"\n\n";
        }
      }
    }
    return out;
  }
  
  @Override
  public String toString() {
    return summary();
  }

  @Override
  public String briefSummary() {
    return "Model: Naive Bayes";
  }
}
