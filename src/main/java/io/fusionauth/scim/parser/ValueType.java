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
 * A type indicator for attribute expression comparison values
 *
 * @author Spencer Witt
 */
public enum ValueType {
  /**
   * The attribute expression does not contain a comparison value. Used with {@link ComparisonOperator#pr}
   */
  none,
  /**
   * A numeric comparison value
   */
  number,
  /**
   * A text comparison value
   */
  text,
  /**
   * A date comparison value
   */
  date,
  /**
   * A boolean comparison value
   */
  bool,
  /**
   * A {@code null} comparison value
   */
  nul
}
