package io.fusionauth.scim.parser.expression;

import io.fusionauth.scim.parser.ComparisonOperator;
import io.fusionauth.scim.utils.ToString;

public class AttributeBooleanExpression implements Expression {
  public String attributePath;

  public boolean comparisonValue;

  public ComparisonOperator operation;

  public AttributeBooleanExpression(String attributePath, ComparisonOperator operation, boolean comparisonValue) {
    this.attributePath = attributePath;
    this.operation = operation;
    this.comparisonValue = comparisonValue;
  }

  @Override
  public boolean match() {
    return false;
  }

  @Override
  public String toString() {
    return ToString.toString(this);
  }
}
