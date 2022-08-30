package io.fusionauth.scim.parser.expression;

import io.fusionauth.scim.parser.LogicalOperator;
import io.fusionauth.scim.utils.ToString;

public class LogicalLinkExpression implements Expression {
  public Expression left;

  public LogicalOperator linkOperator;

  public Expression right;

  public LogicalLinkExpression(Expression left, LogicalOperator linkOperator, Expression right) {
    this.left = left;
    this.linkOperator = linkOperator;
    this.right = right;
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
