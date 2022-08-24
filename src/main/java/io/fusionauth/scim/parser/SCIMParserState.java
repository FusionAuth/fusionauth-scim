package io.fusionauth.scim.parser;

/**
 * SCIM Filter parser
 * <p>
 * <a href="https://www.rfc-editor.org/rfc/rfc7644#section-3.4.2.2">...</a>
 *
 * @author Daniel DeGroff
 */
public enum SCIMParserState {
  start {
    @Override
    public SCIMParserToken next(String s) {
      if (s.charAt(0) == '(') {
        return new SCIMParserToken(openParen, s.substring(1), "(");
      } else {
        int tokenEnd = SCIMParserState.tokenEnd(s, '[', ' ', '(');
        String value = s.substring(0, tokenEnd);
        if (value.equals("not")) {
          return new SCIMParserToken(not, s.substring(tokenEnd), value);
        }
        return new SCIMParserToken(attribute, s.substring(tokenEnd), s.substring(0, tokenEnd));
      }
    }
  },

  attribute {
    @Override
    public SCIMParserToken next(String s) {
      if (s.charAt(0) == '[') {
        return new SCIMParserToken(openBracket, s.substring(1), "[");
      } else {
        // Need an operator
        int tokenEnd = SCIMParserState.tokenEnd(s, ' ', ')');
        String token = s.substring(0, tokenEnd);
        // The `pr` operator does not have an operator value
        return token.equals("pr")
            ? new SCIMParserToken(unaryOp, s.substring(tokenEnd), token)
            : new SCIMParserToken(op, s.substring(tokenEnd), token);
      }
    }
  },

  unaryOp {
    @Override
    public SCIMParserToken next(String s) {
      if (s.charAt(0) == ')') {
        return new SCIMParserToken(closeParen, s.substring(1), ")");
      } else if (s.charAt(0) == ']') {
        return new SCIMParserToken(closeBracket, s.substring(1), "]");
      } else {
        // Must be a logical operator to link Filters
        int tokenEnd = SCIMParserState.tokenEnd(s, ' ');
        return new SCIMParserToken(logicOp, s.substring(tokenEnd), s.substring(0, tokenEnd));
      }
    }
  },

  op {
    @Override
    public SCIMParserToken next(String s) {
      int tokenEnd = SCIMParserState.tokenEnd(s, ')', ']', ' ');
      return new SCIMParserToken(opValue, s.substring(tokenEnd), s.substring(0, tokenEnd));
    }
  },

  opValue {
    @Override
    public SCIMParserToken next(String s) {
      if (s.charAt(0) == ')') {
        return new SCIMParserToken(closeParen, s.substring(1), ")");
      } else if (s.charAt(0) == ']') {
        return new SCIMParserToken(closeBracket, s.substring(1), "]");
      } else {
        // Must be a logical operator to link Filters
        int tokenEnd = SCIMParserState.tokenEnd(s, ' ');
        return new SCIMParserToken(logicOp, s.substring(tokenEnd), s.substring(0, tokenEnd));
      }
    }
  },

  logicOp {
    @Override
    public SCIMParserToken next(String s) {
      if (s.charAt(0) == '(') {
        return new SCIMParserToken(openParen, s.substring(1), "(");
      } else {
        int tokenEnd = SCIMParserState.tokenEnd(s, ' ', '[');
        String value = s.substring(0, tokenEnd);
        if (value.equals("not")) {
          return new SCIMParserToken(not, s.substring(tokenEnd), value);
        } else {
          return new SCIMParserToken(attribute, s.substring(tokenEnd), value);
        }
      }
    }
  },

  not {
    @Override
    public SCIMParserToken next(String s) throws Exception {
      if (s.charAt(0) != '(') {
        throw new Exception("[not] operator must be followed by '('");
      }
      return new SCIMParserToken(openParen, s.substring(1), "(");
    }
  },

  openBracket {
    @Override
    public SCIMParserToken next(String s) {
      return null;
    }
  },

  closeBracket {
    @Override
    public SCIMParserToken next(String s) {
      return null;
    }
  },

  openParen {
    @Override
    public SCIMParserToken next(String s) {
      int tokenEnd = SCIMParserState.tokenEnd(s, ' ', '[');
      String value = s.substring(0, tokenEnd);
      if (value.equals("not")) {
        return new SCIMParserToken(not, s.substring(tokenEnd), value);
      } else {
        return new SCIMParserToken(attribute, s.substring(tokenEnd), value);
      }
    }
  },

  closeParen {
    @Override
    public SCIMParserToken next(String s) {
      if (s.charAt(0) == ')') {
        return new SCIMParserToken(closeParen, s.substring(1), ")");
      } else {
        // Must be a logical operator to link Filters
        int tokenEnd = SCIMParserState.tokenEnd(s, ' ');
        return new SCIMParserToken(logicOp, s.substring(tokenEnd), s.substring(0, tokenEnd));
      }
    }
  };

  private static int tokenEnd(String s, char... chars) {
    int result = s.length();
    for (char c : chars) {
      int index = s.indexOf(c);
      if (index != -1 && index < result) {
        result = index;
      }
    }
    return result;
  }

  /**
   * Read the next token, returning the value and the next state of the parser
   *
   * @param s The input string, starting from the current position
   * @return the token value and next state of the parser.
   */
  public abstract SCIMParserToken next(String s) throws Exception;
}
