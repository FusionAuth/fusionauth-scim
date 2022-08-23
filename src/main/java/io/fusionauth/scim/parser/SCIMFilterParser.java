package io.fusionauth.scim.parser;

import static io.fusionauth.scim.parser.SCIMParserState.space;

/**
 * @author Daniel DeGroff
 */
public class SCIMFilterParser {

  public FilterResult parse(String filter) {
    char[] source = filter.toCharArray();

    SCIMParserState state = SCIMParserState.token;
    StringBuilder sb = new StringBuilder();
    FilterResult result = new FilterResult();

    TokenMode mode = TokenMode.attribute;
    int variableStart = 0;

    // Note:
    // 1. Always start as token.
    // 2. The token type will depend upon context.
    // 3. Mode will indicate what type of token.
    //

    // Token rules:
    //   1. First token has to be the attribute to filter on?
    //   2. Followed by the operation.
    //   3. Followed by?

    for (int i = 0; i < source.length; ) {
      //noinspection EnhancedSwitchMigration
      switch (state) {
        case attribute:
          state = state.next(source[i]);
          if (state == space) {
            result.attribute = sb.toString();
            sb.setLength(0);
            mode = TokenMode.op;
          }
          i++;
          break;
        case attributeValue:
          state = state.next(source[i]);
          if (state == SCIMParserState.attributeValue) {
            sb.append(source[i]);
          }
          i++;
          break;

        case escape:
          // During escape, increment first
          i++;
          state = state.next(source[i]);
          if (state == SCIMParserState.escape) {
            sb.append(source[i]);
          } else {
            // Skip over the escaped character
            sb.append(source[i]);
            i++;
          }
          break;

        case openParen:
          i++;
          break;

        case closeParen:
          i++;
          break;

        case space:
          state = state.next(source[i]);
          if (state != space) {
            result.attribute = sb.toString();
            sb.setLength(0);
          }
          i++;
          break;

        case openSquareBracket:
          state = state.next(source[i]);
          // Opening square bracket, collect the attribute
          if (sb.length() > 0) {
            result.attribute = sb.toString();
            sb.setLength(0);
          }
          i++;
          break;

        case closeSquareBracket:
          state = state.next(source[i]);
          i++;
          break;

        default:
          throw new IllegalStateException("Unexpected state [" + state + "] during kickstart processing");
      }
    }

    return result;
  }

}
