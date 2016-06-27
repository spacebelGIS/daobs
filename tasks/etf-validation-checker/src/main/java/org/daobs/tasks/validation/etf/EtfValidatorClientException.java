package org.daobs.tasks.validation.etf;

/**
 * Exception class for EtfValidatorClient.
 *
 * @author Jose García
 */
public class EtfValidatorClientException extends Exception {
  public EtfValidatorClientException() {
    super();
  }

  public EtfValidatorClientException(String message) {
    super(message);
  }

  public EtfValidatorClientException(String message, Throwable cause) {
    super(message, cause);
  }

  public EtfValidatorClientException(Throwable cause) {
    super(cause);
  }
}
