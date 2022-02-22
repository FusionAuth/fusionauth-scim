package io.fusionauth.scim.domain.api;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class SCIMErrorResponse {
    public List<String> schemas =  new ArrayList<>(Collections.singletonList("urn:ietf:params:scim:api:messages:2.0:Error"));
    public String detail = "";
    public String scimType = null;
    public String status = "500";
}
