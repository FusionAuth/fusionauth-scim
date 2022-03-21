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

import java.net.URI;
import java.security.cert.X509Certificate;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.fusionauth.domain.utils.ToString;

/**
 * Container for SCIM User information.
 *
 * @author Brett Pontarelli
 */
public class BaseSCIMUser<T extends BaseSCIMUser<T>> extends BaseSCIMResource<T> {
  public boolean active;

  public List<SCIMUserAddress> addresses;

  public String displayName;

  public List<SCIMUserEmail> emails;

  public List<String> entitlements;

  public List<SCIMGroup> groups;

  public List<SCIMUserIMS> ims;

  public String locale;

  public SCIMUserName name;

  public String nickName;

  public String password;

  public List<SCIMUserPhoneNumber> phoneNumbers;

  public List<SCIMUserPhoto> photos;

  public String preferredLanguage;

  @JsonProperty("profileUrl")
  public URI profileURL;

  public List<String> roles;

  public ZoneId timezone;

  public URI title;

  public String userName;

  public URI userType;

  public List<X509Certificate> x509Certificates;

  private Map<String, Object> extensions = new HashMap<>();

  @JsonAnyGetter
  public Map<String, Object> any() {
    return extensions;
  }

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
    BaseSCIMUser<?> that = (BaseSCIMUser<?>) o;
    return active == that.active && Objects.equals(displayName, that.displayName) && Objects.equals(emails, that.emails) && Objects.equals(groups, that.groups) && Objects.equals(name, that.name) && Objects.equals(phoneNumbers, that.phoneNumbers) && Objects.equals(photos, that.photos) && Objects.equals(preferredLanguage, that.preferredLanguage) && Objects.equals(timezone, that.timezone) && Objects.equals(userName, that.userName);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), active, displayName, emails, groups, name, phoneNumbers, photos, preferredLanguage, timezone, userName);
  }

  @JsonAnySetter
  public void set(String name, Object value) {
    extensions.put(name, value);
  }

  public void setExtensions(Map<String, Object> newExtensions) {
    extensions = newExtensions;
  }

  @Override
  public String toString() {
    return ToString.toString(this);
  }
}
