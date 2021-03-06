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
 * Container for SCIM Meta data
 *
 * @author Brett Pontarelli
 */
public class SCIMMeta implements Buildable<SCIMMeta> {
  public String created;

  public String lastModified;

  public String location;

  public String resourceType;

  public String version;

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    SCIMMeta scimMeta = (SCIMMeta) o;
    return Objects.equals(created, scimMeta.created) &&
           Objects.equals(lastModified, scimMeta.lastModified) &&
           Objects.equals(location, scimMeta.location) &&
           Objects.equals(resourceType, scimMeta.resourceType) &&
           Objects.equals(version, scimMeta.version);
  }

  @Override
  public int hashCode() {
    return Objects.hash(created, lastModified, location, resourceType, version);
  }

  @Override
  public String toString() {
    return ToString.toString(this);
  }
}
