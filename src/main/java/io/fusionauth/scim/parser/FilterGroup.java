package io.fusionauth.scim.parser;

import java.util.ArrayList;
import java.util.List;

import io.fusionauth.scim.domain.Buildable;

public class FilterGroup implements Buildable<FilterGroup> {
  public List<Filter> filters = new ArrayList<>();

  public List<FilterGroup> subGroups = new ArrayList<>();

  public boolean inverted = false;

  public LogicalOperator logicalOperator = null;

  public String parent = null;

}
