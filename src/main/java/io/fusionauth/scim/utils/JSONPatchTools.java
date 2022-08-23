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
package io.fusionauth.scim.utils;

import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import io.fusionauth.scim.domain.SCIMPatchOperation;

/**
 * @author Daniel DeGroff
 */
public class JSONPatchTools {
  private final static Pattern JSONPointerFilter = Pattern.compile(".*\\[(.*)].*");

  /**
   * Convert SCIM Patch Operations to be compatible with RFC 6902 JSON Patch (application/json+patch+json).
   * <p>
   * Note that it is possible that the returned number of operations is greater than the number provided. This is because a SCIM Patch operation may
   * contain a filter that could match more than one array element.
   *
   * @param objectMapper the Jackson Object Mapper
   * @param source       the source object to patch, this is necessary to resolve JSON pointers using SCIM filters.
   * @param operations   the SCIM Patch operations
   * @return an array of operations compatible with RFC 6902 JSON patch.
   * @throws IOException when something bad happens.
   */
  public static ArrayNode convertSCIMPatchToJSONPatch(ObjectMapper objectMapper, JsonNode source, List<SCIMPatchOperation> operations)
      throws IOException {
    ArrayNode result = JsonNodeFactory.instance.arrayNode();

    for (SCIMPatchOperation op : operations) {
      // Convert to a JsonNode so we can hack it. Not expecting any operations that are not objects.
      byte[] bytes = objectMapper.writeValueAsBytes(op);
      JsonNode node = objectMapper.readTree(bytes);
      if (!(node instanceof ObjectNode operation)) {
        continue;
      }

      // Path is required, add if it is missing.
      JsonNode pathNode = operation.at("/path");
      if (pathNode.isMissingNode()) {
        operation.set("path", TextNode.valueOf("/"));
        result.add(operation);
      } else {
        // If we do have a path, and it contains a filter, replace it with an exact path.
        Matcher matcher = JSONPointerFilter.matcher(pathNode.asText());
        if (matcher.matches()) {
          SCIMFilter filter = parseFilter(pathNode.asText(), matcher.group(1));
          // This essentially must be an array node otherwise the request is invalid.
          // emails[type sw \"w\"].value
          // emails/0/value
          // user.addresses[type = work].city
          JsonNode attributeNode = filter.pathToNode(source);
          if (attributeNode instanceof ArrayNode array) {
            for (int i = 0; i < array.size(); i++) {
              if (filter.matches(array.get(i))) {
                // Make a copy since we may create more than one of these from the initial SCIM op
                // - Add a new op to the result for each matching node. It is plausible we'll match more than one node.
                ObjectNode copy = operation.deepCopy();
                copy.set("path", TextNode.valueOf(filter.getPrefix() + "/" + i + filter.getPostfixAttribute()));
                result.add(copy);
              }
            }
          }
        } else {
          // Path must begin with a slash.
          String path = pathNode.asText();
          if (!path.startsWith("/")) {
            operation.set("path", TextNode.valueOf("/" + path));
          }
          result.add(operation);
        }
      }
    }

    return result;
  }

  private static SCIMFilter parseFilter(String path, String filter) {
    SCIMFilter result = new SCIMFilter();
    String[] parts = filter.split(" ");
    if (parts.length == 3) {
      result.filterAttribute = parts[0];
      result.filterOp = parts[1];
      result.filterValue = parts[2];
      if (result.filterValue.startsWith("\"")) {
        result.filterValue = result.filterValue.substring(1, result.filterValue.length() - 1);
      }
    }

    result.prefix = path.substring(0, path.indexOf(filter) - 1);
    result.postfixAttribute = path.substring(path.indexOf(filter) + filter.length() + 2);

    return result;
  }

  private static class SCIMFilter {
    public String filterAttribute;

    public String filterOp;

    public String filterValue;

    public String postfixAttribute;

    public String prefix;

    public String getPostfixAttribute() {
      if (postfixAttribute == null) {
        return "";
      }

      return "/" + postfixAttribute;
    }

    public String getPrefix() {
      if (prefix == null) {
        return "";
      }

      return "/" + prefix;
    }

    /**
     * eq: equal
     * ne: not equal
     * co: contains
     * sw: starts with
     * ew: ends with
     * pr: present (has value)
     * gt: greater than
     * ge: greater than or equal to
     * lt: less than
     * le: less than or equal to
     *
     * @param node the value to compare
     * @return true if it matches the condition
     */
    public boolean matches(JsonNode node) {
      if (!node.has(filterAttribute)) {
        return false;
      }

      return switch (filterOp) {
        case "eq" -> filterValue.equals(node.asText());
        case "ne" -> !filterValue.equals(node.asText());
        case "co" -> filterValue.contains(node.asText());
        case "sw" -> filterValue.startsWith(node.asText());
        case "ew" -> filterValue.endsWith(node.asText());
        case "pr" -> true;
        case "gt" -> Long.parseLong(filterValue) > node.asLong();
        case "ge" -> Long.parseLong(filterValue) >= node.asLong();
        case "lt" -> Long.parseLong(filterValue) < node.asLong();
        case "le" -> Long.parseLong(filterValue) <= node.asLong();
        default -> false;
      };
    }

    public JsonNode pathToNode(JsonNode node) {
      return node.at("/" + prefix.replace(".", "/"));
    }
  }
}
