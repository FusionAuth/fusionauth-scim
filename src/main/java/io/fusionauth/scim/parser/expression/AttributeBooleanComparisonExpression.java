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
import io.fusionauth.scim.parser.ValueType;
import io.fusionauth.scim.utils.ToString;

public class AttributeBooleanComparisonExpression extends AttributeComparisonExpression {
  public Boolean comparisonValue;

  public AttributeBooleanComparisonExpression(String attributePath, ComparisonOperator operator, Boolean comparisonValue) {
    super(attributePath, operator);
    this.comparisonValue = comparisonValue;
  }

  public AttributeBooleanComparisonExpression(AttributeBooleanComparisonExpression other) {
    super(other.attributePath, other.operator);
    this.comparisonValue = other.comparisonValue;
  }

  @Override
  public AttributeExpression clone() {
    return new AttributeBooleanComparisonExpression(this);
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
    AttributeBooleanComparisonExpression that = (AttributeBooleanComparisonExpression) o;
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
  public Boolean value() {
    return comparisonValue;
  }

  @Override
  public ValueType valueType() {
    return ValueType.bool;
  }
}
