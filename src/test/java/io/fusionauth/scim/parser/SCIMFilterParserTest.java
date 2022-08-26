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
            "A pd",
            "Invalid state transition at [A pd]"
        }
    };
  }

  @DataProvider(name = "goodData")
  public Object[][] goodData() {
    return new Object[][]{
        {
            "A pr",
            SCIMParserState.afterAttributeExpression
        }
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
