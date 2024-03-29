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
import io.fusionauth.scim.parser.ValueType;
import io.fusionauth.scim.utils.ToString;

/**
 * An expression that compares the given attribute to {@code null}. This expression only works with the {@link ComparisonOperator#eq} and
 * {@link ComparisonOperator#ne} operators
 *
 * @author Spencer Witt
 */
public class AttributeNullTestExpression extends AttributeExpression<AttributeNullTestExpression> {
  public AttributeNullTestExpression(String attributePath, ComparisonOperator operator) {
    super(attributePath, operator);
  }

  public AttributeNullTestExpression(AttributeNullTestExpression other) {
    super(other.attributePath, other.operator);
  }

  @Override
  public AttributeNullTestExpression copy() {
    return new AttributeNullTestExpression(this);
  }

  @Override
  public String toString() {
    return ToString.toString(this);
  }

  @Override
  public ValueType valueType() {
    return ValueType.nul;
  }
}
