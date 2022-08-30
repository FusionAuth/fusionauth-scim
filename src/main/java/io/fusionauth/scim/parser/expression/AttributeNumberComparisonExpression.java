package io.fusionauth.scim.parser.expression;

import java.math.BigDecimal;
import java.util.Objects;

import io.fusionauth.scim.parser.ComparisonOperator;
import io.fusionauth.scim.utils.ToString;

public class AttributeNumberComparisonExpression extends AttributeComparisonExpression<BigDecimal> {
  public BigDecimal comparisonValue;

  public AttributeNumberComparisonExpression(String attributePath, ComparisonOperator operation, BigDecimal comparisonValue) {
    super(attributePath, operation);
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
    AttributeNumberComparisonExpression that = (AttributeNumberComparisonExpression) o;
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
  public BigDecimal value() {
    return comparisonValue;
  }
}
