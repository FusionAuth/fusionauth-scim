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
package io.fusionauth.scim.domain.api;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import com.inversoft.json.JacksonConstructor;

/**
 * Base class for all SCIM domain objects.
 *
 * @author Brett Pontarelli
 */
public abstract class BaseSCIMResource {

  public String externalId;

  public UUID id;

  public SCIMMeta meta = new SCIMMeta();

  public List<String> schemas;

  @JacksonConstructor
  public BaseSCIMResource() {
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    BaseSCIMResource that = (BaseSCIMResource) o;
    return Objects.equals(externalId, that.externalId) && Objects.equals(id, that.id) && Objects.equals(meta, that.meta) && Objects.equals(schemas, that.schemas);
  }

  @Override
  public int hashCode() {
    return Objects.hash(externalId, id, meta, schemas);
  }

  // [brettp]Note: This method should go away, but since it will happen for every resource
  //   it might make sense to have a BaseConverter to populate meta?
  //   Note also, I'm still stuck :shrug: on how to pass down the `location` since the converter in
  //   the direction `User -> ScimUser` for example needs to take `baseUrl` or something as a param.
  protected void createMeta(UUID id, String resource, ZonedDateTime created, ZonedDateTime lastModified) {
//    meta.resourceType = resourceType;
//    meta.created = created;
//    meta.lastModified = lastModified;
//    meta.location = "https://basescim.url/scim/v2/" + resourceType + "/" + id;
  }

}
