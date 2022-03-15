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
import java.util.Map;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.fusionauth.domain.utils.ToString;

/**
 * @author Rob Davis
 */
public class SCIMServiceProviderConfig extends BaseSCIMResource<SCIMServiceProviderConfig> implements Buildable<SCIMServiceProviderConfig> {
  public List<Map<String, Object>> authenticationSchemes;

  public Map<String, Object> bulk;

  public Map<String, Object> changePassword;

  @JsonProperty("documentationUri")
  public String documentationURI;

  public Map<String, Object> etag;

  public Map<String, Object> filter;

  public Map<String, Object> patch;

  public Map<String, Object> sort;

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
    SCIMServiceProviderConfig that = (SCIMServiceProviderConfig) o;
    return Objects.equals(authenticationSchemes, that.authenticationSchemes) &&
           Objects.equals(bulk, that.bulk) &&
           Objects.equals(changePassword, that.changePassword) &&
           Objects.equals(documentationURI, that.documentationURI) &&
           Objects.equals(etag, that.etag) &&
           Objects.equals(filter, that.filter) &&
           Objects.equals(patch, that.patch) &&
           Objects.equals(sort, that.sort);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), authenticationSchemes, bulk, changePassword, documentationURI, etag, filter, patch, sort);
  }

  @Override
  public String toString() {
    return ToString.toString(this);
  }
}
