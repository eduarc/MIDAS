/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.midas.classify.gui;

import java.awt.Dimension;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

/**
 *
 * @author eduarc (Eduar Castrillo)
 */
public class ModelsTree extends JTree {
  
  public static final String M_ID3 = "ID3";
  public static final String M_C45 = "C 4.5";
  public static final String M_KNN = "K Nearest Neighbors";
  public static final String M_NAIVE_BAYES = "Naive Bayes";
  
  public ModelsTree() {
    
    DefaultMutableTreeNode root = new DefaultMutableTreeNode("models");
    
    DefaultMutableTreeNode dTree = new DefaultMutableTreeNode("Decision Tree");
    DefaultMutableTreeNode bayes = new DefaultMutableTreeNode("Bayes");
    DefaultMutableTreeNode lazzy = new DefaultMutableTreeNode("Lazzy Classifiers");
    
    DefaultMutableTreeNode id3 = new DefaultMutableTreeNode(M_ID3);
    DefaultMutableTreeNode c45 = new DefaultMutableTreeNode(M_C45);
    
    DefaultMutableTreeNode naiveBayes = new DefaultMutableTreeNode(M_NAIVE_BAYES);
    
    DefaultMutableTreeNode knn = new DefaultMutableTreeNode(M_KNN);
    
    root.add(dTree);
    root.add(bayes);
    root.add(lazzy);
    
    dTree.add(id3);
    dTree.add(c45);
    
    bayes.add(naiveBayes);
    
    lazzy.add(knn);
    
    DefaultTreeModel model = new DefaultTreeModel(root);
    setModel(model);
  }
  
  @Override
  public Dimension getPreferredSize() {
    return new Dimension(400, 50);
  }
}
