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
            "A:\\-0.5"
        },
        {
            "A eq 0E10",
            "A:0E+10"
        },
        {
            "A eq -121.45e+2",
            "A:\\-12145"
        },
        {
            "A eq \") ((( ..eq pr 00.1.1.90)) (\"",
            "A:\") ((( ..eq pr 00.1.1.90)) (\""
        },
        {
            "meta.lastModified eq \"2011-05-13T04:42:34Z\"",
            "meta.lastModified:[2011-05-13T04:42:34.000Z TO 2011-05-13T04:42:34.000Z]"
        },
        {
            "meta.lastModified ge \"2011-05-13T04:42:34Z\"",
            "meta.lastModified:[2011-05-13T04:42:34.000Z TO *]"
        },
        {
            "meta.lastModified gt \"2011-05-13T04:42:34Z\"",
            "meta.lastModified:{2011-05-13T04:42:34.000Z TO *]"
        },
        {
            "meta.lastModified le \"2011-05-13T04:42:34Z\"",
            "meta.lastModified:[* TO 2011-05-13T04:42:34.000Z]"
        },
        {
            "meta.lastModified lt \"2011-05-13T04:42:34Z\"",
            "meta.lastModified:[* TO 2011-05-13T04:42:34.000Z}"
        },
        {
            "meta.lastModified lt \"2011-05-13T04:42:34.061Z\"",
            "meta.lastModified:[* TO 2011-05-13T04:42:34.061Z}"
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
        {
            "A pr and B pr or C pr and D pr or E pr or F pr",
            "(((_exists_:A AND _exists_:B) OR (_exists_:C AND _exists_:D)) OR (_exists_:E OR _exists_:F))"
        },
        {
            "A pr and B pr and C pr or D pr and E pr or F pr",
            "(((_exists_:A AND (_exists_:B AND _exists_:C)) OR (_exists_:D AND _exists_:E)) OR _exists_:F)"
        },
        {
            "((((((A pr))))))",
            "_exists_:A"
        },
        {
            "userType eq \"Employee\" and (emails co \"example.com\" or emails.value co \"example.org\")",
            "(userType:\"Employee\" AND (emails:\"example.com\" OR emails.value:\"example.org\"))"
        },
        {
            "userType eq \"Employee\" and (emails.type eq \"work\")",
            "(userType:\"Employee\" AND emails.type:\"work\")"
        },
        {
            "(A pr or B pr)",
            "(_exists_:A OR _exists_:B)"
        },
        {
            "(A eq 5 or B eq 0)",
            "(A:5 OR B:0)"
        },
        {
            "(A eq 5 or B eq 10)",
            "(A:5 OR B:10)"
        },
        {
            "(A eq 5.0 or B eq 10.0)",
            "(A:5.0 OR B:10.0)"
        },
        {
            "(A eq 50e-1 or B eq 100e-1)",
            "(A:5.0 OR B:10.0)"
        },
        {
            "(A eq true or B eq true)",
            "(A:true OR B:true)"
        },
        {
            "(A eq null or B eq null)",
            "(A:null OR B:null)"
        },
        {
            "Z[A pr or B pr]",
            "(_exists_:Z.A OR _exists_:Z.B)"
        },
        {
            "Z[A eq 5 or B eq 0]",
            "(Z.A:5 OR Z.B:0)"
        },
        {
            "Z[A eq 5 or B eq 10]",
            "(Z.A:5 OR Z.B:10)"
        },
        {
            "Z[A eq 5.0 or B eq 10.0]",
            "(Z.A:5.0 OR Z.B:10.0)"
        },
        {
            "Z[A eq 50e-1 or B eq 100e-1]",
            "(Z.A:5.0 OR Z.B:10.0)"
        },
        {
            "Z[A eq true or B eq true]",
            "(Z.A:true OR Z.B:true)"
        },
        {
            "Z[A eq null or B eq null]",
            "(Z.A:null OR Z.B:null)"
        },
        {
            "Z[A pr and B pr or C pr]",
            "((_exists_:Z.A AND _exists_:Z.B) OR _exists_:Z.C)"
        },
        {
            "Z[A pr or B pr and C pr]",
            "(_exists_:Z.A OR (_exists_:Z.B AND _exists_:Z.C))"
        },
        {
            " (  ( A pr or B pr ) and   ( C pr and ( D   pr   ) )    ) ",
            "((_exists_:A OR _exists_:B) AND (_exists_:C AND _exists_:D))"
        },
        {
            "not (A pr)",
            "!(_exists_:A)"
        },
        {
            "not (A pr and B pr)",
            "!((_exists_:A AND _exists_:B))"
        },
        {
            " (  ( not (A pr) or B pr ) and  not   ( C pr and not ( D   pr   ) )    ) ",
            "((!(_exists_:A) OR _exists_:B) AND !((_exists_:C AND !(_exists_:D))))"
        },
        {
            "userType ne \"Employee\" and not (emails co \"example.com\" or emails.value co \"example.org\")",
            "(!(userType:\"Employee\") AND !((emails:\"example.com\" OR emails.value:\"example.org\")))"
        },
        {
            "emails[type eq \"work\"]",
            "emails.type:\"work\""
        },
        {
            "userType eq \"Employee\" and emails[type eq \"work\" and value co \"@example.com\"]",
            "(userType:\"Employee\" AND (emails.type:\"work\" AND emails.value:\"@example.com\"))"
        },
        {
            "emails[type eq \"work\" and value co \"@example.com\"] or ims[type eq \"xmpp\" and value co \"@foo.com\"]",
            "((emails.type:\"work\" AND emails.value:\"@example.com\") OR (ims.type:\"xmpp\" AND ims.value:\"@foo.com\"))"
        },
        {
            "emails  [   type  eq      \"work\" and value co \"@example.com\"   ]     or ims [type eq \"xmpp\"    and    value co   \"@foo.com\" ]  ",
            "((emails.type:\"work\" AND emails.value:\"@example.com\") OR (ims.type:\"xmpp\" AND ims.value:\"@foo.com\"))"
        },
        {
            "A[B eq 12 and ( C gt 5 or D[E ne -3 and F le 41])] and (G[(H eq null or H eq \"12\") and I co \"@foo.com\"] or J pr)",
            "((A.B:12 AND (A.C:>5 OR (!(A.D.E:\\-3) AND A.D.F:<=41))) AND (((G.H:null OR G.H:\"12\") AND G.I:\"@foo.com\") OR _exists_:J))"
        },
        {
            "A[B[C[(D eq \"text\" or E [F ne \"blob\"])]]]",
            "(A.B.C.D:\"text\" OR !(A.B.C.E.F:\"blob\"))"
        }
    };
  }

  @Test(dataProvider = "data")
  public void transformGood(String scimFilter, String expected) {
    Expression expression = parser.parse(scimFilter);
    String actual = ElasticsearchTransformer.transform(expression);
    assertEquals(expected, actual);
  }
}
