package io.fusionauth.scim.parser.expression;

import io.fusionauth.scim.parser.Op;
import io.fusionauth.scim.utils.ToString;

public class AttributeExpression implements Expression {
  public String attributePath;

  public String comparisonValue;

  public Op operation;

  @Override
  public String toString() {
    return ToString.toString(this);
  }
}
