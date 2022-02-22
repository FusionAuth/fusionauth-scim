package io.fusionauth.scim.domain.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.inversoft.json.JacksonConstructor;

import java.util.List;

// TODO : SCIM : Do we need this?
public class SCIMResourceSchema extends BaseSCIMResource {
  @JsonProperty("id")
  public String schemaId;

  public String description;

  public String name;

  public List<SCIMResourceSchemaAttribute> attributes;

  // TODO : SCIM : Not necessary, can be deleted
  @JacksonConstructor
  public SCIMResourceSchema() {
  }
}
