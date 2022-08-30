package io.fusionauth.scim.parser.expression;

import io.fusionauth.scim.parser.ComparisonOperator;
import io.fusionauth.scim.parser.ValueType;
import io.fusionauth.scim.utils.ToString;

public class AttributeNullTestExpression extends AttributeExpression {
  public AttributeNullTestExpression(String attributePath, ComparisonOperator operator) {
    super(attributePath, operator);
  }

  @Override
  public String toString() {
    return ToString.toString(this);
  }

  @Override
  public ValueType valueType() {
    return ValueType.nul;
  }
}
