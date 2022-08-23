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

  public String filterAttribute;

  public String filterOp;

  public String filterValue;

  public String postfix;

  public String prefix;

  @Override
  public boolean equals(Object o) {
    if (this == o) {return true;}
    if (o == null || getClass() != o.getClass()) {return false;}
    FilterResult that = (FilterResult) o;
    return Objects.equals(filterAttribute, that.filterAttribute) && Objects.equals(filterOp, that.filterOp) && Objects.equals(filterValue,
        that.filterValue) && Objects.equals(postfix, that.postfix) && Objects.equals(prefix, that.prefix);
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
    return Objects.hash(filterAttribute, filterOp, filterValue, postfix, prefix);
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

  @Override
  public String toString() {
    return ToString.toString(this);
  }
}
