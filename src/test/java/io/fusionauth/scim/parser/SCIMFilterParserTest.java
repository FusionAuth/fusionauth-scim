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
package io.fusionauth.scim.parser;

import java.time.ZoneId;
import java.time.ZonedDateTime;

import io.fusionauth.scim.parser.exception.ComparisonValueException;
import io.fusionauth.scim.parser.exception.InvalidStateException;
import io.fusionauth.scim.parser.exception.OperatorException;
import io.fusionauth.scim.parser.expression.AttributeBooleanComparisonExpression;
import io.fusionauth.scim.parser.expression.AttributeDateComparisonExpression;
import io.fusionauth.scim.parser.expression.AttributeNullComparisonExpression;
import io.fusionauth.scim.parser.expression.AttributeNumberComparisonExpression;
import io.fusionauth.scim.parser.expression.AttributePresentExpression;
import io.fusionauth.scim.parser.expression.AttributeTextComparisonExpression;
import io.fusionauth.scim.parser.expression.Expression;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.fail;

/**
 * @author Daniel DeGroff & Spencer Witt
 */
public class SCIMFilterParserTest {
  private final SCIMFilterParser parser = new SCIMFilterParser();

  @DataProvider(name = "goodData")
  public Object[][] goodData() {
    //noinspection TrailingWhitespacesInTextBlock
    return new Object[][]{
        {
            "A pr",
            new AttributePresentExpression("A")
        },
        {
            "A eq true",
            new AttributeBooleanComparisonExpression("A", ComparisonOperator.eq, true)
        },
        {
            "A eq false",
            new AttributeBooleanComparisonExpression("A", ComparisonOperator.eq, false)
        },
        {
            "A eq null",
            new AttributeNullComparisonExpression("A", ComparisonOperator.eq)
        },
        {
            "A eq -121.45e+2",
            new AttributeNumberComparisonExpression("A", ComparisonOperator.eq, -12145)
        },
        {
            "A eq 5E-0",
            new AttributeNumberComparisonExpression("A", ComparisonOperator.eq, 5)
        },
        {
            // Extra spaces are fine
            "A  eq     5",
            new AttributeNumberComparisonExpression("A", ComparisonOperator.eq, 5)
        },
        {
            // Special characters are ignored in text values
            "A eq \") ((( ..eq pr 00.1.1.90)) (\"",
            new AttributeTextComparisonExpression("A", ComparisonOperator.eq, ") ((( ..eq pr 00.1.1.90)) (")
        },
        {
            // Special characters are ignored in text values
            """
            A eq "\\'\\"\\"\\t\\b\\n\\r\\f" """,
            new AttributeTextComparisonExpression("A", ComparisonOperator.eq, """
                '""\t\b
                \r\f""")
        },
        {
            "meta.lastModified ge \"2011-05-13T04:42:34Z\"",
            new AttributeDateComparisonExpression("meta.lastModified", ComparisonOperator.ge, ZonedDateTime.of(2011, 5, 13, 4, 42, 34, 0, ZoneId.of("UTC")).toEpochSecond())
        },
        {
            "meta.lastModified gt \"2011-05-13T04:42:34Z\"",
            new AttributeDateComparisonExpression("meta.lastModified", ComparisonOperator.gt, ZonedDateTime.of(2011, 5, 13, 4, 42, 34, 0, ZoneId.of("UTC")).toEpochSecond())
        },
        {
            "meta.lastModified le \"2011-05-13T04:42:34Z\"",
            new AttributeDateComparisonExpression("meta.lastModified", ComparisonOperator.le, ZonedDateTime.of(2011, 5, 13, 4, 42, 34, 0, ZoneId.of("UTC")).toEpochSecond())
        },
        {
            "meta.lastModified lt \"2011-05-13T04:42:34Z\"",
            new AttributeDateComparisonExpression("meta.lastModified", ComparisonOperator.lt, ZonedDateTime.of(2011, 5, 13, 4, 42, 34, 0, ZoneId.of("UTC")).toEpochSecond())
        },
        {
            "userName eq \"bjensen\"",
            new AttributeTextComparisonExpression("userName", ComparisonOperator.eq, "bjensen")
        },
        {
            "userName ne \"bjensen\"",
            new AttributeTextComparisonExpression("userName", ComparisonOperator.ne, "bjensen")
        },
        {
            "name.familyName co \"O'Malley\"",
            new AttributeTextComparisonExpression("name.familyName", ComparisonOperator.co, "O'Malley")
        },
        {
            "userName sw \"J\"",
            new AttributeTextComparisonExpression("userName", ComparisonOperator.sw, "J")
        },
        {
            "userName ew \"J\"",
            new AttributeTextComparisonExpression("userName", ComparisonOperator.ew, "J")
        },
        {
            "urn:ietf:params:scim:schemas:core:2.0:User:userName sw \"J\"",
            new AttributeTextComparisonExpression("urn:ietf:params:scim:schemas:core:2.0:User:userName", ComparisonOperator.sw, "J")
        },
        {
            "title pr",
            new AttributePresentExpression("title")
        }
//    filter=title pr and userType eq "Employee"
//    filter=title pr or userType eq "Intern"
//    filter=schemas eq "urn:ietf:params:scim:schemas:extension:enterprise:2.0:User"
//    filter=userType eq "Employee" and (emails co "example.com" or
//                                       emails.value co "example.org")
//    filter=userType ne "Employee" and not (emails co "example.com" or
//    emails.value co "example.org")
//    filter=userType eq "Employee" and (emails.type eq "work")
//    filter=userType eq "Employee" and emails[type eq "work" and
//    value co "@example.com"]
//    filter=emails[type eq "work" and value co "@example.com"] or
//    ims[type eq "xmpp" and value co "@foo.com"]
        // TODO : More tests
        //  A eq "(((  D )) "
        //  A eq "\")\"(\")"
    };
  }

  @DataProvider(name = "invalidComparison")
  public Object[][] invalidComparison() {
    return new Object[][]{
        {
            "A eq trul",
            "[trul] is not a valid comparison value"
        },
        {
            "A eq fallse",
            "[fallse] is not a valid comparison value"
        },
        {
            "A eq nuul",
            "[nuul] is not a valid comparison value"
        },
    };
  }

  @DataProvider(name = "invalidOperator")
  public Object[][] invalidOperator() {
    return new Object[][]{
        {
            "A lw 8",
            "No operator for [lw]"
        },
        {
            "A lt false",
            "[lt] is not a valid operator for a boolean comparison"
        },
        {
            "A lt null",
            "[lt] is not a valid operator for a null comparison"
        },
    };
  }

  @DataProvider(name = "invalidState")
  public Object[][] invalidState() {
    return new Object[][]{
        {
            // Invalid operator
            "A pd",
            "Invalid state transition at [A pd]"
        },
        {
            // Invalid operator
            "A hj",
            "Invalid state transition at [A h]"
        },
        {
            // Invalid operator
            "A ez 8",
            "Invalid state transition at [A ez]"
        },
        {
            // No matching comparison type
            "A eq z",
            "Invalid state transition at [A eq z]"
        },
        {
            // No matching comparison type
            "A eq troe",
            "Invalid state transition at [A eq tro]"
        },
        {
            // No matching comparison type
            "A eq nil",
            "Invalid state transition at [A eq ni]"
        },
        {
            // No leading zeroes on number
            "A eq 001",
            "Invalid state transition at [A eq 0]"
        },
        {
            // Only one decimal allowed
            "A eq 12.4.2",
            "Invalid state transition at [A eq 12.4.]"
        },
        {
            // Only one decimal allowed
            "A eq 12.4.2",
            "Invalid state transition at [A eq 12.4.]"
        },
        {
            // Decimal not allowed in exponent
            "A eq 12e1.2",
            "Invalid state transition at [A eq 12e1.]"
        },
//        {
//            // Need exponent or at least one digit after decimal
//            // TODO : according to RFC7159 this should fail, but it would require an extra state
//            //  not sure whether it's worth added complexity, or just allow this one through> it parses fine
//            "A eq 12.",
//            "Invalid state transition at [A eq 12.]"
//        },
        {
            // Exponent sign must be first
            "A eq 12.4e1-2",
            "Invalid state transition at [A eq 12.4e1-]"
        },
        {
            // No random characters in number values
            "A eq 12.4d3",
            "Invalid state transition at [A eq 12.4d]"
        },
        {
            // Invalid escape
            "A eq \"\\c\"",
            "Invalid state transition at [A eq \"\\c]"
        },
    };
  }

  @Test(dataProvider = "goodData")
  public void parseGood(String filter, Expression expected) throws Exception {
    Expression actual = parser.parse(filter);
    assertEquals(expected, actual);
  }

  @Test(dataProvider = "invalidComparison")
  public void parseInvalidComparison(String filter, String expected) throws Exception {
    try {
      parser.parse(filter);
      fail("Expected exception for filter [" + filter + "]");
    } catch (ComparisonValueException e) {
      assertEquals(expected, e.getMessage());
    }
  }

  @Test(dataProvider = "invalidOperator")
  public void parseInvalidOperator(String filter, String expected) throws Exception {
    try {
      parser.parse(filter);
      fail("Expected exception for filter [" + filter + "]");
    } catch (OperatorException e) {
      assertEquals(expected, e.getMessage());
    }
  }

  @Test(dataProvider = "invalidState")
  public void parseInvalidState(String filter, String expected) throws Exception {
    try {
      parser.parse(filter);
      fail("Expected exception for filter [" + filter + "]");
    } catch (InvalidStateException e) {
      assertEquals(expected, e.getMessage());
    }
  }
}
