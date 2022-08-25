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
    Filter currentFilter = null;
    Deque<FilterGroup> scope = new ArrayDeque<>();
    scope.push(new FilterGroup());

    boolean invertNextGroup = false;

    while (!token.remaining.isEmpty()) {
      token = token.state.next(token.remaining);
      FilterGroup currentGroup = scope.peek();
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
          addFilter(currentGroup, currentFilter);
          currentFilter = null;
          break;
        case logicOp:
          LogicalOperator logicalOperator = LogicalOperator.valueOf(token.value);
          if (currentGroup.logicalOperator == null) {
            currentGroup.logicalOperator = logicalOperator;
            currentGroup.lastLogicalOp = logicalOperator;
          } else if (currentGroup.lastLogicalOp != logicalOperator) {
            if (currentGroup.lastLogicalOp == LogicalOperator.or && logicalOperator == LogicalOperator.and) {
              // Going from OR to AND
              if (currentGroup.isLastAddGroup) {
                // The last object added to currentGroup was a subGroup
                // 1a) Remove the last FilterGroup from currentGroup
                FilterGroup fg = currentGroup.subGroups.remove(currentGroup.subGroups.size() - 1);
                // 2b) Create a new FilterGroup with AND operator and the removed FilterGroup
                FilterGroup newGroup = new FilterGroup()
                    .with(g -> g.logicalOperator = LogicalOperator.and)
                    .with(g -> g.lastLogicalOp = LogicalOperator.and)
                    .addSubGroup(fg);
                // 3b) Add new FilterGroup to currentGroup.subGroups
                currentGroup.addSubGroup(newGroup);
              } else {
                // The last object added to currentGroup was a Filter
                // 1b) Remove the last Filter from currentGroup
                Filter f = currentGroup.filters.remove(currentGroup.filters.size() - 1);
                // 2b) Create a new FilterGroup with AND operator and the removed Filter
                FilterGroup newGroup = new FilterGroup()
                    .with(g -> g.logicalOperator = LogicalOperator.and)
                    .with(g -> g.lastLogicalOp = LogicalOperator.and)
                    .addFilter(f);
                // 3b) Add new FilterGroup to currentGroup.subGroups
                currentGroup.addSubGroup(newGroup);
              }
              // 4) Set currentGroup.lastLogicalOp
              currentGroup.lastLogicalOp = logicalOperator;
              // TODO : should we make new FilterGroup the currentGroup by pushing to stack?
            } else if (currentGroup.lastLogicalOp == LogicalOperator.and && logicalOperator == LogicalOperator.or) {
              // Going from AND to OR
              // 1) Remove currentGroup from scope
              FilterGroup fg = scope.pop();
              assert fg == currentGroup;
              // 2) Create a new FilterGroup with currentGroup
              FilterGroup newGroup = new FilterGroup()
                  .with(g -> g.logicalOperator = LogicalOperator.or)
                  .with(g -> g.lastLogicalOp = LogicalOperator.or)
                  .addSubGroup(currentGroup);
              FilterGroup parentGroup = scope.peek();
              // If there is a parentGroup
              if (parentGroup != null) {
                //  3) Remove currentGroup from subGroups
                parentGroup.subGroups.remove(fg);
                //  4) Add newGroup to subGroups
                parentGroup.addSubGroup(newGroup);
              }
              // 5) Add newGroup to scope
              scope.push(newGroup);
            }
          }
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
          addFilter(currentGroup, currentFilter);
          currentFilter = null;
          break;
        case openParen:
          // Create a subGroup
          FilterGroup group = new FilterGroup();
          group.inverted = invertNextGroup;
          invertNextGroup = false;
          // Add to the current FilterGroup's subGroups
          currentGroup.addSubGroup(group);
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

    FilterGroup result = scope.pop();
    // If the result contains a single subGroup, we can make that the result
    if (result.filters.isEmpty() && result.subGroups.size() == 1) {
      result = result.subGroups.get(0);
    }

    return result;
  }

  private void addFilter(FilterGroup currentGroup, Filter newFilter) {
    if (currentGroup.logicalOperator == LogicalOperator.or) {
      if (currentGroup.lastLogicalOp == LogicalOperator.and) {
        // Add to the last subGroup.filters
        currentGroup.subGroups.get(currentGroup.subGroups.size() - 1).addFilter(newFilter);
      } else {
        // Add to currentGroup.filters
        currentGroup.addFilter(newFilter);
      }
    } else {
      // No logicalOperator set, or AND
      currentGroup.addFilter(newFilter);
    }
  }

}
