package io.fusionauth.scim.parser.expression;

import io.fusionauth.scim.parser.ComparisonOperator;

public abstract class AttributeComparisonExpression extends AttributeExpression {
  public AttributeComparisonExpression(String attributePath, ComparisonOperator operator) {
    super(attributePath, operator);
  }

  public abstract Object value();
}
