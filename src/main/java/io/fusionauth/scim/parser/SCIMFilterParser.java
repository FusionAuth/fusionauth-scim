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

import io.fusionauth.scim.parser.exception.InvalidStateException;

/**
 * @author Spencer Witt
 */
public class SCIMFilterParser {

  public SCIMParserState parse(String filter) throws InvalidStateException {
    char[] source = filter.toCharArray();
    SCIMParserState state = SCIMParserState.filterStart;

    for (int i = 0; i < source.length; i++) {
      state = state.next(source[i]);
      if (state == SCIMParserState.invalidState) {
        throw new InvalidStateException("Invalid state transition at [" + filter.substring(0, i + 1) + "]");
      }
    }

    return state;
  }
}
