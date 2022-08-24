package io.fusionauth.scim.parser;

import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayDeque;
import java.util.Deque;

/**
 * @author Spencer Witt
 */
public class SCIMFilterParser {

  public FilterGroup parse(String filter) throws Exception {
    SCIMParserToken token = new SCIMParserToken(SCIMParserState.start, filter.trim(), null);
    FilterGroup result = new FilterGroup();
    Filter currentFilter = null;
    Deque<FilterGroup> scope = new ArrayDeque<>();
    scope.push(result);

    boolean invertNextGroup = false;

    while (!token.remaining.isEmpty()) {
      token = token.state.next(token.remaining);
      switch (token.state) {
        case attribute:
          currentFilter = new Filter(token.value);
          break;
        case op:
          currentFilter.op = Op.valueOf(token.value);
          break;
        case unaryOp:
          // This should always be `pr`
          currentFilter.op = Op.valueOf(token.value);
          currentFilter.valueType = ValueType.none;
          scope.peek().filters.add(currentFilter);
          currentFilter = null;
          break;
        case logicOp:
          scope.peek().logicalOperator = LogicalOperator.valueOf(token.value);
          // TODO : if logical operator has changed, we need a new group
          break;
        case not:
          // We should have a precedence grouping next that needs to be inverted
          invertNextGroup = true;
          break;
        case opValue:
          currentFilter.value = token.value;
          if (currentFilter.value.startsWith("\"") && currentFilter.value.endsWith("\"")) {
            // This is either text or date. Remove start/end quote
            currentFilter.value = currentFilter.value.substring(1, currentFilter.value.length() - 1);
            try {
              // If we can parse as date, it's a date type...
              DateTimeFormatter.ISO_DATE_TIME.parse(currentFilter.value);
              currentFilter.valueType = ValueType.date;
            } catch (DateTimeParseException ignored) {
              // ...otherwise it's text
              currentFilter.valueType = ValueType.text;
            }
          } else if (currentFilter.value.equals("null")) {
            currentFilter.valueType = ValueType.nul;
          } else if (currentFilter.value.equals("true") || currentFilter.value.equals("false")) {
            currentFilter.valueType = ValueType.bool;
          } else {
            try {
              Double.parseDouble(currentFilter.value);
              currentFilter.valueType = ValueType.number;
            } catch (NumberFormatException ignored) {
              throw new Exception("Invalid opValue " + token.value);
            }
          }
          scope.peek().filters.add(currentFilter);
          currentFilter = null;
          break;
        case openParen:
          // Create a subGroup
          FilterGroup group = new FilterGroup();
          group.inverted = invertNextGroup;
          invertNextGroup = false;
          // Add to the current FilterGroup's subGroups
          scope.peek().subGroups.add(group);
          // Make this new FilterGroup the current scope
          scope.push(group);
          break;
        case closeParen:
          // Finish off the current scope
          scope.pop();
          break;
        default:
          throw new Exception("Unexpected state value " + token.state);
      }
    }

    // Only the base result scope should be left in the stack
    assert scope.size() == 1;

    // If the result contains a single subGroup, we can make that the result
    if (result.filters.isEmpty() && result.subGroups.size() == 1) {
      result = result.subGroups.get(0);
    }

    return result;
  }

}
