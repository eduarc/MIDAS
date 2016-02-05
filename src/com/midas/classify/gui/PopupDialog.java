/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.midas.classify.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;

/**
 *
 * @author Eduar Castrillo (eduarc)
 */
public abstract class PopupDialog extends JDialog {
  
  public ClassificationPanel mainPanel;
  
  public static final int OK = 0;
  public static final int CANCEL = 1;
  public static final int DISPOSE = 0;
  public static final int VISIBLE = 1;
  
  int res;
  JButton bOk;
  JButton bCancel;
  
  public PopupDialog(ClassificationPanel cPanel) {
    
    mainPanel = cPanel;
    
    super.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
    setModalityType(ModalityType.APPLICATION_MODAL);
    //setResizable(false);
    //setUndecorated(true);
    
    JPanel pControls = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    
    bOk = new JButton("Ok");
    bOk.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        res = OK;
        if (onOk() == DISPOSE) {
          dispose();
        }
      }
    });
    
    bCancel = new JButton("Cancel");
    bCancel.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        res = CANCEL;
        if (onCancel() == DISPOSE) {
          dispose();
        }
      }
    });
    
    pControls.add(bOk);
    pControls.add(bCancel);
    add(pControls, BorderLayout.SOUTH);
    pack();
  }
  
  public void setViewportView(JPanel view) {
    
    getContentPane().add(view, BorderLayout.CENTER);
    pack();
  }
  
  public int showDialog(Component parent) {
    
    setLocationRelativeTo(parent);
    setVisible(true);
    return res;
  }
  
  public abstract int onCancel();
  public abstract int onOk();
}
