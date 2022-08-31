package io.fusionauth.scim.parser.exception;

public abstract class SCIMParserException extends RuntimeException {
  protected SCIMParserException(String message) {
    super(message);
  }
}
