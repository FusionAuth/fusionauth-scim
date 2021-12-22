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

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import com.inversoft.json.JacksonConstructor;

public class SCIMServiceProviderConfigResponse {
  public UUID id;
  public List<String> schemas;
  public String externalId;
  public HashMap<String, String> meta;
  public String documentationUri;
  public HashMap<String, Object> patch;
  public HashMap<String, Object> bulk;
  public HashMap<String, Object> filter;
  public HashMap<String, Object> changePassword;
  public HashMap<String, Object> sort;
  public HashMap<String, Object> etag;
  public List<HashMap<String, Object>> authenticationSchemes;

  @JacksonConstructor
  public SCIMServiceProviderConfigResponse() {
  }
}
