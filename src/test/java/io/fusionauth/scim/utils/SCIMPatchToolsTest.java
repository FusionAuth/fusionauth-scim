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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Stream;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import io.fusionauth.scim.domain.SCIMPatchRequest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * @author Daniel DeGroff
 */
public class SCIMPatchToolsTest {
  private final ObjectMapper objectMapper = new ObjectMapper()
      .setSerializationInclusion(Include.NON_NULL);

  private Path jsonDir;

  private JsonNode source;

  @BeforeTest
  public void beforeTest() throws IOException {
    jsonDir = Paths.get("").resolve("src/test/json/");
    source = objectMapper.readTree(Files.readAllBytes(jsonDir.resolve("resource.json")));
  }

  @DataProvider(name = "testFiles")
  public Object[][] testFiles() throws IOException {
    List<String> fileNames;
    try (Stream<Path> files = Files.list(jsonDir.resolve("scim-patch"))) {
      fileNames = files.map(f -> f.getFileName().toString()).toList();
    }

    Object[][] result = new Object[fileNames.size()][1];
    for (int i = 0; i < fileNames.size(); i++) {
      result[i] = new Object[]{fileNames.get(i)};
    }

    return result;
  }

  @Test(dataProvider = "testFiles")
  public void testing(String testName) throws Exception {
    String scimPatch = Files.readString(jsonDir.resolve("scim-patch/" + testName));
    SCIMPatchRequest scimPatchRequest = objectMapper.readerFor(SCIMPatchRequest.class).readValue(scimPatch);
    ArrayNode actual = SCIMPatchTools.convertSCIMPatchToJSONPatch(objectMapper, source, scimPatchRequest.Operations);

    Path target = jsonDir.resolve("json-patch/" + testName);
    if (!target.toFile().exists()) {
      throw new AssertionError("You are missing the expected version of the [" + testName + "] in JSON patch version. Create the file json-patch/" + testName + ".");
    }

    String jsonPatch = Files.readString(target);
    ArrayNode expected = (ArrayNode) objectMapper.readTree(jsonPatch);

    if (!actual.equals(expected)) {
      throw new AssertionError("Expected:\n" + ToString.toString(expected) + "\nbut found\n" + ToString.toString(actual) + "\n");
    }
  }
}
