package io.fusionauth.scim.parser.expression;

public interface Expression {
  boolean match();

  String toString();
}
