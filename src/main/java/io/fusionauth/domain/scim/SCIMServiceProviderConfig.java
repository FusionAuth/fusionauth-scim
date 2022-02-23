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
package io.fusionauth.domain.scim;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Rob Davis
 */
public class SCIMServiceProviderConfig extends BaseSCIMResource implements Buildable<SCIMServiceProviderConfig> {
  public ArrayList<Map<String, Object>> authenticationSchemes;

  // TODO : SCIM : Can we use Map.of instead of Stream.of(... etc ?
  // TODO : Don't these need to be configured or set by the SCIM server, should we be initializing them here?
  public Map<String, Object> bulk = Stream.of(new Object[][]{
      {"supported", false},
      {"maxOperations", 1_000},
      {"maxPayloadSize", 104_8576}
  }).collect(Collectors.toMap(data -> (String) data[0], data -> data[1]));

  public Map<String, Object> changePassword = Stream.of(new Object[][]{
      {"supported", false}
  }).collect(Collectors.toMap(data -> (String) data[0], data -> data[1]));

  public String documentationUri = "http://example.com/help/scim.html";

  public Map<String, Object> etag = Stream.of(new Object[][]{
      {"supported", false}
  }).collect(Collectors.toMap(data -> (String) data[0], data -> data[1]));

  public Map<String, Object> filter = Stream.of(new Object[][]{
      {"supported", false},
      {"maxResults", 200}
  }).collect(Collectors.toMap(data -> (String) data[0], data -> data[1]));

  public Map<String, Object> patch = Stream.of(new Object[][]{
      {"supported", false}
  }).collect(Collectors.toMap(data -> (String) data[0], data -> data[1]));

  public Map<String, Object> sort = Stream.of(new Object[][]{
      {"supported", false}
  }).collect(Collectors.toMap(data -> (String) data[0], data -> data[1]));

  public SCIMServiceProviderConfig() {
    schemas = new ArrayList<>(Collections.singletonList("urn:ietf:params:scim:schemas:core:2.0:ServiceProviderConfig"));
    SCIMMeta configMeta = new SCIMMeta();
    configMeta.location = "https://example.com/v2/ServiceProviderConfig";
    configMeta.resourceType = "ServiceProviderConfig";
    meta = configMeta;

    Map<String, Object> authenticationSchemeToken = Stream.of(new Object[][]{
        {"name", "OAuth Bearer Token"},
        {"description", "Authentication scheme using the OAuth Bearer Token Standard"},
        {"specUri", "http://www.rfc-editor.org/info/rfc6750"},
        {"documentationUri", "http://example.com/help/oauth.html"},
        {"type", "oauthbearertoken"},
        {"primary", true}
    }).collect(Collectors.toMap(data -> (String) data[0], data -> data[1]));
    authenticationSchemes = new ArrayList<>(Collections.emptyList());
    authenticationSchemes.add(authenticationSchemeToken);
  }
}
