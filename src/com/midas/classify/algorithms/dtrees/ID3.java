/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.midas.classify.algorithms.dtrees;

import com.midas.classify.algorithms.SupervisedClassifier;
import com.midas.classify.data.Attribute;
import com.midas.classify.data.Instance;
import com.midas.classify.data.Instances;
import com.midas.classify.util.TreeEdge;
import com.midas.classify.util.TreeNode;
import edu.uci.ics.jung.graph.DelegateTree;
import edu.uci.ics.jung.graph.util.EdgeType;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.TreeMap;
import util.Pair;

/*
 * Clasificacion de vectores por medio del algoritmo de aprendizaje
 * supervisado ID3.
 * 
 * @author eduarc (Eduar Castrillo)
 */
public final class ID3 extends SupervisedClassifier {
  
  private DelegateTree<TreeNode, TreeEdge> decisionTree;
  
  private DecisionNode root;
  private boolean[] fixedAttribute;
  private List<DTreeRule> rules;
  private String modelSummary;
  
  public ID3(Instances trSet) {
    super(trSet);
    
    int n = trainingData.size();
    int t = trainingData.numAttributes();
    
    DecisionNode dummy = new DecisionNode(0, 0);
    
    List<Instance> initialSet = new ArrayList(n);
    for (Instance i : trainingData.iterableInstances()) {
      initialSet.add(i);
    }
    fixedAttribute = new boolean[t];
    fixedAttribute[classIndex] = true;
    
    rules = new ArrayList();
    
    dummy.addEdge(-1, buildModel(initialSet));
      // obtener nodo raiz
    root = dummy.getNode(-1);
    
    makeDelegateTree();
    makeModelSummary();
  }
  
  /*
   * Predice la clase del vector de entrada.
   */
  @Override
  public int classify(Instance ins) {
    
    int m = ins.numAttributes();
    for (int i = 0; i < m; i++) {
      if (i != classIndex && ins.isMissing(i)) {
        throw new IllegalArgumentException("Missing values not supported by ID3");
      }
    }
    
    DecisionNode node = root;
    while (!node.isLeaf()) {
      int att = node.attr;
      node = node.getNode((int)ins.value(att));
    }
    return (int)node.value;
  }
  
  public DelegateTree<TreeNode, TreeEdge> getDecisionTree() {
    return decisionTree;
  }
  
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
  
  private List<Instance> getSubSet(List<Instance> set, int attribute, double val) {
    
    List<Instance> subset = new ArrayList();
    
    for (Instance i : set) {
      if (i.value(attribute) == val) {
        subset.add(i);
      }
    }
    return subset;
  }
  
  /*
   * Cuenta la frecuencia de cada valor en countedAttribute en el conjunto set.
   */
  private int[] getAttributeFrequency(List<Instance> set, int countedAttribute) {
    
    int n = trainingData.attribute(countedAttribute).numValues();
    int[] freq = new int[n];
    
    for (Instance i : set) {
      freq[(int)i.value(countedAttribute)]++;
    }
    return freq;
  }
  
  /*
   * Divide en subconjuntos a set. Cada valor de attribute configura un subconjunto
   */
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
  
  /*
   * Crea el arbol de decision ID3
   */
  private DecisionNode buildModel(List<Instance> set) {
    
    double entropy = getEntropy(set, classIndex);
    
    if (entropy == 0) {
      double classy = set.get(0).value(classIndex);
      DecisionNode leaf = new DecisionNode(classIndex, classy);
      return leaf;
    }
    
    double maxGain = -1;
    int n = trainingData.numAttributes();
    int bestAttr = -1;
    
    for (int i = 0; i < n;++i) {
      if (!fixedAttribute[i]) {
        double g = getGain(set, i);
        if (maxGain < g) {
          maxGain = g;
          bestAttr = i;
        }
      }
    }
    
    if (bestAttr == -1) {
      int[] freqs = getAttributeFrequency(set, classIndex);
      int maxi = -1;
      double classy = 0;
      for (int i = 0; i < freqs.length; i++) {
        if (maxi < freqs[i]) {
          maxi = freqs[i];
          classy = i;
        }
      }
      return new DecisionNode(classIndex, classy);  // leaf
    }
    
    fixedAttribute[bestAttr] = true;
    DecisionNode best = new DecisionNode(bestAttr, 0);
    
    for (int v : trainingData.attribute(bestAttr).iterableValues()) {
      List<Instance> sub = getSubSet(set, bestAttr, v);
      if (!sub.isEmpty()) {
        best.addEdge(v, buildModel(sub));
      }
      else {
        int[] freqs = getAttributeFrequency(set, classIndex);
        int maxi = -1;
        double classy = 0;
        for (int i = 0; i < freqs.length; i++) {
          if (maxi < freqs[i]) {
            maxi = freqs[i];
            classy = i;
          }
        }
        best.addEdge(v, new DecisionNode(classIndex, classy));
      }
    }
    fixedAttribute[bestAttr] = false;
    return best;
  }

  private void makeDelegateTree() {
    
    int n = trainingData.numAttributes();
    Attribute[] atts = new Attribute[n];
    for (int i = 0; i < n; i++) {
      atts[i] = trainingData.attribute(i);
    }
    
    decisionTree = new DelegateTree();
    
    Queue<DecisionNode> Q  = new LinkedList();
    Queue<TreeNode>     Q1 = new LinkedList();
    
    Q.add(root);
    Q1.add(new TreeNode(atts[root.attr].name()));
    
    decisionTree.addVertex(Q1.peek());
    
    while (!Q.isEmpty()) {
      DecisionNode node = Q.poll();
      TreeNode tnode = Q1.poll();
      
      if (node.isLeaf()) {
        tnode.setName(atts[classIndex].value((int)node.value));
      } else {
        for (int e : node.edges.keySet()) {
          DecisionNode child = node.getNode(e);
          TreeNode tchild = new TreeNode(atts[child.attr].name());
          String edge_name = atts[node.attr].value(e);
          
          Q.add(child);
          Q1.add(tchild);
          
          decisionTree.addChild(new TreeEdge(edge_name), tnode, tchild, EdgeType.DIRECTED);
        }
      }
    }
  }
  
  private void makeModelSummary() {
    
    int n = trainingData.numAttributes();
    
    Attribute[] atts = new Attribute[n];
    for (int i = 0; i < n; i++) {
      atts[i] = trainingData.attribute(i);
    }
            
    String out = "";
    
    out += "Model: ID3\n\n";
    out += "*** Rules ***\n\n";
    
    treeWalk(root, new DTreeRule());
    
    for (DTreeRule r : rules) {
      int sz = r.antecedents.size();
      int ci = r.consecuent.first;
      double cv = r.consecuent.second;
      Attribute catt = atts[ci];
      
      if (sz == 0) {
        out += catt.name()+" = "+catt.value((int)cv);
      } else {
        Pair<Integer, Double> a = r.antecedents.get(0);
        Attribute aatt = atts[a.first];
        double av = a.second;
        
        out += "IF\n    "+aatt.name()+" = "+aatt.value((int)av);
        for (int i = 1; i < sz; i++) {
          a = r.antecedents.get(i);
          aatt = atts[a.first];
          av = a.second;
          out += "\n  "+aatt.name()+" = "+aatt.value((int)av);
        }
        out += "\nTHEN\n    "+catt.name()+" = "+catt.value((int)cv);
      }
      out += "\n\n";
    }
    modelSummary = out;
  }
  
  private void treeWalk(DecisionNode u, DTreeRule w) {
    
    if (u.isLeaf()) {
      w.consecuent.first  = u.attr;
      w.consecuent.second = u.value;
      rules.add(new DTreeRule(w));
      return ;
    }
    
    for (Entry<Integer, DecisionNode> k : u.edges.entrySet()) {
      DecisionNode v = k.getValue();
      double e = k.getKey();
      
      w.addAntecedent(new Pair(u.attr, e));
      treeWalk(v, w);
      w.removeAntecedent(w.antecedents.size()-1);
    }
  }
  
  @Override
  public String summary() {
    return modelSummary;
  }
  
  /*
   * Imprime el arbol e informacion varia
   */
  @Override
  public String toString() {
    return summary();
  }

  @Override
  public String briefSummary() {
    return "Model: ID3";
  }
  
  // ---------------------------------------- //
  // Nodo en el arbol de decision             //
  // Utilizado en el proceso de clasificacion //
  // ---------------------------------------- //
  private class DecisionNode {
    
    int attr;
    double value;
    TreeMap<Integer, DecisionNode> edges;
    
    public DecisionNode(int attr, double value) {
      
      this.attr = attr;
      this.value = value;
      edges = new TreeMap();
    }
    
    public void addEdge(Integer w, DecisionNode v) {
      edges.put(w, v);
    }
    
    public boolean isLeaf() {
      return edges.isEmpty();
    }
    
    public int getAttribute() {
      return attr;
    }
    
    public double getValue() {
      return value;
    }
    
    public DecisionNode getNode(Integer edge) {
      return edges.get(edge);
    }
  }
}
