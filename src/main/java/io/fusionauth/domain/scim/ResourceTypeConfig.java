/*
 * Copyright (c) 2021, FusionAuth, All Rights Reserved
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

import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import io.fusionauth.domain.utils.ToString;

/**
 * @author Rob Davis
 */
public class ResourceTypeConfig implements Buildable<ResourceTypeConfig> {
  public String description;

  public String endpoint;

  public String id;

  public HashMap<String, String> meta;

  public String name;

  public String schema;

  public List<HashMap<String, Object>> schemaExtensions;

  public List<String> schemas;

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ResourceTypeConfig that = (ResourceTypeConfig) o;
    return Objects.equals(id, that.id) && Objects.equals(name, that.name) && Objects.equals(endpoint, that.endpoint) && Objects.equals(description, that.description) && Objects.equals(schema, that.schema) && Objects.equals(schemas, that.schemas) && Objects.equals(meta, that.meta) && Objects.equals(schemaExtensions, that.schemaExtensions);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, name, endpoint, description, schema, schemas, meta, schemaExtensions);
  }

  @Override
  public String toString() {
    return ToString.toString(this);
  }
}
