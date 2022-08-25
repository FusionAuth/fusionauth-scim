package io.fusionauth.scim.parser;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import static org.testng.AssertJUnit.assertEquals;

/**
 * @author Daniel DeGroff
 */
public class SCIMFilterParserTest {
  private final SCIMFilterParser parser = new SCIMFilterParser();

  @DataProvider(name = "data")
  public Object[][] data() {
    return new Object[][]{
        // Examples from : https://www.rfc-editor.org/rfc/rfc7644.html
        // Examples: https://confluence.soffid.com/display/SOF/5.+SCIM+filter+language#id-5.SCIMfilterlanguage-SCIMfiltering
        {"userName eq \"bjensen\"",
            new FilterGroup().addFilter(
                new Filter("userName")
                    .with(f -> f.op = Op.eq)
                    .with(f -> f.valueType = ValueType.text)
                    .with(f -> f.value = "bjensen")
            )
        },
        {"userName eq true",
            new FilterGroup().addFilter(
                new Filter("userName")
                    .with(f -> f.op = Op.eq)
                    .with(f -> f.valueType = ValueType.bool)
                    .with(f -> f.value = "true")
            )
        },
        {"userName eq false",
            new FilterGroup().addFilter(
                new Filter("userName")
                    .with(f -> f.op = Op.eq)
                    .with(f -> f.valueType = ValueType.bool)
                    .with(f -> f.value = "false")
            )
        },
        {"userName eq 1234",
            new FilterGroup().addFilter(
                new Filter("userName")
                    .with(f -> f.op = Op.eq)
                    .with(f -> f.valueType = ValueType.number)
                    .with(f -> f.value = "1234")
            )
        },
        {"userName eq -123.42",
            new FilterGroup().addFilter(
                new Filter("userName")
                    .with(f -> f.op = Op.eq)
                    .with(f -> f.valueType = ValueType.number)
                    .with(f -> f.value = "-123.42")
            )
        },
        {"userName eq null",
            new FilterGroup().addFilter(
                new Filter("userName")
                    .with(f -> f.op = Op.eq)
                    .with(f -> f.valueType = ValueType.nul)
                    .with(f -> f.value = "null")
            )
        },
        {"name.familyName co \"O'Malley\"",
            new FilterGroup().addFilter(
                new Filter("name.familyName")
                    .with(f -> f.op = Op.co)
                    .with(f -> f.valueType = ValueType.text)
                    .with(f -> f.value = "O'Malley")
            )
        },
        {"userName sw \"J\"",
            new FilterGroup().addFilter(
                new Filter("userName")
                    .with(f -> f.op = Op.sw)
                    .with(f -> f.valueType = ValueType.text)
                    .with(f -> f.value = "J")
            )
        },
        {"urn:ietf:params:scim:schemas:core:2.0:User:userName sw \"J\"",
            new FilterGroup().addFilter(
                new Filter("")
                    // Override the values for this test to check that schema and attribute are split properly
                    .with(f -> f.schema = "urn:ietf:params:scim:schemas:core:2.0:User")
                    .with(f -> f.attribute = "userName")
                    .with(f -> f.op = Op.sw)
                    .with(f -> f.valueType = ValueType.text)
                    .with(f -> f.value = "J")
            )
        },
        {"title pr",
            new FilterGroup().addFilter(
                new Filter("title")
                    .with(f -> f.op = Op.pr)
                    .with(f -> f.valueType = ValueType.none)
            )
        },
        {"meta.lastModified gt \"2011-05-13T04:42:34Z\"",
            new FilterGroup().addFilter(
                new Filter("meta.lastModified")
                    .with(f -> f.op = Op.gt)
                    .with(f -> f.valueType = ValueType.date)
                    .with(f -> f.value = "2011-05-13T04:42:34Z")
            )
        },
        {"meta.lastModified ge \"2011-05-13T04:42:34Z\"",
            new FilterGroup().addFilter(
                new Filter("meta.lastModified")
                    .with(f -> f.op = Op.ge)
                    .with(f -> f.valueType = ValueType.date)
                    .with(f -> f.value = "2011-05-13T04:42:34Z")
            )
        },
        {"meta.lastModified lt \"2011-05-13T04:42:34Z\"",
            new FilterGroup().addFilter(
                new Filter("meta.lastModified")
                    .with(f -> f.op = Op.lt)
                    .with(f -> f.valueType = ValueType.date)
                    .with(f -> f.value = "2011-05-13T04:42:34Z")
            )
        },
        {"meta.lastModified le \"2011-05-13T04:42:34Z\"",
            new FilterGroup().addFilter(
                new Filter("meta.lastModified")
                    .with(f -> f.op = Op.le)
                    .with(f -> f.valueType = ValueType.date)
                    .with(f -> f.value = "2011-05-13T04:42:34Z")
            )
        },
        {"title pr and userType eq \"Employee\"",
            new FilterGroup()
                .with(g -> g.logicalOperator = LogicalOperator.and)
                .addFilter(
                    new Filter("title")
                        .with(f -> f.op = Op.pr)
                        .with(r -> r.valueType = ValueType.none)
                )
                .addFilter(
                new Filter("userType")
                    .with(f -> f.op = Op.eq)
                    .with(f -> f.valueType = ValueType.text)
                    .with(f -> f.value = "Employee")
            )
        },
        {"title pr and userType eq \"Employee\" and meta.lastModified gt \"2011-05-13T04:42:34Z\"",
            new FilterGroup()
                .with(g -> g.logicalOperator = LogicalOperator.and)
                .addFilter(
                    new Filter("title")
                        .with(f -> f.op = Op.pr)
                        .with(r -> r.valueType = ValueType.none)
                )
                .addFilter(
                    new Filter("userType")
                        .with(f -> f.op = Op.eq)
                        .with(f -> f.valueType = ValueType.text)
                        .with(f -> f.value = "Employee")
                )
                .addFilter(
                new Filter("meta.lastModified")
                    .with(f -> f.op = Op.gt)
                    .with(f -> f.valueType = ValueType.date)
                    .with(f -> f.value = "2011-05-13T04:42:34Z")
            )
        },
        {"title pr or userType eq \"Intern\"",
            new FilterGroup()
                .with(g -> g.logicalOperator = LogicalOperator.or)
                .addFilter(
                    new Filter("title")
                        .with(f -> f.op = Op.pr)
                        .with(f -> f.valueType = ValueType.none)
                )
                .addFilter(
                new Filter("userType")
                    .with(f -> f.op = Op.eq)
                    .with(f -> f.valueType = ValueType.text)
                    .with(f -> f.value = "Intern")
            )
        },
        {"schemas eq \"urn:ietf:params:scim:schemas:extension:enterprise:2.0:User\"",
            new FilterGroup().addFilter(
                new Filter("schemas")
                    .with(f -> f.op = Op.eq)
                    .with(f -> f.valueType = ValueType.text)
                    .with(f -> f.value = "urn:ietf:params:scim:schemas:extension:enterprise:2.0:User")
            )
        },
        {"not (userType eq \"Employee\" and title pr)",
            new FilterGroup()
                .with(g -> g.inverted = true)
                .with(g -> g.logicalOperator = LogicalOperator.and)
                .addFilter(
                    new Filter("userType")
                        .with(f -> f.op = Op.eq)
                        .with(f -> f.valueType = ValueType.text)
                        .with(f -> f.value = "Employee")
                )
                .addFilter(
                new Filter("title")
                    .with(f -> f.op = Op.pr)
                    .with(f -> f.valueType = ValueType.none)
            )
        },
        {"userType eq \"Employee\" and (emails co \"example.com\" or emails.value co \"example.org\")",
            new FilterGroup()
                .with(g -> g.logicalOperator = LogicalOperator.and)
                .addFilter(
                    new Filter("userType")
                        .with(f -> f.op = Op.eq)
                        .with(f -> f.valueType = ValueType.text)
                        .with(f -> f.value = "Employee")
                )
                .addSubGroup(new FilterGroup()
                                 .with(g -> g.logicalOperator = LogicalOperator.or)
                                 .addFilter(
                                     new Filter("emails")
                                         .with(f -> f.op = Op.co)
                                         .with(f -> f.valueType = ValueType.text)
                                         .with(f -> f.value = "example.com")
                                 )
                                 .addFilter(
                                     new Filter("emails.value")
                                         .with(f -> f.op = Op.co)
                                         .with(f -> f.valueType = ValueType.text)
                                         .with(f -> f.value = "example.org")
                                 )
            )
        },
        {"userType eq \"Employee\" and (emails co \"example.com\" or not (emails pr))",
            new FilterGroup()
                .with(g -> g.logicalOperator = LogicalOperator.and)
                .addFilter(
                    new Filter("userType")
                        .with(f -> f.op = Op.eq)
                        .with(f -> f.valueType = ValueType.text)
                        .with(f -> f.value = "Employee")
                )
                .addSubGroup(new FilterGroup()
                                 .with(g -> g.logicalOperator = LogicalOperator.or)
                                 .addFilter(
                                     new Filter("emails")
                                         .with(f -> f.op = Op.co)
                                         .with(f -> f.valueType = ValueType.text)
                                         .with(f -> f.value = "example.com")
                                 )
                                 .addSubGroup(new FilterGroup()
                                                  .with(g -> g.inverted = true)
                                                  .addFilter(
                                                      new Filter("emails")
                                                          .with(f -> f.op = Op.pr)
                                                          .with(f -> f.valueType = ValueType.none)
                                                  )
                                 )
            )
        },
        {"userType ne \"Employee\" and not (emails co \"example.com\" or emails.value co \"example.org\")",
            new FilterGroup()
                .with(g -> g.logicalOperator = LogicalOperator.and)
                .addFilter(
                    new Filter("userType")
                        .with(f -> f.op = Op.ne)
                        .with(f -> f.valueType = ValueType.text)
                        .with(f -> f.value = "Employee")
                )
                .addSubGroup(new FilterGroup()
                                 .with(g -> g.inverted = true)
                                 .with(g -> g.logicalOperator = LogicalOperator.or)
                                 .addFilter(
                                     new Filter("emails")
                                         .with(f -> f.op = Op.co)
                                         .with(f -> f.valueType = ValueType.text)
                                         .with(f -> f.value = "example.com")
                                 )
                                 .addFilter(
                                     new Filter("emails.value")
                                         .with(f -> f.op = Op.co)
                                         .with(f -> f.valueType = ValueType.text)
                                         .with(f -> f.value = "example.org")
                                 )
            )
        },
        {"userType eq \"Employee\" and (emails.type eq \"work\")",
            new FilterGroup()
                .with(g -> g.logicalOperator = LogicalOperator.and)
                .addFilter(new Filter("userType")
                               .with(f -> f.op = Op.eq)
                               .with(f -> f.valueType = ValueType.text)
                               .with(f -> f.value = "Employee")
                )
                .addSubGroup(new FilterGroup()
                                 .addFilter(new Filter("emails.type")
                                                .with(f -> f.op = Op.eq)
                                                .with(f -> f.valueType = ValueType.text)
                                                .with(f -> f.value = "work")
                                 )
            )
        },
//        {"userType eq \"Employee\" and emails[type eq \"work\" and value co \"@example.com\"]",
//            new FilterResult()
//                .with(r -> r.attribute = "title")
//                .with(r -> r.op = "pr")},
//        {"emails[type eq \"work\" and value co \"@example.com\"] or ims[type eq \"xmpp\" and value co \"@foo.com\"]",
//            new FilterResult()
//                .with(r -> r.attribute = "title")
//                .with(r -> r.op = "pr")}
    };
  }

  @Test(dataProvider = "data")
  public void parse(String filter, FilterGroup expected) throws Exception {
    FilterGroup actual = parser.parse(filter);
    assertEquals(expected, actual);
  }
}
