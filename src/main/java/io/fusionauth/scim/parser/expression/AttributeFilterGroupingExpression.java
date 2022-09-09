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

/**
 * A complex attribute filter grouping where attribute paths in the contained {@link AttributeFilterGroupingExpression#filterExpression} refer to
 * sub-attributes of {@link AttributeFilterGroupingExpression#parentAttributePath}
 *
 * @author Spencer Witt
 */
public class AttributeFilterGroupingExpression extends Expression {
  /**
   * Sub-expressions for this grouping
   */
  public Expression filterExpression;

  /**
   * The attribute path that all filters contained in {@link AttributeFilterGroupingExpression#filterExpression} will extend from
   */
  public String parentAttributePath;

  public AttributeFilterGroupingExpression(String parentAttributePath) {
    this.parentAttributePath = parentAttributePath;
  }

  public AttributeFilterGroupingExpression(String parentAttributePath, Expression filterExpression) {
    this.filterExpression = filterExpression;
    this.parentAttributePath = parentAttributePath;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    AttributeFilterGroupingExpression that = (AttributeFilterGroupingExpression) o;
    return Objects.equals(filterExpression, that.filterExpression) && Objects.equals(parentAttributePath, that.parentAttributePath);
  }

  @Override
  public int hashCode() {
    return Objects.hash(filterExpression, parentAttributePath);
  }

  @Override
  public ExpressionType type() {
    return ExpressionType.attributeFilterGrouping;
  }
}
