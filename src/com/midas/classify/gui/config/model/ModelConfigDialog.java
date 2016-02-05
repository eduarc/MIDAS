/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.midas.classify.gui.config.model;

import com.midas.classify.factory.ClassifierFactory;
import com.midas.classify.gui.ClassificationPanel;
import com.midas.classify.gui.PopupDialog;

/**
 *
 * @author Eduar Castrillo (eduarc)
 */
public abstract class ModelConfigDialog extends PopupDialog {

  public ModelConfigDialog(ClassificationPanel cPanel) {
    super(cPanel);
  }
  
  public abstract ClassifierFactory getFactory();
}
