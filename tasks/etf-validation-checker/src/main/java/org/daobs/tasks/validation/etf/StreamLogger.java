package org.daobs.tasks.validation.etf;

import org.apache.commons.logging.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

/**
 * Class to log process streams.
 * <p>
 * Example:
 * </p>
 * <pre>
 *  <code>
 *    Process pr = Runtime.getRuntime().exec(command);
 *
 *    StreamLogger outputLogger = new
 *        StreamLogger(pr.getOutputStream(), this.log);
 *
 *    outputLogger.start();
 *  </code>
 * </pre>
 */
public class StreamLogger extends Thread {
  InputStream is;
  Log log;

  public StreamLogger(InputStream is, Log log) {
    this.is = is;
    this.log = log;
  }

  /**
   * Logs stream.
   *
   */
  public void run() {
    try {
      InputStreamReader isr = new InputStreamReader(is, StandardCharsets.UTF_8);
      BufferedReader br = new BufferedReader(isr);
      String line = null;

      while ((line = br.readLine()) != null) {
        log.debug(line);
      }
    } catch (IOException ex) {
      ex.printStackTrace();
    }
  }
}
