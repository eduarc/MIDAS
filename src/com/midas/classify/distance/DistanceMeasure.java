/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.midas.classify.distance;

/**
 *
 * @author Eduar Castrillo (eduarc)
 * @param <A>
 */
public abstract class DistanceMeasure<A> {
  
  public abstract double distance(A i, A j);
  public abstract String summary();
}
