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
import java.time.ZonedDateTime;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.fusionauth.scim.parser.ComparisonOperator;
import io.fusionauth.scim.parser.expression.AttributeBooleanComparisonExpression;
import io.fusionauth.scim.parser.expression.AttributeDateComparisonExpression;
import io.fusionauth.scim.parser.expression.AttributeExpression;
import io.fusionauth.scim.parser.expression.AttributeNumberComparisonExpression;
import io.fusionauth.scim.parser.expression.AttributePresentTestExpression;
import io.fusionauth.scim.parser.expression.AttributeTextComparisonExpression;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import static io.fusionauth.scim.parser.ComparisonOperator.co;
import static io.fusionauth.scim.parser.ComparisonOperator.eq;
import static io.fusionauth.scim.parser.ComparisonOperator.ew;
import static io.fusionauth.scim.parser.ComparisonOperator.ge;
import static io.fusionauth.scim.parser.ComparisonOperator.gt;
import static io.fusionauth.scim.parser.ComparisonOperator.lt;
import static io.fusionauth.scim.parser.ComparisonOperator.ne;
import static io.fusionauth.scim.parser.ComparisonOperator.pr;
import static io.fusionauth.scim.parser.ComparisonOperator.sw;

/**
 * @author Daniel DeGroff
 */
public class SCIMPatchFilterMatcherTest {
  private final ObjectMapper objectMapper = new ObjectMapper().setSerializationInclusion(Include.NON_NULL);

  private JsonNode source;

  @BeforeTest
  public void beforeTest() throws IOException {
    // @formatter:off
    String json = """
        {
          "a": "bbb",
          "b": {
            "b1": "ddd",
            "b2": "hello world"
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
          },
          "g": true,
          "h": false,
          "i": {
            "i1": true,
            "i2": false
          }
        }
        """;
    // @formatter:on
    source = objectMapper.readTree(json);
  }

  @DataProvider(name = "equalNotEqualData")
  public Object[][] equalNotEqualData() {
    // @formatter:off
    return new Object[][]{

          // Attribute type    Path     Operator   Value

          {"bool",             "g",     eq,        true                          },
          {"bool",             "h",     eq,        false                         },
          {"bool",             "i.i1",  eq,        true                          },
          {"bool",             "i.i2",  eq,        false                         },

          {"date",             "f.f1",  lt,        "2022-09-02T15:14:46.061Z"    },

          {"number",           "c",     eq,        "42"                          },
          {"number",           "d.d1",  eq,        "42"                          },
          {"number",           "d.d2",  eq,        "42.42"                       },
          {"number",           "d.d3",  eq,        "42e13"                       },
          {"number",           "d.d4",  eq,        "42e-1"                       },
          {"number",           "d.d5",  eq,        "1.00000000000009"            },

          {"number",           "c",     lt,        "43"                          },
          {"number",           "d.d1",  lt,        "43"                          },
          {"number",           "d.d2",  lt,        "42.43"                       },
          {"number",           "d.d5",  lt,        "1.0000000000001"             },

          {"number",           "d.d5",  ge,        "1.00000000000009"            },

          {"number",           "d.d2",  gt,        "42.41"                       },
          {"number",           "d.d5",  gt,        "1.00000000000008"            },

          {"text",             "a",     eq,        "bbb"                         },
          {"text",             "b.b1",  eq,        "ddd"                         },

          {"text",             "a",     lt,        "aaa"                         },
          {"text",             "b.b1",  lt,        "ccc"                         },

          {"text",             "b.b1",  ge,        "ddd"                         },
          {"text",             "b.b1",  gt,        "eee"                         },

          {"text",             "a",     ne,        "ccc"                         },
          {"text",             "b.b1",  ne,        "aaaa"                        },

          {"text",             "a",     pr,        null                          },
          {"text",             "b",     pr,        null                          },
          {"text",             "b.b1",  pr,        null                          },

          {"text",             "a",     sw,        "b"                           },
          {"text",             "b.b2",  sw,        "he"                          },
          {"text",             "b.b2",  sw,        "hello"                       },
          {"text",             "b.b2",  sw,        "hello w"                     },

          {"text",             "a",     ew,        "b"                           },
          {"text",             "b.b2",  ew,        "d"                           },
          {"text",             "b.b2",  ew,        "ld"                          },
          {"text",             "b.b2",  ew,        "rld"                         },
          {"text",             "b.b2",  ew,        "orld"                        },
          {"text",             "b.b2",  ew,        "world"                       },

          {"text",             "a",     co,        "b"                           },
          {"text",             "b.b2",  co,        "hello"                       },
          {"text",             "b.b2",  co,        "o w"                         },
          {"text",             "b.b2",  co,        "d"                           },
          {"text",             "b.b2",  co,        "ld"                          },
          {"text",             "b.b2",  co,        "rld"                         },
          {"text",             "b.b2",  co,        "orld"                        },
          {"text",             "b.b2",  co,        "world"                       },

      };
    // @formatter:on
  }

  @Test
  public void notPresent() throws Exception {
    // z pr
    noMatch(new AttributePresentTestExpression("z"));

    // b.b2 pr
    noMatch(new AttributePresentTestExpression("b.b99"));
  }

  @Test(dataProvider = "equalNotEqualData")
  public void test(String type, String path, ComparisonOperator op, Object value) {
    if ("bool".equals(type)) {
      Boolean bool = (Boolean) value;
      matches(new AttributeBooleanComparisonExpression(path, op, bool));

      // Run the same test inverted
      if (op == ComparisonOperator.eq) {
        noMatch(new AttributeBooleanComparisonExpression(path, ne, bool));
      } else if (op == ComparisonOperator.ne) {
        noMatch(new AttributeBooleanComparisonExpression(path, eq, bool));
      }

    } else if ("date".equals(type)) {
      ZonedDateTime zonedDateTime = SCIMDateTools.parse((String) value);
      matches(new AttributeDateComparisonExpression(path, op, zonedDateTime));

      // Run the same test inverted
      if (op == ComparisonOperator.eq) {
        noMatch(new AttributeDateComparisonExpression(path, ne, zonedDateTime));
      } else if (op == ComparisonOperator.ne) {
        noMatch(new AttributeDateComparisonExpression(path, eq, zonedDateTime));
      } else if (op == ComparisonOperator.lt) {
        noMatch(new AttributeDateComparisonExpression(path, gt, zonedDateTime));
      } else if (op == ComparisonOperator.gt) {
        noMatch(new AttributeDateComparisonExpression(path, lt, zonedDateTime));
      }

    } else if ("number".equals(type)) {
      BigDecimal bigDecimal = new BigDecimal((String) value);
      matches(new AttributeNumberComparisonExpression(path, op, bigDecimal));

      // Run the same test inverted
      if (op == ComparisonOperator.eq) {
        noMatch(new AttributeNumberComparisonExpression(path, ne, bigDecimal));
      } else if (op == ComparisonOperator.ne) {
        noMatch(new AttributeNumberComparisonExpression(path, eq, bigDecimal));
      } else if (op == ComparisonOperator.lt) {
        noMatch(new AttributeNumberComparisonExpression(path, gt, bigDecimal));
      } else if (op == ComparisonOperator.gt) {
        noMatch(new AttributeNumberComparisonExpression(path, lt, bigDecimal));
      }
    } else if ("text".equals(type)) {
      String string = (String) value;
      matches(new AttributeTextComparisonExpression(path, op, string));

      // Run the same test inverted
      if (op == ComparisonOperator.eq) {
        noMatch(new AttributeTextComparisonExpression(path, ne, string));
      } else if (op == ComparisonOperator.ne) {
        noMatch(new AttributeTextComparisonExpression(path, eq, string));
      }
    } else {
      throw new AssertionError("unexpected type [" + type + "]");
    }
  }

  private void matches(AttributeExpression<?> expression) {
    boolean result = SCIMPatchFilterMatcher.matches(expression, source);

    if (!result) {
      throw new AssertionError("Expected a match.");
    }
  }

  private void noMatch(AttributeExpression<?> expression) {
    boolean result = SCIMPatchFilterMatcher.matches(expression, source);

    if (result) {
      throw new AssertionError("Did not expected a match.");
    }
  }
}
