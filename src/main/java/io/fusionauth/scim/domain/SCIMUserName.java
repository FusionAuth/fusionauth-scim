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

import java.util.Objects;

import io.fusionauth.scim.utils.ToString;

/**
 * @author Brett Pontarelli
 */
public class SCIMUserName implements Buildable<SCIMUserName> {
  public String familyName;

  public String formatted;

  public String givenName;

  public String honorificPrefix;

  public String honorificSuffix;

  public String middleName;

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    SCIMUserName that = (SCIMUserName) o;
    return Objects.equals(familyName, that.familyName) && Objects.equals(formatted, that.formatted) && Objects.equals(givenName, that.givenName) && Objects.equals(honorificPrefix, that.honorificPrefix) && Objects.equals(honorificSuffix, that.honorificSuffix) && Objects.equals(middleName, that.middleName);
  }

  @Override
  public int hashCode() {
    return Objects.hash(familyName, formatted, givenName, honorificPrefix, honorificSuffix, middleName);
  }

  @Override
  public String toString() {
    return ToString.toString(this);
  }
}
