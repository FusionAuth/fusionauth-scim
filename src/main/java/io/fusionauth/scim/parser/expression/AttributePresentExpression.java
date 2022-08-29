package io.fusionauth.scim.parser.expression;

import java.util.Objects;

import io.fusionauth.scim.parser.ComparisonOperator;
import io.fusionauth.scim.utils.ToString;

public class AttributePresentExpression implements Expression {
  public final ComparisonOperator operator = ComparisonOperator.pr;

  public String attributePath;

  public AttributePresentExpression(String attributePath) {
    this.attributePath = attributePath;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    AttributePresentExpression that = (AttributePresentExpression) o;
    return operator == that.operator && Objects.equals(attributePath, that.attributePath);
  }

  @Override
  public int hashCode() {
    return Objects.hash(operator, attributePath);
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
