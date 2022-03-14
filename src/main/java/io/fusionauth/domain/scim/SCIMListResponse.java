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

import java.util.List;
import java.util.Objects;

import io.fusionauth.domain.utils.ToString;

/**
 * @author Rob Davis
 */
public class SCIMListResponse implements SCIMResponse, Buildable<SCIMListResponse> {
  public List<Object> Resources;

  public int itemsPerPage;

  public String schema;

  public List<String> schemas;

  public int startIndex;

  public int totalResults;

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    SCIMListResponse that = (SCIMListResponse) o;
    return itemsPerPage == that.itemsPerPage &&
           startIndex == that.startIndex &&
           totalResults == that.totalResults &&
           Objects.equals(Resources, that.Resources) &&
           Objects.equals(schema, that.schema) &&
           Objects.equals(schemas, that.schemas);
  }

  @Override
  public int hashCode() {
    return Objects.hash(Resources, itemsPerPage, schema, schemas, startIndex, totalResults);
  }

  @Override
  public String toString() {
    return ToString.toString(this);
  }
}