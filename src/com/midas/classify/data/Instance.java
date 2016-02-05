/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.midas.classify.data;

/**
 *
 * @author eduarc
 */
public class Instance {
  
  protected Instances dataset;
  protected double[] values;
  protected double weight;
  
  public Instance(int numAttributes) {
    
    values = new double[numAttributes];
    for (int i = 0; i < numAttributes; i++) {
      values[i] = Double.NaN;
    }
    weight = 1;
    dataset = null;
  }
  
  public Instance(double weight, double[] values) {
    
    this.values = values;
    this.weight = weight;
  }
  
  public Instance(Instance src) {
    
    values = src.values.clone();
    weight = src.weight;
    dataset = src.dataset;
  }
  
  public Instances dataset() {
    return dataset;
  }
  
  public int classIndex() {
    return dataset.classIndex;
  }
  
  public Attribute classAttribute() {
    return dataset.classAttribute();
  }
  
  public int classValue() {
    return (int)values[dataset.classIndex];
  }
  
  public boolean classIsMissing() {
    return Double.isNaN(classValue());
  }
  
  public boolean isMissing(int attIndex) {
    return Double.isNaN(values[attIndex]);
  }
  
  public int numAttributes() {
    return values.length;
  }
  
  public int numClasses() {
    return dataset.classAttribute().numValues();
  }
  
  public void replaceMissingValues(double[] array) {
    
    for (int i = 0; i < array.length; i++) {
      if (isMissing(i)) {
        values[i] = array[i];
      }
    }
  }
  
  public void setClassMissing() {
    values[dataset.classIndex] = Double.NaN;
  }
  
  public void setClassValue(double value) {
    values[dataset.classIndex] = value;
  }
  
  public void setClassValue(String value) {
    
    Attribute att = dataset.classAttribute();
    values[dataset.classIndex] = att.indexOfValue(value);
  }
  
  public void setDataset(Instances dataset) {
    this.dataset = dataset;
  }
  
  public void setMissing(int attIndex) {
    values[attIndex] = Double.NaN;
  }
  
  public void setValue(int attIndex, double value) {
    values[attIndex] = value;
  }
  
  public void setValue(int attIndex, String value) {
    
    Attribute att = dataset.classAttribute();
    values[attIndex] = att.indexOfValue(value);
  }
  
  public void setWeight(double weight) {
    this.weight = weight;
  }
    
  public String stringValue(int attIndex) {
    
    Attribute att = dataset.attribute(attIndex);
    
    if (att.isNominal() || att.isString()) {
      return att.value((int)values[attIndex]);
    }
    else if (att.isNumeric()) {
      return "" + values[attIndex];
    }
    else if (att.isDate()) {
      return att.formatDate(values[attIndex]);
    }
    return "";
  }
  
  public double[] toDoubleArray() {
    return values;
  }
  
  public String toString(int attIndex) {
    
    Attribute att = dataset.attribute(attIndex);
    
    if (isMissing(attIndex)) {
      return "?";
    }
    
    if (att.isNominal() || att.isString()) {
      return "\'" + att.value((int)values[attIndex]) + "\'";
    }
    else if (att.isNumeric()) {
      return "" + values[attIndex];
    }
    else if (att.isDate()) {
      return att.formatDate(values[attIndex]);
    }
    return "";
  }
  
  public String toString() {
    return toStringNoWeight() + " " + weight;
  }
  
  public String toStringNoWeight() {
    
    String out = "";
    
    if (dataset == null) {
      out += values[0];
      for (int i = 1; i < values.length; i++) {
        out += " " + values[i];
      }
    } else {
      for (int i = 0; i < values.length; i++) {
        out += toString(i);
        if (i+1 < values.length) {
          out += ", ";
        }
      }
    }
    return out;
  }
  
  public double value(int attIndex) {
    return values[attIndex];
  }
  
  public double weight() {
    return weight;
  }
}
