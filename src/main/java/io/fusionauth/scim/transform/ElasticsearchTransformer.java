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

package io.fusionauth.scim.transform;

import io.fusionauth.scim.parser.ComparisonOperator;
import io.fusionauth.scim.parser.LogicalOperator;
import io.fusionauth.scim.parser.ValueType;
import io.fusionauth.scim.parser.exception.ComparisonOperatorException;
import io.fusionauth.scim.parser.expression.AttributeComparisonExpression;
import io.fusionauth.scim.parser.expression.AttributeExpression;
import io.fusionauth.scim.parser.expression.AttributeFilterGroupingExpression;
import io.fusionauth.scim.parser.expression.Expression;
import io.fusionauth.scim.parser.expression.LogicalLinkExpression;
import io.fusionauth.scim.parser.expression.LogicalNegationExpression;

/**
 * @author Spencer Witt
 */
public class ElasticsearchTransformer {
  public static String transform(Expression exp) {
    return transform(exp, "");
  }

  private static String appendToParentAttributePath(String currentParentPath, String newPathSegment) {
    return currentParentPath.isEmpty() ? newPathSegment : currentParentPath + "." + newPathSegment;
  }

  private static String createRangeForDateComparison(String value, ComparisonOperator operator) {
    return switch (operator) {
      // The negation for `ne` operator happens
      case eq, ne -> "[" + value + " TO " + value + "]";
      case gt -> "{" + value + " TO *]";
      case ge -> "[" + value + " TO *]";
      case lt -> "[* TO " + value + "}";
      case le -> "[* TO " + value + "]";
      default -> throw new ComparisonOperatorException("[" + operator + "] is not a valid operator for a date comparison");
    };
  }

  private static String prependParentAttributePath(String currentParentPath, String attributePath) {
    return currentParentPath.isEmpty() ? attributePath : currentParentPath + "." + attributePath;
  }

  private static String transform(Expression exp, String parentAttributePath) {
    return switch (exp.type()) {
      case attribute -> transformAttributeExpression((AttributeExpression<?>) exp, parentAttributePath);
      case logicalLink -> transformLogicalExpression((LogicalLinkExpression) exp, parentAttributePath);
      case logicalNegation -> transformNegationExpression((LogicalNegationExpression) exp, parentAttributePath);
      case attributeFilterGrouping -> transformAttributeFilterGrouping((AttributeFilterGroupingExpression) exp, parentAttributePath);
      // GroupingExpressions do not appear in the final parsed SCIM output
      case grouping -> "";
    };
  }

  private static String transformAttributeExpression(AttributeExpression<?> exp, String parentAttributePath) {
    if (exp.valueType() == ValueType.none) {
      return "_exists_:" + prependParentAttributePath(parentAttributePath, exp.attributePath);
    } else if (exp.valueType() == ValueType.nul) {
      String nullComparison = prependParentAttributePath(parentAttributePath, exp.attributePath) + transformComparisonOperator(exp.operator) + "null";
      if (exp.operator == ComparisonOperator.ne) {
        nullComparison = "!(" + nullComparison + ")";
      }
      return nullComparison;
    } else {
      return transformComparisonExpression((AttributeComparisonExpression<?, ?>) exp, parentAttributePath);
    }
  }

  private static String transformAttributeFilterGrouping(AttributeFilterGroupingExpression exp, String parentAttributePath) {
    return transform(exp.filterExpression, appendToParentAttributePath(parentAttributePath, exp.parentAttributePath));
  }

  private static String transformComparisonExpression(AttributeComparisonExpression<?, ?> exp, String parentAttributePath) {
    // TODO : Question: Is calling toString() safe enough? What if the value is a ZonedDateTime?
    //        - We may need to add a getStringValue() or something like that so the expression can return something
    //          equivalent to what we expect to serialize.
    String value = exp.value().toString();
    if (exp.valueType() == ValueType.text) {
      if (exp.operator == ComparisonOperator.sw) {
        // Add wildcard to end of comparison for "starts with"
        value += "*";
      } else if (exp.operator == ComparisonOperator.ew) {
        // Add wildcard to start of comparison for "ends with"
        value = "*" + value;
      }
      value = "\"" + value + "\"";
    } else if (exp.valueType() == ValueType.number && value.startsWith("-")) {
      // The negative sign has to be escaped
      value = "\\" + value;
    } else if (exp.valueType() == ValueType.date) {
      value = createRangeForDateComparison(value, exp.operator);
    }
    String filter =
        prependParentAttributePath(parentAttributePath, exp.attributePath) +
        (exp.valueType() == ValueType.date ? ":" : transformComparisonOperator(exp.operator)) +
        value;
    if (exp.operator == ComparisonOperator.ne) {
      filter = "!(" + filter + ")";
    }
    return filter;
  }

  private static String transformComparisonOperator(ComparisonOperator op) {
    //noinspection EnhancedSwitchMigration
    switch (op) {
      case eq:
      case ne:
      case co:
      case sw:
      case ew:
        return ":";
      case gt:
        return ":>";
      case ge:
        return ":>=";
      case lt:
        return ":<";
      case le:
        return ":<=";
      case pr:
      default:
        return "";
    }
  }

  private static String transformLogicalExpression(LogicalLinkExpression exp, String parentAttributePath) {
    String left = transform(exp.left, parentAttributePath);
    String right = transform(exp.right, parentAttributePath);
    String opString = exp.logicalOperator == LogicalOperator.and ? " AND " : " OR ";
    return "(" + left + opString + right + ")";
  }

  private static String transformNegationExpression(LogicalNegationExpression exp, String parentAttributePath) {
    return "!(" + transform(exp.subExpression, parentAttributePath) + ")";
  }
}
