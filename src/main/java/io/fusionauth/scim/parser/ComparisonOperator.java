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

/**
 * @author Spencer Witt
 */
public enum ComparisonOperator {
  /**
   * Equal. Operator value and attribute value must match exactly
   */
  eq,
  /**
   * Not equal. Operator value and attribute value are not identical
   */
  ne,
  /**
   * Contains. Operator value must be a substring of the attribute value
   */
  co,
  /**
   * Starts with. Operator value must be a substring of the attribute value starting from the beginning
   */
  sw,
  /**
   * Ends with. Operator value must be a substring of the attribute value matching at the end
   */
  ew,
  /**
   * Present. The attribute value is not null or empty. Unary operator
   */
  pr,
  /**
   * Greater than. The attribute value is greater than the operator value
   */
  gt,
  /**
   * Greater than or equal. The attribute value is greater than or equal to the operator value
   */
  ge,
  /**
   * Less than. The attribute value is less than the operator value
   */
  lt,
  /**
   * Less than or equal. The attribute value is less than or equal to the operator value
   */
  le
}
