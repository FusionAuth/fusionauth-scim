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
import io.fusionauth.scim.parser.expression.AttributeComparisonExpression;
import io.fusionauth.scim.parser.expression.AttributeExpression;
import io.fusionauth.scim.parser.expression.Expression;
import io.fusionauth.scim.parser.expression.LogicalLinkExpression;

@SuppressWarnings("EnhancedSwitchMigration")
public class ElasticsearchTransformer {
  public static String transform(Expression exp) {
    switch (exp.type()) {
      case attribute:
        return transformAttributeExpression((AttributeExpression) exp);
      case logicalLink:
        return transformLogicalExpression((LogicalLinkExpression) exp);
      case logicalNegation:
      default:
        return "";
    }
  }

  private static String transformAttributeExpression(AttributeExpression exp) {
    if (exp.valueType() == ValueType.none) {
      return "_exists_:" + exp.attributePath;
    } else if (exp.valueType() == ValueType.nul) {
      return exp.attributePath + transformComparisonOperator(exp.operator) + "null";
    } else {
      return transformComparisonExpression((AttributeComparisonExpression) exp);
    }
  }

  private static String transformComparisonExpression(AttributeComparisonExpression exp) {
    String value = exp.value().toString();
    if (exp.operator == ComparisonOperator.sw) {
      // Add wildcard to end of comparison for "starts with"
      value += "*";
    } else if (exp.operator == ComparisonOperator.ew) {
      // Add wildcard to start of comparison for "ends with"
      value = "*" + value;
    }
    if (exp.valueType() == ValueType.text) {
      value = "\"" + value + "\"";
    }
    String filter = exp.attributePath + transformComparisonOperator(exp.operator) + value;
    if (exp.operator == ComparisonOperator.ne) {
      filter = "!(" + filter + ")";
    }
    return filter;
  }

  private static String transformComparisonOperator(ComparisonOperator op) {
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

  private static String transformLogicalExpression(LogicalLinkExpression exp) {
    String left = transform(exp.left);
    String right = transform(exp.right);
    String opString = exp.logicalOperator == LogicalOperator.and ? " AND " : " OR ";
    return "(" + left + opString + right + ")";
  }
}
