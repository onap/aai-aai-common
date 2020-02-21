/**
 * ============LICENSE_START=======================================================
 * org.onap.aai
 * ================================================================================
 * Copyright © 2017-18 AT&T Intellectual Property. All rights reserved.
 * ================================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ============LICENSE_END=========================================================
 */

package org.onap.aai.setup;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.onap.aai.restclient.RestClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;

public class SchemaVersionsBean {

    private String SCHEMA_SERVICE = "schema-service";
    private SchemaServiceVersions schemaVersions;

    @Value("${schema.service.versions.endpoint}")
    private String versionsUri;

    @Value("${schema.service.versions.override:false}")
    private String overrideSchemaService;

    @Autowired(required = false)
    private SchemaConfigVersions schemaConfigVersions;

    @Qualifier("restClient")
    @Autowired
    private RestClient restClient;

    @PostConstruct
    public void initialize() {
        // Call SchemaService to get versions
        retrieveAllSchemaVersions();
    }

    public void retrieveAllSchemaVersions() throws ExceptionInInitializerError {
        /*
         * Call Schema MS to get versions using RestTemplate
         */
        String content = "";
        Map<String, String> headersMap = new HashMap<>();

        ResponseEntity<String> schemaResponse = restClient.getGetRequest(content, versionsUri, headersMap);
        Gson gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_DASHES).create();
        schemaVersions = gson.fromJson(schemaResponse.getBody(), SchemaServiceVersions.class);
        if (!validateOverrides(schemaVersions)) {
            throw new ExceptionInInitializerError("The versions requested is not supported by SchemaService");
        }
        if ("true".equals(overrideSchemaService)) {
            schemaVersions.initializeFromSchemaConfig(schemaConfigVersions);
        } else {
            schemaVersions.initializeFromSchemaService();
        }

    }

    public boolean validateOverrides(SchemaServiceVersions schemaVersions1) {
        boolean versionsAvailable = true;
        if ("true".equals(overrideSchemaService)) {
            versionsAvailable = schemaConfigVersions.getApiVersions().stream()
                    .allMatch((s) -> schemaVersions1.getVersionsAll().contains(s));

        }
        return versionsAvailable;
    }

    public SchemaServiceVersions getSchemaVersions() {
        return schemaVersions;
    }

    public void setSchemaVersions(SchemaServiceVersions schemaVersions) {
        this.schemaVersions = schemaVersions;
    }

    public List<SchemaVersion> getVersions() {
        return getSchemaVersions().getVersions();
    }

}
