package io.fusionauth.scim.parser;

import static io.fusionauth.scim.parser.SCIMParserState.space;

/**
 * @author Daniel DeGroff
 */
public class SCIMFilterParser {

  public FilterResult parse(String filter) {
    char[] source = filter.toCharArray();

    SCIMParserState state = SCIMParserState.attributeValue;
    StringBuilder sb = new StringBuilder();
    FilterResult result = new FilterResult();

    int variableStart = 0;

    for (int i = 0; i < source.length; ) {
      //noinspection EnhancedSwitchMigration
      switch (state) {
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
