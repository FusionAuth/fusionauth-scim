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

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayDeque;
import java.util.Deque;

import io.fusionauth.scim.parser.exception.AttributePathException;
import io.fusionauth.scim.parser.exception.ComparisonOperatorException;
import io.fusionauth.scim.parser.exception.ComparisonValueException;
import io.fusionauth.scim.parser.exception.GroupingException;
import io.fusionauth.scim.parser.exception.InvalidStateException;
import io.fusionauth.scim.parser.exception.LogicalOperatorException;
import io.fusionauth.scim.parser.expression.AttributeBooleanComparisonExpression;
import io.fusionauth.scim.parser.expression.AttributeDateComparisonExpression;
import io.fusionauth.scim.parser.expression.AttributeNullTestExpression;
import io.fusionauth.scim.parser.expression.AttributeNumberComparisonExpression;
import io.fusionauth.scim.parser.expression.AttributePresentTestExpression;
import io.fusionauth.scim.parser.expression.AttributeTextComparisonExpression;
import io.fusionauth.scim.parser.expression.Expression;
import io.fusionauth.scim.parser.expression.GroupingExpression;
import io.fusionauth.scim.parser.expression.LogicalExpression;
import io.fusionauth.scim.parser.expression.LogicalLinkExpression;
import io.fusionauth.scim.parser.expression.LogicalNegationExpression;

/**
 * @author Spencer Witt
 */
public class SCIMFilterParser {
  public Expression parse(String filter) {
    // Add a trailing space to ensure all tokens are parsed
    char[] source = new char[filter.length() + 1];
    filter.getChars(0, filter.length(), source, 0);
    source[source.length - 1] = ' ';
    SCIMParserState state = SCIMParserState.filterStart;
    String attrPath = null;
    ComparisonOperator attrOp = null;
    Deque<Expression> result = new ArrayDeque<>();
    Deque<Expression> hold = new ArrayDeque<>();
    StringBuilder sb = new StringBuilder();

    for (int i = 0; i < source.length; i++) {
      char c = source[i];
      //noinspection EnhancedSwitchMigration
      switch (state) {
        case filterStart:
          state = state.next(c);
          if (state == SCIMParserState.attributePath) {
            sb.append(c);
          } else if (state == SCIMParserState.openParen) {
            hold.push(new GroupingExpression());
          }
          break;
        case attributePath:
          state = state.next(c);
          if (state == SCIMParserState.attributePath) {
            sb.append(c);
          } else if (state == SCIMParserState.beforeOperator) {
            attrPath = sb.toString();
            if (attrPath.equals("not")) {
              // This was actually a logical negation. Cannot know until the token is parsed
              attrPath = null;
              hold.push(new LogicalNegationExpression());
              state = SCIMParserState.negationOperator;
            } else if (!validateAttributePath(attrPath)) {
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
              result.push(new AttributePresentTestExpression(attrPath));
              attrPath = null;
              sb.setLength(0);
            } catch (IllegalArgumentException e) {
              throw new ComparisonOperatorException("No comparison operator for [" + sb + "]");
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
              throw new ComparisonOperatorException("No comparison operator for [" + sb + "]");
            }
          }
          break;
        case beforeComparisonValue:
          state = state.next(c);
          if (state == SCIMParserState.booleanValue ||
              state == SCIMParserState.nullValue ||
              state == SCIMParserState.minus ||
              state == SCIMParserState.decimalValue ||
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
              result.push(new AttributeBooleanComparisonExpression(attrPath, attrOp, true));
            } else if (sb.toString().equals("false")) {
              result.push(new AttributeBooleanComparisonExpression(attrPath, attrOp, false));
            } else {
              throw new ComparisonValueException("[" + sb + "] is not a valid comparison value");
            }
            sb.setLength(0);
            if (attrOp != ComparisonOperator.eq && attrOp != ComparisonOperator.ne) {
              throw new ComparisonOperatorException("[" + attrOp + "] is not a valid operator for a boolean comparison");
            }
          }
          break;
        case nullValue:
          state = state.next(c);
          if (state == SCIMParserState.nullValue) {
            sb.append(c);
          } else if (state == SCIMParserState.afterAttributeExpression) {
            if (sb.toString().equals("null")) {
              result.push(new AttributeNullTestExpression(attrPath, attrOp));
              sb.setLength(0);
            } else {
              throw new ComparisonValueException("[" + sb + "] is not a valid comparison value");
            }
          }
          if (attrOp != ComparisonOperator.eq && attrOp != ComparisonOperator.ne) {
            throw new ComparisonOperatorException("[" + attrOp + "] is not a valid operator for a null comparison");
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
              result.push(new AttributeNumberComparisonExpression(attrPath, attrOp, new BigDecimal(sb.toString())));
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
              result.push(new AttributeNumberComparisonExpression(attrPath, attrOp, new BigDecimal(sb.toString())));
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
              result.push(new AttributeNumberComparisonExpression(attrPath, attrOp, new BigDecimal(sb.toString())));
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
              result.push(new AttributeDateComparisonExpression(attrPath, attrOp, ZonedDateTime.from(DateTimeFormatter.ISO_DATE_TIME.parse(sb.toString()))));
            } catch (DateTimeParseException e) {
              // ...otherwise treat as text
              result.push(new AttributeTextComparisonExpression(attrPath, attrOp, sb.toString()));
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
        case afterAttributeExpression:
          state = state.next(c);
          if (state == SCIMParserState.logicalOperator) {
            sb.append(c);
          } else if (state == SCIMParserState.closeParen) {
            boolean success = handleCloseParen(hold, result);
            if (!success) {
              throw new GroupingException("Extra closed parenthesis at [" + filterAtParsedLocation(filter, i) + "]");
            }
          }
          break;
        case logicalOperator:
          state = state.next(c);
          if (state == SCIMParserState.logicalOperator) {
            sb.append(c);
          } else if (state == SCIMParserState.filterStart) {
            try {
              LogicalLinkExpression newLogicalExpression = new LogicalLinkExpression(LogicalOperator.valueOf(sb.toString()));
              // hold.peek() cannot return null here because of the hold.isEmpty() check
              //noinspection ConstantConditions
              if (hold.isEmpty() ||
                  hold.peek().type() == ExpressionType.grouping ||
                  precedence(newLogicalExpression.logicalOperator) >= precedence(((LogicalExpression) hold.peek()).logicalOperator)
              ) {
                hold.push(newLogicalExpression);
              } else {
                // hold.peek() cannot return null here because of !hold.isEmpty() check
                //noinspection ConstantConditions
                while (!hold.isEmpty() &&
                       hold.peek().type() != ExpressionType.grouping &&
                       precedence(((LogicalExpression) hold.peek()).logicalOperator) >= precedence(newLogicalExpression.logicalOperator)
                ) {
                  result.push(hold.pop());
                }
                hold.push(newLogicalExpression);
              }
              sb.setLength(0);
            } catch (IllegalArgumentException e) {
              throw new LogicalOperatorException("No logical operator for [" + sb + "]");
            }
          }
          break;
        case negationOperator:
          state = state.next(c);
          if (state == SCIMParserState.openParen) {
            hold.push(new GroupingExpression());
          }
          break;
        case openParen:
          state = state.next(c);
          if (state == SCIMParserState.openParen) {
            hold.push(new GroupingExpression());
          } else if (state == SCIMParserState.attributePath) {
            if (c != ' ') {
              sb.append(c);
            }
          }
          break;
        case closeParen:
          state = state.next(c);
          if (state == SCIMParserState.closeParen) {
            boolean success = handleCloseParen(hold, result);
            if (!success) {
              throw new GroupingException("Extra closed parenthesis at [" + filterAtParsedLocation(filter, i) + "]");
            }
          }
          break;
      }

      if (state == SCIMParserState.invalidState) {
        throw new InvalidStateException("Invalid state transition at [" + filterAtParsedLocation(filter, i) + "]");
      }
    }

    while (!hold.isEmpty()) {
      Expression exp = hold.pop();
      if (exp.type() == ExpressionType.grouping) {
        throw new GroupingException("Unclosed parenthesis in filter [" + filter + "]");
      }
      result.push(exp);
    }

    Deque<Expression> operands = new ArrayDeque<>();
    while (!result.isEmpty()) {
      // Now we work through postfix expressions as a queue
      // removeLast() will take from the bottom of the stack
      Expression exp = result.removeLast();
      if (exp.type() == ExpressionType.logicalLink) {
        // Logical link operators are processed immediately by grabbing the top two operands from the stack
        LogicalLinkExpression linkExpression = (LogicalLinkExpression) exp;
        linkExpression.right = operands.pop();
        linkExpression.left = operands.pop();
        // After it has its left and right populated, the LogicalLinkExpression is just another operand
        operands.push(linkExpression);
      } else if (exp.type() == ExpressionType.logicalNegation) {
        // Logical negation operators are processed immediately by grabbing the top operand from the stack
        LogicalNegationExpression negationExpression = (LogicalNegationExpression) exp;
        negationExpression.subExpression = operands.pop();
        // After its sub-expression is populated, add it to the operand stack
        operands.push(negationExpression);
      } else {
        // Operands are pushed to a stack
        operands.push(exp);
      }
    }
    assert operands.size() == 1;

    return operands.pop();
  }

  private String filterAtParsedLocation(String filter, int i) {
    return filter.substring(0, Math.min(i + 1, filter.length()));
  }

  private boolean handleCloseParen(Deque<Expression> hold, Deque<Expression> result) {
    while (!hold.isEmpty() &&
           hold.peek().type() != ExpressionType.grouping
    ) {
      result.push(hold.pop());
    }
    if (hold.isEmpty() || hold.peek().type() != ExpressionType.grouping) {
      return false;
    } else {
      // Remove the GroupExpression
      hold.pop();
      return true;
    }
  }

  @SuppressWarnings("EnhancedSwitchMigration")
  private int precedence(LogicalOperator op) {
    switch (op) {
      case not:
        return 3;
      case and:
        return 2;
      case or:
        return 1;
      default:
        return -1;
    }
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
