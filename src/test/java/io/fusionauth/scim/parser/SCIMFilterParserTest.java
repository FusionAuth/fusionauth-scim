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
            new FilterResult()
                .with(r -> r.attribute = "userName")
                .with(r -> r.op = "eq")
                .with(r -> r.value = "bjensen")},
        {"name.familyName co \"O'Malley\"",
            new FilterResult()
                .with(r -> r.attribute = "name.familyName")
                .with(r -> r.op = "co")
                .with(r -> r.value = "O'Malley")},
        {"userName sw \"J\"",
            new FilterResult()
                .with(r -> r.attribute = "userName")
                .with(r -> r.op = "sw")
                .with(r -> r.value = "J")},
        {"urn:ietf:params:scim:schemas:core:2.0:User:userName sw \"J\"",
            new FilterResult()
                // Note, the fully qualified user schema is implied if not provided. Not sure if we should capture it or not yet.
                .with(r -> r.attributeSchema = "urn:ietf:params:scim:schemas:core:2.0:User")
                .with(r -> r.attribute = "userName")
                .with(r -> r.op = "sw")
                .with(r -> r.value = "J")},
        {"title pr",
            new FilterResult()
                .with(r -> r.attribute = "title")
                .with(r -> r.op = "pr")},
        {"meta.lastModified gt \"2011-05-13T04:42:34Z\"",
            new FilterResult()
                .with(r -> r.attribute = "meta.lastModified")
                .with(r -> r.op = "gt")
                .with(r -> r.value = "2011-05-13T04:42:34Z")},
        {"meta.lastModified ge \"2011-05-13T04:42:34Z\"",
            new FilterResult()
                .with(r -> r.attribute = "meta.lastModified")
                .with(r -> r.op = "ge")
                .with(r -> r.value = "2011-05-13T04:42:34Z")},
        {"meta.lastModified lt \"2011-05-13T04:42:34Z\"",
            new FilterResult()
                .with(r -> r.attribute = "meta.lastModified")
                .with(r -> r.op = "lt")
                .with(r -> r.value = "2011-05-13T04:42:34Z")},
        {"meta.lastModified le \"2011-05-13T04:42:34Z\"",
            new FilterResult()
                .with(r -> r.attribute = "meta.lastModified")
                .with(r -> r.op = "le")
                .with(r -> r.value = "2011-05-13T04:42:34Z")},
        // TODO : Need to handle ANDs and ORs
        {"title pr and userType eq \"Employee\"",
            new FilterResult()
                .with(r -> r.attribute = "title")
                .with(r -> r.op = "pr")
                .with(r -> r.value = "pr")},
        {"title pr or userType eq \"Intern\"",
            new FilterResult()
                .with(r -> r.attribute = "title")
                .with(r -> r.op = "pr")},
        {"schemas eq \"urn:ietf:params:scim:schemas:extension:enterprise:2.0:User\"",
            new FilterResult()
                .with(r -> r.attribute = "schemas")
                .with(r -> r.op = "eq")},
        {"userType eq \"Employee\" and (emails co \"example.com\" or emails.value co \"example.org\")",
            new FilterResult()
                .with(r -> r.attribute = "title")
                .with(r -> r.op = "pr")},
        {"userType ne \"Employee\" and not (emails co \"example.com\" or emails.value co \"example.org\")",
            new FilterResult()
                .with(r -> r.attribute = "title")
                .with(r -> r.op = "pr")},
        {"userType eq \"Employee\" and (emails.type eq \"work\")",
            new FilterResult()
                .with(r -> r.attribute = "title")
                .with(r -> r.op = "pr")},
        {"userType eq \"Employee\" and emails[type eq \"work\" and value co \"@example.com\"]",
            new FilterResult()
                .with(r -> r.attribute = "title")
                .with(r -> r.op = "pr")},
        {"emails[type eq \"work\" and value co \"@example.com\"] or ims[type eq \"xmpp\" and value co \"@foo.com\"]",
            new FilterResult()
                .with(r -> r.attribute = "title")
                .with(r -> r.op = "pr")}
    };
  }

  @Test(dataProvider = "data")
  public void parse(String filter, FilterResult expected) {
    FilterResult actual = parser.parse(filter);
    assertEquals(expected, actual);
  }
}
