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

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;

import com.fasterxml.jackson.databind.JsonNode;
import io.fusionauth.scim.parser.ComparisonOperator;
import io.fusionauth.scim.parser.InvalidFilterExpressionException;
import io.fusionauth.scim.parser.ValueType;
import io.fusionauth.scim.parser.expression.AttributeBooleanComparisonExpression;
import io.fusionauth.scim.parser.expression.AttributeComparisonExpression;
import io.fusionauth.scim.parser.expression.AttributeDateComparisonExpression;
import io.fusionauth.scim.parser.expression.AttributeExpression;
import io.fusionauth.scim.parser.expression.AttributeNullTestExpression;
import io.fusionauth.scim.parser.expression.AttributeNumberComparisonExpression;
import io.fusionauth.scim.parser.expression.AttributePresentTestExpression;
import io.fusionauth.scim.parser.expression.AttributeTextComparisonExpression;
import io.fusionauth.scim.parser.expression.Expression;

/**
 * @author Daniel DeGroff
 */
public class SCIMPatchFilterMatcher {
  public static final DateTimeFormatter SCIMDateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

  /**
   * Match a JsonNode against a Filter.
   *
   * @param expression the filter
   * @param node       the JSON node
   * @return true if the node matches the filter
   */
  public static boolean matches(Expression expression, JsonNode node) {
    if (expression instanceof AttributePresentTestExpression present) {
      String attributePath = "/" + present.attributePath.replace(".", "/");
      return !node.at(attributePath).isMissingNode();
    }

    if (expression instanceof AttributeNullTestExpression nullTest) {
      // null can only be used with equal and not equal operations, but the parser will have failed, so just assume it is correct.
      //noinspection SimplifiableConditionalExpression
      return nullTest.operator == ComparisonOperator.eq
          ? node.isNull()
          : !node.isNull();
    }

    if (expression instanceof AttributeComparisonExpression attributeExpression) {
      ComparisonOperator operator = attributeExpression.operator;
      JsonNode subAttribute = node.at("/" + attributeExpression.attributePath.replace(".", "/"));

      return switch (operator) {
        case eq -> equal(attributeExpression, subAttribute);
        case ne -> !equal(attributeExpression, subAttribute);
        case co -> contains(attributeExpression, subAttribute);
        case sw -> startsWith(attributeExpression, subAttribute);
        case ew -> endsWith(attributeExpression, subAttribute);
        case pr -> true;
        case gt -> greaterThan(attributeExpression, subAttribute);
        case ge -> greaterThan(attributeExpression, subAttribute) || equal(attributeExpression, subAttribute);
        case lt -> lessThan(attributeExpression, subAttribute);
        case le -> lessThan(attributeExpression, subAttribute) || equal(attributeExpression, subAttribute);
      };
    }

    return false;
  }

  private static boolean contains(AttributeComparisonExpression filter, JsonNode attribute) {
    if (filter.valueType() != ValueType.text) {
      return false;
    }

    AttributeTextComparisonExpression textComparisonExpression = (AttributeTextComparisonExpression) filter;
    String value = textComparisonExpression.value();
    return attribute.asText().contains(value);
  }

  private static boolean endsWith(AttributeComparisonExpression filter, JsonNode attribute) {
    if (filter.valueType() != ValueType.text) {
      return false;
    }

    AttributeTextComparisonExpression textComparisonExpression = (AttributeTextComparisonExpression) filter;
    String value = textComparisonExpression.value();
    return attribute.asText().endsWith(value);
  }

  private static boolean equal(AttributeComparisonExpression expression, JsonNode attribute) {
    ValueType valueType = expression.valueType();

    if (valueType == ValueType.text) {
      AttributeTextComparisonExpression text = (AttributeTextComparisonExpression) expression;
      return text.value().equals(attribute.asText());
    } else if (valueType == ValueType.number) {
      AttributeNumberComparisonExpression number = (AttributeNumberComparisonExpression) expression;
      if (attribute.isBigInteger() || attribute.isLong() || attribute.isInt()) {
        return number.value().toBigInteger().equals(attribute.bigIntegerValue());
      }

      // Assume some sort of decimal
      return number.value().equals(attribute.decimalValue());
    } else if (valueType == ValueType.bool) {
      AttributeBooleanComparisonExpression bool = (AttributeBooleanComparisonExpression) expression;
      return bool.value() == attribute.asBoolean();
    } else if (valueType == ValueType.date) {
      AttributeDateComparisonExpression date = (AttributeDateComparisonExpression) expression;
      String value = SCIMDateFormatter.format(date.value());
      return value.equals(attribute.asText());
    }

    return false;
  }

  private static boolean greaterThan(AttributeComparisonExpression expression, JsonNode attribute) {
    ValueType valueType = expression.valueType();

    if (valueType == ValueType.text) {
      AttributeTextComparisonExpression text = (AttributeTextComparisonExpression) expression;
      return text.value().compareTo(attribute.asText()) > 0;
    } else if (valueType == ValueType.number) {
      AttributeNumberComparisonExpression number = (AttributeNumberComparisonExpression) expression;
      if (attribute.isBigInteger() || attribute.isLong() || attribute.isInt()) {
        return attribute.bigIntegerValue().compareTo(number.value().toBigInteger()) > 0;
      }

      // Assume some sort of decimal
      return attribute.decimalValue().compareTo(number.value()) > 0;
    } else if (valueType == ValueType.bool) {
      throw new InvalidFilterExpressionException("The gt or ge operator cannot be used with a boolean type value.");
    } else if (valueType == ValueType.date) {
      AttributeDateComparisonExpression date = (AttributeDateComparisonExpression) expression;
      TemporalAccessor result = SCIMDateFormatter.parse(attribute.asText());
      ZonedDateTime actual = ZonedDateTime.from(result);
      return actual.isAfter(date.value());
    }
    return false;
  }

  private static boolean lessThan(AttributeExpression expression, JsonNode attribute) {
    ValueType valueType = expression.valueType();

    if (valueType == ValueType.text) {
      AttributeTextComparisonExpression text = (AttributeTextComparisonExpression) expression;
      return text.value().compareTo(attribute.asText()) < 0;
    } else if (valueType == ValueType.number) {
      AttributeNumberComparisonExpression number = (AttributeNumberComparisonExpression) expression;
      if (attribute.isBigInteger() || attribute.isLong() || attribute.isInt()) {
        return attribute.bigIntegerValue().compareTo(number.value().toBigInteger()) < 0;
      }

      // Assume some sort of decimal
      return attribute.decimalValue().compareTo(number.value()) < 0;
    } else if (valueType == ValueType.bool) {
      throw new InvalidFilterExpressionException("The lt or le operator cannot be used with a boolean type value.");
    } else if (valueType == ValueType.date) {
      AttributeDateComparisonExpression date = (AttributeDateComparisonExpression) expression;
      TemporalAccessor result = SCIMDateFormatter.parse(attribute.asText());
      ZonedDateTime actual = ZonedDateTime.from(result);
      return actual.isBefore(date.value());
    }
    return false;
  }

  private static boolean startsWith(AttributeExpression filter, JsonNode attribute) {
    if (filter.valueType() != ValueType.text) {
      return false;
    }

    AttributeTextComparisonExpression textComparisonExpression = (AttributeTextComparisonExpression) filter;
    String value = textComparisonExpression.value();
    return attribute.asText().startsWith(value);
  }
}
