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
package io.fusionauth.scim.domain;

import com.fasterxml.jackson.annotation.JsonCreator;

/**
 * @author Daniel DeGroff
 */
public enum SCIMPatchOperationName {
  add,
  remove,
  replace;

  /**
   * Accept a case-insensitive version of the 'op' during deserialization.
   * <p>
   * <a href="https://datatracker.ietf.org/doc/html/rfc7644#section-3.5.2">RFC 7644 Section 3.5.2</a> only mentions 'add', 'remove' and 'replace' and
   * does not provide any indication that mixed case versions are allowed.
   * <p>
   * However, Azure AD uses 'Add', 'Remove' and 'Replace'... because they don't play well with others.
   *
   * @param name the op name
   * @return the enum value or null if the op name is invalid.
   */
  @JsonCreator
  public static SCIMPatchOperationName safeValueOf(String name) {
    String lc = name.toLowerCase();
    try {
      return SCIMPatchOperationName.valueOf(lc);
    } catch (Exception ignore) {
    }

    return null;
  }
}
