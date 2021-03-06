/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.midas.classify.util;

import java.util.ArrayList;

/**
 * @author eduarc (Eduar Castrillo)
 */
public class DataSet {
    /* Tipos de datos predefinidos */
  public static final int NUMERIC = 0;
  public static final int INTEGER = 1;
  public static final int REAL    = 2;
  public static final int STRING  = 3;
  public static final int DATE    = 4;
  public static final int NOMINAL = 5;
    // @see SupervisedClassifier
  public static final int CLASS   = 6;
  
    /* Nombre del DataSet */
  private String name;
    /* Numero de dimensiones por registro */
  private int numAttributes;
   /* Nombre de cada atributo */
  private String[] names;
    /* Tipo de cada atributo */
  private int[] types;
    /* Valores nominales para cada atributo */
  private ArrayList<String>[] nominals;
    /* Datos */
  ArrayList<Object[]> data;
  
  public DataSet(String name, int attrs) {
    
    this.name = name;
    numAttributes = attrs;
    names = new String[attrs];
    types = new int[attrs];
    nominals = new ArrayList[attrs];
    data = new ArrayList();
  }
  
  public DataSet(DataSet src, boolean copyData) {
    
    name = src.getName();
    numAttributes = src.getNumAttributes();
    types = src.types.clone();
    names = src.names.clone();
    nominals = src.nominals.clone();
    if (copyData) {
      data = (ArrayList<Object[]>)src.data.clone();
    } else {
      data = new ArrayList();
    }
  }
  
  public void setName(String name) {
    this.name = name;
  }
  
  public String getName() {
    return name;
  }
  
  public int getAttributeIndex(String name) {
    
    for (int i = 0; i < numAttributes; ++i) {
      if (name.equals(names[i])) {
        return i;
      }
    }
    return -1;
  }
  
  public int getNumAttributes() {
    return numAttributes;
  }
  
  public void setAttributeName(int attr, String name) {
    names[attr] = name;
  }
  
  public String getAttributeName(int attr) {
    return names[attr];
  }
  
  public void setAttributeType(int attr, int type) {
    types[attr] = type;
  }
  
  public int getAttributeType(int dim) {
    return types[dim];
  }
  
  public void setNominalValues(int attr, ArrayList<String> values) {
    nominals[attr] = values;
  }
  
  public ArrayList<String> getNominalValues(int attr) {
    return nominals[attr];
  }
  
  public int size() {
    return data.size();
  }
  
  public void setValue(int entry, int attr, Object val) {
    data.get(entry)[attr] = val;
  }
  
  public void add(DataEntry entry) {
    data.add(entry.data);
  }
  
  public void add(Object[] r) {
    
    int l = r.length;
    if (l != numAttributes) {
      throw new IllegalArgumentException("Incompatible spaces: expected "+numAttributes+" found "+l);
    }
    Object[] entry = new Object[l];
    for (int i = 0; i < l; ++i) {
      entry[i] = r[i];
    }
    data.add(entry);
  }
  
  public void set(int index, Object[] r) {
    
    int l = r.length;
    for (int i = 0; i < l; ++i) {
      data.get(index)[i] = r[i];
    }
  }
  
  public void clear() {
    data.clear();
  }
  
  public void remove(int index) {
    data.remove(index);
  }
  
  public Object[] get(int index) {
    return data.get(index);
  }
  
  public DataEntry getEntry(int index) {
    return new DataEntry(data.get(index), names, types);
  }
  
  public int[] getTypes() {
    return types;
  }
  
  public String[] getNames() {
    return names;
  }
  
  @Override
  public String toString() {
    String out = "";
    
    out += "** NAME **";
    out += "\n"+name;
    out += "\n** # ATTRIBUTES **";
    out += "\n"+numAttributes;
    out += "\n** INSTANCES **";
    out += "\n"+data.size();
    
    out += "\n\n** ATTRIBUTES **";
    
    for (int i = 0; i < numAttributes; ++i) {
      out += "\n"+names[i]+" "+types[i];
      if (types[i] == DataSet.NOMINAL || types[i] == DataSet.CLASS) {
        for (int j = 0; j < nominals[i].size(); ++j) {
          out += "\n   -> "+nominals[i].get(j);
        }
      }
    }
    out += "\n\n** DATA **";
    int sz = data.size();
    for (int i = 0; i < sz; ++i) {
      Object[] r = data.get(i);
      String s = "";
      for (int j = 0; j < numAttributes; ++j) {
        s += r[j]+",";
      }
      out += "\n"+s;
    }
    return out;
  }
}
