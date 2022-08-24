package io.fusionauth.scim.parser;

/**
 * @author Daniel DeGroff
 */
public class SCIMFilterParser {

  public FilterGroup parse(String filter) {
    SCIMParserToken token = new SCIMParserToken(SCIMParserState.start, filter.trim(), null);
    FilterGroup result = new FilterGroup();

    while (!token.remaining.isEmpty()) {
      token = token.state.next(token.remaining);
    }

    return result;
  }

}
