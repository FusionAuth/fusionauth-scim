package io.fusionauth.scim.parser.expression;

import java.util.Objects;

import io.fusionauth.scim.parser.ComparisonOperator;
import io.fusionauth.scim.parser.ValueType;
import io.fusionauth.scim.utils.ToString;

public class AttributeBooleanComparisonExpression extends AttributeComparisonExpression<Boolean> {
  public Boolean comparisonValue;

  public AttributeBooleanComparisonExpression(String attributePath, ComparisonOperator operator, Boolean comparisonValue) {
    super(attributePath, operator);
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
    if (!super.equals(o)) {
      return false;
    }
    AttributeBooleanComparisonExpression that = (AttributeBooleanComparisonExpression) o;
    return Objects.equals(comparisonValue, that.comparisonValue);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), comparisonValue);
  }

  @Override
  public String toString() {
    return ToString.toString(this);
  }

  @Override
  public Boolean value() {
    return comparisonValue;
  }

  @Override
  public ValueType valueType() {
    return ValueType.bool;
  }
}
