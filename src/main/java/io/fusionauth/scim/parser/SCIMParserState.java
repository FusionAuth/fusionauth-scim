package io.fusionauth.scim.parser;

/**
 * SCIM Filter parser
 * <p>
 * <a href="https://www.rfc-editor.org/rfc/rfc7644#section-3.4.2.2">...</a>
 *
 * @author Daniel DeGroff
 */
public enum SCIMParserState {
  escape {
    @Override
    public SCIMParserState next(char c) {
      return attributeValue;
    }
  },

  start {
    @Override
    public SCIMParserState next(char c) {
      if (c == '(') {
        return openParen;
      } else if (c == '[') {
        return openSquareBracket;
      } else if (c == '\\') {
        return escape;
      } else {
        return attributeValue;
      }
    }
  },

  openParen {
    @Override
    public SCIMParserState next(char c) {
      if (c == ')') {
        return SCIMParserState.closeParen;
      }

      return SCIMParserState.attributeValue;
    }
  },

  closeParen {
    @Override
    public SCIMParserState next(char c) {
      if (c == '(') {
        return SCIMParserState.openParen;
      } else if (c == '[') {
        return SCIMParserState.openSquareBracket;
      } else {
        return SCIMParserState.attributeValue;
      }
    }
  },

  openSquareBracket {
    @Override
    public SCIMParserState next(char c) {
      if (c == '(') {
        return SCIMParserState.openParen;
      } else if (c == '[') {
        return SCIMParserState.openSquareBracket;
      } else {
        return SCIMParserState.attributeValue;
      }
    }
  },

  closeSquareBracket {
    @Override
    public SCIMParserState next(char c) {
      if (c == '(') {
        return SCIMParserState.openParen;
      } else if (c == '[') {
        return SCIMParserState.openSquareBracket;
      } else {
        return SCIMParserState.attributeValue;
      }
    }
  },

  space {
    @Override
    public SCIMParserState next(char c) {
      if (c == ' ') {
        return SCIMParserState.space;
      } else if (c == '(') {
        return SCIMParserState.openParen;
      } else if (c == '[') {
        return SCIMParserState.openSquareBracket;
      } else {
        return SCIMParserState.attributeValue;
      }
    }
  },

  attribute {
    @Override
    public SCIMParserState next(char c) {
      if (c == '\\') {
        return escape;
      } else if (c == '[') {
        return openSquareBracket;
      } else if (c == '(') {
        return openParen;
      } else {
        return attributeValue;
      }
    }
  },

  attributeValue {
    @Override
    public SCIMParserState next(char c) {
      if (c == '\\') {
        return escape;
      } else if (c == '[') {
        return openSquareBracket;
      } else if (c == '(') {
        return openParen;
      } else {
        return attributeValue;
      }
    }
  },

  operation {
    @Override
    public SCIMParserState next(char c) {
      if (c == ' ') {
        return space;
      } else {
        return operation;
      }
    }
  },

  complete {
    @Override
    public SCIMParserState next(char c) {
      return complete;
    }
  };

  /**
   * Transition the parser to the next state based upon the current character.
   *
   * @param c the current character on the input string.
   * @return the next state of the parser.
   */
  public abstract SCIMParserState next(char c);
}
