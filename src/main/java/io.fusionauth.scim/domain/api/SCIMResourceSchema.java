package io.fusionauth.scim.domain.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.inversoft.json.JacksonConstructor;

import java.util.List;

public class SCIMResourceSchema extends BaseSCIMResource {

    @JsonProperty("id")
    public String schemaId;
    public String name;
    public String description;
    public List<SCIMResourceSchemaAttribute> attributes;

    @JacksonConstructor
    public SCIMResourceSchema() {
    }
}
