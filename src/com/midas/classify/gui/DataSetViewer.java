/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.midas.classify.gui;

import com.midas.classify.data.Instances;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import javax.swing.JDialog;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;

/**
 *
 * @author Eduar Castrillo (eduarc)
 */
public class DataSetViewer extends JDialog {
  
  JTextPane taOutput;
  JScrollPane scrollOutput;
  
  public DataSetViewer() {
    
    taOutput = new JTextPane();
    taOutput.setContentType("text/plain"); // NOI18N
    taOutput.setFont(new java.awt.Font(Font.MONOSPACED, 0, 14)); // NOI18N
    
    scrollOutput = new JScrollPane();
    scrollOutput.setViewportView(taOutput);
    
    add(scrollOutput, BorderLayout.CENTER);
    pack();
    setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
  }
  
  public void show(Instances ins) {
    
    taOutput.setText(ins.toString());
    setTitle("Dataset: "+ins.relationName());
    setLocationRelativeTo(this);
    setVisible(true);
  }
  
  @Override
  public Dimension getPreferredSize() {
    return new Dimension(500, 700);
  }
}
