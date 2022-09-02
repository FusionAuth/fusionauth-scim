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
import java.time.DateTimeException;
import java.util.ArrayDeque;
import java.util.Deque;

import io.fusionauth.scim.parser.exception.AttributeFilterGroupingException;
import io.fusionauth.scim.parser.exception.AttributePathException;
import io.fusionauth.scim.parser.exception.ComparisonOperatorException;
import io.fusionauth.scim.parser.exception.ComparisonValueException;
import io.fusionauth.scim.parser.exception.GroupingException;
import io.fusionauth.scim.parser.exception.InvalidStateException;
import io.fusionauth.scim.parser.exception.LogicalOperatorException;
import io.fusionauth.scim.parser.expression.AttributeBooleanComparisonExpression;
import io.fusionauth.scim.parser.expression.AttributeDateComparisonExpression;
import io.fusionauth.scim.parser.expression.AttributeFilterGroupingExpression;
import io.fusionauth.scim.parser.expression.AttributeNullTestExpression;
import io.fusionauth.scim.parser.expression.AttributeNumberComparisonExpression;
import io.fusionauth.scim.parser.expression.AttributePresentTestExpression;
import io.fusionauth.scim.parser.expression.AttributeTextComparisonExpression;
import io.fusionauth.scim.parser.expression.Expression;
import io.fusionauth.scim.parser.expression.GroupingExpression;
import io.fusionauth.scim.parser.expression.LogicalExpression;
import io.fusionauth.scim.parser.expression.LogicalLinkExpression;
import io.fusionauth.scim.parser.expression.LogicalNegationExpression;
import io.fusionauth.scim.utils.SCIMDateTools;

/**
 * A parser for SCIM filter expressions supporting the filter grammar from RFC 7644
 *
 * @author Spencer Witt
 * @see <a href="https://www.rfc-editor.org/rfc/rfc7644">RFC 7644</a>
 * @see <a href="https://www.rfc-editor.org/rfc/rfc7644#section-3.4.2.2">SCIM Filtering</a>
 */
public class SCIMFilterParser {
  /**
   * Parse SCIM filter string into an {@link Expression} tree that can be used for matching or transformation
   *
   * @param filter The SCIM filter string
   * @return A single {@link Expression} representing the parsed filter string
   */
  // The duplicated branches and code fragments were intentionally left in place to make parsing logic
  // and state transitions more clear
  @SuppressWarnings({"DuplicateBranchesInSwitch", "DuplicatedCode"})
  public Expression parse(String filter) {
    // Add a trailing space to ensure all tokens are parsed
    char[] source = new char[filter.length() + 1];
    filter.getChars(0, filter.length(), source, 0);
    source[source.length - 1] = ' ';
    SCIMParserState state = SCIMParserState.filterStart;
    String attributePath = null;
    ComparisonOperator comparisonOperator = null;
    Deque<Expression> postfix = new ArrayDeque<>();
    Deque<Expression> operators = new ArrayDeque<>();
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
            operators.push(new GroupingExpression());
          }
          break;
        case attributePath:
          state = state.next(c);
          if (state == SCIMParserState.attributePath) {
            sb.append(c);
          } else if (state == SCIMParserState.openBracket) {
            attributePath = sb.toString();
            if (attributePath.equals("not")) {
              // This was actually a logical negation which is not allowed immediately before [ ]
              throw new AttributeFilterGroupingException("Attribute filter grouping with [ ] must be preceded by an attribute path, found logical negation operator");
            } else {
              validateAttributePath(attributePath);
            }
            operators.push(new AttributeFilterGroupingExpression(attributePath));
            attributePath = null;
            sb.setLength(0);
          } else if (state == SCIMParserState.beforeOperator) {
            attributePath = sb.toString();
            if (attributePath.equals("not")) {
              // This was actually a logical negation. Cannot know until the token is parsed
              attributePath = null;
              operators.push(new LogicalNegationExpression());
              state = SCIMParserState.negationOperator;
            } else {
              validateAttributePath(attributePath);
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
          } else if (state == SCIMParserState.openBracket) {
            // The held attributePath value is actually parent attribute path for AttributeFilterGroupingExpression
            operators.push(new AttributeFilterGroupingExpression(attributePath));
            attributePath = null;
          }
          break;
        case unaryOperator:
          state = state.next(c);
          if (state == SCIMParserState.afterAttributeExpression) {
            sb.append(c);
            postfix.push(new AttributePresentTestExpression(attributePath));
            attributePath = null;
            sb.setLength(0);
          }
          break;
        case comparisonOperator:
          state = state.next(c);
          if (state == SCIMParserState.beforeComparisonValue) {
            sb.append(c);
            try {
              comparisonOperator = ComparisonOperator.valueOf(sb.toString());
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
              state == SCIMParserState.leadingZero ||
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
          } else if (state == SCIMParserState.afterAttributeExpression || state == SCIMParserState.closeParen || state == SCIMParserState.closeBracket) {
            if (sb.toString().equals("true")) {
              postfix.push(new AttributeBooleanComparisonExpression(attributePath, comparisonOperator, true));
            } else if (sb.toString().equals("false")) {
              postfix.push(new AttributeBooleanComparisonExpression(attributePath, comparisonOperator, false));
            } else {
              throw new ComparisonValueException("[" + sb + "] is not a valid comparison value");
            }
            sb.setLength(0);
            if (comparisonOperator != ComparisonOperator.eq && comparisonOperator != ComparisonOperator.ne) {
              throw new ComparisonOperatorException("[" + comparisonOperator + "] is not a valid operator for a boolean comparison");
            }
            handleOptionalGroupClose(filter, i, state, operators, postfix);
          }
          break;
        case nullValue:
          state = state.next(c);
          if (state == SCIMParserState.nullValue) {
            sb.append(c);
          } else if (state == SCIMParserState.afterAttributeExpression || state == SCIMParserState.closeParen || state == SCIMParserState.closeBracket) {
            if (sb.toString().equals("null")) {
              postfix.push(new AttributeNullTestExpression(attributePath, comparisonOperator));
              sb.setLength(0);
            } else {
              throw new ComparisonValueException("[" + sb + "] is not a valid comparison value");
            }
            handleOptionalGroupClose(filter, i, state, operators, postfix);
          }
          if (comparisonOperator != ComparisonOperator.eq && comparisonOperator != ComparisonOperator.ne) {
            throw new ComparisonOperatorException("[" + comparisonOperator + "] is not a valid operator for a null comparison");
          }
          break;
        case minus:
          state = state.next(c);
          if (state == SCIMParserState.leadingZero ||
              state == SCIMParserState.decimalValue ||
              state == SCIMParserState.numberValue) {
            sb.append(c);
          }
          break;
        case leadingZero:
          state = state.next(c);
          if (state == SCIMParserState.decimalValue || state == SCIMParserState.exponentSign) {
            sb.append(c);
          } else if (state == SCIMParserState.afterAttributeExpression || state == SCIMParserState.closeParen || state == SCIMParserState.closeBracket) {
            try {
              postfix.push(new AttributeNumberComparisonExpression(attributePath, comparisonOperator, new BigDecimal(sb.toString())));
              sb.setLength(0);
            } catch (NumberFormatException e) {
              throw new ComparisonValueException("[" + sb + "] is not a valid comparison value");
            }
            handleOptionalGroupClose(filter, i, state, operators, postfix);
          }
          break;
        case numberValue:
          state = state.next(c);
          if (state == SCIMParserState.numberValue || state == SCIMParserState.decimalValue || state == SCIMParserState.exponentSign) {
            sb.append(c);
          } else if (state == SCIMParserState.afterAttributeExpression || state == SCIMParserState.closeParen || state == SCIMParserState.closeBracket) {
            try {
              postfix.push(new AttributeNumberComparisonExpression(attributePath, comparisonOperator, new BigDecimal(sb.toString())));
              sb.setLength(0);
            } catch (NumberFormatException e) {
              throw new ComparisonValueException("[" + sb + "] is not a valid comparison value");
            }
            handleOptionalGroupClose(filter, i, state, operators, postfix);
          }
          break;
        case decimalValue:
          state = state.next(c);
          if (state == SCIMParserState.decimalValue || state == SCIMParserState.exponentSign) {
            sb.append(c);
          } else if (state == SCIMParserState.afterAttributeExpression || state == SCIMParserState.closeParen || state == SCIMParserState.closeBracket) {
            try {
              postfix.push(new AttributeNumberComparisonExpression(attributePath, comparisonOperator, new BigDecimal(sb.toString())));
              sb.setLength(0);
            } catch (NumberFormatException e) {
              throw new ComparisonValueException("[" + sb + "] is not a valid comparison value");
            }
            handleOptionalGroupClose(filter, i, state, operators, postfix);
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
          } else if (state == SCIMParserState.afterAttributeExpression || state == SCIMParserState.closeParen || state == SCIMParserState.closeBracket) {
            try {
              postfix.push(new AttributeNumberComparisonExpression(attributePath, comparisonOperator, new BigDecimal(sb.toString())));
              sb.setLength(0);
            } catch (NumberFormatException e) {
              throw new ComparisonValueException("[" + sb + "] is not a valid comparison value");
            }
            handleOptionalGroupClose(filter, i, state, operators, postfix);
          }
          break;
        case textValue:
          state = state.next(c);
          if (state == SCIMParserState.textValue) {
            sb.append(c);
          } else if (state == SCIMParserState.afterAttributeExpression) {
            try {
              // Try to parse as Date...
              postfix.push(new AttributeDateComparisonExpression(attributePath, comparisonOperator, SCIMDateTools.parse(sb.toString())));
            } catch (DateTimeException e) {
              // ...otherwise treat as text
              postfix.push(new AttributeTextComparisonExpression(attributePath, comparisonOperator, sb.toString()));
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
          } else {
            handleOptionalGroupClose(filter, i, state, operators, postfix);
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
              if (operators.isEmpty() ||
                  operators.peek().type() == ExpressionType.grouping ||
                  operators.peek().type() == ExpressionType.attributeFilterGrouping ||
                  precedence(newLogicalExpression.logicalOperator) >= precedence(((LogicalExpression) operators.peek()).logicalOperator)
              ) {
                operators.push(newLogicalExpression);
              } else {
                // hold.peek() cannot return null here because of !hold.isEmpty() check
                //noinspection ConstantConditions
                while (!operators.isEmpty() &&
                       operators.peek().type() != ExpressionType.grouping &&
                       operators.peek().type() != ExpressionType.attributeFilterGrouping &&
                       precedence(((LogicalExpression) operators.peek()).logicalOperator) >= precedence(newLogicalExpression.logicalOperator)
                ) {
                  postfix.push(operators.pop());
                }
                operators.push(newLogicalExpression);
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
            operators.push(new GroupingExpression());
          }
          break;
        case openParen:
          state = state.next(c);
          if (state == SCIMParserState.openParen) {
            operators.push(new GroupingExpression());
          } else if (state == SCIMParserState.attributePath) {
            sb.append(c);
          }
          break;
        case openBracket:
          state = state.next(c);
          if (state == SCIMParserState.attributePath) {
            sb.append(c);
          } else if (state == SCIMParserState.openParen) {
            operators.push(new GroupingExpression());
          }
          break;
        case closeParen:
        case closeBracket:
          state = state.next(c);
          handleOptionalGroupClose(filter, i, state, operators, postfix);
          break;
      }

      if (state == SCIMParserState.invalidState) {
        throw new InvalidStateException("Invalid state transition at [" + filterAtParsedLocation(filter, i) + "]");
      }
    }

    while (!operators.isEmpty()) {
      Expression exp = operators.pop();
      if (exp.type() == ExpressionType.grouping) {
        throw new GroupingException("Unclosed parenthesis in filter [" + filter + "]");
      } else if (exp.type() == ExpressionType.attributeFilterGrouping) {
        throw new GroupingException("Unclosed bracket in filter [" + filter + "]");
      }
      postfix.push(exp);
    }

    Deque<Expression> result = new ArrayDeque<>();
    while (!postfix.isEmpty()) {
      // Now we work through postfix expressions as a queue
      // removeLast() will take from the bottom of the stack
      Expression exp = postfix.removeLast();
      if (exp.type() == ExpressionType.logicalLink) {
        // Logical link operators are processed immediately by grabbing the top two operands from the stack
        LogicalLinkExpression linkExpression = (LogicalLinkExpression) exp;
        linkExpression.right = result.pop();
        linkExpression.left = result.pop();
        // After it has its left and right populated, the LogicalLinkExpression is just another operand
        result.push(linkExpression);
      } else if (exp.type() == ExpressionType.logicalNegation) {
        // Logical negation operators are processed immediately by grabbing the top operand from the stack
        LogicalNegationExpression negationExpression = (LogicalNegationExpression) exp;
        negationExpression.subExpression = result.pop();
        // After its sub-expression is populated, add it to the operand stack
        result.push(negationExpression);
      } else if (exp.type() == ExpressionType.attributeFilterGrouping) {
        // Complex attribute filter grouping is processed by grabbing the top operand from the stack
        AttributeFilterGroupingExpression groupingExpression = (AttributeFilterGroupingExpression) exp;
        groupingExpression.filterExpression = result.pop();
        result.push(groupingExpression);
      } else {
        // Operands are pushed to a stack
        result.push(exp);
      }
    }
    assert result.size() == 1;

    return result.pop();
  }

  /**
   * Helper to display SCIM filter substring at the parsed location
   *
   * @param filter     The SCIM filter string
   * @param parseIndex Current character index in filter parsing
   * @return A substring of the filter that stops at the current character being parsed
   */
  private String filterAtParsedLocation(String filter, int parseIndex) {
    return filter.substring(0, Math.min(parseIndex + 1, filter.length()));
  }

  /**
   * Handle a closing square bracket by moving operators to the postfix expression
   * until the matching opening square bracket is found.
   *
   * @param filter     The SCIM filter string
   * @param parseIndex Current character index in filter parsing
   * @param operators  A stack containing operators encountered while parsing the filter
   * @param postfix    A work in progress stack for building a postfix representation of the filter
   */
  private void handleCloseBracket(String filter, int parseIndex, Deque<Expression> operators, Deque<Expression> postfix) {
    while (!operators.isEmpty() &&
           operators.peek().type() != ExpressionType.attributeFilterGrouping
    ) {
      postfix.push(operators.pop());
    }
    if (operators.isEmpty() || operators.peek().type() != ExpressionType.attributeFilterGrouping) {
      throw new GroupingException("Extra closed bracket at [" + filterAtParsedLocation(filter, parseIndex) + "]");
    } else {
      // Remove the AttributeFilterGroupingExpression from operators stack and add to postfix stack
      postfix.push(operators.pop());
    }
  }

  /**
   * Handle a closing parenthesis by moving operators to the postfix expression
   * until the matching opening parenthesis is found.
   *
   * @param filter     The SCIM filter string
   * @param parseIndex Current character index in filter parsing
   * @param operators  A stack containing operators encountered while parsing the filter
   * @param postfix    A work in progress stack for building a postfix representation of the filter
   */
  private void handleCloseParen(String filter, int parseIndex, Deque<Expression> operators, Deque<Expression> postfix) {
    while (!operators.isEmpty() &&
           operators.peek().type() != ExpressionType.grouping
    ) {
      postfix.push(operators.pop());
    }
    if (operators.isEmpty() || operators.peek().type() != ExpressionType.grouping) {
      throw new GroupingException("Extra closed parenthesis at [" + filterAtParsedLocation(filter, parseIndex) + "]");
    } else {
      // Remove the GroupExpression from the operators stack
      operators.pop();
    }
  }

  /**
   * Optionally handles a group closing with parenthesis or square bracket when moving into one of those states.
   * <p>
   * This is a convenience method to cut down on code duplication in parser state change handling. It is safe to
   * call this method in places where the state could have transitioned to a group close state without first checking
   * the current state.
   *
   * @param filter     The SCIM filter string
   * @param parseIndex Current character index in filter parsing
   * @param state      The current state of the parser
   * @param operators  A stack containing operators encountered while parsing the filter
   * @param postfix    A work in progress stack for building a postfix representation of the filter
   */
  private void handleOptionalGroupClose(String filter, int parseIndex, SCIMParserState state, Deque<Expression> operators,
                                        Deque<Expression> postfix) {
    if (state == SCIMParserState.closeParen) {
      handleCloseParen(filter, parseIndex, operators, postfix);
    } else if (state == SCIMParserState.closeBracket) {
      handleCloseBracket(filter, parseIndex, operators, postfix);
    }
  }

  /**
   * Retrieve a numeric representation of logical operator precedence
   *
   * @param op The logical operator
   * @return An integer representation of operator precedence. A higher value means higher precedence
   */
  private int precedence(LogicalOperator op) {
    return switch (op) {
      case not -> 3;
      case and -> 2;
      case or -> 1;
    };
  }

  /**
   * Validate an attribute path's optional sub-attribute
   *
   * @param attributePath The attribute path to validate
   */
  private void validateAttributePath(String attributePath) {
    // TODO : Does this need more URI validation for schema URIs?
    int lastColon = attributePath.lastIndexOf(':');
    String lastSegment = lastColon != -1 ? attributePath.substring(lastColon) : attributePath;
    if (lastSegment.chars().filter(c -> c == '.').count() > 1) {
      // Last segment can have at most one period
      throw new AttributePathException("The attribute path [" + attributePath + "] is not valid. Attribute paths can have at most one sub-attribute.");
    }
    int lastPeriod = attributePath.lastIndexOf('.');
    if (lastPeriod > lastColon) {
      // A period after the last colon (or absent a colon) indicates the period is the start of a sub-attribute
      if (attributePath.length() == lastPeriod + 1) {
        // Cannot end with a period
        throw new AttributePathException("The attribute path [" + attributePath + "] is not valid. A sub-attribute must be provided after the period.");
      } else {
        // A sub-attribute must start with a letter
        if (!Character.isAlphabetic(attributePath.codePointAt(lastPeriod + 1))) {
          throw new AttributePathException("The attribute path [" + attributePath + "] is not valid. A sub-attribute must start with an alphabetic character.");
        }
      }
    }
  }
}
