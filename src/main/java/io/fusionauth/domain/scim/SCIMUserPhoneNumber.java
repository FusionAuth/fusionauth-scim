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

import java.util.Objects;

import io.fusionauth.domain.utils.ToString;

/**
 * @author Brett Pontarelli
 */
public class SCIMUserPhoneNumber implements Buildable<SCIMUserPhoneNumber> {
  public boolean primary;

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
    SCIMUserPhoneNumber that = (SCIMUserPhoneNumber) o;
    return primary == that.primary && Objects.equals(type, that.type) && Objects.equals(value, that.value);
  }

  @Override
  public int hashCode() {
    return Objects.hash(primary, type, value);
  }

  @Override
  public String toString() {
    return ToString.toString(this);
  }
}
