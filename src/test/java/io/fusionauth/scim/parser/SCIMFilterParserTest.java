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

import java.math.BigDecimal;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;

import io.fusionauth.scim.parser.exception.AttributeFilterGroupingException;
import io.fusionauth.scim.parser.exception.AttributePathException;
import io.fusionauth.scim.parser.exception.ComparisonOperatorException;
import io.fusionauth.scim.parser.exception.ComparisonValueException;
import io.fusionauth.scim.parser.exception.GroupingException;
import io.fusionauth.scim.parser.exception.InvalidStateException;
import io.fusionauth.scim.parser.exception.LogicalOperatorException;
import io.fusionauth.scim.parser.expression.AttributeBooleanComparisonExpression;
import io.fusionauth.scim.parser.expression.AttributeDateComparisonExpression;
import io.fusionauth.scim.parser.expression.AttributeFilterGroupingExpression;
import io.fusionauth.scim.parser.expression.AttributeNullTestExpression;
import io.fusionauth.scim.parser.expression.AttributeNumberComparisonExpression;
import io.fusionauth.scim.parser.expression.AttributePresentTestExpression;
import io.fusionauth.scim.parser.expression.AttributeTextComparisonExpression;
import io.fusionauth.scim.parser.expression.Expression;
import io.fusionauth.scim.parser.expression.LogicalLinkExpression;
import io.fusionauth.scim.parser.expression.LogicalNegationExpression;
import io.fusionauth.scim.utils.SCIMDateTools;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.fail;

/**
 * @author Daniel DeGroff & Spencer Witt
 */
public class SCIMFilterParserTest {
  private final SCIMFilterParser parser = new SCIMFilterParser();

  @Test
  public void dateConverter() {
    // Ensure we can convert all valid formats
    // - yyyy-MM-dd'T'HH:mm:ssXX
    // - yyyy-MM-dd'T'HH:mm:ssXXX
    // - yyyy-MM-dd'T'HH:mm:ss.SSSXX
    // - yyyy-MM-dd'T'HH:mm:ss.SSSXXX

    // All 'Z' (UTC)
    assertDateEquals("2013-04-16T09:14:02Z", ZonedDateTime.of(2013, 4, 16, 9, 14, 2, 0, ZoneOffset.UTC));
    assertDateEquals("2013-04-16T09:14:02.123Z", ZonedDateTime.of(2013, 4, 16, 9, 14, 2, 0, ZoneOffset.UTC).plus(123, ChronoUnit.MILLIS));
    assertDateEquals("2022-09-02T15:14:46.061Z", ZonedDateTime.of(2022, 9, 2, 15, 14, 46, 0, ZoneOffset.UTC).plus(61, ChronoUnit.MILLIS));

    // Various version of the same time, UTC, and offsets
    ZonedDateTime actual = ZonedDateTime.of(2013, 4, 16, 9, 14, 2, 0, ZoneOffset.UTC);
    assertDateEquals("2013-04-16T09:14:02Z", actual);
    assertDateEquals("2013-04-16T11:14:02+0200", actual);
    assertDateEquals("2013-04-16T11:14:02.000+02:00", actual);

    // Various version of the same time, UTC, and offsets with ms
    actual = ZonedDateTime.of(2022, 9, 2, 15, 14, 46, 0, ZoneOffset.UTC).plus(61, ChronoUnit.MILLIS);
    assertDateEquals("2022-09-02T15:14:46.061Z", actual);
    assertDateEquals("2022-09-02T22:14:46.061+0700", actual);
    assertDateEquals("2022-09-02T22:14:46.061+07:00", actual);
  }

  @DataProvider(name = "goodData")
  public Object[][] goodData() {
    //noinspection TrailingWhitespacesInTextBlock
    return new Object[][]{
        {
            "A pr",
            new AttributePresentTestExpression("A")
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
            "A ne true",
            new AttributeBooleanComparisonExpression("A", ComparisonOperator.ne, true)
        },
        {
            "A ne false",
            new AttributeBooleanComparisonExpression("A", ComparisonOperator.ne, false)
        },
        {
            "A eq null",
            new AttributeNullTestExpression("A", ComparisonOperator.eq)
        },
        {
            "A ne null",
            new AttributeNullTestExpression("A", ComparisonOperator.ne)
        },
        {
            "A eq 0",
            new AttributeNumberComparisonExpression("A", ComparisonOperator.eq, new BigDecimal("0"))
        },
        {
            "A eq 0.5",
            new AttributeNumberComparisonExpression("A", ComparisonOperator.eq, new BigDecimal("0.5"))
        },
        {
            "A eq .5",
            new AttributeNumberComparisonExpression("A", ComparisonOperator.eq, new BigDecimal("0.5"))
        },
        {
            "A eq -.5",
            new AttributeNumberComparisonExpression("A", ComparisonOperator.eq, new BigDecimal("-0.5"))
        },
        {
            "A eq 0E10",
            new AttributeNumberComparisonExpression("A", ComparisonOperator.eq, new BigDecimal("0E+10"))
        },
        {
            "A eq -0.5",
            new AttributeNumberComparisonExpression("A", ComparisonOperator.eq, new BigDecimal("-0.5"))
        },
        {
            "A eq -0e10",
            new AttributeNumberComparisonExpression("A", ComparisonOperator.eq, new BigDecimal("0E+10"))
        },
        {
            "A eq -121.45e+2",
            new AttributeNumberComparisonExpression("A", ComparisonOperator.eq, new BigDecimal(-12145))
        },
        {
            "A eq 5.0E-0",
            new AttributeNumberComparisonExpression("A", ComparisonOperator.eq, new BigDecimal("5.0"))
        },
        {
            "A eq 5E-0",
            new AttributeNumberComparisonExpression("A", ComparisonOperator.eq, new BigDecimal("5"))
        },
        {
            // Extra spaces are fine
            "A  eq     5",
            new AttributeNumberComparisonExpression("A", ComparisonOperator.eq, new BigDecimal(5))
        },
        {
            // Leading decimal is fine
            "A eq .5",
            new AttributeNumberComparisonExpression("A", ComparisonOperator.eq, new BigDecimal(".5"))
        },
        {
            // Special characters are ignored in text values
            "A eq \") ((( ..eq pr 00.1.1.90)) (\"",
            new AttributeTextComparisonExpression("A", ComparisonOperator.eq, ") ((( ..eq pr 00.1.1.90)) (")
        },
        {
            // Special characters are ignored in text values
            """
            A eq "\\'\\"\\"\\t\\b\\n\\r\\f\\\\" """,
            new AttributeTextComparisonExpression("A", ComparisonOperator.eq, """
                '""\t\b
                \r\f\\""")
        },
        {
            "meta.lastModified ge \"2011-05-13T04:42:34Z\"",
            new AttributeDateComparisonExpression("meta.lastModified", ComparisonOperator.ge, ZonedDateTime.of(2011, 5, 13, 4, 42, 34, 0, ZoneId.of("Z")))
        },
        {
            "meta.lastModified gt \"2011-05-13T04:42:34Z\"",
            new AttributeDateComparisonExpression("meta.lastModified", ComparisonOperator.gt, ZonedDateTime.of(2011, 5, 13, 4, 42, 34, 0, ZoneId.of("Z")))
        },
        {
            "meta.lastModified le \"2011-05-13T04:42:34Z\"",
            new AttributeDateComparisonExpression("meta.lastModified", ComparisonOperator.le, ZonedDateTime.of(2011, 5, 13, 4, 42, 34, 0, ZoneId.of("Z")))
        },
        {
            "meta.lastModified lt \"2011-05-13T04:42:34Z\"",
            new AttributeDateComparisonExpression("meta.lastModified", ComparisonOperator.lt, ZonedDateTime.of(2011, 5, 13, 4, 42, 34, 0, ZoneId.of("Z")))
        },
        {
            "meta.lastModified lt \"2011-05-13T04:42:34.061Z\"",
            new AttributeDateComparisonExpression("meta.lastModified", ComparisonOperator.lt, ZonedDateTime.of(2011, 5, 13, 4, 42, 34, 0, ZoneId.of("Z")).plus(61, ChronoUnit.MILLIS))
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
            "urn:ietf:params:scim:schemas:core:2.0:User:name.firstName sw \"J\"",
            new AttributeTextComparisonExpression("urn:ietf:params:scim:schemas:core:2.0:User:name.firstName", ComparisonOperator.sw, "J")
        },
        {
            "schemas eq \"urn:ietf:params:scim:schemas:extension:enterprise:2.0:User\"",
            new AttributeTextComparisonExpression("schemas", ComparisonOperator.eq, "urn:ietf:params:scim:schemas:extension:enterprise:2.0:User")
        },
        {
            "title pr",
            new AttributePresentTestExpression("title")
        },
        {
            "attribute-paths_can_have-other_symbols pr",
            new AttributePresentTestExpression("attribute-paths_can_have-other_symbols")
        },
        {
            "title pr and userType eq \"Employee\"",
            new LogicalLinkExpression(
                new AttributePresentTestExpression("title"),
                LogicalOperator.and,
                new AttributeTextComparisonExpression("userType", ComparisonOperator.eq, "Employee")
            )
        },
        {
            "title pr or userType eq \"Intern\"",
            new LogicalLinkExpression(
                new AttributePresentTestExpression("title"),
                LogicalOperator.or,
                new AttributeTextComparisonExpression("userType", ComparisonOperator.eq, "Intern")
            )
        },
        {
            "A pr and B pr and C pr and D pr",
            new LogicalLinkExpression(
                new AttributePresentTestExpression("A"),
                LogicalOperator.and,
                new LogicalLinkExpression(
                    new AttributePresentTestExpression("B"),
                    LogicalOperator.and,
                    new LogicalLinkExpression(
                        new AttributePresentTestExpression("C"),
                        LogicalOperator.and,
                        new AttributePresentTestExpression("D")
                    )
                )
            )
        },
        {
            "A pr or B pr or C pr or D pr",
            new LogicalLinkExpression(
                new AttributePresentTestExpression("A"),
                LogicalOperator.or,
                new LogicalLinkExpression(
                    new AttributePresentTestExpression("B"),
                    LogicalOperator.or,
                    new LogicalLinkExpression(
                        new AttributePresentTestExpression("C"),
                        LogicalOperator.or,
                        new AttributePresentTestExpression("D")
                    )
                )
            )
        },
        {
            "A pr or B pr and C pr or D pr",
            new LogicalLinkExpression(
                new LogicalLinkExpression(
                    new AttributePresentTestExpression("A"),
                    LogicalOperator.or,
                    new LogicalLinkExpression(
                        new AttributePresentTestExpression("B"),
                        LogicalOperator.and,
                        new AttributePresentTestExpression("C")
                    )
                ),
                LogicalOperator.or,
                new AttributePresentTestExpression("D")
            )
        },
        {
            "A pr and B pr or C pr and D pr or E pr or F pr",
            new LogicalLinkExpression(
                new LogicalLinkExpression(
                    new LogicalLinkExpression(
                        new AttributePresentTestExpression("A"),
                        LogicalOperator.and,
                        new AttributePresentTestExpression("B")
                    ),
                    LogicalOperator.or,
                    new LogicalLinkExpression(
                        new AttributePresentTestExpression("C"),
                        LogicalOperator.and,
                        new AttributePresentTestExpression("D")
                    )
                ),
                LogicalOperator.or,
                new LogicalLinkExpression(
                    new AttributePresentTestExpression("E"),
                    LogicalOperator.or,
                    new AttributePresentTestExpression("F")
                )
            )
        },
        {
            "A pr and B pr and C pr or D pr and E pr or F pr",
            new LogicalLinkExpression(
                new LogicalLinkExpression(
                    new LogicalLinkExpression(
                        new AttributePresentTestExpression("A"),
                        LogicalOperator.and,
                        new LogicalLinkExpression(
                            new AttributePresentTestExpression("B"),
                            LogicalOperator.and,
                            new AttributePresentTestExpression("C")
                        )
                    ),
                    LogicalOperator.or,
                    new LogicalLinkExpression(
                        new AttributePresentTestExpression("D"),
                        LogicalOperator.and,
                        new AttributePresentTestExpression("E")
                    )
                ),
                LogicalOperator.or,
                new AttributePresentTestExpression("F")
            )
        },
        {
            "((((((A pr))))))",
            new AttributePresentTestExpression("A")
        },
        {
            "userType eq \"Employee\" and (emails co \"example.com\" or emails.value co \"example.org\")",
            new LogicalLinkExpression(
                new AttributeTextComparisonExpression("userType", ComparisonOperator.eq, "Employee"),
                LogicalOperator.and,
                new LogicalLinkExpression(
                    new AttributeTextComparisonExpression("emails", ComparisonOperator.co, "example.com"),
                    LogicalOperator.or,
                    new AttributeTextComparisonExpression("emails.value", ComparisonOperator.co, "example.org")
                )
            )
        },
        {
            "userType eq \"Employee\" and (emails.type eq \"work\")",
            new LogicalLinkExpression(
                new AttributeTextComparisonExpression("userType", ComparisonOperator.eq, "Employee"),
                LogicalOperator.and,
                new AttributeTextComparisonExpression("emails.type", ComparisonOperator.eq, "work")
            )
        },
        {
            "(A pr or B pr)",
            new LogicalLinkExpression(
                new AttributePresentTestExpression("A"),
                LogicalOperator.or,
                new AttributePresentTestExpression("B")
            )
        },
        {
            "(A eq 5 or B eq 0)",
            new LogicalLinkExpression(
                new AttributeNumberComparisonExpression("A", ComparisonOperator.eq, new BigDecimal(5)),
                LogicalOperator.or,
                new AttributeNumberComparisonExpression("B", ComparisonOperator.eq, new BigDecimal(0))
            )
        },
        {
            "(A eq 5 or B eq 10)",
            new LogicalLinkExpression(
                new AttributeNumberComparisonExpression("A", ComparisonOperator.eq, new BigDecimal(5)),
                LogicalOperator.or,
                new AttributeNumberComparisonExpression("B", ComparisonOperator.eq, new BigDecimal(10))
            )
        },
        {
            "(A eq 5.0 or B eq 10.0)",
            new LogicalLinkExpression(
                new AttributeNumberComparisonExpression("A", ComparisonOperator.eq, new BigDecimal("5.0")),
                LogicalOperator.or,
                new AttributeNumberComparisonExpression("B", ComparisonOperator.eq, new BigDecimal("10.0"))
            )
        },
        {
            "(A eq 50e-1 or B eq 100e-1)",
            new LogicalLinkExpression(
                new AttributeNumberComparisonExpression("A", ComparisonOperator.eq, new BigDecimal("5.0")),
                LogicalOperator.or,
                new AttributeNumberComparisonExpression("B", ComparisonOperator.eq, new BigDecimal("10.0"))
            )
        },
        {
            "(A eq true or B eq true)",
            new LogicalLinkExpression(
                new AttributeBooleanComparisonExpression("A", ComparisonOperator.eq, true),
                LogicalOperator.or,
                new AttributeBooleanComparisonExpression("B", ComparisonOperator.eq, true)
            )
        },
        {
            "(A eq null or B eq null)",
            new LogicalLinkExpression(
                new AttributeNullTestExpression("A", ComparisonOperator.eq),
                LogicalOperator.or,
                new AttributeNullTestExpression("B", ComparisonOperator.eq)
            )
        },
        {
            "Z[A pr or B pr]",
            new AttributeFilterGroupingExpression(
                "Z",
                new LogicalLinkExpression(
                    new AttributePresentTestExpression("A"),
                    LogicalOperator.or,
                    new AttributePresentTestExpression("B")
                )
            )
        },
        {
            "Z[A eq 5 or B eq 0]",
            new AttributeFilterGroupingExpression(
                "Z",
                new LogicalLinkExpression(
                    new AttributeNumberComparisonExpression("A", ComparisonOperator.eq, new BigDecimal(5)),
                    LogicalOperator.or,
                    new AttributeNumberComparisonExpression("B", ComparisonOperator.eq, new BigDecimal(0))
                )
            )
        },
        {
            "Z[A eq 5 or B eq 10]",
            new AttributeFilterGroupingExpression(
                "Z",
                new LogicalLinkExpression(
                    new AttributeNumberComparisonExpression("A", ComparisonOperator.eq, new BigDecimal(5)),
                    LogicalOperator.or,
                    new AttributeNumberComparisonExpression("B", ComparisonOperator.eq, new BigDecimal(10))
                )
            )
        },
        {
            "Z[A eq 5.0 or B eq 10.0]",
            new AttributeFilterGroupingExpression(
                "Z",
                new LogicalLinkExpression(
                    new AttributeNumberComparisonExpression("A", ComparisonOperator.eq, new BigDecimal("5.0")),
                    LogicalOperator.or,
                    new AttributeNumberComparisonExpression("B", ComparisonOperator.eq, new BigDecimal("10.0"))
                )
            )
        },
        {
            "Z[A eq 50e-1 or B eq 100e-1]",
            new AttributeFilterGroupingExpression(
                "Z",
                new LogicalLinkExpression(
                    new AttributeNumberComparisonExpression("A", ComparisonOperator.eq, new BigDecimal("5.0")),
                    LogicalOperator.or,
                    new AttributeNumberComparisonExpression("B", ComparisonOperator.eq, new BigDecimal("10.0"))
                )
            )
        },
        {
            "Z[A eq true or B eq true]",
            new AttributeFilterGroupingExpression(
                "Z",
                new LogicalLinkExpression(
                    new AttributeBooleanComparisonExpression("A", ComparisonOperator.eq, true),
                    LogicalOperator.or,
                    new AttributeBooleanComparisonExpression("B", ComparisonOperator.eq, true)
                )
            )
        },
        {
            "Z[A eq null or B eq null]",
            new AttributeFilterGroupingExpression(
                "Z",
                new LogicalLinkExpression(
                    new AttributeNullTestExpression("A", ComparisonOperator.eq),
                    LogicalOperator.or,
                    new AttributeNullTestExpression("B", ComparisonOperator.eq)
                )
            )
        },
        {
            "Z[A pr and B pr or C pr]",
            new AttributeFilterGroupingExpression(
                "Z",
                new LogicalLinkExpression(
                    new LogicalLinkExpression(
                        new AttributePresentTestExpression("A"),
                        LogicalOperator.and,
                        new AttributePresentTestExpression("B")
                    ),
                    LogicalOperator.or,
                    new AttributePresentTestExpression("C")
                )
            )
        },
        {
            "Z[A pr or B pr and C pr]",
            new AttributeFilterGroupingExpression(
                "Z",
                new LogicalLinkExpression(
                    new AttributePresentTestExpression("A"),
                    LogicalOperator.or,
                    new LogicalLinkExpression(
                        new AttributePresentTestExpression("B"),
                        LogicalOperator.and,
                        new AttributePresentTestExpression("C")
                    )
                )
            )
        },
        {
            " (  ( A pr or B pr ) and   ( C pr and ( D   pr   ) )    ) ",
            new LogicalLinkExpression(
                new LogicalLinkExpression(
                    new AttributePresentTestExpression("A"),
                    LogicalOperator.or,
                    new AttributePresentTestExpression("B")
                ),
                LogicalOperator.and,
                new LogicalLinkExpression(
                    new AttributePresentTestExpression("C"),
                    LogicalOperator.and,
                    new AttributePresentTestExpression("D")
                )
            )
        },
        {
            "not (A pr and B pr)",
            new LogicalNegationExpression(
                new LogicalLinkExpression(
                    new AttributePresentTestExpression("A"),
                    LogicalOperator.and,
                    new AttributePresentTestExpression("B")
                )
            )
        },
        {
            " (  ( not (A pr) or B pr ) and  not   ( C pr and not ( D   pr   ) )    ) ",
            new LogicalLinkExpression(
                new LogicalLinkExpression(
                    new LogicalNegationExpression(
                        new AttributePresentTestExpression("A")
                    ),
                    LogicalOperator.or,
                    new AttributePresentTestExpression("B")
                ),
                LogicalOperator.and,
                new LogicalNegationExpression(
                    new LogicalLinkExpression(
                        new AttributePresentTestExpression("C"),
                        LogicalOperator.and,
                        new LogicalNegationExpression(
                            new AttributePresentTestExpression("D")
                        )
                    )
                )
            )
        },
        {
            "userType ne \"Employee\" and not (emails co \"example.com\" or emails.value co \"example.org\")",
            new LogicalLinkExpression(
                new AttributeTextComparisonExpression("userType", ComparisonOperator.ne, "Employee"),
                LogicalOperator.and,
                new LogicalNegationExpression(
                    new LogicalLinkExpression(
                        new AttributeTextComparisonExpression("emails", ComparisonOperator.co, "example.com"),
                        LogicalOperator.or,
                        new AttributeTextComparisonExpression("emails.value", ComparisonOperator.co, "example.org")
                    )
                )
            )
        },
        {
            "emails[type eq \"work\"]",
            new AttributeFilterGroupingExpression(
                "emails",
                new AttributeTextComparisonExpression("type", ComparisonOperator.eq, "work")
            )
        },
        {
            "userType eq \"Employee\" and emails[type eq \"work\" and value co \"@example.com\"]",
            new LogicalLinkExpression(
                new AttributeTextComparisonExpression("userType", ComparisonOperator.eq, "Employee"),
                LogicalOperator.and,
                new AttributeFilterGroupingExpression(
                    "emails",
                    new LogicalLinkExpression(
                        new AttributeTextComparisonExpression("type", ComparisonOperator.eq, "work"),
                        LogicalOperator.and,
                        new AttributeTextComparisonExpression("value", ComparisonOperator.co, "@example.com")
                    )
                )
            )
        },
        {
            "emails[type eq \"work\" and value co \"@example.com\"] or ims[type eq \"xmpp\" and value co \"@foo.com\"]",
            new LogicalLinkExpression(
                new AttributeFilterGroupingExpression(
                    "emails",
                    new LogicalLinkExpression(
                        new AttributeTextComparisonExpression("type", ComparisonOperator.eq, "work"),
                        LogicalOperator.and,
                        new AttributeTextComparisonExpression("value", ComparisonOperator.co, "@example.com")
                    )
                ),
                LogicalOperator.or,
                new AttributeFilterGroupingExpression(
                    "ims",
                    new LogicalLinkExpression(
                        new AttributeTextComparisonExpression("type", ComparisonOperator.eq, "xmpp"),
                        LogicalOperator.and,
                        new AttributeTextComparisonExpression("value", ComparisonOperator.co, "@foo.com")
                    )
                )
            )
        },
        {
            "emails  [   type  eq      \"work\" and value co \"@example.com\"   ]     or ims [type eq \"xmpp\"    and    value co   \"@foo.com\" ]  ",
            new LogicalLinkExpression(
                new AttributeFilterGroupingExpression(
                    "emails",
                    new LogicalLinkExpression(
                        new AttributeTextComparisonExpression("type", ComparisonOperator.eq, "work"),
                        LogicalOperator.and,
                        new AttributeTextComparisonExpression("value", ComparisonOperator.co, "@example.com")
                    )
                ),
                LogicalOperator.or,
                new AttributeFilterGroupingExpression(
                    "ims",
                    new LogicalLinkExpression(
                        new AttributeTextComparisonExpression("type", ComparisonOperator.eq, "xmpp"),
                        LogicalOperator.and,
                        new AttributeTextComparisonExpression("value", ComparisonOperator.co, "@foo.com")
                    )
                )
            )
        },
        {
            "A[B eq 12 and ( C gt 5 or D[E ne -3 and F le 41])] and (G[(H eq null or H eq \"12\") and I co \"@foo.com\"] or J pr)",
            new LogicalLinkExpression(
                new AttributeFilterGroupingExpression(
                    "A",
                    new LogicalLinkExpression(
                        new AttributeNumberComparisonExpression("B", ComparisonOperator.eq, new BigDecimal(12)),
                        LogicalOperator.and,
                        new LogicalLinkExpression(
                            new AttributeNumberComparisonExpression("C", ComparisonOperator.gt, new BigDecimal(5)),
                            LogicalOperator.or,
                            new AttributeFilterGroupingExpression(
                                "D",
                                new LogicalLinkExpression(
                                    new AttributeNumberComparisonExpression("E", ComparisonOperator.ne, new BigDecimal(-3)),
                                    LogicalOperator.and,
                                    new AttributeNumberComparisonExpression("F", ComparisonOperator.le, new BigDecimal(41))
                                )
                            )
                        )
                    )
                ),
                LogicalOperator.and,
                new LogicalLinkExpression(
                    new AttributeFilterGroupingExpression(
                        "G",
                        new LogicalLinkExpression(
                            new LogicalLinkExpression(
                                new AttributeNullTestExpression("H", ComparisonOperator.eq),
                                LogicalOperator.or,
                                new AttributeTextComparisonExpression("H", ComparisonOperator.eq, "12")
                            ),
                            LogicalOperator.and,
                            new AttributeTextComparisonExpression("I", ComparisonOperator.co, "@foo.com")
                        )
                    ),
                    LogicalOperator.or,
                    new AttributePresentTestExpression("J")
                )
            )
        },
        {
            "A[B[C[(D eq \"text\" or E [F ne \"blob\"])]]]",
            new AttributeFilterGroupingExpression(
                "A",
                new AttributeFilterGroupingExpression(
                    "B",
                    new AttributeFilterGroupingExpression(
                        "C",
                        new LogicalLinkExpression(
                            new AttributeTextComparisonExpression("D", ComparisonOperator.eq, "text"),
                            LogicalOperator.or,
                            new AttributeFilterGroupingExpression(
                                "E",
                                new AttributeTextComparisonExpression("F", ComparisonOperator.ne, "blob")
                            )
                        )
                    )
                )
            )
        }
    };
  }

  @DataProvider(name = "invalidAttributeFilterGrouping")
  public Object[][] invalidAttributeFilterGrouping() {
    return new Object[][]{
        {
            "not[type eq \"work\"]",
            "Attribute filter grouping with [ ] must be preceded by an attribute path, found logical negation operator"
        },
    };
  }

  @DataProvider(name = "invalidAttributePath")
  public Object[][] invalidAttributePath() {
    return new Object[][]{
        {
            "A.0a pr",
            "The attribute path [A.0a] is not valid. A sub-attribute must start with an alphabetic character."
        },
        {
            "A.b.c pr",
            "The attribute path [A.b.c] is not valid. Attribute paths can have at most one sub-attribute."
        },
        {
            "A. pr",
            "The attribute path [A.] is not valid. A sub-attribute must be provided after the period."
        },
        {
            "urn:ietf:params:scim:schemas:core:2.0:User:name. pr",
            "The attribute path [urn:ietf:params:scim:schemas:core:2.0:User:name.] is not valid. A sub-attribute must be provided after the period."
        },
        {
            "urn:ietf:params:scim:schemas:core:2.0:User:name.firstName.initial pr",
            "The attribute path [urn:ietf:params:scim:schemas:core:2.0:User:name.firstName.initial] is not valid. Attribute paths can have at most one sub-attribute."
        },
        {
            "urn:ietf:params:scim:schemas:core:2.0:User:name.0th pr",
            "The attribute path [urn:ietf:params:scim:schemas:core:2.0:User:name.0th] is not valid. A sub-attribute must start with an alphabetic character."
        },
        {
            "A.b.c[D pr]",
            "The attribute path [A.b.c] is not valid. Attribute paths can have at most one sub-attribute."
        },
    };
  }

  @DataProvider(name = "invalidComparisonOperator")
  public Object[][] invalidComparisonOperator() {
    return new Object[][]{
        {
            "A lw 8",
            "No comparison operator for [lw]"
        },
        {
            "A lt false",
            "[lt] is not a valid operator for a boolean comparison"
        },
        {
            "A lt null",
            "[lt] is not a valid operator for a null comparison"
        },
        {
            "A co \"2013-04-16T09:14:02Z\"",
            "[co] is not a valid operator for a date comparison"
        },
        {
            "A sw \"2013-04-16T09:14:02Z\"",
            "[sw] is not a valid operator for a date comparison"
        },
        {
            "A ew \"2013-04-16T09:14:02Z\"",
            "[ew] is not a valid operator for a date comparison"
        },
        {
            "A co 0",
            "[co] is not a valid operator for a number comparison"
        },
        {
            "A sw 0",
            "[sw] is not a valid operator for a number comparison"
        },
        {
            "A ew 0",
            "[ew] is not a valid operator for a number comparison"
        },
        {
            "A co 5",
            "[co] is not a valid operator for a number comparison"
        },
        {
            "A sw 5",
            "[sw] is not a valid operator for a number comparison"
        },
        {
            "A ew 5",
            "[ew] is not a valid operator for a number comparison"
        },
        {
            "A co 12.3",
            "[co] is not a valid operator for a number comparison"
        },
        {
            "A sw 12.3",
            "[sw] is not a valid operator for a number comparison"
        },
        {
            "A ew 12.3",
            "[ew] is not a valid operator for a number comparison"
        },
        {
            "A co 12e3",
            "[co] is not a valid operator for a number comparison"
        },
        {
            "A sw 12e3",
            "[sw] is not a valid operator for a number comparison"
        },
        {
            "A ew 12e3",
            "[ew] is not a valid operator for a number comparison"
        },
    };
  }

  @DataProvider(name = "invalidComparisonValue")
  public Object[][] invalidComparisonValue() {
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
        {
            "A eq 12e+",
            "[12e+] is not a valid comparison value"
        },
    };
  }

  @DataProvider(name = "invalidGrouping")
  public Object[][] invalidGrouping() {
    return new Object[][]{
        {
            // Extra opening parenthesis
            "(A pr and B pr",
            "Unclosed parenthesis in filter [(A pr and B pr]"
        },
        {
            // Extra closed parenthesis after attribute
            "(A pr or B pr) and C pr)",
            "Extra closed parenthesis at [(A pr or B pr) and C pr)]"
        },
        {
            // Extra closed parenthesis after attribute
            "(A pr or B pr) and C eq true)",
            "Extra closed parenthesis at [(A pr or B pr) and C eq true)]"
        },
        {
            // Extra closed parenthesis after attribute
            "(A pr or B pr) and C eq null)",
            "Extra closed parenthesis at [(A pr or B pr) and C eq null)]"
        },
        {
            // Extra closed parenthesis after attribute
            "(A pr or B pr) and C eq \"work\")",
            "Extra closed parenthesis at [(A pr or B pr) and C eq \"work\")]"
        },
        {
            // Extra closed parenthesis after attribute
            "(A pr or B pr) and C eq 5)",
            "Extra closed parenthesis at [(A pr or B pr) and C eq 5)]"
        },
        {
            // Extra closed parenthesis after attribute
            "(A pr or B pr) and C eq 5.0)",
            "Extra closed parenthesis at [(A pr or B pr) and C eq 5.0)]"
        },
        {
            // Extra closed parenthesis after attribute
            "(A pr or B pr) and C eq 50e-1)",
            "Extra closed parenthesis at [(A pr or B pr) and C eq 50e-1)]"
        },
        {
            // Extra closed parenthesis after zero
            "(A pr or B pr) and C eq 0)",
            "Extra closed parenthesis at [(A pr or B pr) and C eq 0)]"
        },
        {
            // Extra closed parenthesis after closed parenthesis
            "(A pr or B pr)) and C pr",
            "Extra closed parenthesis at [(A pr or B pr))]"
        },
        {
            // Extra opening bracket
            "Z[A pr and B pr",
            "Unclosed bracket in filter [Z[A pr and B pr]"
        },
        {
            // Extra closed bracket after attribute
            "Z[A pr or B pr] and C pr]",
            "Extra closed bracket at [Z[A pr or B pr] and C pr]]"
        },
        {
            // Extra closed bracket after attribute
            "Z[A pr or B pr] and C eq true]",
            "Extra closed bracket at [Z[A pr or B pr] and C eq true]]"
        },
        {
            // Extra closed bracket after attribute
            "Z[A pr or B pr] and C eq null]",
            "Extra closed bracket at [Z[A pr or B pr] and C eq null]]"
        },
        {
            // Extra closed bracket after attribute
            "Z[A pr or B pr] and C eq \"work\"]",
            "Extra closed bracket at [Z[A pr or B pr] and C eq \"work\"]]"
        },
        {
            // Extra closed bracket after attribute
            "Z[A pr or B pr] and C eq 5]",
            "Extra closed bracket at [Z[A pr or B pr] and C eq 5]]"
        },
        {
            // Extra closed bracket after attribute
            "Z[A pr or B pr] and C eq 5.0]",
            "Extra closed bracket at [Z[A pr or B pr] and C eq 5.0]]"
        },
        {
            // Extra closed bracket after attribute
            "Z[A pr or B pr] and C eq 50e-1]",
            "Extra closed bracket at [Z[A pr or B pr] and C eq 50e-1]]"
        },
        {
            // Extra closed bracket after zero
            "Z[A pr or B pr] and C eq 0]",
            "Extra closed bracket at [Z[A pr or B pr] and C eq 0]]"
        },
        {
            // Extra closed bracket after closed bracket
            "Z[A pr or B pr]] and C pr",
            "Extra closed bracket at [Z[A pr or B pr]]]"
        },
    };
  }

  @DataProvider(name = "invalidLogicalOperator")
  public Object[][] invalidLogicalOperator() {
    return new Object[][]{
        {
            "A pr andr B pr",
            "No logical operator for [andr]"
        },
        {
            "A pr ord B pr",
            "No logical operator for [ord]"
        },
    };
  }

  @DataProvider(name = "invalidState")
  public Object[][] invalidState() {
    return new Object[][]{
        {
            ". pr",
            "Invalid state transition at [.]"
        },
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
            // Leading zero must be followed by a decimal or exponent
            "A eq -01",
            "Invalid state transition at [A eq -01]"
        },
        {
            // Minus sign means a number should be next
            "A eq -B",
            "Invalid state transition at [A eq -B]"
        },
        {
            // Mixing number and letter
            "A eq 45B",
            "Invalid state transition at [A eq 45B]"
        },
        {
            // Leading zero must be followed by decimal or exponent
            "A eq 001",
            "Invalid state transition at [A eq 00]"
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
            // Exponent must have value
            "A eq 12e",
            "Invalid state transition at [A eq 12e]"
        },
        {
            // Invalid escape
            "A eq \"\\c\"",
            "Invalid state transition at [A eq \"\\c]"
        },
        {
            // No linking logical operator
            "A pr B pr",
            "Invalid state transition at [A pr B]"
        },
        {
            // Invalid characters in logical operator
            "A pr anb B pr",
            "Invalid state transition at [A pr anb]"
        },
        {
            // Negation must be followed by (
            "not A pr",
            "Invalid state transition at [not A]"
        },
        {
            // This case should use []
            "Z(A pr)",
            "Invalid state transition at [Z(]"
        },
        {
            // An open bracket must be followed by an alphabetic character
            "Z[0A pr]",
            "Invalid state transition at [Z[0]"
        },
        {
            // An open parenthesis must be followed by an alphabetic character
            "(0A pr)",
            "Invalid state transition at [(0]"
        },
        {
            // A closing bracket must be followed by a space or another group closing marker
            "Z[A pr]and",
            "Invalid state transition at [Z[A pr]a]"
        },
        {
            // A closing parenthesis must be followed by a space or another group closing marker
            "(A pr)and",
            "Invalid state transition at [(A pr)a]"
        },
        {
            // Cannot double-up opening brackets
            "Z[[A pr]]",
            "Invalid state transition at [Z[[]"
        }
    };
  }

  @Test(dataProvider = "goodData")
  public void parseGood(String filter, Expression expected) {
    Expression actual = parser.parse(filter);
    assertEquals(expected, actual);
  }

  @Test(dataProvider = "invalidAttributeFilterGrouping")
  public void parseInvalidAttributeFilterGrouping(String filter, String expected) {
    try {
      parser.parse(filter);
      fail("Expected exception for filter [" + filter + "]");
    } catch (AttributeFilterGroupingException e) {
      assertEquals(expected, e.getMessage());
    }
  }

  @Test(dataProvider = "invalidAttributePath")
  public void parseInvalidAttributePath(String filter, String expected) {
    try {
      parser.parse(filter);
      fail("Expected exception for filter [" + filter + "]");
    } catch (AttributePathException e) {
      assertEquals(expected, e.getMessage());
    }
  }

  @Test(dataProvider = "invalidComparisonOperator")
  public void parseInvalidComparisonOperator(String filter, String expected) {
    try {
      parser.parse(filter);
      fail("Expected exception for filter [" + filter + "]");
    } catch (ComparisonOperatorException e) {
      assertEquals(expected, e.getMessage());
    }
  }

  @Test(dataProvider = "invalidComparisonValue")
  public void parseInvalidComparisonValue(String filter, String expected) {
    try {
      parser.parse(filter);
      fail("Expected exception for filter [" + filter + "]");
    } catch (ComparisonValueException e) {
      assertEquals(expected, e.getMessage());
    }
  }

  @Test(dataProvider = "invalidGrouping")
  public void parseInvalidGrouping(String filter, String expected) {
    try {
      parser.parse(filter);
      fail("Expected exception for filter [" + filter + "]");
    } catch (GroupingException e) {
      assertEquals(expected, e.getMessage());
    }
  }

  @Test(dataProvider = "invalidLogicalOperator")
  public void parseInvalidLogicalOperator(String filter, String expected) {
    try {
      parser.parse(filter);
      fail("Expected exception for filter [" + filter + "]");
    } catch (LogicalOperatorException e) {
      assertEquals(expected, e.getMessage());
    }
  }

  @Test(dataProvider = "invalidState")
  public void parseInvalidState(String filter, String expected) {
    try {
      parser.parse(filter);
      throw new AssertionError("Expected exception for filter [" + filter + "]");
    } catch (InvalidStateException e) {
      assertEquals(expected, e.getMessage());
    }
  }

  private void assertDateEquals(String s, ZonedDateTime z) {
    ZonedDateTime actual = SCIMDateTools.parse(s);
    long ms1 = z.toInstant().toEpochMilli();
    long ms2 = actual.toInstant().toEpochMilli();
    if (ms1 != ms2) {
      throw new AssertionError("Expected [" + ms1 + "] but found [" + ms2 + "]");
    }
  }
}
