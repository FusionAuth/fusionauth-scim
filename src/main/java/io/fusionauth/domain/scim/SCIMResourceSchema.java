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

import com.fasterxml.jackson.annotation.JsonProperty;
import io.fusionauth.domain.utils.ToString;

/**
 * @author Rob Davis
 */
public class SCIMResourceSchema extends BaseSCIMResource implements Buildable<SCIMResourceSchema> {
  public List<SCIMResourceSchemaAttribute> attributes;

  public String description;

  public String name;

  @JsonProperty("id")
  public String schemaId;

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    if (!super.equals(o)) {
      return false;
    }
    SCIMResourceSchema that = (SCIMResourceSchema) o;
    return Objects.equals(schemaId, that.schemaId) && Objects.equals(description, that.description) && Objects.equals(name, that.name) && Objects.equals(attributes, that.attributes);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), schemaId, description, name, attributes);
  }

  @Override
  public String toString() {
    return ToString.toString(this);
  }
}