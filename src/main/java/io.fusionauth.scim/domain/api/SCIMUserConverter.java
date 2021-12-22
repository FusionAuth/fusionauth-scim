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

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import io.fusionauth.domain.User;

public class SCIMUserConverter {

  public static User fromSCIMUser(SCIMUser source) {
    User target = new User();
    // Build a valid User using the SCIMUser data.
    target.active = source.active;
    target.id = source.id;
    target.username = source.userName;
    target.fullName = source.name.formatted;
    target.lastName = source.name.familyName;
    target.firstName = source.name.givenName;
    target.middleName = source.name.middleName;
    if(source.phoneNumbers != null) {
      target.mobilePhone = source.phoneNumbers.stream()
                                              .filter(p -> p.type.equalsIgnoreCase("mobile"))
                                              .findFirst()
                                              .map(p -> p.value)
                                              .orElse("");
    }
    if(source.emails != null) {
      target.email = source.emails.stream()
                                  .filter(e -> e.primary)
                                  .findFirst()
                                  .map(e -> e.value)
                                  .orElse("");
    }
    return target;
  }

  public static SCIMUser toSCIMUser(User source) {
    SCIMUser target = new SCIMUser();
      // Build a valid ScimUser using the FusionAuth User data.
      target.active = source.active;
      target.id = source.id;
      target.externalId = source.username;
      target.userName = source.username;
      target.schemas = new ArrayList<>();
      target.schemas.add("urn:ietf:params:scim:schemas:core:2.0:User");
      SCIMMeta meta = new SCIMMeta();
      meta.resourceType = "User";
      meta.created = source.insertInstant.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"));
      meta.lastModified =  source.lastUpdateInstant.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"));
      meta.location = "https://fusionauth.io/api/scim/resource/v2/Users/" + source.id;
      meta.version = "";
      target.meta = meta;
      SCIMUserName name = new SCIMUserName();
      name.formatted = source.fullName;
      name.familyName = source.lastName;
      name.givenName = source.firstName;
      name.middleName = source.middleName;
      name.honorificPrefix = "";
      name.honorificSuffix = "";
      target.name = name;
      SCIMUserPhoneNumber phoneNumber = new SCIMUserPhoneNumber();
      phoneNumber.primary = true;
      phoneNumber.value = source.mobilePhone;
      phoneNumber.type = "mobile";
      target.phoneNumbers = new ArrayList<>();
      target.phoneNumbers.add(phoneNumber);
      SCIMUserEmail email = new SCIMUserEmail();
      email.value = source.email;
      email.type ="work";
      email.primary = true;
      target.emails = new ArrayList<>();
      target.emails.add(email);
      return target;
  }
}
