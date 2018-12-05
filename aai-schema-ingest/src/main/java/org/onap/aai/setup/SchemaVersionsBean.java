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
import org.onap.aai.restclient.RestClient;
import org.onap.aai.restclient.RestClientFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SchemaVersionsBean {

    private String SCHEMA_SERVICE = "schema-service";
    private SchemaServiceVersions schemaVersions;

    @Value("${schema.service.versions.endpoint}")
    private String versionsUri;

    @Autowired
    private RestClientFactory restClientFactory;

    @PostConstruct
    public void initialize() {
        //Call SchemaService to get versions
        retrieveAllSchemaVersions();
    }

    public void retrieveAllSchemaVersions() {
	    /*
	    Call Schema MS to get versions using RestTemplate
	     */
        String content = "";
        Map<String, String> headersMap = new HashMap<>();
        RestClient restClient = restClientFactory
            .getRestClient(SCHEMA_SERVICE);

        ResponseEntity<String> schemaResponse = restClient.getGetRequest( content, versionsUri, headersMap);
        Gson gson = new GsonBuilder()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_DASHES)
            .create();
        schemaVersions = gson.fromJson(schemaResponse.getBody(), SchemaServiceVersions.class);
        schemaVersions.initializeFromSchemaService();

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
