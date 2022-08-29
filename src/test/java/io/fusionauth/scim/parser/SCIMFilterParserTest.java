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

import io.fusionauth.scim.parser.exception.InvalidStateException;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.fail;

/**
 * @author Daniel DeGroff & Spencer Witt
 */
public class SCIMFilterParserTest {
  private final SCIMFilterParser parser = new SCIMFilterParser();

  @DataProvider(name = "badData")
  public Object[][] badData() {
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
        {
            // Need exponent or at least one digit after decimal
            "A eq 12. ",
            "Invalid state transition at [A eq 12. ]"
        },
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
    };
  }

  @DataProvider(name = "goodData")
  public Object[][] goodData() {
    return new Object[][]{
        {
            "A pr",
            SCIMParserState.afterAttributeExpression
        },
        {
            "A eq true",
            SCIMParserState.booleanValue
        },
        {
            "A eq false",
            SCIMParserState.booleanValue
        },
        {
            "A eq null",
            SCIMParserState.nullValue
        },
        {
            "A eq -121.45e+2",
            SCIMParserState.exponentValue
        },
        {
            "A eq 5E-0",
            SCIMParserState.exponentValue
        },
        // TODO : More tests
        //  A eq "(((  D )) "
        //  A eq "\")\"(\")"
    };
  }

  @Test(dataProvider = "badData")
  public void parseBad(String filter, String expected) throws Exception {
    try {
      parser.parse(filter);
      fail("Expected exception for filter [" + filter + "]");
    } catch (InvalidStateException e) {
      assertEquals(expected, e.getMessage());
    }
  }

  @Test(dataProvider = "goodData")
  public void parseGood(String filter, SCIMParserState expected) throws Exception {
    SCIMParserState actual = parser.parse(filter);
    assertEquals(expected, actual);
  }
}
