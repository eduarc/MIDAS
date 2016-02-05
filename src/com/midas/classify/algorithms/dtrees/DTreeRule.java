/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.midas.classify.algorithms.dtrees;

import java.util.ArrayList;
import java.util.List;
import util.Pair;

/**
 *
 * @author eduarc (Eduar Castrillo)
 */
public class DTreeRule {
  
  /* Pareja attributo, valor */
  protected List<Pair<Integer, Double>> antecedents;
  /* Pareja Attributo */
  protected Pair<Integer, Double> consecuent;
  
  public DTreeRule() {
    antecedents = new ArrayList();
    consecuent = new Pair(-1, -1);
  }
  
  public DTreeRule(DTreeRule src) {
    antecedents = new ArrayList(src.antecedents);
    consecuent = new Pair(src.consecuent);
  }
  
  public void setConsecuent(Pair<Integer, Double> c) {
    consecuent = c;
  }
  
  public void addAntecedent(Pair<Integer, Double> a) {
    antecedents.add(a);
  }
  
  public void removeAntecedent(Pair<Integer, Double> a) {
    antecedents.remove(a);
  }
  
  public void removeAntecedent(int index) {
    antecedents.remove(index);
  }
  
  public Iterable<Pair<Integer, Double>> getAntecedents() {
    return antecedents;
  }
  
  public Pair<Integer, Double> getConsecuent() {
    return consecuent;
  }
}
