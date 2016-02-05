/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.midas.classify.util;

/**
 *
 * @author eduarc (Eduar Castrillo)
 */
public class TreeNode {
  protected String name;

  public TreeNode(String name) {
    this.name = name;
  }
  
  public void setName(String name) {
    this.name = name;
  }
  
  @Override
  public String toString() {
    return name;
  }
}
