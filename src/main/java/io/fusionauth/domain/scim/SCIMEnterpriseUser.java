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
import java.util.Map;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.fusionauth.domain.utils.ToString;

/**
 * Container for SCIM Enterprise User information.
 *
 * @author Brett Pontarelli
 */
public class SCIMEnterpriseUser extends BaseSCIMUser<SCIMEnterpriseUser> {
  @JsonProperty(SCIMSchemas.EnterpriseUser)
  public SCIMEnterpriseSchemaExtension extension;

  private Map<String, Object> extensions = new HashMap<>();

  @JsonAnyGetter
  public Map<String, Object> any() {
    return extensions;
  }

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
    SCIMEnterpriseUser that = (SCIMEnterpriseUser) o;
    return Objects.equals(extension, that.extension) && Objects.equals(extensions, that.extensions);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), extension, extensions);
  }

  @JsonAnySetter
  public void set(String name, Object value) {
    extensions.put(name, value);
  }

  public void setExtensions(Map<String, Object> newExtensions) {
    extensions = newExtensions;
  }

  @Override
  public String toString() {
    return ToString.toString(this);
  }
}
