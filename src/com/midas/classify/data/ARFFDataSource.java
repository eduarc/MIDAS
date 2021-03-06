/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.midas.classify.data;

import com.midas.classify.util.ARFFStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author eduarc
 */
public class ARFFDataSource extends DataSource {
  
  public static final String COMMENT_SEQ = "%";
  public static final String RELATION_TAG = "@RELATION";
  public static final String ATTRIBUTE_TAG = "@ATTRIBUTE";
  public static final String DATA_TAG = "@DATA";
  
  public static final String NUMERIC_DTYPE = "NUMERIC";
  public static final String REAL_DTYPE = "REAL";
  public static final String INTEGER_DTYPE = "INTEGER";
  public static final String STRING_DTYPE = "STRING";
  public static final String NOMINAL_DTYPE = "}";

  private String relationName = "";
  private ArrayList<String> attributeName;
  private ArrayList<Integer> attributeType;
  public ArrayList<ArrayList<String>> nominalValues;
  private Instances dataSet;
  private int currentLine;
  
  public ARFFDataSource() {
    
  }
  
  public ARFFDataSource(Instances s) {
    dataSet = new Instances(s, false);
  }
  
  public Instances getDataset() {
    return dataSet;
  }
  
  public Instances read(boolean readRelationName, InputStream stream) throws IOException {
    
    currentLine = 0;
    relationName = "";
    attributeName = new ArrayList();
    attributeType = new ArrayList();
    nominalValues = new ArrayList();
    
    BufferedReader reader = new BufferedReader(new InputStreamReader(stream));

    if (readRelationName) {
      relationName = readRelationName(reader);
    }
    String nextTag = readAttributes(reader);
    
    int n = attributeName.size();
    List<Attribute> atts = new ArrayList();
    for (int i = 0; i < n; ++i) {
      Attribute att = null;
      int t = attributeType.get(i);
      if (t == Attribute.NUMERIC) {
        att = new Attribute(attributeName.get(i), i);
      }
      else if (t == Attribute.NOMINAL) {
        att = new Attribute(attributeName.get(i), nominalValues.get(i), i);
      }
      else if (t == Attribute.STRING) {
        att = new Attribute(attributeName.get(i), (List)null, i);
      }
      else if (t == Attribute.DATE) {
        att = new Attribute(attributeName.get(i), (String)null, i);
      }
      atts.add(att);
    }
    dataSet = new Instances(relationName, atts);
    
    readData(reader, nextTag);
    return dataSet;
  }
  
  @Override
  public Instances read(InputStream stream) throws IOException {
    return read(true, stream);
  }

  private String readRelationName(BufferedReader reader) throws IOException {
    
    String line;
    while ((line = reader.readLine()) != null) {
      ++currentLine;
      
      line = line.trim();
      if (line.length() == 0 || line.startsWith(COMMENT_SEQ)) {
        continue;
      }
      line = normalice(line);
      
      String upper = line.toUpperCase();
      if (upper.startsWith(RELATION_TAG)) {
        line = line.substring(RELATION_TAG.length());
        return parse(line,1).get(0);
      } else {
        throw new IOException("ARFFStream: @RELATION not found");
      }
    }
    return null;
  }
  
  private String readAttributes(BufferedReader reader) throws IOException {
    
    String line,upper;
    
    while ((line = reader.readLine()) != null) {
      ++currentLine;
      
      line = normalice(line);
      line = line.trim();
      if (line.length() == 0 || line.startsWith(COMMENT_SEQ)) {
        continue;
      }
      upper = line.toUpperCase();
      
      if (upper.startsWith(ATTRIBUTE_TAG)) {
        line = line.substring(ATTRIBUTE_TAG.length());
        upper = line.toUpperCase();
        
          // -----------------------
          // ATTRIBUTE, NOMINAL LIST
        if (upper.endsWith(NOMINAL_DTYPE)) {
          String nominalList = null;
          for (int i = line.length()-2; ; --i) {
            if (line.charAt(i) == '{') {
              nominalList = line.substring(i+1,line.length()-1);
              line = line.substring(0,i);
              break;
            }
          }
            // error
          if (nominalList == null) {
            throw new IOException("ARFFStream: Invalid nominal list. line "+currentLine);
          }
          attributeName.add(parse(line,1).get(0));
          attributeType.add(Attribute.NOMINAL);
          nominalValues.add(parse(nominalList,Integer.MAX_VALUE));
        }
        else if (upper.endsWith(" "+NUMERIC_DTYPE)
                  || upper.endsWith(" "+REAL_DTYPE)
                    || upper.endsWith(" "+INTEGER_DTYPE)
                      || upper.endsWith(" "+STRING_DTYPE)) {
          String type = "";
          
          for (int i = upper.length()-1; i >= 0; --i) {
            if (upper.charAt(i) == ' ') {
              type = upper.substring(i+1);
              line = line.substring(0,i);
              break;
            }
          }
          String name = parse(line,1).get(0);
          attributeName.add(name);
          nominalValues.add(null);
          
          switch (type) {
            case NUMERIC_DTYPE:
              attributeType.add(Attribute.NUMERIC);
              break;
            case REAL_DTYPE:
              attributeType.add(Attribute.NUMERIC);
              break;
            case INTEGER_DTYPE:
              attributeType.add(Attribute.NUMERIC);
              break;
            case STRING_DTYPE:
              attributeType.add(Attribute.STRING);
              break;
          }
        }
      }
        // -----------------------
        // ended list of attributes
      else break;
    }
    if (attributeName.isEmpty())
      throw new IOException("ARFFStream: @ATTRIBUTE not found. line "+currentLine);
    return line;
  }
  
  public void readData(BufferedReader reader, String header) throws IOException {
      // check header
    if (!header.toUpperCase().endsWith(ARFFStream.DATA_TAG)) {
      throw new IOException("ARFFStream: @DATA tag not found. line "+currentLine);
    }
    List<Attribute> attrs = dataSet.attributes;
    int n = attrs.size();
    String line;
    
    while ((line = reader.readLine()) != null) {
      ++currentLine;
      
      line = line.trim();
      if (line.length() == 0 || line.startsWith(COMMENT_SEQ)) {
        continue;
      }
      line = normalice(line);
      ArrayList<String> data = parse(line, n);
      if (data.size() < n) {
        throw new IOException("ARFFStream: Missing tokens. line "+currentLine);
      }
      Instance ins = new Instance(1, internalForm(attrs, data));
      ins.setDataset(dataSet);
      dataSet.add(ins);
    }
  }
  
  public double[] internalForm(List<Attribute> attrs, List<String> data) throws IOException {
    
    int sz = data.size();
    double[] vals = new double[sz];
    
    for (int i = 0; i < sz; i++) {
      if (data.get(i).equals("?")) {
        vals[i] = Double.NaN;
        continue;
      }
      Attribute att = attrs.get(i);
      if (att.isNumeric()) {
        String s = data.get(i);
        try {
          vals[i] = Double.parseDouble(s);
        } catch (NumberFormatException e) {
          throw new IOException("ARFFStream: Invalid number for attribute "+att.name+": "+s);
        }
      }
      else if (att.isDate()) {
        vals[i] = att.parseDate(data.get(i));
      }
      else if (att.isNominal()) {
        String s = data.get(i);
        vals[i] = att.indexOfValue(s);
        if (vals[i] == -1) {
          throw new IOException("ARFFStream: Unknown value for nominal attribute "+att.name+": "+s);
        }
      }
      else if (att.isString()) {
        vals[i] = att.addStringValue(data.get(i));
      }
    }
    return vals;
  }
  
  public ArrayList<String> parse(String src, int maxTokens) throws IOException {
    
    ArrayList<String> list = new ArrayList();
    src += ",";
    int len = src.length();
    String curr = "";
    
    for (int i = 0; i < len; ++i) {
      char c = src.charAt(i);
      
        // skip leading spaces
      if (c == ' ') continue;
      
      if (list.size() == maxTokens) {
        throw new IOException("ARFFStream: Unexpected token. line "+currentLine+" col "+i+": "+src.charAt(i));
      }
        // add current token
      if (c == ',') {
        list.add(curr);
        curr = "";
        continue;
      }
        // -------------
        // Quoted string
      else if (c == '\'' || c == '\"') {
        for (++i; i < len; ++i) {
          char v = src.charAt(i);
          if (v == c) {
            ++i;
            break;
          }
          if (v == '\\') ++i;
          if (i == len) {
            throw new IOException("ARFFStream: Invalid scape sequence. line "+currentLine);
          }
          curr += src.charAt(i);
        }
        if (i == len) {
          throw new IOException("ARFFStream: Missing "+c+ " character. line "+currentLine);
        }
      }
        // ----------------
        // No spaced string
      else if (c != ' ') {
        for (; i < len; ++i) {
          char v = src.charAt(i);
          if (v == ' ' || v == ',') break;
          if (c == '\\') ++i;
          if (i == len) {
            throw new IOException("ARFFStream: Invalid scape sequence. line "+currentLine);
          }
          curr += src.charAt(i);
        }
      }
        // skip trailing spaces
      while (i < len) {
        char v = src.charAt(i);
        if (v == ' ') ++i;
        else if (v == ',') break;
        else {
          throw new IOException("ARFFStream: Unexpected token. line "+currentLine+" col: "+i+": "+src.charAt(i));
        }
      }
      --i;
    }
    return list;
  }
  
  private String normalice(String s) {
    return s.replace('\t',' ');
  }
  
  @Override
  public void write(OutputStream stream, Instances ds) {
    
  }
  
}
