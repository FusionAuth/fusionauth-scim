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

package io.fusionauth.scim.utils;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author Daniel DeGroff
 */
public class SCIMDateTools {
  public static final DateTimeFormatter SCIMDateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

  public static final DateTimeFormatter SCIMDateTimeParser = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss[.SSS][XXX][XX]['Z']");

  private SCIMDateTools() {
  }

  /**
   * Format the provided {@link ZonedDateTime} into a SCIM compatible string.
   *
   * @param zonedDateTime the value to format
   * @return a string representation
   */
  public static String format(ZonedDateTime zonedDateTime) {
    return SCIMDateTimeFormatter.format(zonedDateTime);
  }

  /**
   * Parse a date string for SCIM into a {@link ZonedDateTime}.
   * <p>
   * Note this will throw an exception if the string cannot be parsed or converted.
   *
   * @param s an input string to parse
   * @return a {@link ZonedDateTime}
   */
  public static ZonedDateTime parse(String s) {
    return ZonedDateTime.from(SCIMDateTimeParser.parse(s));
  }
}
