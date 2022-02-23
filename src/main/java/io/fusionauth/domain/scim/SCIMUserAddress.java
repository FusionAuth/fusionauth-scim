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
public class SCIMUserAddress implements Buildable<SCIMUserAddress> {
  public String country;

  public String formatted;

  public String locality;

  public String postalCode;

  public boolean primary;  // Not exactly in spec?, but in the example?

  public String region;

  public String streetAddress;

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    SCIMUserAddress that = (SCIMUserAddress) o;
    return primary == that.primary && Objects.equals(country, that.country) && Objects.equals(formatted, that.formatted) && Objects.equals(locality, that.locality) && Objects.equals(postalCode, that.postalCode) && Objects.equals(region, that.region) && Objects.equals(streetAddress, that.streetAddress);
  }

  @Override
  public int hashCode() {
    return Objects.hash(country, formatted, locality, postalCode, primary, region, streetAddress);
  }

  @Override
  public String toString() {
    return ToString.toString(this);
  }
}
