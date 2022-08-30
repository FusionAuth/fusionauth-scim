package io.fusionauth.scim.parser.expression;

import io.fusionauth.scim.parser.ComparisonOperator;
import io.fusionauth.scim.utils.ToString;

public class AttributePresentTestExpression extends AttributeExpression {
  public AttributePresentTestExpression(String attributePath) {
    super(attributePath, ComparisonOperator.pr);
  }

  @Override
  public String toString() {
    return ToString.toString(this);
  }
}
