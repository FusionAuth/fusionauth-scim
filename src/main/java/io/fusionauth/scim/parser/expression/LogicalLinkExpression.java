/*
 * Copyright (c) 2022, FusionAuth, All Rights Reserved
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 */

package io.fusionauth.scim.parser.expression;

import java.util.Objects;

import io.fusionauth.scim.parser.ExpressionType;
import io.fusionauth.scim.parser.LogicalOperator;
import io.fusionauth.scim.utils.ToString;

/**
 * An expression linking two sub-expressions with a logical operator (e.g. {@code A and B})
 *
 * @author Spencer Witt
 */
public class LogicalLinkExpression extends LogicalExpression {
  /**
   * The left sub-expression for the logical operator
   */
  public Expression left;

  /**
   * The right sub-expression for the logical operator
   */
  public Expression right;

  public LogicalLinkExpression(LogicalOperator logicalOperator) {
    super(logicalOperator);
  }

  public LogicalLinkExpression(Expression left, LogicalOperator logicalOperator, Expression right) {
    super(logicalOperator);
    this.left = left;
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
    if (!super.equals(o)) {
      return false;
    }
    LogicalLinkExpression that = (LogicalLinkExpression) o;
    return Objects.equals(left, that.left) && Objects.equals(right, that.right);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), left, right);
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
