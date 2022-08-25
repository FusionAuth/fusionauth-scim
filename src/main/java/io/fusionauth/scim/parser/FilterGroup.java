package io.fusionauth.scim.parser;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import io.fusionauth.scim.domain.Buildable;

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
    return "FilterGroup{" +
           "filters=" + filters +
           ", subGroups=" + subGroups +
           ", inverted=" + inverted +
           ", logicalOperator=" + logicalOperator +
           ", parent='" + parent + '\'' +
           '}';
  }
}
