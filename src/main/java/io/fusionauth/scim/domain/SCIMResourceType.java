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
package io.fusionauth.scim.domain;

import java.util.List;
import java.util.Objects;

import io.fusionauth.scim.utils.ToString;

/**
 * @author Rob Davis
 */
public class SCIMResourceType implements Buildable<SCIMResourceType>, SCIMResource {
  public static final String EnterpriseUser = "EnterpriseUser";

  public static final String Group = "Group";

  public static final String User = "User";

  public String description;

  public String endpoint;

  public String externalId;

  public String id;

  public SCIMMeta meta;

  public String name;

  public String schema;

  public List<SchemaExtension> schemaExtensions;

  public List<String> schemas;

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    SCIMResourceType that = (SCIMResourceType) o;
    return Objects.equals(description, that.description) &&
           Objects.equals(endpoint, that.endpoint) &&
           Objects.equals(id, that.id) &&
           Objects.equals(meta, that.meta) &&
           Objects.equals(name, that.name) &&
           Objects.equals(schema, that.schema) &&
           Objects.equals(schemaExtensions, that.schemaExtensions) &&
           Objects.equals(schemas, that.schemas);
  }

  @Override
  public int hashCode() {
    return Objects.hash(description, endpoint, id, meta, name, schema, schemaExtensions, schemas);
  }

  @Override
  public String toString() {
    return ToString.toString(this);
  }
}
