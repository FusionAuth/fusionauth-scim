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
        {"userName eq \"bjensen\"",
            new FilterResult()
                .with(r -> r.filterAttribute = "userName")
                .with(r -> r.filterOp = "eq")
                .with(r -> r.filterValue = "bjensen")},
        {"name.familyName co \"O'Malley\"",
            new FilterResult()
                .with(r -> r.filterAttribute = "name.familyName")
                .with(r -> r.filterOp = "co")
                .with(r -> r.filterValue = "O'Malley")},
        {"userName sq \"J\"",
            new FilterResult()
                .with(r -> r.filterAttribute = "userName")
                .with(r -> r.filterOp = "sq")
                .with(r -> r.filterValue = "J")}
    };
  }

// Examples from : https://www.rfc-editor.org/rfc/rfc7644.html

//  filter=urn:ietf:params:scim:schemas:core:2.0:User:userName sw "J"
//
//  filter=title pr
//
//  filter=meta.lastModified gt "2011-05-13T04:42:34Z"
//
//  filter=meta.lastModified ge "2011-05-13T04:42:34Z"
//
//  filter=meta.lastModified lt "2011-05-13T04:42:34Z"
//
//  filter=meta.lastModified le "2011-05-13T04:42:34Z"
//
//  filter=title pr and userType eq "Employee"
//
//  filter=title pr or userType eq "Intern"
//
//  filter=
//  schemas eq "urn:ietf:params:scim:schemas:extension:enterprise:2.0:User"
//
//  filter=userType eq "Employee" and (emails co "example.com" or
//      emails.value co "example.org")
//
//  filter=userType ne "Employee" and not (emails co "example.com" or
//      emails.value co "example.org")
//
//  filter=userType eq "Employee" and (emails.type eq "work")
//
//  filter=userType eq "Employee" and emails[type eq "work" and
//  value co "@example.com"]
//
//  filter=emails[type eq "work" and value co "@example.com"] or
//  ims[type eq "xmpp" and value co "@foo.com"]

  @Test(dataProvider = "data")
  public void parse(String filter, FilterResult expected) {
    FilterResult actual = parser.parse(filter);
    assertEquals(expected, actual);
  }
}
