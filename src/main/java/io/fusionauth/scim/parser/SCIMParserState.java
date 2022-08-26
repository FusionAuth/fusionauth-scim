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
      return invalidState;
    }
  },
  attributePath {
    @Override
    public SCIMParserState next(char c) {
      if (Character.isAlphabetic(c) || Character.isDigit(c) || c == '_' || c == '-' || c == ':') {
        return attributePath;
      } else if (c == '.') {
        return beforeSubAttribute;
      } else if (c == ' ') {
        return beforeOperator;
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
  beforeSubAttribute {
    @Override
    public SCIMParserState next(char c) {
      if (Character.isAlphabetic(c)) {
        return subAttribute;
      }
      return invalidState;
    }
  },
  comparisonOperator {
    @Override
    public SCIMParserState next(char c) {
      return null;
    }
  },
  filterStart {
    @Override
    public SCIMParserState next(char c) {
      if (Character.isAlphabetic(c)) {
        return attributePath;
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
  subAttribute {
    @Override
    public SCIMParserState next(char c) {
      if (Character.isAlphabetic(c) || Character.isDigit(c) || c == '_' || c == '-') {
        return subAttribute;
      } else if (c == ' ') {
        return beforeOperator;
      }
      return invalidState;
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
