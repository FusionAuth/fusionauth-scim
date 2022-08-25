package io.fusionauth.scim.parser;

import java.util.Objects;

import io.fusionauth.scim.domain.Buildable;

public class Filter implements Buildable<Filter> {
  public String attribute;

  public Op op;

  public String schema = null;

  public String value = null;

  public ValueType valueType;

  public Filter(String attribute) {
    if (attribute.startsWith("urn:")) {
      int lastColon = attribute.lastIndexOf(':');
      this.schema = attribute.substring(0, lastColon);
      this.attribute = attribute.substring(lastColon + 1);
    } else {
      this.attribute = attribute;
    }
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Filter filter = (Filter) o;
    return attribute.equals(filter.attribute) && Objects.equals(schema, filter.schema) && op == filter.op && valueType == filter.valueType && Objects.equals(value, filter.value);
  }

  @Override
  public int hashCode() {
    return Objects.hash(attribute, schema, op, valueType, value);
  }

  @Override
  public String toString() {
    return "Filter{" +
           "attribute='" + attribute + '\'' +
           ", schema='" + schema + '\'' +
           ", op=" + op +
           ", valueType=" + valueType +
           ", value='" + value + '\'' +
           '}';
  }
}
