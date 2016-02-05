/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.midas.classify.util;

/**
 *
 * @author tkd
 */
public class DataEntry {
  
    /* Datos de la entrada */
  protected Object[] data;
    /* Nombre de cada dimension */
  protected String[] names;
    /* Tipos de cada dimension */
  protected int[] types;
  
  public DataEntry(Object[] data, String[] names, int[] types) {
    
    this.data  = data;
    this.names = names;
    this.types = types;
  }
  
  public int getNumAttributes() {
    return data.length;
  }
  
  public void set(int dim, Object val) {
    data[dim] = val;
  }
  
  public Object get(int index) {
    return data[index];
  }
  
  public int getAttribute(String name) {
    
    int n = getNumAttributes();
    for (int i = 0; i < n; ++i) {
      if (name.equals(names[i])) {
        return i;
      }
    }
    return -1;
  }
  
  public String setName(int dim) {
    return names[dim];
  }
  
  public int setType(int dim) {
    return types[dim];
  }
  
  public String getName(int dim) {
    return names[dim];
  }
  
  public int getType(int dim) {
    return types[dim];
  }
}
