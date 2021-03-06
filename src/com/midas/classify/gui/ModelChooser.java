/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.midas.classify.gui;

import java.awt.Component;
import javax.swing.JOptionPane;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

/**
 *
 * @author Eduar Castrillo (eduarc)
 */
public class ModelChooser extends javax.swing.JDialog {

  public static final int M_ID3 = 0;
  public static final int M_C45 = 1;
  public static final int M_NAIVE_BAYES = 2;
  public static final int M_KNN = 3;
  public static final int M_ADABOOST = 4;
  public static final int M_EADABOOST = 5;
  public static final int M_BAGGING = 6;
  public static final int M_EBAGGING = 7;
  
  public static final String[] MODEL_NAME = {"ID 3",
                                             "C 4.5",
                                             "Naive Bayes",
                                             "K-Nearest Neighbors",
                                             "AdaBoost",
                                             "Extended AdaBoost",
                                             "Bagging",
                                             "Extended Bagging"};
  
  /**
   * Creates new form ModelSelector
   * @param c
   */
  public ModelChooser() {
    
    setModalityType(ModalityType.APPLICATION_MODAL);
    setResizable(false);
    setUndecorated(true);
    
    initComponents();
    DefaultMutableTreeNode root = new DefaultMutableTreeNode("models");
    
    DefaultMutableTreeNode dTree = new DefaultMutableTreeNode("Decision Tree");
    DefaultMutableTreeNode bayes = new DefaultMutableTreeNode("Bayes");
    DefaultMutableTreeNode lazzy = new DefaultMutableTreeNode("Lazzy Classifier");
    DefaultMutableTreeNode meta = new DefaultMutableTreeNode("Meta Classifier");
    
    DefaultMutableTreeNode id3 = new DefaultMutableTreeNode(MODEL_NAME[M_ID3]);
    DefaultMutableTreeNode c45 = new DefaultMutableTreeNode(MODEL_NAME[M_C45]);
    
    DefaultMutableTreeNode naiveBayes = new DefaultMutableTreeNode(MODEL_NAME[M_NAIVE_BAYES]);
    
    DefaultMutableTreeNode knn = new DefaultMutableTreeNode(MODEL_NAME[M_KNN]);
    
    DefaultMutableTreeNode adaboost = new DefaultMutableTreeNode(MODEL_NAME[M_ADABOOST]);
    DefaultMutableTreeNode bagging = new DefaultMutableTreeNode(MODEL_NAME[M_BAGGING]);
    DefaultMutableTreeNode ebagging = new DefaultMutableTreeNode(MODEL_NAME[M_EBAGGING]);
    DefaultMutableTreeNode eadaboost = new DefaultMutableTreeNode(MODEL_NAME[M_EADABOOST]);
    
    root.add(dTree);
    root.add(bayes);
    root.add(lazzy);
    root.add(meta);
    
    dTree.add(id3);
    dTree.add(c45);
    
    bayes.add(naiveBayes);
    
    lazzy.add(knn);
    
    meta.add(adaboost);
    meta.add(eadaboost);
    meta.add(bagging);
    meta.add(ebagging);
    
    DefaultTreeModel model = new DefaultTreeModel(root);
    treeModels.setModel(model); 
  }
  
  /**
   * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The content of this method is always regenerated by the Form Editor.
   */
  @SuppressWarnings("unchecked")
  // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
  private void initComponents() {

    jScrollPane1 = new javax.swing.JScrollPane();
    treeModels = new javax.swing.JTree();
    bOK = new javax.swing.JButton();
    bCancel = new javax.swing.JButton();

    setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

    jScrollPane1.setViewportView(treeModels);

    bOK.setText("OK");
    bOK.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        bOKActionPerformed(evt);
      }
    });

    bCancel.setText("Cancel");
    bCancel.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        bCancelActionPerformed(evt);
      }
    });

    javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
    getContentPane().setLayout(layout);
    layout.setHorizontalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(layout.createSequentialGroup()
        .addGap(0, 0, Short.MAX_VALUE)
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
            .addComponent(bOK)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(bCancel)
            .addGap(6, 6, 6))
          .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 276, javax.swing.GroupLayout.PREFERRED_SIZE)))
    );
    layout.setVerticalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(layout.createSequentialGroup()
        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 275, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(bOK)
          .addComponent(bCancel))
        .addGap(0, 0, 0))
    );

    pack();
  }// </editor-fold>//GEN-END:initComponents

  private void bOKActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bOKActionPerformed
    
    TreePath path = treeModels.getSelectionPath();
    int i = -1;
    if (path != null) {
      String s = (String)path.getLastPathComponent().toString();
      for (int j = 0; j < MODEL_NAME.length; j++) {
        if (s.equals(MODEL_NAME[j])) {
          i = j;
          break;
        }
      }
    }
    if (i == -1) {
      JOptionPane.showMessageDialog(this, "Select a model", "Warning", JOptionPane.WARNING_MESSAGE);
    } else {
      dispose();
      selected = i;
    }
  }//GEN-LAST:event_bOKActionPerformed

  private void bCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bCancelActionPerformed
    
    selected = -1;
    dispose();
  }//GEN-LAST:event_bCancelActionPerformed

  public int showDialog(Component parent) {
    
    setLocationRelativeTo(parent);
    setVisible(true);
    return selected;
  }
  
  public int getSelectedIndex() {
    return selected;
  }
  
  public String getSelectedName() {
    
    if (selected != -1) {
      return MODEL_NAME[selected];
    }
    return null;
  }

  private int selected;
  // Variables declaration - do not modify//GEN-BEGIN:variables
  private javax.swing.JButton bCancel;
  private javax.swing.JButton bOK;
  private javax.swing.JScrollPane jScrollPane1;
  private javax.swing.JTree treeModels;
  // End of variables declaration//GEN-END:variables
}
