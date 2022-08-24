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
        int tokenEnd = Math.min(s.indexOf('['), s.indexOf(' '));
        return new SCIMParserToken(attribute, s.substring(tokenEnd).trim(), s.substring(0, tokenEnd));
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
        int tokenEnd = s.indexOf(' ');
        String token = s.substring(0, tokenEnd);
        // The `pr` operator does not have an operator value
        return token.equals("pr")
            ? new SCIMParserToken(unaryOp, s.substring(tokenEnd).trim(), token)
            : new SCIMParserToken(op, s.substring(tokenEnd).trim(), token);
      }
    }
  },

  unaryOp {
    @Override
    public SCIMParserToken next(String s) {
      return null;
    }
  },

  op {
    @Override
    public SCIMParserToken next(String s) {
      return null;
    }
  },

  opValue {
    @Override
    public SCIMParserToken next(String s) {
      return null;
    }
  },

  openBracket {
    @Override
    public SCIMParserToken next(String s) {
      return null;
    }
  },

  openParen {
    @Override
    public SCIMParserToken next(String s) {
      return null;
    }
  };

  /**
   * Read the next token, returning the value and the next state of the parser
   *
   * @param s The input string, starting from the current position
   * @return the token value and next state of the parser.
   */
  public abstract SCIMParserToken next(String s);
}
