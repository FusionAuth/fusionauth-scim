package io.fusionauth.scim.parser.expression;

import io.fusionauth.scim.parser.ComparisonOperator;
import io.fusionauth.scim.utils.ToString;

public class AttributePresentExpression implements Expression {
  public final ComparisonOperator operation = ComparisonOperator.pr;

  public String attributePath;

  public AttributePresentExpression(String attributePath) {
    this.attributePath = attributePath;
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
