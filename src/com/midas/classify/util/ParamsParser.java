/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.midas.classify.util;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 *
 * @author eduarc (Eduar Castrillo)
 */
public class ParamsParser {
  
  public static List<String> getTokens(String str) {
    
    List<String> p = new ArrayList();
    StringTokenizer tok = new StringTokenizer(str, " ");
    
    while (tok.hasMoreTokens()) {
      p.add(tok.nextToken());
    }
    return p;
  }
}
