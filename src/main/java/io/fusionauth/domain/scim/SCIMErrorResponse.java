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

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import io.fusionauth.domain.utils.ToString;

/**
 * @author Rob Davis
 */
public class SCIMErrorResponse implements SCIMResponse, Buildable<SCIMErrorResponse> {
  public String detail;

  public List<String> schemas = Collections.singletonList(SCIMSchemas.Error);

  public String scimType;

  public String status;

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    SCIMErrorResponse that = (SCIMErrorResponse) o;
    return Objects.equals(detail, that.detail) &&
           Objects.equals(schemas, that.schemas) &&
           Objects.equals(scimType, that.scimType) &&
           Objects.equals(status, that.status);
  }

  @Override
  public int hashCode() {
    return Objects.hash(detail, schemas, scimType, status);
  }

  @Override
  public String toString() {
    return ToString.toString(this);
  }
}
