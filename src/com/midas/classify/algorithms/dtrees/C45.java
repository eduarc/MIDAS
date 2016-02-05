/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
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
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 *
 * @author eduarc (Eduar Castrillo)
 */
public class C45 extends SupervisedClassifier {
  
  private DelegateTree<TreeNode, TreeEdge> decisionTree;
  
  private DecisionNode root;
  private boolean[] fixedAttribute;
  private double[] gainFactor;
  private List<C45Rule> rules;
  private String modelSummary;
  
  public C45(Instances trSet) {
    super(trSet);
    
    int n = trainingData.size();
    int t = trainingData.numAttributes();
    
    DecisionNode dummy = new DecisionNode(0, 0, 0, 0);
    
    gainFactor = new double[t];
    
    List<Instance> initialSet = new ArrayList(n);
    
    for (Instance i : trainingData.iterableInstances()) {
      for (int j = 0; j < t; j++) {
        if (i.isMissing(j)) {
          gainFactor[j]++;
        }
      }
      i.setWeight(1);
      initialSet.add(i);
    }
    for (int j = 0; j < t; j++) {
      gainFactor[j] = n-gainFactor[j];
      gainFactor[j] /= n;
    }
    
    fixedAttribute = new boolean[t];
    fixedAttribute[classIndex] = true;
    
    rules = new ArrayList();
    
    dummy.addEdge(-1, buildModel(initialSet, 0));
      // obtener nodo raiz
    root = dummy.getNode(-1);
    
    makeDelegateTree();
    makeModelSummary();
    treeWalk(root, new C45Rule());
  }

  @Override
  public int classify(Instance sample) {
    
    double bestP = -1, classy = -1;
    Queue<DecisionNode> Q = new LinkedList();
    Q.add(root);
    
    while (!Q.isEmpty()) {
      DecisionNode node = Q.poll();
      
      if (node.isLeaf()) {
        if (bestP < node.S) {
          bestP = node.S;
          classy = node.value;
        }
        continue;
      }
      int attr = node.getAttribute();
      
      if (sample.isMissing(attr)) {
        if (trainingData.attribute(attr).isNumeric()) {
          Q.add(node.getNode(0));
          Q.add(node.getNode(1));
        } else {
          int n = trainingData.attribute(attr).numValues();
          for (int i = 0; i < n; i++) {
            Q.add(node.getNode(i));
          }
        }
      } else {
        double v = sample.value(attr);
        if (trainingData.attribute(attr).isNumeric()) {
          double z = node.getValue();
          Q.add(node.getNode(v <= z ? 0 : 1));
        } else {
          Q.add(node.getNode((int)v));
        }
      }
    }
    return (int)classy;
  }
  
  public DelegateTree<TreeNode, TreeEdge> getDecisionTree() {
    return decisionTree;
  }
  
  private int[] getAttributeFrequency(List<Instance> set, int countedAttribute) {
    
    int n = trainingData.attribute(countedAttribute).numValues();
    int[] freq = new int[n];
    
    for (Instance i : set) {
      if (!i.isMissing(countedAttribute)) {
        freq[(int)i.value(countedAttribute)]++;
      }
    }
    return freq;
  }
  
  private List<Instance> getSubSet(List<Instance> set, int attribute, double val) {
    
    List<Instance> subset = new ArrayList();
    
    for (Instance i : set) {
      if (i.isMissing(attribute) || i.value(attribute) == val) {
        subset.add(i);
      }
    }
    return subset;
  }
  
  private List<Instance>[] split(List<Instance> set, int attribute) {
    
    int n = trainingData.attribute(attribute).numValues();
    List<Instance>[] subsets = new ArrayList[n];
    
    for (int i = 0; i < n; i++) {
      subsets[i] = new ArrayList();
    }
    for (Instance i : set) {
      if (i.isMissing(attribute)) {
        for (int j = 0; j < n; j++) {
          subsets[j].add(i);
        }
      } else {
        int v = (int)i.value(attribute);
        subsets[v].add(i);
      }
    }
    return subsets;
  }
  
  private List<Instance>[] numericSplit(List<Instance> set, int attribute, double z) {
    
    List<Instance>[] subsets = new List[2];
    subsets[0] = new ArrayList();
    subsets[1] = new ArrayList();
    
    for (Instance i : set) {
      if (i.isMissing(attribute)) {
        subsets[0].add(i);
        subsets[1].add(i);
      } else {
        int s = 0;
        if (i.value(attribute) > z) {
          ++s;
        }
        subsets[s].add(i);
      }
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
  private double getNumericSplitInfo(List<Instance> set, int attribute, double z) {
    
    int n = trainingData.attribute(attribute).numValues();
    double sz = set.size();
    double great, less = 0;
    double logBase = Math.log(2);
    int missing = 0;
    
    for (Instance i : set) {
      if (i.isMissing(attribute)) {
        missing++;
      }
      else if (i.value(attribute) <= z) {
        ++less;
      }
    }
    great = sz-missing-less;
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
      if (Sk.isEmpty()) {
        continue;
      }
      int missing = 0;
      for (Instance i : Sk) {
        if (i.isMissing(attribute)) {
          missing++;
        }
      }
      int isz = Sk.size()-missing;
      double pSk = isz/sz;
      double eSk = getEntropy(Sk, classIndex);
      gain += pSk*eSk;
    }
    return entropy-gain;
  }
  
  private double getNumericGain(List<Instance> set, int attribute, double z) {
    
    double gain = 0;
    double entropy = getEntropy(set, classIndex);
    
    double sz = set.size();
    List<Instance>[] subsets = numericSplit(set, attribute, z);
    
    for (List<Instance> Sk : subsets) {
      if (Sk.isEmpty()) {
        continue;
      }
      int missing = 0;
      for (Instance i : Sk) {
        if (i.isMissing(attribute)) {
          missing++;
        }
      }
      int isz = Sk.size()-missing;
      double pSk = isz/sz;
      double eSk = getEntropy(Sk, classIndex);
      gain += pSk*eSk;
    }
    return entropy-gain;
  }
  
  public double getGainRatio(List<Instance> set, int attribute) {
    
    double gain = gainFactor[attribute]*getGain(set, attribute);
    double splitInfo = getSplitInfo(set, attribute);
    return gain/splitInfo;
  }
  
  public double getNumericGainRatio(List<Instance> set, int attribute, double z) {
    
    double gain = gainFactor[attribute]*getNumericGain(set, attribute, z);
    double splitInfo = getNumericSplitInfo(set, attribute, z);
    return gain/splitInfo;
  }
  
  private DecisionNode buildModel(List<Instance> set, double S) {
    
    double entropy = getEntropy(set, classIndex);
    if (entropy == 0) {
      double classy = set.get(0).value(classIndex);
      DecisionNode leaf = new DecisionNode(classIndex, classy, S, 0);
      return leaf;
    }
    
    double maxGain = -1;
    int n = trainingData.numAttributes();
    int bestAttr = -1;
    double z = -1;
    
    for (int i = 0; i < n;++i) {
      if (!fixedAttribute[i]) {
        Attribute attr = trainingData.attribute(i);
          // -----------------
          // Obtener Threshold
        if (attr.isNumeric()) {
          TreeSet<Double> pos = new TreeSet();
          for (int j = 0; j < set.size(); j++) {
            Instance ins = set.get(j);
            if (ins.isMissing(i)) {
              continue;
            }
            pos.add(ins.value(i));
          }
            // no hay elementos
          if (pos.isEmpty()) {
            continue;
          }
            // solo un posible valor
          if (pos.size() == 1) {
            double first = pos.first();
            double g = getNumericGainRatio(set, i, first);
            if (maxGain < g) {
              maxGain = g;
              bestAttr = i;
              z = first;
            }
          } else {
            int k = 0;
            double[] holds = new double[pos.size()];
            Iterator<Double> it = pos.iterator();
            while (it.hasNext()) {
              holds[k++] = it.next();
            }
            for (int j = 0; j < holds.length-1; j++) {
              double g = getNumericGainRatio(set, i, holds[j]);
              if (maxGain < g) {
                maxGain = g;
                bestAttr = i;
                z = holds[j];
              }
            }
          }
        }
        else {
          double g = getGainRatio(set, i);
          if (maxGain < g) {
            maxGain = g;
            bestAttr = i;
          }
        }
      }
    }
    
    if (bestAttr == -1) {
      return getLeafByClassFrequency(set, S);
    }
    
    if (!trainingData.attribute(bestAttr).isNumeric()) {
      z = -1;
    }
    double missingInSet = 0;
    for (Instance i : set) {
      if (i.isMissing(bestAttr)) {
        missingInSet++;
      }
    }
    
    fixedAttribute[bestAttr] = true;
    DecisionNode best = new DecisionNode(bestAttr, z, S, 0);
    
    if (trainingData.attribute(bestAttr).isNumeric()) {
      List<Instance>[] subs = numericSplit(set, bestAttr, z);
      for (int i = 0; i < 2; i++) {
        addEdge(best, i, bestAttr, set.size()-missingInSet, set, subs[i]);
      }
    } else {
      for (int v : trainingData.attribute(bestAttr).iterableValues()) {
        List<Instance> sub = getSubSet(set, bestAttr, v);
        addEdge(best, v, bestAttr, set.size()-missingInSet, set, sub);
      }
    }
    fixedAttribute[bestAttr] = false;
    return best;
  }
  
  private void addEdge(DecisionNode node, int v, int attribute, double setSize,
                       List<Instance> set, List<Instance> subset) {
    int missing = 0;
    for (Instance i : subset) {
      if (i.isMissing(attribute)) {
        missing++;
      }
    }
    if (subset.size()-missing > 0) {
      double S = subset.size()-missing;
      double PSi = S/setSize;
      // modificar peso
      for (Instance i : subset) {
        double w = i.weight();
        if (i.isMissing(attribute)) {
          i.setWeight(w*PSi);
          S += w*PSi;
        }
      }
      node.addEdge(v, buildModel(subset, S));
        // restaurar peso
      for (Instance i : subset) {
        if (i.isMissing(attribute)) {
          i.setWeight(i.weight()/PSi);
        }
      }
    } else {
      node.addEdge(v, getLeafByClassFrequency(set, 0));
    }
  }
  
  private DecisionNode getLeafByClassFrequency(List<Instance> set, double S) {
    
    int[] freqs = getAttributeFrequency(set, classIndex);
    int maxi = -1;
    double classy = 0;
    for (int i = 0; i < freqs.length; i++) {
      if (maxi < freqs[i]) {
        maxi = freqs[i];
        classy = i;
      }
    }
    return new DecisionNode(classIndex, classy, S, set.size()-maxi);
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
        tnode.setName(atts[classIndex].value((int)node.value)+" ("+node.S+"/"+node.E+")");
      } else {
        for (int e : node.edges.keySet()) {
          DecisionNode child = node.getNode(e);
          TreeNode tchild = new TreeNode(atts[child.attr].name());
          String edge_name = atts[node.attr].value(e);
          if (atts[node.attr].isNumeric()) {
            edge_name = (e == 0 ? "<= ":"> ")+node.value;
          }
          Q.add(child);
          Q1.add(tchild);
          
          decisionTree.addChild(new TreeEdge(edge_name), tnode, tchild, EdgeType.DIRECTED);
        }
      }
    }
  }
  
  private void makeModelSummary() {
    modelSummary = toString();
  }
  
  private void treeWalk(DecisionNode u, C45Rule w) {
    
    if (u.isLeaf()) {
      w.consecuent.attribute = u.attr;
      w.consecuent.classy = (int)u.value;
      w.consecuent.S = u.S;
      w.consecuent.E = u.E;
      rules.add(new C45Rule(w));
      return ;
    }
    
    for (Entry<Integer, DecisionNode> k : u.edges.entrySet()) {
      DecisionNode v = k.getValue();
      double e = k.getKey();
      
      if (trainingData.attribute(u.attr).isNumeric()) {
        w.addAntecedent(new RuleAntecedent(u.attr, u.value, e));
      } else {
        w.addAntecedent(new RuleAntecedent(u.attr, e, -1));
      }
      treeWalk(v, w);
      w.removeAntecedent(w.antecedents.size()-1);
    }
  }

  @Override
  public String summary() {
    
    String out = "";
    out += "Model: C 4.5\n\n";
    out += "*** Rules ***\n\n";
    for (C45Rule r : rules) {
      out += r+"\n";
    }
    return out;
  }
  
  @Override
  public String briefSummary() {
    return "Model: C 4.5";
  }
  
  // ---------------------------------------- //
  // Nodo en el arbol de decision             //
  // Utilizado en el proceso de clasificacion //
  // ---------------------------------------- //
  private class DecisionNode {
    
    int attr;
    double value;
    double S;
    double E;
    
    TreeMap<Integer,DecisionNode> edges;
    
    public DecisionNode(int attr, double value, double S, double E) {
      
      this.attr = attr;
      this.value = value;
      this.S = S;
      this.E = E;
      edges = new TreeMap();
    }
    
    public void addEdge(Integer w, DecisionNode v) {
      edges.put(w, v);
    }
    
    public boolean isLeaf() {
      return edges.isEmpty();
    }
    
    public double getS() {
      return S;
    }
    
    public double getE() {
      return E;
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
  
    // ----- //
    // RULES //
    // ----- //
  public class C45Rule {
    List<RuleAntecedent> antecedents;
    RuleConsecuent consecuent;

    public C45Rule() {
      
      antecedents = new ArrayList();
      consecuent = new RuleConsecuent(0, 0, 0, 0);
    }

    public C45Rule(C45Rule src) {
      
      antecedents = new ArrayList(src.antecedents);
      consecuent = new RuleConsecuent(src.consecuent);
    }

    public void setConsecuent(RuleConsecuent c) {
      consecuent = c;
    }

    public void addAntecedent(RuleAntecedent a) {
      antecedents.add(a);
    }

    public RuleAntecedent removeAntecedent(int i) {
      return antecedents.remove(i);
    }

    public RuleAntecedent getAntecedent(int i) {
      return antecedents.get(i);
    }
    
    @Override
    public String toString() {
      
      int nAttributes = trainingData.numAttributes();
      Attribute classAttribute = trainingData.classAttribute();
      
      Attribute[] atts = new Attribute[nAttributes];
      for (int i = 0; i < nAttributes; i++) {
        atts[i] = trainingData.attribute(i);
      }
      
      String out = "";
      
      if (antecedents.isEmpty()) {
        out += classAttribute.name()+" = "+classAttribute.value(consecuent.classy);
      } else {
        out += "IF\n";
        for (RuleAntecedent a : antecedents) {
          out += "    "+atts[a.attribute].name();
          if (atts[a.attribute].isNumeric()) {
            if (a.comparation == 0) {
              out += " <= ";
            } else {
              out += " > ";
            }
            out += a.value+"\n";
          } else {
            out += " = "+atts[a.attribute].value((int)a.value)+"\n";
          }
        }
        out += "THEN\n    "+classAttribute.name()+" = "+classAttribute.value(consecuent.classy);
        out += " ("+consecuent.S+"/"+consecuent.E+")\n";
      }
      return out;
    }
  }

    // ANTECEDENT
  public class RuleAntecedent {
    public int attribute;
    public double value;
    public double comparation;

    public RuleAntecedent(int attr, double value, double comp) {
      
      this.attribute = attr;
      this.value = value;
      this.comparation = comp;
    }

    public RuleAntecedent(RuleAntecedent r) {
      
      this.attribute = r.attribute;
      this.value = r.value;
      this.comparation = r.comparation;
    }

    @Override
    public boolean equals(Object o) {
      
      if (o instanceof RuleAntecedent) {
        RuleAntecedent a = (RuleAntecedent)o;
        return attribute == a.attribute && value == a.value && comparation == a.comparation;
      }
      return false;
    }

    @Override
    public int hashCode() {
      int hash = 7;
      hash = 47 * hash + this.attribute;
      hash = 47 * hash + (int) (Double.doubleToLongBits(this.value) ^ (Double.doubleToLongBits(this.value) >>> 32));
      hash = 47 * hash + (int) (Double.doubleToLongBits(this.comparation) ^ (Double.doubleToLongBits(this.comparation) >>> 32));
      return hash;
    }
  }

    // CONSECUENT
  public class RuleConsecuent {
    public int attribute;
    public int classy;
    public double S;
    public double E;

    public RuleConsecuent(int attr, int classy, double S, double E) {
      this.attribute = attr;
      this.classy = classy;
      this.S = S;
      this.E = E;
    }

    public RuleConsecuent(RuleConsecuent r) {
      this.attribute = r.attribute;
      this.classy = r.classy;
      this.S = r.S;
      this.E = r.E;
    }

    @Override
    public boolean equals(Object o) {
      if (o instanceof RuleConsecuent) {
        RuleConsecuent c = (RuleConsecuent)o;
        return attribute == c.attribute && classy == c.classy && S == c.S && E == c.E;
      }
      return false;
    }

    @Override
    public int hashCode() {
      int hash = 5;
      hash = 97 * hash + this.attribute;
      hash = 97 * hash + this.classy;
      hash = 97 * hash + (int) (Double.doubleToLongBits(this.S) ^ (Double.doubleToLongBits(this.S) >>> 32));
      hash = 97 * hash + (int) (Double.doubleToLongBits(this.E) ^ (Double.doubleToLongBits(this.E) >>> 32));
      return hash;
    }
  }

  @Override
  public String toString() {
    return summary();
  }
}
