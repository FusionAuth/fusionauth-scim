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

import java.math.BigDecimal;
import java.util.Objects;

import io.fusionauth.scim.parser.ComparisonOperator;
import io.fusionauth.scim.parser.ValueType;
import io.fusionauth.scim.utils.ToString;

/**
 * An expression comparing an attribute's value to a given numeric value.
 *
 * @author Spencer Witt
 */
public class AttributeNumberComparisonExpression extends AttributeComparisonExpression<AttributeNumberComparisonExpression, BigDecimal> {
  /**
   * The numeric value the attribute will be compared to
   */
  public BigDecimal comparisonValue;

  public AttributeNumberComparisonExpression(String attributePath, ComparisonOperator operator, BigDecimal comparisonValue) {
    super(attributePath, operator);
    this.comparisonValue = comparisonValue;
  }

  public AttributeNumberComparisonExpression(AttributeNumberComparisonExpression other) {
    super(other.attributePath, other.operator);
    this.comparisonValue = other.comparisonValue;
  }

  @Override
  public AttributeNumberComparisonExpression copy() {
    return new AttributeNumberComparisonExpression(this);
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
    AttributeNumberComparisonExpression that = (AttributeNumberComparisonExpression) o;
    return Objects.equals(comparisonValue, that.comparisonValue);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), comparisonValue);
  }

  @Override
  public String toString() {
    return ToString.toString(this);
  }

  @Override
  public BigDecimal value() {
    return comparisonValue;
  }

  @Override
  public String valueAsString() {
    return comparisonValue.toString();
  }

  @Override
  public ValueType valueType() {
    return ValueType.number;
  }
}
