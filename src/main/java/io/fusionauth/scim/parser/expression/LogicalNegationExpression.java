package io.fusionauth.scim.parser.expression;

import java.util.Objects;

import io.fusionauth.scim.parser.ExpressionType;
import io.fusionauth.scim.utils.ToString;

public class LogicalNegationExpression extends Expression {
  public Expression subExpression;

  public LogicalNegationExpression(Expression subExpression) {
    this.subExpression = subExpression;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    LogicalNegationExpression that = (LogicalNegationExpression) o;
    return Objects.equals(subExpression, that.subExpression);
  }

  @Override
  public int hashCode() {
    return Objects.hash(subExpression);
  }

  @Override
  public String toString() {
    return ToString.toString(this);
  }

  @Override
  public ExpressionType type() {
    return ExpressionType.logicalNegation;
  }
}
