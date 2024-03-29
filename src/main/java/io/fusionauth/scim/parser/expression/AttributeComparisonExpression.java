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

import io.fusionauth.scim.parser.ComparisonOperator;

/**
 * An expression comparing an attribute's value to a given comparison value
 *
 * @param <T> The concrete {@code AttributeExpression} subclass
 * @param <V> The type of the comparison value
 * @author Spencer Witt
 */
public abstract class AttributeComparisonExpression<T, V> extends AttributeExpression<T> {
  public AttributeComparisonExpression(String attributePath, ComparisonOperator operator) {
    super(attributePath, operator);
  }

  /**
   * Retrieves the comparison value for the expression
   *
   * @return The comparison value
   */
  public abstract V value();

  /**
   * Retrieve the value as a string that is ok to use in a literal filter query.
   *
   * @return a string form of the value returned by calling {@link #value()}.
   */
  public abstract String valueAsString();
}
