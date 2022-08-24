package io.fusionauth.scim.parser;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import io.fusionauth.scim.domain.Buildable;

public class FilterGroup implements Buildable<FilterGroup> {
  public List<Filter> filters = new ArrayList<>();

  public List<FilterGroup> subGroups = new ArrayList<>();

  public boolean inverted = false;

  public LogicalOperator logicalOperator = null;

  public String parent = null;

  @Override
  public boolean equals(Object o) {
    if (this == o) {return true;}
    if (o == null || getClass() != o.getClass()) {return false;}
    FilterGroup that = (FilterGroup) o;
    return inverted == that.inverted && filters.equals(that.filters) && subGroups.equals(that.subGroups) && logicalOperator == that.logicalOperator && Objects.equals(parent, that.parent);
  }

  @Override
  public int hashCode() {
    return Objects.hash(filters, subGroups, inverted, logicalOperator, parent);
  }
}
