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
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ============LICENSE_END=========================================================
 */

package org.onap.aai.setup;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.onap.aai.restclient.RestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

/**
 * <b>AAIConfigTranslator</b> is responsible for looking at the schema files and
 * edge files based on the available versions Also has the ability to exclude
 * them based on the node.exclusion.pattern
 */
@Component
@ConditionalOnProperty(name = "schema.translator.list", havingValue = "schema-service", matchIfMissing = false)
public class SchemaServiceTranslator extends Translator {

    private static final Logger LOGGER = LoggerFactory.getLogger(SchemaServiceTranslator.class);

    @Value("${schema.service.nodes.endpoint}")
    private String nodeSchemaUri;

    @Value("${schema.service.edges.endpoint}")
    private String edgeSchemaUri;

    @Autowired
    private RestClient restClient;

    public SchemaServiceTranslator(SchemaVersions schemaVersions) {
        super(schemaVersions);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.aai.setup.ConfigTranslator#getNodeFiles()
     */

    @Override
    public List<InputStream> getVersionNodeStream(SchemaVersion version) throws IOException {

        final Map<String, String> headersMap = new HashMap<>();
        headersMap.put(HttpHeaders.ACCEPT, MediaType.APPLICATION_XML.toString());
        headersMap.put(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_XML.toString());
        final String content = "";
        final String uri = nodeSchemaUri + version.toString();
        ResponseEntity<Resource> schemaResponse = restClient.getGetResource(content, uri, headersMap);

        verifySchemaServiceResponse(schemaResponse.getStatusCode());
        LOGGER.debug("SchemaResponse Status code" + schemaResponse.getStatusCode());

        Resource resultBody = schemaResponse.getBody();
        return resultBody != null
            ? Collections.singletonList(resultBody.getInputStream())
            : Collections.emptyList();
    }

    @Override
    public List<String> getJsonPayload(SchemaVersion version) throws IOException {
        /*
         * Call Schema MS to get versions using RestTemplate
         */
        final String content = "";
        final String uri = edgeSchemaUri + version.toString();
        final Map<String, String> headersMap = new HashMap<>();

        ResponseEntity<String> schemaResponse = restClient.getGetRequest(content, uri, headersMap);
        verifySchemaServiceResponse(schemaResponse.getStatusCode());
        LOGGER.debug("SchemaResponse Status code" + schemaResponse.getStatusCode());
        return Collections.singletonList(schemaResponse.getBody());

    }

    private void verifySchemaServiceResponse(HttpStatusCode statusCode) throws IOException {
        if (!statusCode.equals(HttpStatusCode.valueOf(HttpStatus.OK.value()))) {
            LOGGER.error("Please check the Schema Service. It returned with the status code {}", statusCode);
            throw new IOException("SchemaService is not available");
        }
    }

}
