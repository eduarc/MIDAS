/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.midas.classify.data;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author eduarc
 */
public class Attribute {
  /* Tipos de datos soportados */
  public static final int NUMERIC = 0;
  public static final int STRING  = 1;
  public static final int NOMINAL = 2;
  public static final int DATE    = 3;
  
  protected String name;
  protected int index;
  protected int type;
  protected double weight = 1;
  
  protected double lowerBound;
  protected double upperBound;
  
    /* Atributos Date */
  protected String dateFormat;
  
    /* Atributos Nominal */
    /* Atributos Nominal y String */
  private TreeMap<String, Integer> stringValues;
  
  public Attribute(String name) {
    
    this.name = name;
    type = NUMERIC;
  }
  
  public Attribute(String name, int index) {
    
    this(name);
    this.index = index;
    type = NUMERIC;
  }
  
  public Attribute(String name, List<String> nominals) {
    
    this.name = name;
    type = NOMINAL;
    if (nominals.isEmpty()) {
      type = STRING;
    }
    stringValues = new TreeMap();
    for (String v : nominals) {
      addStringValue(v);
    }
  }
  
  public Attribute(String name, List<String> nominals, int index) {
    
    this(name, nominals);
    this.index = index;
    stringValues = new TreeMap();
    for (String v : nominals) {
      addStringValue(v);
    }
  }
  
  public Attribute(String name, String dateFormat) {
    
    this.name = name;
    this.dateFormat = dateFormat;
    type = DATE;
  }
  
  public Attribute(String name, String dateFormat, int index) {
    
    this(name, dateFormat);
    this.index = index;
  }
  
  public Attribute(Attribute a) {
    
    name = a.name;
    index = a.index;
    type = a.type;
    weight = a.weight;
    
    lowerBound = a.lowerBound;
    upperBound = a.upperBound;
    
    dateFormat = a.dateFormat;
    if (a.stringValues != null) {
      stringValues = new TreeMap(a.stringValues);
    } else {
      stringValues = new TreeMap();
    }
  }
  
  /* Setters */
  
  public void setWeight(int weight) {
    this.weight = weight;
  }
  
  /* Getters */
  public String name() {
    return name;
  }
  
  public int index() {
    return index;
  }
  
  public int type() {
    return type;
  }
  
  public double weight() {
    return weight;
  }
  
  public int numValues() {
    
    if (isNumeric() || isDate()) {
      return 0;
    }
    return stringValues.size();
  }
  
  public Iterable<Integer> iterableValues() {
    
    if (isNumeric() || isDate()) {
      return null;
    }
    return stringValues.values();
  }
  
  public String getDateFormat() {
    return dateFormat;
  }
  
  public int indexOfValue(String value) {
    
    if (isNumeric() || isDate()) {
      return -1;
    }
    Integer i = stringValues.get(value);
    return i == null ? -1 : i;
  }
  
  public String value(int valIndex) {
    
    if (isNumeric() || isDate()) {
      return "";
    }
    for (String k : stringValues.keySet()) {
      if (stringValues.get(k) == valIndex) {
        return k;
      }
    }
    return "";
  }
  
  public List<String> values() {
    if (isNumeric() || isDate()) {
      return null;
    }
    return new ArrayList<>(stringValues.keySet());
  }
  
  public double getNumericLowerBound() {
    return lowerBound;
  }
  
  public double getNumericUpperBound() {
    return upperBound;
  }
  
  /* Checkers */
  
  public boolean isString() {
    return type == STRING;
  }
  
  public boolean isNominal() {
    return type == NOMINAL;
  }
  
  public boolean isNumeric() {
    return type == NUMERIC;
  }
  
  public boolean isDate() {
    return type == DATE;
  }
  
  public void setType(int type) {
      this.type = type;
  }
  
  public void setName(String name) {
      this.name = name;
  }
  
  @Override
  public boolean equals(Object o) {
    
    if (o instanceof Attribute) {
      Attribute a = (Attribute)o;
      return hashCode() == a.hashCode();
    }
    return false;
  }

  @Override
  public int hashCode() {
    
    int hash = 3;
    hash = 47 * hash + Objects.hashCode(this.name);
    hash = 47 * hash + this.index;
    hash = 47 * hash + this.type;
    hash = 47 * hash + (int) (Double.doubleToLongBits(this.weight) ^ (Double.doubleToLongBits(this.weight) >>> 32));
    hash = 47 * hash + Objects.hashCode(this.dateFormat);
    hash = 47 * hash + Objects.hashCode(this.stringValues);
    return hash;
  }
  
  public int setStringValue(String value) {
    
    stringValues.clear();
    stringValues.put(value, 0);
    return 0;
  }
  
  public int addStringValue(String value) {
    if (isNumeric()) {
      return -1;
    }
    Integer i = stringValues.get(value);
    if (i == null) {
      i = stringValues.size();
      stringValues.put(value, i);
    }
    return i;
  }
  
  public double parseDate(String date) {
    
    SimpleDateFormat f = new SimpleDateFormat(dateFormat);
    try {
      return (double)f.parse(date).getTime();
    } catch (ParseException ex) {
      throw new IllegalArgumentException("Illegal date format: "+date);
    }
  }
  
  public String formatDate(double date) {
    
    SimpleDateFormat f = new SimpleDateFormat(dateFormat);
    return f.format(new Date((long)date));
  }
  
  @Override
  public String toString() {
    String out = "";
    if (iterableValues() == null) {
      out += Attribute.type(type())+"\n";
    } else {
      out += "{";
      boolean first = true;
      for (int value : iterableValues()) {
        if (!first) {
          out += ",";
        }
        first = false;
        //stringBuilder.append(att.value(value));
        }
        //stringBuilder.append("}\n");
    }
    return "@ATTRIBUTE \'"+name+"\' ";
  }
  
    public static String type(int key) {
        if (key == 0) {
            return "NUMERIC";
        } else if (key == 1) {
            return "STRING";
        } else if (key == 2) {
            return "NOMINAL";
        } else if (key == 3) {
            return "DATE";
        } else {
            return "";
        }
    }
}
