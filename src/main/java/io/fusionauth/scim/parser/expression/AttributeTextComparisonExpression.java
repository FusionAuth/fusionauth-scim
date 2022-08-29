package io.fusionauth.scim.parser.expression;

import java.util.Objects;

import io.fusionauth.scim.parser.ComparisonOperator;
import io.fusionauth.scim.utils.ToString;

public class AttributeTextComparisonExpression implements Expression {
  public String attributePath;

  public String comparisonValue;

  public ComparisonOperator operator;

  public AttributeTextComparisonExpression(String attributePath, ComparisonOperator operation, String comparisonValue) {
    this.attributePath = attributePath;
    this.operator = operation;
    this.comparisonValue = comparisonValue;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    AttributeTextComparisonExpression that = (AttributeTextComparisonExpression) o;
    return comparisonValue.equals(that.comparisonValue) && Objects.equals(attributePath, that.attributePath) && operator == that.operator;
  }

  @Override
  public int hashCode() {
    return Objects.hash(attributePath, comparisonValue, operator);
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
