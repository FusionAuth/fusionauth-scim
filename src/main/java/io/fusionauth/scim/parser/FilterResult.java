package io.fusionauth.scim.parser;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.JsonNode;
import io.fusionauth.scim.domain.Buildable;
import io.fusionauth.scim.utils.ToString;

/**
 * @author Daniel DeGroff
 */
public class FilterResult implements Buildable<FilterResult> {
  public String attribute;

  public String attributeSchema;

  public String op;

  public String postfix;

  public String prefix;

  public String value;

  @Override
  public boolean equals(Object o) {
    if (this == o) {return true;}
    if (o == null || getClass() != o.getClass()) {return false;}
    FilterResult that = (FilterResult) o;
    return Objects.equals(attribute, that.attribute) &&
           Objects.equals(op, that.op) &&
           Objects.equals(value, that.value) &&
           Objects.equals(postfix, that.postfix) &&
           Objects.equals(prefix, that.prefix);
  }

  @JsonIgnore
  public String getPostfix() {
    if (postfix == null) {
      return "";
    }

    return "/" + postfix;
  }

  @JsonIgnore
  public String getPrefix() {
    if (prefix == null) {
      return "";
    }

    return "/" + prefix;
  }

  @Override
  public int hashCode() {
    return Objects.hash(attribute, op, value, postfix, prefix);
  }

  /**
   * <pre>
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
   * </pre>
   *
   * @param node the value to compare
   * @return true if it matches the condition
   */
  public boolean matches(JsonNode node) {
    if (!node.has(attribute)) {
      return false;
    }
    // TODO : See definitions: https://www.rfc-editor.org/rfc/rfc7644#section-3.4.2.2
    //        gt, ge, lt, le will need to apply to strings, dates, and integers I think.

    return switch (op) {
      case "eq" -> value.equals(node.asText());
      case "ne" -> !value.equals(node.asText());
      case "co" -> value.contains(node.asText());
      case "sw" -> value.startsWith(node.asText());
      case "ew" -> value.endsWith(node.asText());
      case "pr" -> true;
      case "gt" -> Long.parseLong(value) > node.asLong();
      case "ge" -> Long.parseLong(value) >= node.asLong();
      case "lt" -> Long.parseLong(value) < node.asLong();
      case "le" -> Long.parseLong(value) <= node.asLong();
      default -> false;
    };
  }

  @Override
  public String toString() {
    return ToString.toString(this);
  }
}
