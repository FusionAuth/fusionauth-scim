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
package io.fusionauth.domain.scim;

import java.util.List;
import java.util.Map;

/**
 * @author Rob Davis
 */
public class SCIMServiceProviderConfig extends BaseSCIMResource implements Buildable<SCIMServiceProviderConfig> {
  public List<Map<String, Object>> authenticationSchemes;

  public Map<String, Object> bulk;

  public Map<String, Object> changePassword;

  public String documentationUri;

  public Map<String, Object> etag;

  public Map<String, Object> filter;

  public Map<String, Object> patch;

  public Map<String, Object> sort;
}
