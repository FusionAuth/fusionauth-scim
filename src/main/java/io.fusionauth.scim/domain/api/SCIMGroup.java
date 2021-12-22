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

import java.util.List;
import java.util.Objects;

import com.inversoft.json.JacksonConstructor;

/**
 * Container for SCIM Group information.
 *
 * @author Brett Pontarelli
 */
public class SCIMGroup extends BaseSCIMResource {

  public String displayName;

  public List<SCIMMember> members;

  @JacksonConstructor
  public SCIMGroup() {
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    SCIMGroup scimGroup = (SCIMGroup) o;
    return Objects.equals(displayName, scimGroup.displayName) && Objects.equals(members, scimGroup.members);
  }

  @Override
  public int hashCode() {
    return Objects.hash(displayName, members);
  }
}
