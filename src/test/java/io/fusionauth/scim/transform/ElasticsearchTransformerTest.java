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
            "A eq true",
            "A:true"
        },
        {
            "A eq false",
            "A:false"
        },
        {
            "A ne true",
            "!(A:true)"
        },
        {
            "A ne false",
            "!(A:false)"
        },
        {
            "A eq null",
            "A:null"
        },
        {
            "A ne null",
            "!(A:null)"
        },
        {
            "A eq 0",
            "A:0"
        },
        {
            "A eq -.5",
            "A:-0.5"
        },
        {
            "A eq 0E10",
            "A:0E+10"
        },
        {
            "A eq -121.45e+2",
            "A:-12145"
        },
        {
            "A eq \") ((( ..eq pr 00.1.1.90)) (\"",
            "A:\") ((( ..eq pr 00.1.1.90)) (\""
        },
        {
            // Special characters are ignored in text values
            """
            A eq "\\'\\"\\"\\t\\b\\n\\r\\f\\\\" """,
            """
                A:"'""\t\b
                \r\f\\" """
        },
        {
            "meta.lastModified ge \"2011-05-13T04:42:34Z\"",
            "meta.lastModified:>=2011-05-13T04:42:34Z"
        },
        {
            "meta.lastModified gt \"2011-05-13T04:42:34Z\"",
            "meta.lastModified:>2011-05-13T04:42:34Z"
        },
        {
            "meta.lastModified le \"2011-05-13T04:42:34Z\"",
            "meta.lastModified:<=2011-05-13T04:42:34Z"
        },
        {
            "meta.lastModified lt \"2011-05-13T04:42:34Z\"",
            "meta.lastModified:<2011-05-13T04:42:34Z"
        },
        {
            "meta.lastModified lt \"2011-05-13T04:42:34.061Z\"",
            "meta.lastModified:<2011-05-13T04:42:34.061Z"
        },
        {
            "userName eq \"bjensen\"",
            "userName:\"bjensen\""
        },
        {
            "userName ne \"bjensen\"",
            "!(userName:\"bjensen\")"
        },
        {
            "name.familyName co \"O'Malley\"",
            "name.familyName:\"O'Malley\""
        },
        {
            "userName sw \"J\"",
            "userName:\"J*\""
        },
        {
            "userName ew \"J\"",
            "userName:\"*J\""
        },
        {
            "urn:ietf:params:scim:schemas:core:2.0:User:userName sw \"J\"",
            "urn:ietf:params:scim:schemas:core:2.0:User:userName:\"J*\"",
        },
        {
            "urn:ietf:params:scim:schemas:core:2.0:User:name.firstName sw \"J\"",
            "urn:ietf:params:scim:schemas:core:2.0:User:name.firstName:\"J*\""
        },
        {
            "schemas eq \"urn:ietf:params:scim:schemas:extension:enterprise:2.0:User\"",
            "schemas:\"urn:ietf:params:scim:schemas:extension:enterprise:2.0:User\""
        },
        {
            "title pr",
            "_exists_:title"
        },
        {
            "attribute-paths_can_have-other_symbols pr",
            "_exists_:attribute-paths_can_have-other_symbols"
        },
        {
            "title pr and userType eq \"Employee\"",
            "(_exists_:title AND userType:\"Employee\")"
        },
        {
            "title pr or userType eq \"Intern\"",
            "(_exists_:title OR userType:\"Intern\")"
        },
        {
            "A pr and B pr and C pr and D pr",
            "(_exists_:A AND (_exists_:B AND (_exists_:C AND _exists_:D)))"
        },
        {
            "A pr or B pr or C pr or D pr",
            "(_exists_:A OR (_exists_:B OR (_exists_:C OR _exists_:D)))"
        },
        {
            "A pr or B pr and C pr or D pr",
            "((_exists_:A OR (_exists_:B AND _exists_:C)) OR _exists_:D)"
        },
//        {
//            "A pr and B pr or C pr and D pr or E pr or F pr",
//            new LogicalLinkExpression(
//                new LogicalLinkExpression(
//                    new LogicalLinkExpression(
//                        new AttributePresentTestExpression("A"),
//                        LogicalOperator.and,
//                        new AttributePresentTestExpression("B")
//                    ),
//                    LogicalOperator.or,
//                    new LogicalLinkExpression(
//                        new AttributePresentTestExpression("C"),
//                        LogicalOperator.and,
//                        new AttributePresentTestExpression("D")
//                    )
//                ),
//                LogicalOperator.or,
//                new LogicalLinkExpression(
//                    new AttributePresentTestExpression("E"),
//                    LogicalOperator.or,
//                    new AttributePresentTestExpression("F")
//                )
//            )
//        },
//        {
//            "A pr and B pr and C pr or D pr and E pr or F pr",
//            new LogicalLinkExpression(
//                new LogicalLinkExpression(
//                    new LogicalLinkExpression(
//                        new AttributePresentTestExpression("A"),
//                        LogicalOperator.and,
//                        new LogicalLinkExpression(
//                            new AttributePresentTestExpression("B"),
//                            LogicalOperator.and,
//                            new AttributePresentTestExpression("C")
//                        )
//                    ),
//                    LogicalOperator.or,
//                    new LogicalLinkExpression(
//                        new AttributePresentTestExpression("D"),
//                        LogicalOperator.and,
//                        new AttributePresentTestExpression("E")
//                    )
//                ),
//                LogicalOperator.or,
//                new AttributePresentTestExpression("F")
//            )
//        },
//        {
//            "((((((A pr))))))",
//            new AttributePresentTestExpression("A")
//        },
//        {
//            "userType eq \"Employee\" and (emails co \"example.com\" or emails.value co \"example.org\")",
//            new LogicalLinkExpression(
//                new AttributeTextComparisonExpression("userType", ComparisonOperator.eq, "Employee"),
//                LogicalOperator.and,
//                new LogicalLinkExpression(
//                    new AttributeTextComparisonExpression("emails", ComparisonOperator.co, "example.com"),
//                    LogicalOperator.or,
//                    new AttributeTextComparisonExpression("emails.value", ComparisonOperator.co, "example.org")
//                )
//            )
//        },
//        {
//            "userType eq \"Employee\" and (emails.type eq \"work\")",
//            new LogicalLinkExpression(
//                new AttributeTextComparisonExpression("userType", ComparisonOperator.eq, "Employee"),
//                LogicalOperator.and,
//                new AttributeTextComparisonExpression("emails.type", ComparisonOperator.eq, "work")
//            )
//        },
//        {
//            "(A pr or B pr)",
//            new LogicalLinkExpression(
//                new AttributePresentTestExpression("A"),
//                LogicalOperator.or,
//                new AttributePresentTestExpression("B")
//            )
//        },
//        {
//            "(A eq 5 or B eq 0)",
//            new LogicalLinkExpression(
//                new AttributeNumberComparisonExpression("A", ComparisonOperator.eq, new BigDecimal(5)),
//                LogicalOperator.or,
//                new AttributeNumberComparisonExpression("B", ComparisonOperator.eq, new BigDecimal(0))
//            )
//        },
//        {
//            "(A eq 5 or B eq 10)",
//            new LogicalLinkExpression(
//                new AttributeNumberComparisonExpression("A", ComparisonOperator.eq, new BigDecimal(5)),
//                LogicalOperator.or,
//                new AttributeNumberComparisonExpression("B", ComparisonOperator.eq, new BigDecimal(10))
//            )
//        },
//        {
//            "(A eq 5.0 or B eq 10.0)",
//            new LogicalLinkExpression(
//                new AttributeNumberComparisonExpression("A", ComparisonOperator.eq, new BigDecimal("5.0")),
//                LogicalOperator.or,
//                new AttributeNumberComparisonExpression("B", ComparisonOperator.eq, new BigDecimal("10.0"))
//            )
//        },
//        {
//            "(A eq 50e-1 or B eq 100e-1)",
//            new LogicalLinkExpression(
//                new AttributeNumberComparisonExpression("A", ComparisonOperator.eq, new BigDecimal("5.0")),
//                LogicalOperator.or,
//                new AttributeNumberComparisonExpression("B", ComparisonOperator.eq, new BigDecimal("10.0"))
//            )
//        },
//        {
//            "(A eq true or B eq true)",
//            new LogicalLinkExpression(
//                new AttributeBooleanComparisonExpression("A", ComparisonOperator.eq, true),
//                LogicalOperator.or,
//                new AttributeBooleanComparisonExpression("B", ComparisonOperator.eq, true)
//            )
//        },
//        {
//            "(A eq null or B eq null)",
//            new LogicalLinkExpression(
//                new AttributeNullTestExpression("A", ComparisonOperator.eq),
//                LogicalOperator.or,
//                new AttributeNullTestExpression("B", ComparisonOperator.eq)
//            )
//        },
//        {
//            "Z[A pr or B pr]",
//            new AttributeFilterGroupingExpression(
//                "Z",
//                new LogicalLinkExpression(
//                    new AttributePresentTestExpression("A"),
//                    LogicalOperator.or,
//                    new AttributePresentTestExpression("B")
//                )
//            )
//        },
//        {
//            "Z[A eq 5 or B eq 0]",
//            new AttributeFilterGroupingExpression(
//                "Z",
//                new LogicalLinkExpression(
//                    new AttributeNumberComparisonExpression("A", ComparisonOperator.eq, new BigDecimal(5)),
//                    LogicalOperator.or,
//                    new AttributeNumberComparisonExpression("B", ComparisonOperator.eq, new BigDecimal(0))
//                )
//            )
//        },
//        {
//            "Z[A eq 5 or B eq 10]",
//            new AttributeFilterGroupingExpression(
//                "Z",
//                new LogicalLinkExpression(
//                    new AttributeNumberComparisonExpression("A", ComparisonOperator.eq, new BigDecimal(5)),
//                    LogicalOperator.or,
//                    new AttributeNumberComparisonExpression("B", ComparisonOperator.eq, new BigDecimal(10))
//                )
//            )
//        },
//        {
//            "Z[A eq 5.0 or B eq 10.0]",
//            new AttributeFilterGroupingExpression(
//                "Z",
//                new LogicalLinkExpression(
//                    new AttributeNumberComparisonExpression("A", ComparisonOperator.eq, new BigDecimal("5.0")),
//                    LogicalOperator.or,
//                    new AttributeNumberComparisonExpression("B", ComparisonOperator.eq, new BigDecimal("10.0"))
//                )
//            )
//        },
//        {
//            "Z[A eq 50e-1 or B eq 100e-1]",
//            new AttributeFilterGroupingExpression(
//                "Z",
//                new LogicalLinkExpression(
//                    new AttributeNumberComparisonExpression("A", ComparisonOperator.eq, new BigDecimal("5.0")),
//                    LogicalOperator.or,
//                    new AttributeNumberComparisonExpression("B", ComparisonOperator.eq, new BigDecimal("10.0"))
//                )
//            )
//        },
//        {
//            "Z[A eq true or B eq true]",
//            new AttributeFilterGroupingExpression(
//                "Z",
//                new LogicalLinkExpression(
//                    new AttributeBooleanComparisonExpression("A", ComparisonOperator.eq, true),
//                    LogicalOperator.or,
//                    new AttributeBooleanComparisonExpression("B", ComparisonOperator.eq, true)
//                )
//            )
//        },
//        {
//            "Z[A eq null or B eq null]",
//            new AttributeFilterGroupingExpression(
//                "Z",
//                new LogicalLinkExpression(
//                    new AttributeNullTestExpression("A", ComparisonOperator.eq),
//                    LogicalOperator.or,
//                    new AttributeNullTestExpression("B", ComparisonOperator.eq)
//                )
//            )
//        },
//        {
//            "Z[A pr and B pr or C pr]",
//            new AttributeFilterGroupingExpression(
//                "Z",
//                new LogicalLinkExpression(
//                    new LogicalLinkExpression(
//                        new AttributePresentTestExpression("A"),
//                        LogicalOperator.and,
//                        new AttributePresentTestExpression("B")
//                    ),
//                    LogicalOperator.or,
//                    new AttributePresentTestExpression("C")
//                )
//            )
//        },
//        {
//            "Z[A pr or B pr and C pr]",
//            new AttributeFilterGroupingExpression(
//                "Z",
//                new LogicalLinkExpression(
//                    new AttributePresentTestExpression("A"),
//                    LogicalOperator.or,
//                    new LogicalLinkExpression(
//                        new AttributePresentTestExpression("B"),
//                        LogicalOperator.and,
//                        new AttributePresentTestExpression("C")
//                    )
//                )
//            )
//        },
//        {
//            " (  ( A pr or B pr ) and   ( C pr and ( D   pr   ) )    ) ",
//            new LogicalLinkExpression(
//                new LogicalLinkExpression(
//                    new AttributePresentTestExpression("A"),
//                    LogicalOperator.or,
//                    new AttributePresentTestExpression("B")
//                ),
//                LogicalOperator.and,
//                new LogicalLinkExpression(
//                    new AttributePresentTestExpression("C"),
//                    LogicalOperator.and,
//                    new AttributePresentTestExpression("D")
//                )
//            )
//        },
//        {
//            "not (A pr and B pr)",
//            new LogicalNegationExpression(
//                new LogicalLinkExpression(
//                    new AttributePresentTestExpression("A"),
//                    LogicalOperator.and,
//                    new AttributePresentTestExpression("B")
//                )
//            )
//        },
//        {
//            " (  ( not (A pr) or B pr ) and  not   ( C pr and not ( D   pr   ) )    ) ",
//            new LogicalLinkExpression(
//                new LogicalLinkExpression(
//                    new LogicalNegationExpression(
//                        new AttributePresentTestExpression("A")
//                    ),
//                    LogicalOperator.or,
//                    new AttributePresentTestExpression("B")
//                ),
//                LogicalOperator.and,
//                new LogicalNegationExpression(
//                    new LogicalLinkExpression(
//                        new AttributePresentTestExpression("C"),
//                        LogicalOperator.and,
//                        new LogicalNegationExpression(
//                            new AttributePresentTestExpression("D")
//                        )
//                    )
//                )
//            )
//        },
//        {
//            "userType ne \"Employee\" and not (emails co \"example.com\" or emails.value co \"example.org\")",
//            new LogicalLinkExpression(
//                new AttributeTextComparisonExpression("userType", ComparisonOperator.ne, "Employee"),
//                LogicalOperator.and,
//                new LogicalNegationExpression(
//                    new LogicalLinkExpression(
//                        new AttributeTextComparisonExpression("emails", ComparisonOperator.co, "example.com"),
//                        LogicalOperator.or,
//                        new AttributeTextComparisonExpression("emails.value", ComparisonOperator.co, "example.org")
//                    )
//                )
//            )
//        },
//        {
//            "emails[type eq \"work\"]",
//            new AttributeFilterGroupingExpression(
//                "emails",
//                new AttributeTextComparisonExpression("type", ComparisonOperator.eq, "work")
//            )
//        },
//        {
//            "userType eq \"Employee\" and emails[type eq \"work\" and value co \"@example.com\"]",
//            new LogicalLinkExpression(
//                new AttributeTextComparisonExpression("userType", ComparisonOperator.eq, "Employee"),
//                LogicalOperator.and,
//                new AttributeFilterGroupingExpression(
//                    "emails",
//                    new LogicalLinkExpression(
//                        new AttributeTextComparisonExpression("type", ComparisonOperator.eq, "work"),
//                        LogicalOperator.and,
//                        new AttributeTextComparisonExpression("value", ComparisonOperator.co, "@example.com")
//                    )
//                )
//            )
//        },
//        {
//            "emails[type eq \"work\" and value co \"@example.com\"] or ims[type eq \"xmpp\" and value co \"@foo.com\"]",
//            new LogicalLinkExpression(
//                new AttributeFilterGroupingExpression(
//                    "emails",
//                    new LogicalLinkExpression(
//                        new AttributeTextComparisonExpression("type", ComparisonOperator.eq, "work"),
//                        LogicalOperator.and,
//                        new AttributeTextComparisonExpression("value", ComparisonOperator.co, "@example.com")
//                    )
//                ),
//                LogicalOperator.or,
//                new AttributeFilterGroupingExpression(
//                    "ims",
//                    new LogicalLinkExpression(
//                        new AttributeTextComparisonExpression("type", ComparisonOperator.eq, "xmpp"),
//                        LogicalOperator.and,
//                        new AttributeTextComparisonExpression("value", ComparisonOperator.co, "@foo.com")
//                    )
//                )
//            )
//        },
//        {
//            "emails  [   type  eq      \"work\" and value co \"@example.com\"   ]     or ims [type eq \"xmpp\"    and    value co   \"@foo.com\" ]  ",
//            new LogicalLinkExpression(
//                new AttributeFilterGroupingExpression(
//                    "emails",
//                    new LogicalLinkExpression(
//                        new AttributeTextComparisonExpression("type", ComparisonOperator.eq, "work"),
//                        LogicalOperator.and,
//                        new AttributeTextComparisonExpression("value", ComparisonOperator.co, "@example.com")
//                    )
//                ),
//                LogicalOperator.or,
//                new AttributeFilterGroupingExpression(
//                    "ims",
//                    new LogicalLinkExpression(
//                        new AttributeTextComparisonExpression("type", ComparisonOperator.eq, "xmpp"),
//                        LogicalOperator.and,
//                        new AttributeTextComparisonExpression("value", ComparisonOperator.co, "@foo.com")
//                    )
//                )
//            )
//        },
//        {
//            "A[B eq 12 and ( C gt 5 or D[E ne -3 and F le 41])] and (G[(H eq null or H eq \"12\") and I co \"@foo.com\"] or J pr)",
//            new LogicalLinkExpression(
//                new AttributeFilterGroupingExpression(
//                    "A",
//                    new LogicalLinkExpression(
//                        new AttributeNumberComparisonExpression("B", ComparisonOperator.eq, new BigDecimal(12)),
//                        LogicalOperator.and,
//                        new LogicalLinkExpression(
//                            new AttributeNumberComparisonExpression("C", ComparisonOperator.gt, new BigDecimal(5)),
//                            LogicalOperator.or,
//                            new AttributeFilterGroupingExpression(
//                                "D",
//                                new LogicalLinkExpression(
//                                    new AttributeNumberComparisonExpression("E", ComparisonOperator.ne, new BigDecimal(-3)),
//                                    LogicalOperator.and,
//                                    new AttributeNumberComparisonExpression("F", ComparisonOperator.le, new BigDecimal(41))
//                                )
//                            )
//                        )
//                    )
//                ),
//                LogicalOperator.and,
//                new LogicalLinkExpression(
//                    new AttributeFilterGroupingExpression(
//                        "G",
//                        new LogicalLinkExpression(
//                            new LogicalLinkExpression(
//                                new AttributeNullTestExpression("H", ComparisonOperator.eq),
//                                LogicalOperator.or,
//                                new AttributeTextComparisonExpression("H", ComparisonOperator.eq, "12")
//                            ),
//                            LogicalOperator.and,
//                            new AttributeTextComparisonExpression("I", ComparisonOperator.co, "@foo.com")
//                        )
//                    ),
//                    LogicalOperator.or,
//                    new AttributePresentTestExpression("J")
//                )
//            )
//        },
//        {
//            "A[B[C[(D eq \"text\" or E [F ne \"blob\"])]]]",
//            new AttributeFilterGroupingExpression(
//                "A",
//                new AttributeFilterGroupingExpression(
//                    "B",
//                    new AttributeFilterGroupingExpression(
//                        "C",
//                        new LogicalLinkExpression(
//                            new AttributeTextComparisonExpression("D", ComparisonOperator.eq, "text"),
//                            LogicalOperator.or,
//                            new AttributeFilterGroupingExpression(
//                                "E",
//                                new AttributeTextComparisonExpression("F", ComparisonOperator.ne, "blob")
//                            )
//                        )
//                    )
//                )
//            )
//        }
    };
  }

  @Test(dataProvider = "data")
  public void transformGood(String scimFilter, String expected) {
    Expression expression = parser.parse(scimFilter);
    String actual = ElasticsearchTransformer.transform(expression);
    assertEquals(expected, actual);
  }
}
