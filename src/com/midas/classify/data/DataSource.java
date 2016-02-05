/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.midas.classify.data;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 *
 * @author eduarc
 */
public abstract class DataSource {
  
  /* Lee un DataSet desde el flujo de datos */
  public abstract Instances read(InputStream stream) throws IOException;
  /* Escribe un DataSet en el flujo de datos */
  public abstract void write(OutputStream stream, Instances ds) throws IOException;
}
