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
package io.fusionauth.scim.parser;

/**
 * SCIM Filter parser
 * <p>
 * <a href="https://www.rfc-editor.org/rfc/rfc7644#section-3.4.2.2">...</a>
 *
 * @author Spencer Witt
 */
public enum SCIMParserState {
  afterAttributeExpression {
    @Override
    public SCIMParserState next(char c) {
      if (c == ' ') {
        return afterAttributeExpression;
      } else if (c == 'a' || c == 'o') {
        return logicalOperator;
      } else if (c == ')') {
        return closeParen;
      }
      return invalidState;
    }
  },
  attributePath {
    @Override
    public SCIMParserState next(char c) {
      if (Character.isAlphabetic(c) || Character.isDigit(c) || c == '_' || c == '-' || c == ':' || c == '.') {
        return attributePath;
      } else if (c == ' ') {
        return beforeOperator;
      }
      return invalidState;
    }
  },
  beforeComparisonValue {
    @Override
    public SCIMParserState next(char c) {
      if (c == ' ') {
        return beforeComparisonValue;
      } else if (c == '"') {
        return textValue;
      } else if (c == 't' || c == 'f') {
        return booleanValue;
      } else if (c == 'n') {
        return nullValue;
      } else if (Character.isDigit(c) && c != '0') {
        // A leading zero is not allowed according to RFC 7159
        return numberValue;
      } else if (c == '-') {
        return minus;
      } else if (c == '.') {
        return decimalValue;
      }
      return invalidState;
    }
  },
  beforeOperator {
    @Override
    public SCIMParserState next(char c) {
      if (c == ' ') {
        return beforeOperator;
      } else if (c == 'p') {
        return unaryOperator;
      } else if (c == 'n' || c == 's' || c == 'e' || c == 'c' || c == 'g' || c == 'l') {
        return comparisonOperator;
      }
      return invalidState;
    }
  },
  booleanValue {
    @Override
    public SCIMParserState next(char c) {
      if (c == 'r' || c == 'u' || c == 'e' || c == 'a' || c == 'l' || c == 's') {
        return booleanValue;
      } else if (c == ' ') {
        return afterAttributeExpression;
      }
      return invalidState;
    }
  },
  closeParen {
    @Override
    public SCIMParserState next(char c) {
      if (c == ' ') {
        return afterAttributeExpression;
      } else if (c == ')') {
        return closeParen;
      }
      return invalidState;
    }
  },
  comparisonOperator {
    @Override
    public SCIMParserState next(char c) {
      if (c == 'e' ||
          c == 'o' ||
          c == 'q' ||
          c == 't' ||
          c == 'w'
      ) {
        // The parser must check that the two characters are a valid operator
        return beforeComparisonValue;
      }
      return invalidState;
    }
  },
  escapedText {
    @Override
    public SCIMParserState next(char c) {
      if (c == 't' ||
          c == 'b' ||
          c == 'n' ||
          c == 'r' ||
          c == 'f' ||
          c == '\'' ||
          c == '"' ||
          c == '\\'
      ) {
        return textValue;
      }
      return invalidState;
    }
  },
  filterStart {
    @Override
    public SCIMParserState next(char c) {
      if (Character.isAlphabetic(c)) {
        return attributePath;
      } else if (c == '(') {
        return openParen;
      } else if (c == ' ') {
        return filterStart;
      }
      return invalidState;
    }
  },
  invalidState {
    @Override
    public SCIMParserState next(char c) {
      return invalidState;
    }
  },
  logicalOperator {
    @Override
    public SCIMParserState next(char c) {
      if (c == 'n' || c == 'd' || c == 'r') {
        return logicalOperator;
      } else if (c == ' ') {
        return filterStart;
      }
      return invalidState;
    }
  },
  minus {
    @Override
    public SCIMParserState next(char c) {
      if (Character.isDigit(c) && c != '0') {
        return numberValue;
      }
      return invalidState;
    }
  },
  nullValue {
    @Override
    public SCIMParserState next(char c) {
      // The parser must check that token evaluates to "null"
      if (c == 'u' || c == 'l') {
        return nullValue;
      } else if (c == ' ') {
        return afterAttributeExpression;
      }
      return invalidState;
    }
  },
  numberValue {
    @Override
    public SCIMParserState next(char c) {
      if (Character.isDigit(c)) {
        return numberValue;
      } else if (c == '.') {
        return decimalValue;
      } else if (c == 'e' || c == 'E') {
        return exponentSign;
      } else if (c == ' ') {
        return afterAttributeExpression;
      }
      return invalidState;
    }
  },
  openParen {
    @Override
    public SCIMParserState next(char c) {
      if (Character.isAlphabetic(c)) {
        return attributePath;
      } else if (c == ' ') {
        return filterStart;
      } else if (c == '(') {
        return openParen;
      }
      return invalidState;
    }
  },
  exponentSign {
    @Override
    public SCIMParserState next(char c) {
      if (c == '+' || c == '-' || Character.isDigit(c)) {
        return exponentValue;
      }
      return invalidState;
    }
  },
  exponentValue {
    @Override
    public SCIMParserState next(char c) {
      if (Character.isDigit(c)) {
        return exponentValue;
      } else if (c == ' ') {
        return afterAttributeExpression;
      }
      return invalidState;
    }
  },
  decimalValue {
    @Override
    public SCIMParserState next(char c) {
      if (Character.isDigit(c)) {
        return decimalValue;
      } else if (c == 'e' || c == 'E') {
        return exponentSign;
      } else if (c == ' ') {
        return afterAttributeExpression;
      }
      return invalidState;
    }
  },
  textValue {
    @Override
    public SCIMParserState next(char c) {
      if (c == '\\') {
        return escapedText;
      } else if (c == '"') {
        return afterAttributeExpression;
      }
      return textValue;
    }
  },
  unaryOperator {
    @Override
    public SCIMParserState next(char c) {
      if (c == 'r') {
        return afterAttributeExpression;
      }
      return invalidState;
    }
  };

  /**
   * Return the next state of the parser based on the current state and next character
   *
   * @param c the next character in the SCIM filter string
   * @return the token value and next state of the parser.
   */
  public abstract SCIMParserState next(char c);
}
