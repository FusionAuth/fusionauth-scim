package io.fusionauth.scim.parser;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import com.fasterxml.jackson.databind.util.ISO8601DateFormat;

/**
 * @author Daniel DeGroff
 */
public class SCIMFilterParser {

  public FilterGroup parse(String filter) throws Exception {
    SCIMParserToken token = new SCIMParserToken(SCIMParserState.start, filter.trim(), null);
    FilterGroup result = new FilterGroup();
    Filter currentFilter = null;

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
          result.filters.add(currentFilter);
          currentFilter = null;
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
          result.filters.add(currentFilter);
          currentFilter = null;
          break;
      }
    }

    return result;
  }

}
