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

import io.fusionauth.scim.parser.exception.ComparisonValueException;
import io.fusionauth.scim.parser.exception.InvalidStateException;
import io.fusionauth.scim.parser.exception.OperatorException;
import io.fusionauth.scim.parser.expression.AttributeBooleanExpression;
import io.fusionauth.scim.parser.expression.AttributePresentExpression;
import io.fusionauth.scim.parser.expression.Expression;

/**
 * @author Spencer Witt
 */
public class SCIMFilterParser {

  public Expression parse(String filter) throws InvalidStateException, OperatorException, ComparisonValueException {
    char[] source = filter.toCharArray();
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
          if (state == SCIMParserState.attributePath || state == SCIMParserState.beforeSubAttribute) {
            sb.append(c);
          } else if (state == SCIMParserState.beforeOperator) {
            attrPath = sb.toString();
            sb.setLength(0);
          }
          break;
        case beforeSubAttribute:
          state = state.next(c);
          if (state == SCIMParserState.subAttribute) {
            sb.append(c);
          }
          break;
        case subAttribute:
          state = state.next(c);
          if (state == SCIMParserState.subAttribute) {
            sb.append(c);
          } else if (state == SCIMParserState.beforeOperator) {
            attrPath = sb.toString();
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
            } catch (IllegalArgumentException e) {
              throw new OperatorException("No operator for [" + sb + "]");
            }
            sb.setLength(0);
          }
          break;
        case comparisonOperator:
          state = state.next(c);
          if (state == SCIMParserState.beforeComparisonValue) {
            sb.append(c);
            try {
              attrOp = ComparisonOperator.valueOf(sb.toString());
            } catch (IllegalArgumentException e) {
              throw new OperatorException("No operator for [" + sb + "]");
            }
            sb.setLength(0);
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
            if (sb.toString().equalsIgnoreCase("true")) {
              currentExpression = new AttributeBooleanExpression(attrPath, attrOp, true);
            } else if (sb.toString().equalsIgnoreCase("false")) {
              currentExpression = new AttributeBooleanExpression(attrPath, attrOp, false);
            } else {
              throw new ComparisonValueException("[" + sb + "] is not a valid boolean comparison value");
            }
            if (attrOp != ComparisonOperator.eq && attrOp != ComparisonOperator.ne) {
              throw new OperatorException("[" + attrOp + "] is not a valid operator for a boolean comparison");
            }
          }
          break;
      }

      if (state == SCIMParserState.invalidState) {
        throw new InvalidStateException("Invalid state transition at [" + filter.substring(0, i + 1) + "]");
      }
    }

    return currentExpression;
  }
}
