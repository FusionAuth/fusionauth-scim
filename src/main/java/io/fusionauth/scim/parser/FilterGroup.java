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
package io.fusionauth.scim.parser;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import io.fusionauth.scim.domain.Buildable;
import io.fusionauth.scim.utils.ToString;

/**
 * @author Spencer Witt
 */
public class FilterGroup implements Buildable<FilterGroup> {
  public List<Filter> filters = new ArrayList<>();

  public boolean inverted = false;

  public boolean isLastAddGroup = false;

  /**
   * This is used to handle mixed and-or logical operators at the same level
   * without nesting properly (e.g. A * B + C + D * E * F)
   */
  public LogicalOperator lastLogicalOp = null;

  public LogicalOperator logicalOperator = null;

  public String parent = null;

  public List<FilterGroup> subGroups = new ArrayList<>();

  public FilterGroup() {
  }

  public FilterGroup(LogicalOperator logicalOperator) {
    this.logicalOperator = logicalOperator;
  }

  public FilterGroup addFilter(Filter filter) {
    filters.add(filter);
    isLastAddGroup = false;
    return this;
  }

  public FilterGroup addSubGroup(FilterGroup group) {
    subGroups.add(group);
    isLastAddGroup = true;
    return this;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    FilterGroup that = (FilterGroup) o;
    return inverted == that.inverted && filters.equals(that.filters) && subGroups.equals(that.subGroups) && logicalOperator == that.logicalOperator && Objects.equals(parent, that.parent);
  }

  @Override
  public int hashCode() {
    return Objects.hash(filters, subGroups, inverted, logicalOperator, parent);
  }

  @Override
  public String toString() {
    return ToString.toString((this));
  }
}
