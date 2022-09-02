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

import io.fusionauth.scim.parser.LogicalOperator;

/**
 * An expression applying a logical operator to one or more sub-expressions
 *
 * @author Spencer Witt
 */
public abstract class LogicalExpression extends Expression {
  /**
   * The logical operator for this expression
   */
  public LogicalOperator logicalOperator;

  public LogicalExpression(LogicalOperator logicalOperator) {
    this.logicalOperator = logicalOperator;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    LogicalExpression that = (LogicalExpression) o;
    return logicalOperator == that.logicalOperator;
  }

  @Override
  public int hashCode() {
    return Objects.hash(logicalOperator);
  }
}
