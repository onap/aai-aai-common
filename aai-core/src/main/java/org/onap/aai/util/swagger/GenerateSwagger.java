/**
 * ============LICENSE_START=======================================================
 * org.onap.aai
 * ================================================================================
 * Copyright © 2017-2018 AT&T Intellectual Property. All rights reserved.
 * ================================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ============LICENSE_END=========================================================
 */

package org.onap.aai.util.swagger;

import com.fasterxml.jackson.dataformat.yaml.snakeyaml.Yaml;
import com.fasterxml.jackson.dataformat.yaml.snakeyaml.constructor.SafeConstructor;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

import org.onap.aai.setup.SchemaVersions;

public class GenerateSwagger {

    public static final String LINE_SEPARATOR = System.getProperty("line.separator");
    public static final String DEFAULT_WIKI = "";

    public static final String DEFAULT_SCHEMA_DIR = "../aai-schema";
    // if the program is run from aai-common, use this directory as default"
    public static final String ALT_SCHEMA_DIR = "aai-schema";
    // used to check to see if program is run from aai-core
    public static final String DEFAULT_RUN_DIR = "aai-core";

    public static SchemaVersions schemaVersions;

    public SchemaVersions getSchemaVersions() {
        return schemaVersions;
    }

    public static void main(String[] args) throws IOException, TemplateException {

        // SchemaVersions schemaVersions = SpringContextAware.getBean(SchemaVersions.class);
        String CURRENT_VERSION = schemaVersions.getDefaultVersion().toString();
        String schemaDir = System.getProperty("aai.schema.dir");
        String versionToGenerate = System.getProperty("aai.generate.version");
        String wikiLink = System.getProperty("aai.wiki.link");
        String release = System.getProperty("aai.release", "onap");

        if (schemaDir == null) {
            if (System.getProperty("user.dir") != null
                && !System.getProperty("user.dir").contains(DEFAULT_RUN_DIR)) {
                System.out
                    .println("Warning: Schema directory is not set so using default schema dir: "
                        + ALT_SCHEMA_DIR);
                schemaDir = ALT_SCHEMA_DIR;
            } else {
                System.out
                    .println("Warning: Schema directory is not set so using default schema dir: "
                        + DEFAULT_SCHEMA_DIR);
                schemaDir = DEFAULT_SCHEMA_DIR;
            }
        }

        if (versionToGenerate == null) {
            System.out.println(
                "Warning: Version to generate is not set so using default versionToGenerate "
                    + CURRENT_VERSION);
            versionToGenerate = CURRENT_VERSION;
        }

        if (wikiLink == null) {
            System.out.println("Warning: aai.wiki.link property is not set so using default");
            wikiLink = DEFAULT_WIKI;
        }

        String yamlFile = schemaDir + "/src/main/resources/" + release
            + "/aai_swagger_yaml/aai_swagger_" + versionToGenerate + ".yaml";
        File swaggerYamlFile = new File(yamlFile);

        if (!swaggerYamlFile.exists()) {
            System.err.println("Unable to find the swagger yaml file: " + swaggerYamlFile);
            System.exit(1);
        }

        Yaml yaml = new Yaml(new SafeConstructor());
        Map<String, Object> swaggerMap = null;

        try (BufferedReader reader = new BufferedReader(new FileReader(swaggerYamlFile))) {
            swaggerMap = (Map<String, Object>) yaml.load(reader);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        if (null == swaggerMap) {
            throw new IOException();
        }

        Map<String, Object> map = (Map<String, Object>) swaggerMap.get("paths");
        Map<String, Object> schemaDefinitionmap =
            (Map<String, Object>) swaggerMap.get("definitions");
        Map<String, Object> infoMap = (Map<String, Object>) swaggerMap.get("info");
        Map<String, List<Api>> tagMap = new LinkedHashMap<>();

        List<Api> apis = convertToApi(map);
        apis.forEach((api) -> {
            if (!tagMap.containsKey(api.getTag())) {
                List<Api> newApis = new ArrayList<>();
                newApis.add(api);
                tagMap.put(api.getTag(), newApis);
            } else {
                tagMap.get(api.getTag()).add(api);
            }
        });

        Map<String, List<Api>> sortedTagMap = new TreeMap<>(tagMap);
        sortedTagMap.forEach((key, value) -> {
            value.sort(Comparator.comparing(Api::getPath));
        });

        Map<String, Object> resultMap = new HashMap<>();

        List<Definition> definitionList = convertToDefinition(schemaDefinitionmap);

        definitionList =
            definitionList.stream().sorted(Comparator.comparing(Definition::getDefinitionName))
                .collect(Collectors.toList());

        resultMap.put("aaiApis", tagMap);
        resultMap.put("sortedAaiApis", sortedTagMap);
        resultMap.put("wikiLink", wikiLink);
        resultMap.put("definitions", definitionList);
        resultMap.put("version", versionToGenerate);
        if (infoMap.containsKey("description")) {
            String infoDescription = infoMap.get("description").toString();

            infoDescription = Arrays.stream(infoDescription.split("\n")).map(line -> {
                line = line.trim();
                String hyperLink = "";
                if (line.trim().contains("Differences versus")) {
                    return String.format("");
                }
                if (line.trim().contains("https://")) {
                    int startIndex = line.indexOf("https://");
                    int endIndex = line.lastIndexOf("/");
                    hyperLink = line.substring(startIndex, endIndex);
                    return String.format("<a href=\"%s\">%s</a><br/>", hyperLink, line);
                }
                return String.format("%s<br/>", line);
            })

                .collect(Collectors.joining(LINE_SEPARATOR));

            resultMap.put("description", infoDescription);
        }

        Configuration configuration = new Configuration();
        configuration.setClassForTemplateLoading(Api.class, "/");
        String resourcePath = "src/main/resources";
        if (System.getProperty("user.dir") != null
            && !System.getProperty("user.dir").contains(DEFAULT_RUN_DIR)) {
            configuration
                .setDirectoryForTemplateLoading(new File(DEFAULT_RUN_DIR + "/" + resourcePath));
        } else {
            configuration.setDirectoryForTemplateLoading(new File(resourcePath));
        }
        Template template = configuration.getTemplate("swagger.html.ftl");

        String outputDirStr = schemaDir + "/src/main/resources/" + release + "/aai_swagger_html";

        File outputDir = new File(outputDirStr);

        if (!outputDir.exists()) {
            boolean resp = outputDir.mkdir();
            if (!resp) {
                System.err.println("Unable to create the directory: " + outputDirStr);
                System.exit(1);
            }
        } else if (outputDir.isFile()) {
            System.err.println("Unable to create the directory: " + outputDirStr
                + " since a filename with that string exists");
            System.exit(1);
        }

        Writer file =
            new FileWriter(new File(outputDirStr + "/aai_swagger_" + versionToGenerate + ".html"));
        template.process(resultMap, file);
    }

    public static List<Api> convertToApi(Map<String, Object> pathMap) {

        if (pathMap == null)
            throw new IllegalArgumentException();

        List<Api> apis = new ArrayList<>();

        pathMap.forEach((pathKey, pathValue) -> {

            Api api = new Api();
            Map<String, Object> httpVerbMap = (Map<String, Object>) pathValue;
            List<Api.HttpVerb> httpVerbs = new ArrayList<>();

            api.setPath(pathKey);

            httpVerbMap.forEach((httpVerbKey, httpVerbValue) -> {

                Api.HttpVerb httpVerb = new Api.HttpVerb();

                Map<String, Object> httpVerbValueMap = (Map<String, Object>) httpVerbValue;

                httpVerb.setType(httpVerbKey);

                if (httpVerbValueMap.containsKey("tags")) {
                    httpVerb.setTags((List<String>) httpVerbValueMap.get("tags"));
                }

                if (httpVerbValueMap.containsKey("summary")) {
                    httpVerb.setSummary((String) httpVerbValueMap.get("summary"));
                }

                if (httpVerbValueMap.containsKey("operationId")) {
                    httpVerb.setOperationId((String) httpVerbValueMap.get("operationId"));
                }

                if (httpVerbValueMap.containsKey("consumes")) {
                    httpVerb.setConsumes((List<String>) httpVerbValueMap.get("consumes"));
                    if (httpVerb.getConsumes() != null) {
                        httpVerb.setConsumerEnabled(true);
                    }
                }

                if (httpVerbValueMap.containsKey("produces")) {
                    httpVerb.setProduces((List<String>) httpVerbValueMap.get("produces"));
                }

                if (httpVerbValueMap.containsKey("parameters")) {
                    List<Map<String, Object>> parameters =
                        (List<Map<String, Object>>) httpVerbValueMap.get("parameters");
                    List<Map<String, Object>> requestParameters = parameters.stream()
                        .filter((parameter) -> !parameter.get("name").equals("body"))
                        .collect(Collectors.toList());
                    httpVerb.setParameters(requestParameters);
                    if (httpVerb.getParameters() != null) {
                        httpVerb.setParametersEnabled(true);
                    }

                    List<Map<String, Object>> requestBodyList = parameters.stream()
                        .filter((parameter) -> parameter.get("name").equals("body"))
                        .collect(Collectors.toList());

                    Map<String, Object> requestBody = null;

                    if (requestBodyList != null && requestBodyList.size() == 1) {
                        requestBody = requestBodyList.get(0);
                        for (String key : requestBody.keySet()) {
                            // Filter out all the relationship links that appear in the YAML
                            if (key.equals("description")) {
                                String reqBody = (String) requestBody.get(key);
                                if (reqBody.replaceAll("\\[.*.json\\)", "") != reqBody) {
                                    requestBody.put(key, reqBody.replaceAll("\\[.*.json\\)", ""));
                                }
                            }
                            // Filter out all the patchDefinition links that appear in the YAML
                            if (key.equals("schema")) {
                                LinkedHashMap<String, String> reqBody =
                                    (LinkedHashMap<String, String>) requestBody.get(key);
                                String schema = reqBody.get("$ref");
                                String schemaNopatch =
                                    schema.replace("patchDefinitions", "definitions");

                                if (!schema.equals(schemaNopatch)) {
                                    reqBody.put("$ref", schemaNopatch);
                                    requestBody.put(key, reqBody);
                                }
                            }
                        }
                        httpVerb.setBodyParametersEnabled(true);
                        httpVerb.setBodyParameters(requestBody);

                        if (requestBody != null && requestBody.containsKey("schema")) {
                            Map<String, Object> schemaMap =
                                (Map<String, Object>) requestBody.get("schema");
                            if (schemaMap != null && schemaMap.containsKey("$ref")) {
                                String schemaLink = schemaMap.get("$ref").toString();
                                httpVerb.setSchemaLink(schemaLink);
                                int retCode = schemaLink.lastIndexOf('/');
                                if (retCode != -1 && retCode != schemaLink.length()) {
                                    httpVerb.setSchemaType(schemaLink.substring(retCode));
                                }
                            }
                        }
                    }
                }

                if (httpVerbValueMap.containsKey("responses")) {

                    List<Api.HttpVerb.Response> responses = new ArrayList<Api.HttpVerb.Response>();

                    Map<String, Object> responsesMap =
                        (Map<String, Object>) httpVerbValueMap.get("responses");

                    responsesMap.entrySet().stream()
                        .filter((res) -> !"default".equalsIgnoreCase(res.getKey()))
                        .forEach((responseMap) -> {

                            Map<String, Object> responseValueMap =
                                (Map<String, Object>) responseMap.getValue();

                            Api.HttpVerb.Response response = new Api.HttpVerb.Response();

                            response.setResponseCode(responseMap.getKey());
                            response.setDescription((String) responseValueMap.get("description"));
                            response.setVersion((String) responseValueMap.get("version"));

                            if (responseValueMap != null
                                && responseValueMap.containsKey("schema")) {
                                Map<String, Object> schemaMap =
                                    (Map<String, Object>) responseValueMap.get("schema");
                                if (schemaMap != null && schemaMap.containsKey("$ref")) {
                                    String schemaLink = schemaMap.get("$ref").toString();
                                    httpVerb.setHasReturnSchema(true);
                                    // Filter out all the getDefinition links that appear in the
                                    // YAML
                                    httpVerb.setReturnSchemaLink(
                                        schemaLink.replace("getDefinitions", "definitions"));
                                    int retCode = schemaLink.lastIndexOf('/');
                                    if (retCode != -1 && retCode != schemaLink.length()) {
                                        httpVerb
                                            .setReturnSchemaObject(schemaLink.substring(retCode));
                                    }
                                }
                            }

                            responses.add(response);
                        });

                    httpVerb.setResponses(responses);
                }

                httpVerbs.add(httpVerb);
            });

            api.setHttpMethods(httpVerbs);
            apis.add(api);
        });

        return apis;
    }

    public static List<Definition> convertToDefinition(Map<String, Object> definitionMap) {

        if (definitionMap == null)
            throw new IllegalArgumentException();

        List<Definition> defintionsList = new ArrayList<>();

        definitionMap.entrySet().forEach((entry) -> {

            Definition definition = new Definition();
            String key = entry.getKey();
            Map<String, Object> valueMap = (Map<String, Object>) entry.getValue();

            definition.setDefinitionName(key);

            if (valueMap.containsKey("description")) {
                String description = valueMap.get("description").toString();
                description = formatDescription(description);
                definition.setDefinitionDescription(description);
                definition.setHasDescription(true);
            }

            List<Definition.Property> definitionProperties = new ArrayList<>();

            List<String> requiredProperties = (valueMap.get("required") == null) ? new ArrayList<>()
                : (List<String>) valueMap.get("required");

            Set<String> requiredPropsSet = requiredProperties.stream().collect(Collectors.toSet());

            valueMap.entrySet().stream().filter((e) -> "properties".equals(e.getKey()))
                .forEach((propertyEntries) -> {
                    Map<String, Object> propertyRealEntries =
                        (Map<String, Object>) propertyEntries.getValue();
                    propertyRealEntries.entrySet().forEach((propertyEntry) -> {
                        Definition.Property definitionProperty = new Definition.Property();
                        String propertyKey = propertyEntry.getKey();
                        if (requiredPropsSet.contains(propertyKey)) {
                            definitionProperty.setRequired(true);
                        }
                        definitionProperty.setPropertyName(propertyKey);
                        Map<String, Object> definitionPropertyMap =
                            (Map<String, Object>) propertyEntry.getValue();

                        if (definitionPropertyMap.containsKey("description")) {
                            definitionProperty.setPropertyDescription(
                                definitionPropertyMap.get("description").toString());
                            definitionProperty.setHasPropertyDescription(true);
                        }
                        if (definitionPropertyMap.containsKey("type")) {
                            String type = definitionPropertyMap.get("type").toString();
                            definitionProperty.setPropertyType(type);
                            definitionProperty.setHasType(true);
                            if ("array".equals(type)) {
                                definitionProperty.setPropertyType("object[]");
                                if (!definitionPropertyMap.containsKey("items")) {
                                    throw new RuntimeException(
                                        "Unable to find the property items even though the type is array for "
                                            + propertyEntry.getKey());
                                } else {
                                    Map<String, Object> itemMap =
                                        (Map<String, Object>) definitionPropertyMap.get("items");
                                    if (itemMap.containsKey("$ref")) {
                                        definitionProperty.setHasPropertyReference(true);
                                        String refItem = itemMap.get("$ref").toString();
                                        int retCode = refItem.lastIndexOf('/');
                                        if (retCode != -1 && retCode != refItem.length()) {
                                            definitionProperty.setPropertyReferenceObjectName(
                                                refItem.substring(retCode + 1));
                                        }
                                        definitionProperty.setPropertyReference(refItem);
                                    }
                                }
                            } else {
                                if (definitionPropertyMap.containsKey("$ref")) {
                                    definitionProperty.setHasPropertyReference(true);
                                    String refItem = definitionPropertyMap.get("$ref").toString();
                                    int retCode = refItem.lastIndexOf('/');
                                    if (retCode != -1 && retCode != refItem.length()) {
                                        definitionProperty.setPropertyReferenceObjectName(
                                            refItem.substring(retCode + 1));
                                    }
                                    definitionProperty.setPropertyReference(refItem);
                                }
                            }
                        }
                        definitionProperties.add(definitionProperty);
                    });
                });

            definition.setPropertyList(definitionProperties);

            List<Definition.Property> schemaProperties = definitionProperties.stream()
                .filter((o) -> o.isHasPropertyReference()).collect(Collectors.toList());

            List<Definition.Property> regularProperties = definitionProperties.stream()
                .filter((o) -> !o.isHasPropertyReference()).collect(Collectors.toList());

            definition.setRegularPropertyList(regularProperties);
            definition.setSchemaPropertyList(schemaProperties);

            defintionsList.add(definition);
        });
        return defintionsList;
    }

    public static String formatDescription(String description) {

        description = Arrays.stream(description.split("\n")).map((line) -> {
            line = line.trim();
            if (line.contains("######")) {
                line = line.replaceAll("#", "");
                line = line.trim();
                String headerId = line.toLowerCase().replaceAll("\\s", "-");

                if (line.contains("Related Nodes")) {
                    return String.format("<h6 id=\"%s\">%s</h6>%s<ul>", headerId, line,
                        LINE_SEPARATOR);
                } else {
                    return String.format("<h6 id=\"%s\">%s</h6>", headerId, line);
                }
            } else if (line.startsWith("-")) {
                line = line.replaceFirst("-", "");
                line = line.trim();
                return String.format("<li>%s</li>", line);
            } else {
                return String.format("<p>%s</p>", line);
            }
        }).collect(Collectors.joining(LINE_SEPARATOR));

        if (description.contains("<ul>")) {
            description = description + "</ul>";
        }

        return description;
    }

}
