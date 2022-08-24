package io.fusionauth.scim.parser;

import io.fusionauth.scim.domain.Buildable;

public class Filter implements Buildable<Filter> {
  public String attribute;

  public String schema;

  public Op op;

  public ValueType valueType;

  public String value;
}
