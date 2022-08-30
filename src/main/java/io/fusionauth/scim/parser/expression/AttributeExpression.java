package io.fusionauth.scim.parser.expression;

import java.util.Objects;

import io.fusionauth.scim.parser.ComparisonOperator;
import io.fusionauth.scim.parser.ExpressionType;

public abstract class AttributeExpression extends Expression {
  public String attributePath;

  public ComparisonOperator operator;

  public AttributeExpression(String attributePath, ComparisonOperator operator) {
    this.attributePath = attributePath;
    this.operator = operator;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    AttributeExpression that = (AttributeExpression) o;
    return Objects.equals(attributePath, that.attributePath) && operator == that.operator;
  }

  @Override
  public int hashCode() {
    return Objects.hash(attributePath, operator);
  }

  @Override
  public ExpressionType type() {
    return ExpressionType.attribute;
  }
}
