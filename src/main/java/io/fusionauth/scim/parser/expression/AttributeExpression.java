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

import io.fusionauth.scim.parser.ComparisonOperator;
import io.fusionauth.scim.parser.ExpressionType;
import io.fusionauth.scim.parser.ValueType;

/**
 * An expression that performs some check against the given attribute
 *
 * @param <T> The concrete {@code AttributeExpression} subclass
 * @author Spencer Witt
 */
public abstract class AttributeExpression<T> extends Expression implements Copyable<T> {
  /**
   * Path to the attribute to compare
   */
  public String attributePath;

  /**
   * The operator indicating which comparison or check should be performed
   */
  public ComparisonOperator operator;

  public AttributeExpression(String attributePath, ComparisonOperator operator) {
    this.attributePath = attributePath;
    this.operator = operator;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    AttributeExpression<?> that = (AttributeExpression<?>) o;
    return Objects.equals(attributePath, that.attributePath) && operator == that.operator;
  }

  @Override
  public int hashCode() {
    return Objects.hash(attributePath, operator);
  }

  @Override
  public ExpressionType type() {
    return ExpressionType.attribute;
  }

  /**
   * The type of the comparison value for this expression
   *
   * @return The comparison value's type
   */
  public abstract ValueType valueType();
}
