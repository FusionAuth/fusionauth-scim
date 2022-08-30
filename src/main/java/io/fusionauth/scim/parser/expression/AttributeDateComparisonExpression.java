package io.fusionauth.scim.parser.expression;

import java.time.ZonedDateTime;
import java.util.Objects;

import io.fusionauth.scim.parser.ComparisonOperator;
import io.fusionauth.scim.parser.ValueType;
import io.fusionauth.scim.utils.ToString;

public class AttributeDateComparisonExpression extends AttributeComparisonExpression<ZonedDateTime> {
  public ZonedDateTime comparisonValue;

  public AttributeDateComparisonExpression(String attributePath, ComparisonOperator operation, ZonedDateTime comparisonValue) {
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
    AttributeDateComparisonExpression that = (AttributeDateComparisonExpression) o;
    return Objects.equals(comparisonValue, that.comparisonValue);
  }

  @SuppressWarnings("unused")
  public long getComparisonValue() {
    return comparisonValue.toInstant().toEpochMilli();
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
  public ZonedDateTime value() {
    return comparisonValue;
  }

  @Override
  public ValueType valueType() {
    return ValueType.date;
  }
}
