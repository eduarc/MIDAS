/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.midas.classify.data;

import com.midas.classify.util.DataSet;
import java.util.ArrayList;

/**
 *
 * @author eduarc
 */
public class ARFFDataSet extends DataSet {
  
  private String[] attributeFormat; // In case of Data data type
  
  public ARFFDataSet(String name, int numAttrs) {
    super(name,numAttrs);
    
    int n = numAttrs;
    attributeFormat = new String[n];
  }
  
  public void setAttributeFormat(int attr, String format) {
    attributeFormat[attr] = format;
  }
  
  public String getAttributeFormat(int attr) {
    return attributeFormat[attr];
  }
}
