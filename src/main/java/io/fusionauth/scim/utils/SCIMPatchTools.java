/*
 * Copyright (c) 2022-2024, FusionAuth, All Rights Reserved
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
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.IntNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import io.fusionauth.scim.domain.SCIMPatchOperation;
import io.fusionauth.scim.parser.ComparisonOperator;
import io.fusionauth.scim.parser.SCIMFilterParser;
import io.fusionauth.scim.parser.expression.AttributeTextComparisonExpression;
import io.fusionauth.scim.parser.expression.Expression;

/**
 * @author Daniel DeGroff
 */
public class SCIMPatchTools {
  private final static Pattern JSONPointerFilter = Pattern.compile("(.*)\\[(.*)](.*)");

  private final static Pattern SCIMPathPattern = Pattern.compile("^[/]?(.+:)?([^:]*)");

  private SCIMPatchTools() {
  }

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
      throws Exception {
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
      if (pathNode.isMissingNode() || pathNode.isNull()) {
        // For each element in the value object, build an op
        JsonNode value = operation.at("/value");
        if (!isAdd(operation) && value instanceof ObjectNode objectNode) {
          objectNode.fieldNames().forEachRemaining(field -> {
            ObjectNode copy = operation.deepCopy();
            copy.set("path", TextNode.valueOf("/" + field));
            copy.set("value", objectNode.get(field));
            result.add(copy);
          });
        } else {
          operation.set("path", TextNode.valueOf("/"));
          result.add(operation);
        }
      } else {
        // If we do have a path, and it contains a filter, replace it with an exact path.
        Matcher matcher = JSONPointerFilter.matcher(pathNode.asText());
        if (matcher.matches()) {

          String attrPathPointer = "/" + matcher.group(1).replace(".", "/");
          String valFilter = matcher.group(2);
          String subAttrPointer = matcher.group(3).replace(".", "/");

          Expression expression = new SCIMFilterParser().parse(valFilter);

          JsonNode attributeNode = source.at(attrPathPointer);
          if (attributeNode instanceof ArrayNode array) {
            for (int i = 0; i < array.size(); i++) {
              if (SCIMPatchFilterMatcher.matches(expression, array.get(i))) {
                // Make a copy since we may create more than one of these from the initial SCIM op
                // - Add a new op to the result for each matching node. It is plausible we'll match more than one node.
                ObjectNode copy = operation.deepCopy();
                copy.set("path", TextNode.valueOf(attrPathPointer + "/" + i + subAttrPointer));
                result.add(copy);
              }
            }
          }
        } else {
          String path = pathNode.asText();

          matcher = SCIMPathPattern.matcher(path);

          if (!matcher.matches()) {
            // This is a malformed path
            throw new IOException("Unable to parse op path: " + path);
          }

          String schema = matcher.group(1);
          String valueAndSubattrString = matcher.group(2);

          // Start the JSON path with a slash
          path = "/";

          // append the schema if present
          if (schema != null) {
            path += schema.substring(0, schema.length() - 1) + "/";
          }

          // add the attribute, replacing dots (which separate subattributes) with slashes (for nesting in a JSON object)
          path += valueAndSubattrString.replace(".", "/");

          // Ensure that if the target is an array we append to the end of the array.
          if (source.at(path).isArray() && !path.endsWith("/")) {

            if (isAdd(operation)) {
              path = path + "/-";
            }

            JsonNode value = operation.at("/value");
            if (value instanceof ArrayNode arrayNode) {
              if (isReplace(operation)) {
                operation.set("path", TextNode.valueOf(path));
                result.add(operation);
              } else {
                for (JsonNode n : arrayNode) {
                  ObjectNode copy = operation.deepCopy();
                  copy.set("path", TextNode.valueOf(path));
                  copy.set("value", n);

                  result.add(copy);
                }
              }
            }
          } else {
            operation.set("path", TextNode.valueOf(path));
            result.add(operation);
          }
        }
      }
    }

    // Take a second pass and check for remaining filter ops
    ArrayNode result2 = JsonNodeFactory.instance.arrayNode();
    List<ObjectNode> arrayRemoveOps = new ArrayList<>();

    for (JsonNode operation : result) {
      JsonNode value = operation.at("/value");
      if (isRemove(operation) && !value.isMissingNode()) {
        // This is essentially a filter
        JsonNode path = operation.at("/path");

        JsonNode attributeNode = source.at(path.asText());
        if (attributeNode instanceof ArrayNode array) {

          // Assume we have just a single value in the "value" node, assuming it will have to be an object to have a named key.
          String attributePath = null;
          if (value instanceof ObjectNode objectNode) {
            attributePath = objectNode.fieldNames().next();
          }

          if (attributePath == null) {
            continue;
          }

          String filterValue = value.get(attributePath).asText();

          for (int i = 0; i < array.size(); i++) {
            AttributeTextComparisonExpression expression = new AttributeTextComparisonExpression(attributePath, ComparisonOperator.eq, filterValue);
            if (SCIMPatchFilterMatcher.matches(expression, array.get(i))) {
              // Make a copy since we may create more than one of these from the initial SCIM op
              // - Add a new op to the result for each matching node. It is plausible we'll match more than one node.
              ObjectNode copy = operation.deepCopy();
              copy.set("path", TextNode.valueOf(path.asText() + "/" + i));
              // Temporarily set the value for later sorting and add to list
              copy.set("value", objectMapper.createArrayNode().add(TextNode.valueOf(path.asText())).add(IntNode.valueOf(i)));
              arrayRemoveOps.add(copy);
            }
          }
        }
      } else {
        result2.add(operation);
      }
    }

    // Sort array removal operations and add to the result
    if (!arrayRemoveOps.isEmpty()) {
      arrayRemoveOps.sort(
          (o1, o2) -> {
            // Sort by path and then descending index
            Integer path1 = o1.at("/value").get(0).asInt();
            Integer path2 = o2.at("/value").get(0).asInt();
            int pathComparison = path1.compareTo(path2);
            if (pathComparison != 0) {
              return pathComparison;
            }

            Integer index1 = o1.at("/value").get(1).asInt();
            Integer index2 = o2.at("/value").get(1).asInt();
            return index1.compareTo(index2) * -1;
          }
      );
      // Clear the value node after sorting
      arrayRemoveOps.forEach(o -> o.remove("value"));
    }

    result2.addAll(arrayRemoveOps);

    return result2;
  }

  private static boolean isAdd(JsonNode operation) {
    JsonNode opName = operation.at("/op");
    return "add".equalsIgnoreCase(opName.asText());
  }

  private static boolean isRemove(JsonNode operation) {
    JsonNode opName = operation.at("/op");
    return "remove".equalsIgnoreCase(opName.asText());
  }

  private static boolean isReplace(JsonNode operation) {
    JsonNode opName = operation.at("/op");
    return "replace".equalsIgnoreCase(opName.asText());
  }
}
