package io.fusionauth.scim.domain.api;

import com.inversoft.json.JacksonConstructor;

import java.util.List;

public class SCIMResourceSchemaAttribute {
  public String name;

  public String type;

  public boolean multiValued;

  public String description;

  public boolean required;

  public boolean caseExact;

  public String mutability;

  public String returned;

  public String uniqueness;

  public List<SCIMResourceSchemaAttribute> subAttributes;

  @JacksonConstructor
  public SCIMResourceSchemaAttribute() {
  }

  public SCIMResourceSchemaAttribute(String name,
                                     String type,
                                     boolean multiValued,
                                     String description,
                                     boolean required,
                                     boolean caseExact,
                                     String mutability,
                                     String returned,
                                     String uniqueness,
                                     List<SCIMResourceSchemaAttribute> subAttributes) {
    this.name = name;
    this.type = type;
    this.multiValued = multiValued;
    this.description = description;
    this.required = required;
    this.caseExact = caseExact;
    this.mutability = mutability;
    this.returned = returned;
    this.uniqueness = uniqueness;
    this.subAttributes = subAttributes;
  }
}
