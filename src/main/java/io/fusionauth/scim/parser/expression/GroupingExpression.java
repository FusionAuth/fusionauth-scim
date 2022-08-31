package io.fusionauth.scim.parser.expression;

import io.fusionauth.scim.parser.ExpressionType;

public class GroupingExpression extends Expression {
  @Override
  public ExpressionType type() {
    return ExpressionType.grouping;
  }
}
