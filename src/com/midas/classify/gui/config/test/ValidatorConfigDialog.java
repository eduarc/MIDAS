/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.midas.classify.gui.config.test;

import com.midas.classify.gui.ClassificationPanel;
import com.midas.classify.gui.PopupDialog;
import com.midas.classify.metrics.Validator;

/**
 *
 * @author eduarc
 */
public abstract class ValidatorConfigDialog extends PopupDialog {

  public ValidatorConfigDialog(ClassificationPanel cPanel) {
    super(cPanel);
  }
  
  public abstract Validator getValidator();
}
