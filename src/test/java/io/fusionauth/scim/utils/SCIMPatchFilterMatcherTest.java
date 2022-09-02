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
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.fusionauth.scim.parser.ComparisonOperator;
import io.fusionauth.scim.parser.expression.AttributeDateComparisonExpression;
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
          "a": "bbb",
          "b": {
            "b1": "ddd"
          },
          "c": 42,
          "d": {
            "d1": 42,
            "d2": 42.42,
            "d3": 42e13,
            "d4": 42e-1,
            "d5": 1.00000000000009
          },
          "e": "2022-09-02T15:14:45.061Z",
          "f": {
            "f1": "2022-09-02T15:14:44Z",
            "f2": "2022-09-02T15:14:45Z",
            "f3": "2022-09-02T15:14:46Z"
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
    matches(new AttributeTextComparisonExpression("a", ComparisonOperator.eq, "bbb"));

    // b.b1 eq d
    matches(new AttributeTextComparisonExpression("b.b1", ComparisonOperator.eq, "ddd"));

    // a eq c
    noMatch(new AttributeTextComparisonExpression("a", ComparisonOperator.eq, "ccc"));

    // b.b1 eq a
    noMatch(new AttributeTextComparisonExpression("b.b1", ComparisonOperator.eq, "aaaa"));

    // -----------------------------------------------------------------------------------------------------------------------
    // Numbers
    // -----------------------------------------------------------------------------------------------------------------------

    // c eq 42
    matches(new AttributeNumberComparisonExpression("c", ComparisonOperator.eq, new BigDecimal(42)));

    // d.d1 eq 42
    matches(new AttributeNumberComparisonExpression("d.d1", ComparisonOperator.eq, new BigDecimal((42))));

    // d.d2 eq 42.42
    matches(new AttributeNumberComparisonExpression("d.d2", ComparisonOperator.eq, new BigDecimal("42.42")));

    // d.d3 eq 42e13
    matches(new AttributeNumberComparisonExpression("d.d3", ComparisonOperator.eq, new BigDecimal("42e13")));

    // d.d4 eq 42e-1
    matches(new AttributeNumberComparisonExpression("d.d4", ComparisonOperator.eq, new BigDecimal("42e-1")));

    // d.d5 eq 1.00000000000009
    matches(new AttributeNumberComparisonExpression("d.d5", ComparisonOperator.eq, new BigDecimal("1.00000000000009")));
  }

  @Test
  public void lessThan() {
    // -----------------------------------------------------------------------------------------------------------------------
    // Date
    // -----------------------------------------------------------------------------------------------------------------------

    // f.f1 lt "2022-09-02T15:14:46.061Z"
    matches(new AttributeDateComparisonExpression("f.f1", ComparisonOperator.lt, scimDate("2022-09-02T15:14:46.061Z")));

    // -----------------------------------------------------------------------------------------------------------------------
    // Text
    // -----------------------------------------------------------------------------------------------------------------------

    // a lt a
    matches(new AttributeTextComparisonExpression("a", ComparisonOperator.lt, "aaa"));

    // b.b1 lt c
    matches(new AttributeTextComparisonExpression("b.b1", ComparisonOperator.lt, "ccc"));

    // b.b1 lt d|e
    noMatch(new AttributeTextComparisonExpression("b.b1", ComparisonOperator.lt, "ddd"));
    noMatch(new AttributeTextComparisonExpression("b.b1", ComparisonOperator.lt, "eee"));

    // -----------------------------------------------------------------------------------------------------------------------
    // Numbers
    // -----------------------------------------------------------------------------------------------------------------------

    // c lt 43
    matches(new AttributeNumberComparisonExpression("c", ComparisonOperator.lt, new BigDecimal(43)));

    // d.d1 lt 43
    matches(new AttributeNumberComparisonExpression("d.d1", ComparisonOperator.lt, new BigDecimal(43)));

    // d.d2 lt 42.43
    matches(new AttributeNumberComparisonExpression("d.d2", ComparisonOperator.lt, new BigDecimal("42.43")));

    // d.d5 lt 1.0000000000001
    matches(new AttributeNumberComparisonExpression("d.d5", ComparisonOperator.lt, new BigDecimal("1.0000000000001")));

    // d.d2 lt 42.42|42.41
    noMatch(new AttributeNumberComparisonExpression("d.d2", ComparisonOperator.lt, new BigDecimal("42.42")));
    noMatch(new AttributeNumberComparisonExpression("d.d2", ComparisonOperator.lt, new BigDecimal("42.41")));

    // d.d5 lt 1.00000000000009|1.00000000000008
    noMatch(new AttributeNumberComparisonExpression("d.d5", ComparisonOperator.lt, new BigDecimal("1.00000000000009")));
    noMatch(new AttributeNumberComparisonExpression("d.d5", ComparisonOperator.lt, new BigDecimal("1.00000000000008")));
  }

  @Test
  public void present() throws Exception {
    // a pr
    matches(new AttributePresentTestExpression("a"));

    // b pr
    matches(new AttributePresentTestExpression("b"));

    // b.b1 pr
    matches(new AttributePresentTestExpression("b.b1"));

    // z pr
    noMatch(new AttributePresentTestExpression("z"));

    // b.b2 pr
    noMatch(new AttributePresentTestExpression("b.b2"));
  }

  private void matches(AttributeExpression<?> expression) {
    boolean result = SCIMPatchFilterMatcher.matches(expression, source);

    if (!result) {
      fail("Expected a match.");
    }
  }

  private void noMatch(AttributeExpression<?> expression) {
    boolean result = SCIMPatchFilterMatcher.matches(expression, source);

    if (result) {
      fail("Did not expected a match.");
    }
  }

  private ZonedDateTime scimDate(String s) {
    return ZonedDateTime.ofInstant(Instant.parse(s), ZoneOffset.UTC);
  }
}
