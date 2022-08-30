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

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import io.fusionauth.scim.parser.exception.AttributePathException;
import io.fusionauth.scim.parser.exception.ComparisonValueException;
import io.fusionauth.scim.parser.exception.InvalidStateException;
import io.fusionauth.scim.parser.exception.OperatorException;
import io.fusionauth.scim.parser.expression.AttributeBooleanComparisonExpression;
import io.fusionauth.scim.parser.expression.AttributeDateComparisonExpression;
import io.fusionauth.scim.parser.expression.AttributeNullComparisonExpression;
import io.fusionauth.scim.parser.expression.AttributeNumberComparisonExpression;
import io.fusionauth.scim.parser.expression.AttributePresentExpression;
import io.fusionauth.scim.parser.expression.AttributeTextComparisonExpression;
import io.fusionauth.scim.parser.expression.Expression;

/**
 * @author Spencer Witt
 */
public class SCIMFilterParser {

  public Expression parse(String filter) throws InvalidStateException, OperatorException, ComparisonValueException, AttributePathException {
    // Add a trailing space to ensure all tokens are parsed
    char[] source = new char[filter.length() + 1];
    filter.getChars(0, filter.length(), source, 0);
    source[source.length - 1] = ' ';
    SCIMParserState state = SCIMParserState.filterStart;
    String attrPath = null;
    ComparisonOperator attrOp = null;
    Expression currentExpression = null;
    StringBuilder sb = new StringBuilder();

    for (int i = 0; i < source.length; i++) {
      char c = source[i];
      //noinspection EnhancedSwitchMigration
      switch (state) {
        case filterStart:
          state = state.next(c);
          if (state == SCIMParserState.attributePath) {
            sb.append(c);
          }
          break;
        case attributePath:
          state = state.next(c);
          if (state == SCIMParserState.attributePath) {
            sb.append(c);
          } else if (state == SCIMParserState.beforeOperator) {
            attrPath = sb.toString();
            if (!validateAttributePath(attrPath)) {
              throw new AttributePathException("The attribute path [" + attrPath + "] is not valid");
            }
            sb.setLength(0);
          }
          break;
        case beforeOperator:
          state = state.next(c);
          if (state == SCIMParserState.unaryOperator ||
              state == SCIMParserState.comparisonOperator
          ) {
            sb.append(c);
          }
          break;
        case unaryOperator:
          state = state.next(c);
          if (state == SCIMParserState.afterAttributeExpression) {
            sb.append(c);
            try {
              assert ComparisonOperator.valueOf(sb.toString()) == ComparisonOperator.pr;
              currentExpression = new AttributePresentExpression(attrPath);
              attrPath = null;
              sb.setLength(0);
            } catch (IllegalArgumentException e) {
              throw new OperatorException("No operator for [" + sb + "]");
            }
          }
          break;
        case comparisonOperator:
          state = state.next(c);
          if (state == SCIMParserState.beforeComparisonValue) {
            sb.append(c);
            try {
              attrOp = ComparisonOperator.valueOf(sb.toString());
              sb.setLength(0);
            } catch (IllegalArgumentException e) {
              throw new OperatorException("No operator for [" + sb + "]");
            }
          }
          break;
        case beforeComparisonValue:
          state = state.next(c);
          if (state == SCIMParserState.booleanValue ||
              state == SCIMParserState.nullValue ||
              state == SCIMParserState.minus ||
              state == SCIMParserState.numberValue
          ) {
            sb.append(c);
          }
          // Skip appending for beginning of textValue. We don't need the leading "
          break;
        case booleanValue:
          state = state.next(c);
          if (state == SCIMParserState.booleanValue) {
            sb.append(c);
          } else if (state == SCIMParserState.afterAttributeExpression) {
            if (sb.toString().equals("true")) {
              currentExpression = new AttributeBooleanComparisonExpression(attrPath, attrOp, true);
            } else if (sb.toString().equals("false")) {
              currentExpression = new AttributeBooleanComparisonExpression(attrPath, attrOp, false);
            } else {
              throw new ComparisonValueException("[" + sb + "] is not a valid comparison value");
            }
            sb.setLength(0);
            if (attrOp != ComparisonOperator.eq && attrOp != ComparisonOperator.ne) {
              throw new OperatorException("[" + attrOp + "] is not a valid operator for a boolean comparison");
            }
          }
          break;
        case nullValue:
          state = state.next(c);
          if (state == SCIMParserState.nullValue) {
            sb.append(c);
          } else if (state == SCIMParserState.afterAttributeExpression) {
            if (sb.toString().equals("null")) {
              currentExpression = new AttributeNullComparisonExpression(attrPath, attrOp);
              sb.setLength(0);
            } else {
              throw new ComparisonValueException("[" + sb + "] is not a valid comparison value");
            }
          }
          if (attrOp != ComparisonOperator.eq && attrOp != ComparisonOperator.ne) {
            throw new OperatorException("[" + attrOp + "] is not a valid operator for a null comparison");
          }
          break;
        case minus:
          state = state.next(c);
          if (state == SCIMParserState.numberValue) {
            sb.append(c);
          }
          break;
        case numberValue:
          state = state.next(c);
          if (state == SCIMParserState.numberValue || state == SCIMParserState.decimalValue || state == SCIMParserState.exponentSign) {
            sb.append(c);
          } else if (state == SCIMParserState.afterAttributeExpression) {
            try {
              currentExpression = new AttributeNumberComparisonExpression(attrPath, attrOp, Double.parseDouble(sb.toString()));
              sb.setLength(0);
            } catch (NumberFormatException e) {
              throw new ComparisonValueException("[" + sb + "] is not a valid comparison value");
            }
          }
          break;
        case decimalValue:
          state = state.next(c);
          if (state == SCIMParserState.decimalValue || state == SCIMParserState.exponentSign) {
            sb.append(c);
          } else if (state == SCIMParserState.afterAttributeExpression) {
            try {
              currentExpression = new AttributeNumberComparisonExpression(attrPath, attrOp, Double.parseDouble(sb.toString()));
              sb.setLength(0);
            } catch (NumberFormatException e) {
              throw new ComparisonValueException("[" + sb + "] is not a valid comparison value");
            }
          }
          break;
        case exponentSign:
          state = state.next(c);
          if (state == SCIMParserState.exponentValue) {
            sb.append(c);
          }
          break;
        case exponentValue:
          state = state.next(c);
          if (state == SCIMParserState.exponentValue) {
            sb.append(c);
          } else if (state == SCIMParserState.afterAttributeExpression) {
            try {
              currentExpression = new AttributeNumberComparisonExpression(attrPath, attrOp, Double.parseDouble(sb.toString()));
              sb.setLength(0);
            } catch (NumberFormatException e) {
              throw new ComparisonValueException("[" + sb + "] is not a valid comparison value");
            }
          }
          break;
        case textValue:
          state = state.next(c);
          if (state == SCIMParserState.textValue) {
            sb.append(c);
          } else if (state == SCIMParserState.afterAttributeExpression) {
            try {
              // Try to parse as Date...
              currentExpression = new AttributeDateComparisonExpression(attrPath, attrOp, ZonedDateTime.from(DateTimeFormatter.ISO_DATE_TIME.parse(sb.toString())).toEpochSecond());
            } catch (DateTimeParseException e) {
              // ...otherwise treat as text
              currentExpression = new AttributeTextComparisonExpression(attrPath, attrOp, sb.toString());
            }
            sb.setLength(0);
          }
          break;
        case escapedText:
          state = state.next(c);
          if (state == SCIMParserState.textValue) {
            if (c == 't') {
              sb.append('\t');
            } else if (c == 'b') {
              sb.append('\b');
            } else if (c == 'n') {
              sb.append('\n');
            } else if (c == 'r') {
              sb.append('\r');
            } else if (c == 'f') {
              sb.append('\f');
            } else if (c == '\'') {
              sb.append('\'');
            } else if (c == '"') {
              sb.append('"');
            } else if (c == '\\') {
              sb.append('\\');
            }
          }
          break;
      }

      if (state == SCIMParserState.invalidState) {
        throw new InvalidStateException("Invalid state transition at [" + filter.substring(0, Math.min(i + 1, filter.length())) + "]");
      }
    }

    return currentExpression;
  }

  /**
   * Validate an attribute path's optional sub-attribute
   *
   * @param attrPath The attribute path to validate
   * @return {@code true} if {@code attrPath} is valid, {@code false} otherwise
   */
  private boolean validateAttributePath(String attrPath) {
    // TODO : Does this need more URI validation for schema URIs?
    int lastColon = attrPath.lastIndexOf(':');
    String lastSegment = lastColon != -1 ? attrPath.substring(lastColon) : attrPath;
    if (lastSegment.chars().filter(c -> c == '.').count() > 1) {
      // Last segment can have at most one period
      return false;
    }
    int lastPeriod = attrPath.lastIndexOf('.');
    if (lastPeriod > lastColon) {
      // A period after the last colon (or absent a colon) indicates the period is the start of a sub-attribute
      if (attrPath.length() == lastPeriod + 1) {
        // Cannot end with a period
        return false;
      } else {
        // A sub-attribute must start with a letter
        return Character.isAlphabetic(attrPath.codePointAt(lastPeriod + 1));
      }
    }
    return true;
  }
}
