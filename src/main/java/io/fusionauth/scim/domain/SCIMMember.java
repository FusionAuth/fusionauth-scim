/*
 * Copyright (c) 2021-2022, FusionAuth, All Rights Reserved
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

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.fusionauth.scim.utils.ToString;

/**
 * Container for SCIM Member information
 *
 * @author Brett Pontarelli
 */
public class SCIMMember implements Buildable<SCIMMember> {
  public String display;

  @JsonProperty("$ref")
  public String ref;

  public String type;

  public String value;

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    SCIMMember that = (SCIMMember) o;
    return Objects.equals(display, that.display) &&
           Objects.equals(ref, that.ref) &&
           Objects.equals(type, that.type) &&
           Objects.equals(value, that.value);
  }

  @Override
  public int hashCode() {
    return Objects.hash(display, ref, type, value);
  }

  @Override
  public String toString() {
    return ToString.toString(this);
  }
}
