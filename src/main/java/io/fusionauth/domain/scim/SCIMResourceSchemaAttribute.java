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
package io.fusionauth.domain.scim;

import java.util.List;
import java.util.Objects;

import io.fusionauth.domain.utils.ToString;

/**
 * @author Rob Davis
 */
public class SCIMResourceSchemaAttribute implements Buildable<SCIMResourceSchemaAttribute> {
  public boolean caseExact;

  public String description;

  public boolean multiValued;

  public String mutability;

  public String name;

  public boolean required;

  public String returned;

  public List<SCIMResourceSchemaAttribute> subAttributes;

  public String type;

  public String uniqueness;

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    SCIMResourceSchemaAttribute that = (SCIMResourceSchemaAttribute) o;
    return multiValued == that.multiValued && required == that.required && caseExact == that.caseExact && Objects.equals(name, that.name) && Objects.equals(type, that.type) && Objects.equals(description, that.description) && Objects.equals(mutability, that.mutability) && Objects.equals(returned, that.returned) && Objects.equals(uniqueness, that.uniqueness) && Objects.equals(subAttributes, that.subAttributes);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, type, multiValued, description, required, caseExact, mutability, returned, uniqueness, subAttributes);
  }

  @Override
  public String toString() {
    return ToString.toString(this);
  }
}
