package io.fusionauth.scim.parser.expression;

import io.fusionauth.scim.parser.ComparisonOperator;
import io.fusionauth.scim.parser.ValueType;

public abstract class AttributeComparisonExpression<T> extends AttributeExpression {
  public AttributeComparisonExpression(String attributePath, ComparisonOperator operator) {
    super(attributePath, operator);
  }

  public abstract T value();

  public abstract ValueType valueType();
}
