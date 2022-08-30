package io.fusionauth.scim.parser.expression;

import java.util.Objects;

import io.fusionauth.scim.parser.ExpressionType;
import io.fusionauth.scim.parser.LogicalOperator;
import io.fusionauth.scim.utils.ToString;

public class LogicalLinkExpression extends Expression {
  public Expression left;

  public LogicalOperator logicalOperator;

  public Expression right;

  public LogicalLinkExpression(LogicalOperator logicalOperator) {
    this.logicalOperator = logicalOperator;
  }

  public LogicalLinkExpression(Expression left, LogicalOperator logicalOperator, Expression right) {
    this.left = left;
    this.logicalOperator = logicalOperator;
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
    return Objects.equals(left, that.left) && logicalOperator == that.logicalOperator && Objects.equals(right, that.right);
  }

  @Override
  public int hashCode() {
    return Objects.hash(left, logicalOperator, right);
  }

  @Override
  public String toString() {
    return ToString.toString(this);
  }

  @Override
  public ExpressionType type() {
    return ExpressionType.logicalLink;
  }
}
