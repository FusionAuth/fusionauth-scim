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
        return ":=";
      case co:
        return ":";
      case gt:
        return ":>";
      case ge:
        return ":>=";
      case lt:
        return ":<";
      case le:
        return ":<=";
      case sw:
      case ew:
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
