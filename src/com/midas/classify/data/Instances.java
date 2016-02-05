/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.midas.classify.data;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.TreeSet;

/**
 *
 * @author eduarc
 */
public class Instances {

    protected String name;
    protected List<Attribute> attributes;
    protected List<Instance> instances;
    protected int classIndex = -1;

     
    public Instances(String name) {

        this.name = name;
        instances = new ArrayList();
        attributes = new ArrayList();
    }

    public Instances(String name, List<Attribute> attInfos) {

        this.name = name;
        attributes = attInfos;
        instances = new ArrayList();
    }

    public Instances(Instances c) {
      
      name = c.name;
      classIndex = c.classIndex;
      attributes = new ArrayList(c.attributes.size());
      instances = new ArrayList(c.instances.size());
      
      for (Attribute a : c.attributes) {
        Attribute aa = new Attribute(a);
        attributes.add(aa);
      }
      for (Instance i : c.instances) {
        Instance ii = new Instance(i);
        ii.setDataset(this);
        instances.add(ii);
      }
    }
    
    public Instances(Instances c, boolean refInstances) {

        this.attributes = c.attributes;
        if (refInstances) {
            this.instances = c.instances;
        } else {
            this.instances = new ArrayList();
        }
        this.name = c.name;
        this.classIndex = c.classIndex;
    }

    public void add(Instance instance) {
        instances.add(instance);
    }

    public void add(int index, Instance instance) {
        instances.add(index, instance);
    }

    public Attribute attribute(int index) {
        return attributes.get(index);
    }

    public Attribute attribute(String name) {

        for (Attribute att : attributes) {
            if (name.equals(att.name)) {
                return att;
            }
        }
        return null;
    }

    public boolean checkForAttributeType(int attType) {

        for (Attribute att : attributes) {
            if (att.type == attType) {
                return true;
            }
        }
        return false;
    }

    public boolean checkForStringAttributes() {

        for (Attribute att : attributes) {
            if (att.type == Attribute.STRING) {
                return true;
            }
        }
        return false;
    }

    public Attribute classAttribute() {
        return attributes.get(classIndex);
    }

    public int classIndex() {
        return classIndex;
    }

    public void delete() {
        instances.clear();
    }

    public void deleteWithMissing(int attIndex) {

        for (int i = 0; i < instances.size(); i++) {
            Instance ins = instances.get(i);
            if (ins.isMissing(attIndex)) {
                instances.remove(i);
                i--;
            }
        }
    }

    public void deleteWithMissingClass() {
        deleteWithMissing(classIndex);
    }

    public Iterable<Attribute> iterableAttributes() {
        return attributes;
    }

    public Iterable<Instance> iterableInstances() {
        return instances;
    }

    public Instance firstInstance() {
        return instances.get(0);
    }

    public Instance get(int index) {
        return instances.get(index);
    }

    public Instance instance(int index) {
        return instances.get(index);
    }

    public double kthSmalletValue(int attIndex, int k) {

        PriorityQueue<Double> pq = new PriorityQueue();
        for (Instance i : instances) {
            pq.add(i.value(attIndex));
        }
        while (--k > 0) {
            pq.poll();
        }
        return pq.peek();
    }

    public Instance lastInstance() {
        return instances.get(instances.size() - 1);
    }

    public double meanOrMode(int attIndex) {

        Attribute att = attributes.get(attIndex);
        double ret = 0;

        if (att.isNumeric()) {
            for (Instance i : instances) {
                ret += i.value(attIndex);
            }
            ret /= instances.size();
        } else if (att.isNominal()) {
            int[] freq = new int[att.numValues()];

            for (Instance i : instances) {
                freq[(int) i.value(attIndex)]++;
            }
            for (int i = 0; i < freq.length; i++) {
                if (ret < freq[i]) {
                    ret = freq[i];
                }
            }
        }
        return ret;
    }

    public int numAttributes() {
        return attributes.size();
    }

    public int numClasses() {
        return attributes.get(classIndex).numValues();
    }

    public int numDistincsValues(int attIndex) {

        Attribute att = attributes.get(attIndex);

        if (att.isString()) {
            return instances.size();
        }
        TreeSet<Double> s = new TreeSet();
        for (Instance i : instances) {
            s.add(i.value(attIndex));
        }
        return s.size();
    }

    public int getNumNumericAttributes() {
        int answer = 0;
        for (int i = 0; i < numAttributes(); i++) {
            if (attribute(i).type == Attribute.NUMERIC) {
                answer++;
            }
        }
        return answer;
    }

    public int getNumNominalAttributes() {
        int answer = 0;
        for (int i = 0; i < numAttributes(); i++) {
            if (attribute(i).type == Attribute.NOMINAL) {
                answer++;
            }
        }
        return answer;
    }

    public List<Attribute> getAttributes() {
        return attributes;
    }
    
    public List<Attribute> getNumericAttributes() {
        List<Attribute> atts = new ArrayList<>();
        for (int i = 0; i < numAttributes(); i++) {
            if (attribute(i).type == Attribute.NUMERIC) {
                atts.add(attribute(i));
            }
        }
        return atts;
    }

    public List<Attribute> getNominalAttributes() {
        List<Attribute> atts = new ArrayList<>();
        for (int i = 0; i < numAttributes(); i++) {
            if (attribute(i).type == Attribute.NOMINAL) {
                atts.add(attribute(i));
            }
        }
        return atts;
    }
        
    public ArrayList<double[]> getElements() {
        ArrayList<double[]> e = new ArrayList<>();
        ArrayList<Integer> att = new ArrayList<>();
        for (int i = 0; i < numAttributes(); i++) {
            if (getAttributes().get(i).type() == 0) {
                att.add(i);
            }
        }
        for (int i = 0; i < numInstances(); i++) {
            Instance I = instances.get(i);
            double[] aux = new double[att.size()];
            for (int j = 0; j < att.size(); j++) {
                aux[j] = I.value(att.get(j));
            }
            e.add(aux);
        }
        return e;
    }

    public int numInstances() {
        return instances.size();
    }

    public String relationName() {
        return name;
    }

    public Instance remove(int index) {
        return instances.remove(index);
    }

    public void set(int index, Instance instance) {
        instances.set(index, instance);
    }

    public void setClassIndex(int index) {
        classIndex = index;
    }

    public void setRelationName(String name) {
        this.name = name;
    }

    public int size() {
        return instances.size();
    }

    public void setAttributes(List<Attribute> other) {
        attributes = other;
    }

    public double variance(int attIndex) {

        double u = meanOrMode(attIndex);
        double s = 0;

        for (Instance i : instances) {
            double x = i.value(attIndex);
            s += (x - u) * (x - u);
        }
        return s / (double) instances.size();
    }

    public void swap(int i, int j) {

        Instance tmp = instances.get(i);
        instances.set(i, instances.get(j));
        instances.set(j, tmp);
    }

    public List<Instance> getInstancesForValue(Attribute att, double value) {
        List<Instance> answer = new ArrayList<>();
        int attId = 0;
        for (Attribute attribute : attributes) {
            if (attribute.name().equals(att.name())) {
                break;
            }
            attId++;
        }
        for (Instance ins : instances) {
            if (ins.value(attId) == value) {
                answer.add(ins);
            }
        }
        return answer;
    }
    
    public boolean HaveClass(){
        for (int i = 0; i < numAttributes(); i++) {
            if (getAttributes().get(i).type() == Attribute.NOMINAL && getAttributes().get(i).name().equals("class")) {
                return true;
            }
        }
        return false;
    }
    
    public  ArrayList<String> getClassNames(){
        for (int i = 0; i < numAttributes(); i++) {
            if (getAttributes().get(i).type() == Attribute.NOMINAL && getAttributes().get(i).name().equals("class")) {
                return (ArrayList<String>) getAttributes().get(i).values();
            }
        } 
        return null;
    }
    
     public ArrayList<Integer> getClassesID() {
        ArrayList<Integer> c = new ArrayList<>();
        int id = -1;
        for (int i = 0; i < numAttributes(); i++) {
            if (getAttributes().get(i).type() == Attribute.NOMINAL
                    && getAttributes().get(i).name().equals("class")) {
                id = i;
                break;
            }
        }
        if (id == -1) {
            for(int i = 0; i < numInstances(); i++)
                c.add(0);
        } else {
            for (int i = 0; i < numInstances(); i++) {
                c.add((int) (get(i).value(id)));
            }
        }
        return c;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("@RELATION ").append(name).append("\n");
        for (Attribute att : attributes) {
            stringBuilder.append("@ATTRIBUTE ").append(att.name()).append("\t");
            if (att.iterableValues() == null) {
                stringBuilder.append(Attribute.type(att.type())).append("\n");
            } else {
                stringBuilder.append("{");
                boolean first = true;
                for (int value : att.iterableValues()) {
                    if (!first) {
                        stringBuilder.append(",");
                    }
                    first = false;
                    stringBuilder.append(att.value(value));
                }
                stringBuilder.append("}\n");
            }
        }
        stringBuilder.append("\n@DATA\n");
        for (int index = 0; index < this.size(); index++) {
            Instance instance = this.instance(index);
            stringBuilder.append(instance.toString());
            /*boolean first = true;
            for (int i = 0; i < instance.numAttributes(); i++) {
                if (!first) {
                    stringBuilder.append(",");
                }
                first = false;
                if (attribute(i).type == Attribute.NOMINAL) {
                    stringBuilder.append(attribute(i).value((int) instance.value(i)));
                } else {
                    stringBuilder.append(instance.value(i));
                }
            }*/
            stringBuilder.append("\n");
        }
        return stringBuilder.toString();
    }
    
    public String summary() {
      
      String s = "Dataset: "+name+"\nAttributes: ("+numAttributes()+")\n";
      for (Attribute a : attributes) {
        s += "  - "+a.name+"\t";
        if (a.type == Attribute.NOMINAL) {
          s += "NOMINAL"; 
          if (a.index == classIndex) {
            s += " (Class)";
          }
        }
        if (a.type == Attribute.NUMERIC) {
          s += "NUMERIC";
        }
        if (a.type == Attribute.STRING) {
          s += "STRING";
        }
        if (a.type == Attribute.DATE) {
          s += "DATE";
        }
        s += "\n";
      }
      s += "Instances: "+size()+"\n";
      s += "Missing Values: "+(hasMissingValue() ? "Yes" : "No")+"\n";
      return s;
    }
    
    public boolean hasMissingValue() {
      
      for (Instance i : instances) {
        for (double v : i.values) {
          if (Double.isNaN(v)) {
            return true;
          }
        }
      }
      return false;
    }
    
    public boolean noClassCompatible(Instances ins) {
      
      List<Attribute> attrs = ins.attributes;
      int n = numAttributes(), m = ins.numAttributes();
      
      for (int i = 0, j = 0; i < n && j < m;) {
        if (ins.classIndex == j) {
          ++j;
          continue;
        }
        if (classIndex == i) {
          ++i;
          continue;
        }
        Attribute a = attributes.get(i);
        Attribute b = attrs.get(j);
        if (a.type != b.type) {
          return false;
        }
        if (a.type == Attribute.NOMINAL) {
          List<String> la = a.values();
          List<String> lb = b.values();
          if (!la.equals(lb)) {
            return false;
          }
        }
        ++i;
        ++j;
      }
      return true;
    }
    
    public boolean compatible(Instances ins) {
      
      List<Attribute> attrs = ins.attributes;
      int n = ins.numAttributes();
      
      if (n != numAttributes()) {
        return false;
      }
      for (int i = 0; i < n; i++) {
        Attribute a = attributes.get(i);
        Attribute b = attrs.get(i);
        
        if (a.type != b.type) {
          return false;
        }
        if (a.type == Attribute.NOMINAL) {
          List<String> la = a.values();
          List<String> lb = b.values();
          if (!la.equals(lb)) {
            return false;
          }
        }
      }
      return true;
    }
}
