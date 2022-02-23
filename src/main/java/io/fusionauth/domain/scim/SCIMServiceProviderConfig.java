package io.fusionauth.domain.scim;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SCIMServiceProviderConfig extends BaseSCIMResource implements Buildable<SCIMServiceProviderConfig> {

  public ArrayList<Map<String, Object>> authenticationSchemes;

  public Map<String, Object> bulk = Stream.of(new Object[][]{
      {"supported", false},
      {"maxOperations", 1000},
      {"maxPayloadSize", 1048576}
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
