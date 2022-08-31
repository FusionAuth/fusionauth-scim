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
import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.fusionauth.scim.parser.ComparisonOperator;
import io.fusionauth.scim.parser.expression.AttributeExpression;
import io.fusionauth.scim.parser.expression.AttributeNumberComparisonExpression;
import io.fusionauth.scim.parser.expression.AttributePresentTestExpression;
import io.fusionauth.scim.parser.expression.AttributeTextComparisonExpression;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import static org.testng.FileAssert.fail;

/**
 * @author Daniel DeGroff
 */
public class SCIMPatchFilterMatcherTest {
  private final ObjectMapper objectMapper = new ObjectMapper().setSerializationInclusion(Include.NON_NULL);

  private JsonNode source;

  @BeforeTest
  public void beforeTest() throws IOException {
    String json = """
        {
          "a": "b",
          "b": {
            "c": "d"
          },
          "c": 42,
          "d": {
            "e": 42,
            "f": 42.42,
            "g": 42e13,
            "h": 42e-1,
            "i": 1.00000000000009
          }
        }
        """;
    source = objectMapper.readTree(json);
  }

  @Test
  public void equal() {
    // -----------------------------------------------------------------------------------------------------------------------
    // Text
    // -----------------------------------------------------------------------------------------------------------------------

    // a eq b
    matches(new AttributeTextComparisonExpression("a", ComparisonOperator.eq, "b"));

    // b.c eq d
    matches(new AttributeTextComparisonExpression("b.c", ComparisonOperator.eq, "d"));

    // a eq c
    noMatch(new AttributeTextComparisonExpression("a", ComparisonOperator.eq, "c"));

    // b.c eq a
    noMatch(new AttributeTextComparisonExpression("b.c", ComparisonOperator.eq, "a"));

    // -----------------------------------------------------------------------------------------------------------------------
    // Numbers
    // -----------------------------------------------------------------------------------------------------------------------

    // c eq 42
    matches(new AttributeNumberComparisonExpression("c", ComparisonOperator.eq, new BigDecimal(42)));

    // d.e eq 42
    matches(new AttributeNumberComparisonExpression("d.e", ComparisonOperator.eq, new BigDecimal((42))));

    // d.f eq 42.42
    matches(new AttributeNumberComparisonExpression("d.f", ComparisonOperator.eq, new BigDecimal("42.42")));

    // d.g eq 42e13
    matches(new AttributeNumberComparisonExpression("d.g", ComparisonOperator.eq, new BigDecimal("42e13")));

    // d.h eq 42e-1
    matches(new AttributeNumberComparisonExpression("d.h", ComparisonOperator.eq, new BigDecimal("42e-1")));

    // d.i eq 1.00000000000009
    matches(new AttributeNumberComparisonExpression("d.i", ComparisonOperator.eq, new BigDecimal("1.00000000000009")));

  }

  @Test
  public void lessThan() {
    // -----------------------------------------------------------------------------------------------------------------------
    // Text
    // -----------------------------------------------------------------------------------------------------------------------

    // a lt a
    matches(new AttributeTextComparisonExpression("a", ComparisonOperator.lt, "a"));

    // b.c lt c
    matches(new AttributeTextComparisonExpression("b.c", ComparisonOperator.lt, "c"));

    // b.c lt d|e
    noMatch(new AttributeTextComparisonExpression("b.c", ComparisonOperator.lt, "d"));
    noMatch(new AttributeTextComparisonExpression("b.c", ComparisonOperator.lt, "e"));

    // -----------------------------------------------------------------------------------------------------------------------
    // Numbers
    // -----------------------------------------------------------------------------------------------------------------------

    // c lt 43
    matches(new AttributeNumberComparisonExpression("c", ComparisonOperator.lt, new BigDecimal(43)));

    // d.e lt 43
    matches(new AttributeNumberComparisonExpression("d.e", ComparisonOperator.lt, new BigDecimal(43)));

    // d.f lt 42.43
    matches(new AttributeNumberComparisonExpression("d.f", ComparisonOperator.lt, new BigDecimal("42.43")));

    // d.i lt 1.0000000000001
    matches(new AttributeNumberComparisonExpression("d.i", ComparisonOperator.lt, new BigDecimal("1.0000000000001")));

    // d.f lt 42.42|42.41
    noMatch(new AttributeNumberComparisonExpression("d.f", ComparisonOperator.lt, new BigDecimal("42.42")));
    noMatch(new AttributeNumberComparisonExpression("d.f", ComparisonOperator.lt, new BigDecimal("42.41")));

    // d.i lt 1.00000000000009|1.00000000000008
    noMatch(new AttributeNumberComparisonExpression("d.i", ComparisonOperator.lt, new BigDecimal("1.00000000000009")));
    noMatch(new AttributeNumberComparisonExpression("d.i", ComparisonOperator.lt, new BigDecimal("1.00000000000008")));
  }

  @Test
  public void present() throws Exception {
    // a pr
    matches(new AttributePresentTestExpression("a"));

    // b pr
    matches(new AttributePresentTestExpression("b"));

    // b.c pr
    matches(new AttributePresentTestExpression("b.c"));

    // z pr
    noMatch(new AttributePresentTestExpression("z"));

    // b.d pr
    noMatch(new AttributePresentTestExpression("b.d"));
  }

  private void matches(AttributeExpression expression) {
    boolean result = SCIMPatchFilterMatcher.matches(expression, source);

    if (!result) {
      fail("Expected a match.");
    }
  }

  private void noMatch(AttributeExpression expression) {
    boolean result = SCIMPatchFilterMatcher.matches(expression, source);

    if (result) {
      fail("Did not expected a match.");
    }
  }
}
