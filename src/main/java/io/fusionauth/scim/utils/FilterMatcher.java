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

import java.math.BigDecimal;
import java.math.BigInteger;

import com.fasterxml.jackson.databind.JsonNode;
import io.fusionauth.scim.parser.Filter;
import io.fusionauth.scim.parser.InvalidFilterExpressionException;
import io.fusionauth.scim.parser.ValueType;

/**
 * @author Daniel DeGroff
 */
public class FilterMatcher {

  /**
   * Match a JsonNode against a Filter.
   *
   * @param filter the filter
   * @param node   the JSON node
   * @return true if the node matches the filter
   */
  public static boolean matches(Filter filter, JsonNode node) {
    if (!node.has(filter.attribute)) {
      return false;
    }

    JsonNode subAttribute = node.get(filter.attribute);
    return switch (filter.op) {
      case eq -> equal(filter, subAttribute);
      case ne -> !equal(filter, subAttribute);
      case co -> contains(filter, subAttribute);
      case sw -> startsWith(filter, subAttribute);
      case ew -> endsWith(filter, subAttribute);
      case pr -> true;
      case gt -> greaterThan(filter, subAttribute);
      case ge -> greaterThan(filter, subAttribute) || equal(filter, subAttribute);
      case lt -> lessThan(filter, subAttribute);
      case le -> lessThan(filter, subAttribute) || equal(filter, subAttribute);
    };
  }

  private static boolean contains(Filter filter, JsonNode attribute) {
    if (filter.valueType != ValueType.text) {
      return false;
    }

    return attribute.asText().contains(filter.value);
  }

  private static boolean endsWith(Filter filter, JsonNode attribute) {
    if (filter.valueType != ValueType.text) {
      return false;
    }

    return attribute.asText().endsWith(filter.value);
  }

  private static boolean equal(Filter filter, JsonNode attribute) {
    String value = filter.value;
    ValueType valueType = filter.valueType;

    if (valueType == ValueType.text) {
      return filter.value.equals(attribute.asText());
    } else if (valueType == ValueType.number) {
      if (attribute.isBigInteger()) {
        return new BigInteger(value).equals(BigInteger.valueOf(attribute.asLong()));
      } else if (attribute.isBigDecimal()) {
        return new BigDecimal(value).equals(BigDecimal.valueOf(attribute.asDouble()));
      }
      return false;
    } else if (valueType == ValueType.bool) {
      return Boolean.parseBoolean(value) == attribute.asBoolean();
    } else if (valueType == ValueType.date) {
      return filter.value.equals(attribute.asText());
    } else if (valueType == ValueType.nul) {
      return (value == null || value.equals("null")) && attribute.isNull();
    }

    return false;
  }

  private static boolean greaterThan(Filter filter, JsonNode attribute) {
    String value = filter.value;
    ValueType valueType = filter.valueType;

    if (valueType == ValueType.text) {
      return filter.value.compareTo(attribute.asText()) > 0;
    } else if (valueType == ValueType.number) {
      if (attribute.isBigInteger()) {
        return new BigInteger(value).compareTo(BigInteger.valueOf(attribute.asLong())) > 0;
      } else if (attribute.isBigDecimal()) {
        return new BigDecimal(value).compareTo(BigDecimal.valueOf(attribute.asDouble())) > 0;
      }

      // TODO : What else could this be?
      return false;
    } else if (valueType == ValueType.bool) {
      throw new InvalidFilterExpressionException("The gt or ge operator cannot be used with a boolean type value.");
    } else if (valueType == ValueType.date) {
      // TODO : Needs a chronological comparison
      return filter.value.equals(attribute.asText());
    } else if (valueType == ValueType.nul) {
      // TODO : Is this correct
      throw new InvalidFilterExpressionException("The gt or ge operator cannot be used with a null type value.");
    }
    return false;
  }

  private static boolean lessThan(Filter filter, JsonNode attribute) {
    String value = filter.value;
    ValueType valueType = filter.valueType;

    if (valueType == ValueType.text) {
      return filter.value.compareTo(attribute.asText()) < 0;
    } else if (valueType == ValueType.number) {
      if (attribute.isBigInteger()) {
        return new BigInteger(value).compareTo(BigInteger.valueOf(attribute.asLong())) < 0;
      } else if (attribute.isBigDecimal()) {
        return new BigDecimal(value).compareTo(BigDecimal.valueOf(attribute.asDouble())) < 0;
      }

      // TODO : What else could this be?
      return false;
    } else if (valueType == ValueType.bool) {
      throw new InvalidFilterExpressionException("The lt or le operator cannot be used with a boolean type value.");
    } else if (valueType == ValueType.date) {
      // TODO : Needs a chronological comparison
      return filter.value.equals(attribute.asText());
    } else if (valueType == ValueType.nul) {
      // TODO : Is this correct
      throw new InvalidFilterExpressionException("The lt or le operator cannot be used with a null type value.");
    }
    return false;
  }

  private static boolean startsWith(Filter filter, JsonNode attribute) {
    if (filter.valueType != ValueType.text) {
      return false;
    }

    return attribute.asText().startsWith(filter.value);
  }
}
