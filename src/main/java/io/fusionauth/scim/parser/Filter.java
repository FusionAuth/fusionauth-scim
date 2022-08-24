package io.fusionauth.scim.parser;

import java.util.Objects;

import io.fusionauth.scim.domain.Buildable;

public class Filter implements Buildable<Filter> {
  public String attribute;

  public String schema = null;

  public Op op;

  public ValueType valueType;

  public String value = null;

  public Filter(String attribute) {
    this.attribute = attribute;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {return true;}
    if (o == null || getClass() != o.getClass()) {return false;}
    Filter filter = (Filter) o;
    return attribute.equals(filter.attribute) && Objects.equals(schema, filter.schema) && op == filter.op && valueType == filter.valueType && Objects.equals(value, filter.value);
  }

  @Override
  public int hashCode() {
    return Objects.hash(attribute, schema, op, valueType, value);
  }
}
