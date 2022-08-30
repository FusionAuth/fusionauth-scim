package io.fusionauth.scim.parser.expression;

import java.util.Objects;

import io.fusionauth.scim.parser.LogicalOperator;
import io.fusionauth.scim.utils.ToString;

public class LogicalLinkExpression implements Expression {
  public Expression left;

  public LogicalOperator linkOperator;

  public Expression right;

  public LogicalLinkExpression(LogicalOperator linkOperator) {
    this.linkOperator = linkOperator;
  }

  public LogicalLinkExpression(Expression left, LogicalOperator linkOperator, Expression right) {
    this.left = left;
    this.linkOperator = linkOperator;
    this.right = right;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    LogicalLinkExpression that = (LogicalLinkExpression) o;
    return Objects.equals(left, that.left) && linkOperator == that.linkOperator && Objects.equals(right, that.right);
  }

  @Override
  public int hashCode() {
    return Objects.hash(left, linkOperator, right);
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
