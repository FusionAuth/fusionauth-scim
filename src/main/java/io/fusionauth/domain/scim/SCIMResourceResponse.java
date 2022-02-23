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
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.fusionauth.domain.utils.ToString;

/**
 * @author Rob Davis
 */
public class SCIMResourceResponse implements Buildable<SCIMResourceResponse> {
  public List<HashMap<String, Object>> emails;

  @JsonProperty("urn:ietf:params:scim:schemas:extension:enterprise:2.0:User")
  public HashMap<String, Object> enterpriseUserExtension;

  public String externalId;

  public UUID id;

  public HashMap<String, String> meta;

  public HashMap<String, String> name;

  public List<HashMap<String, String>> phoneNumbers;

  public List<String> schemas;

  public String userName;

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    SCIMResourceResponse that = (SCIMResourceResponse) o;
    return Objects.equals(id, that.id) && Objects.equals(schemas, that.schemas) && Objects.equals(externalId, that.externalId) && Objects.equals(meta, that.meta) && Objects.equals(name, that.name) && Objects.equals(userName, that.userName) && Objects.equals(phoneNumbers, that.phoneNumbers) && Objects.equals(emails, that.emails) && Objects.equals(enterpriseUserExtension, that.enterpriseUserExtension);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, schemas, externalId, meta, name, userName, phoneNumbers, emails, enterpriseUserExtension);
  }

  @Override
  public String toString() {
    return ToString.toString(this);
  }
}
