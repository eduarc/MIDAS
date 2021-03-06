/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.midas.classify.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 *
 * @author eduarc (Eduar Castrillo)
 */
public abstract class DataStream {
  /* Lee un DataSet desde el flujo de datos */
  public abstract DataSet read(InputStream stream) throws IOException;
  /* Escribe un DataSet en el flujo de datos */
  public abstract void write(OutputStream stream, DataSet ds) throws IOException;
}
