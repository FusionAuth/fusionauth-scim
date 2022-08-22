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
package io.fusionauth.scim.domain;

/**
 * SCIM Schemas
 *
 * @author Daniel DeGroff
 */
public class SCIMSchemas {
  public static final String EnterpriseUser = "urn:ietf:params:scim:schemas:extension:enterprise:2.0:User";

  public static final String Error = "urn:ietf:params:scim:api:messages:2.0:Error";

  public static final String Group = "urn:ietf:params:scim:schemas:core:2.0:Group";

  public static final String ListResponse = "urn:ietf:params:scim:api:messages:2.0:ListResponse";

  public static final String PatchOp = "urn:ietf:params:scim:api:messages:2.0:PatchOp";

  public static final String ResourceType = "urn:ietf:params:scim:schemas:core:2.0:ResourceType";

  public static final String ServiceProviderConfig = "urn:ietf:params:scim:schemas:core:2.0:ServiceProviderConfig";

  public static final String User = "urn:ietf:params:scim:schemas:core:2.0:User";

  private SCIMSchemas() {
  }
}
