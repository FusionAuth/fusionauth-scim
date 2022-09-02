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
package io.fusionauth.scim.transform;

import io.fusionauth.scim.parser.SCIMFilterParser;
import io.fusionauth.scim.parser.expression.Expression;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import static org.testng.AssertJUnit.assertEquals;

/**
 * @author Daniel DeGroff & Spencer Witt
 */
public class ElasticsearchTransformerTest {
  private final SCIMFilterParser parser = new SCIMFilterParser();

  @DataProvider(name = "data")
  public Object[][] data() {
    return new Object[][]{
        {
            "A pr",
            "_exists_:A"
        },
        {
            "A eq null",
            "A:null"
        },
        {
            "A eq 12.5",
            "A:12.5"
        },
        {
            "A ne 12.5",
            "!(A:12.5)"
        },
        {
            "A gt \"2011-05-13T04:42:34Z\"",
            "A:>2011-05-13T04:42:34Z"
        },
        {
            "A ge \"dog\"",
            "A:>=\"dog\""
        },
        {
            "A lt 12.5",
            "A:<12.5"
        },
        {
            "A le 12.5",
            "A:<=12.5"
        },
        {
            "A co \"cat\"",
            "A:\"cat\""
        },
        {
            "A sw \"cat\"",
            "A:\"cat*\""
        },
        {
            "A ew \"cat\"",
            "A:\"*cat\""
        },
        {
            "A lt 12.5 and B gt 3",
            "(A:<12.5 AND B:>3)"
        },
        {
            "A lt 12.5 or B gt 3",
            "(A:<12.5 OR B:>3)"
        },
        {
            "A pr and B pr or C pr and D pr or E pr or F pr",
            "(((_exists_:A AND _exists_:B) OR (_exists_:C AND _exists_:D)) OR (_exists_:E OR _exists_:F))"
        },
        {
            "A pr and B pr and C pr or D pr and E pr or F pr",
            "(((_exists_:A AND (_exists_:B AND _exists_:C)) OR (_exists_:D AND _exists_:E)) OR _exists_:F)"
        },
    };
  }

  @Test(dataProvider = "data")
  public void transformGood(String scimFilter, String expected) {
    Expression expression = parser.parse(scimFilter);
    String actual = ElasticsearchTransformer.transform(expression);
    assertEquals(expected, actual);
  }
}
