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

import java.util.Objects;

import io.fusionauth.scim.domain.Buildable;
import io.fusionauth.scim.utils.ToString;

/**
 * @author Spencer Witt
 */
public class Filter implements Buildable<Filter> {
  public String attribute;

  public Op op;

  public String schema;

  public String value;

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
    return attribute.equals(filter.attribute) &&
           op == filter.op &&
           Objects.equals(schema, filter.schema) &&
           Objects.equals(value, filter.value) &&
           valueType == filter.valueType;
  }

  @Override
  public int hashCode() {
    return Objects.hash(attribute, op, schema, value, valueType);
  }

  @Override
  public String toString() {
    return ToString.toString((this));
  }
}
