/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.midas.classify.algorithms.dtrees;

import com.midas.classify.algorithms.SupervisedClassifier;
import com.midas.classify.data.Instance;
import com.midas.classify.data.Instances;
import com.midas.classify.util.TreeEdge;
import com.midas.classify.util.TreeNode;
import edu.uci.ics.jung.graph.DelegateTree;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

/**
 *
 * @author eduarc
 */
public abstract class DTree extends SupervisedClassifier {

  private DelegateTree<TreeNode, TreeEdge> decisionTree;
  
  protected DecisionNode root;
  protected int[] attrFreq;
  protected boolean[] fixedAttribute;
  protected String modelSummary;
  
  public DTree(Instances trset) {
    super(trset);
    
    int n = trainingData.size();
    int t = trainingData.numAttributes();
    
    DecisionNode dummy = new DecisionNode(-1, 0, "dummyNode");
    
    List<Instance> initialSet = new ArrayList(n);
    for (Instance i : trainingData.iterableInstances()) {
      initialSet.add(i);
    }
    fixedAttribute = new boolean[t];
    fixedAttribute[classIndex] = true;
    attrFreq = new int[n];
    
    dummy.addEdge(-1, makeDecisionTree(initialSet));
      // obtener nodo raiz
    root = dummy.getNode(-1);
    
    makeDelegateTree();
    makeModelSummary();
  }
  
  private int[] getAttributeFrequency(List<Instance> set, int countedAttribute) {
    
    int n = trainingData.attribute(countedAttribute).numValues();
    int[] freq = new int[n];
    
    for (Instance i : set) {
      freq[(int)i.value(countedAttribute)]++;
    }
    return freq;
  }
  
  private List<Instance> getSubSet(List<Instance> set, int attribute, double val) {
    
    List<Instance> subset = new ArrayList();
    
    for (Instance i : set) {
      if (i.value(attribute) == val) {
        subset.add(i);
      }
    }
    return subset;
  }
  
  private List<Instance>[] split(List<Instance> set, int attribute) {
    
    int n = trainingData.attribute(attribute).numValues();
    List<Instance>[] subsets = new ArrayList[n];
    
    for (Instance i : set) {
      int v = (int)i.value(attribute);
        // Sval
      if (subsets[v] == null) {
        subsets[v] = new ArrayList();
      }
      subsets[v].add(i);
    }
    return subsets;
  }
  
  private List<Instance>[] numericSplit(List<Instance> set, int attribute, int z) {
    
    List<Instance>[] subsets = new List[2];
    subsets[0] = new ArrayList();
    subsets[1] = new ArrayList();
    
    for (Instance i : set) {
      int s = 0;
      if (i.value(attribute) > z) {
        ++s;
      }
      subsets[s].add(i);
    }
    return subsets;
  }
  
  /* 
   * Calcula E(set, attribute)
   */
  private double getEntropy(List<Instance> set, int attribute) {
    
    double e = 0;
    double sz = set.size();
    int[] valFreq = getAttributeFrequency(set, attribute);
    double logBase = Math.log(2);
    
    for (int i = 0; i < valFreq.length; i++) {
      if (valFreq[i] == 0) {
        continue;
      }
      double p = valFreq[i]/sz;
      double logp = Math.log(p)/logBase;
      e += -p*logp;
    }
    return e;
  }
  
  private double getSplitInfo(List<Instance> set, int attribute) {
    return getEntropy(set, attribute);
  }
  
  /*
   * Calcula el valor intrinseco IV(set, attribute, z) para valores x <= z y x > z
   */
  private double getNumericSplitInfo(List<Instance> set, int attribute, int z) {
    
    int n = trainingData.attribute(attribute).numValues();
    double sz = set.size();
    double great, less = 0;
    double logBase = Math.log(2);
    
    for (Instance i : set) {
      if (i.value(attribute) <= z) ++less;
    }
    great = sz - less;
    less  = (less/sz)*(Math.log(less/sz)/logBase);
    great = (great/sz)*(Math.log(great/sz)/logBase);
    return -less-great;
  }
  
  /*
   * Retorna la ganancia de informacion para set, con respecto a attribute
   */
  private double getGain(List<Instance> set, int attribute) {
    
    double gain = 0;
    double entropy = getEntropy(set, classIndex);
    
    double sz = set.size();
    List<Instance>[] subsets = split(set, attribute);
    
    for (List<Instance> Sk : subsets) {
      if (Sk == null) {
        continue;
      }
      int isz = Sk.size();
      double pSk = isz/sz;
      double eSk = getEntropy(Sk, classIndex);
      gain += pSk*eSk;
    }
    return entropy-gain;
  }
  
  private double getNumericGain(List<Instance> set, int attribute, int z) {
    
    double gain = 0;
    double entropy = getEntropy(set, classIndex);
    
    double sz = set.size();
    List<Instance>[] subsets = numericSplit(set, attribute, z);
    
    for (List<Instance> Sk : subsets) {
      if (Sk == null) {
        continue;
      }
      int isz = Sk.size();
      double pSk = isz/sz;
      double eSk = getEntropy(Sk, classIndex);
      gain += pSk*eSk;
    }
    return entropy-gain;
  }
  
  public double getGainRatio(List<Instance> set, int attribute) {
    
    double gain = getGain(set, attribute);
    double splitInfo = getSplitInfo(set, attribute);
    return gain/splitInfo;
  }
  
  public double getNumericGainRatio(List<Instance> set, int attribute, int z) {
    
    double gain = getNumericGain(set, attribute, z);
    double splitInfo = getNumericSplitInfo(set, attribute, z);
    return gain/splitInfo;
  }
  
  protected abstract DecisionNode makeDecisionTree(List<Instance> set);
  protected abstract void makeDelegateTree();
  protected abstract void makeModelSummary();
  
  // ---------------------------------------- //
  // Nodo en el arbol de decision             //
  // Utilizado en el proceso de clasificacion //
  // ---------------------------------------- //
  protected class DecisionNode {
    
    int id;
    int value;
    String name;
    TreeMap<Integer,DecisionNode> edges;
    
    public DecisionNode(int value,int id, String name) {
      
      this.id = id;
      this.value = value;
      this.name = name;
      edges = new TreeMap();
    }
    
    public void addEdge(Integer w,DecisionNode v) {
      edges.put(w,v);
    }
    
    public boolean isLeaf() {
      return edges.isEmpty();
    }
    
    public int getId() {
      return id;
    }
    
    public int getValue() {
      return value;
    }
    
    public String getName() {
      return name;
    }
    
    public DecisionNode getNode(Integer edge) {
      return edges.get(edge);
    }
  }
}
