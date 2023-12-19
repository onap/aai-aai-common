/**
 * ============LICENSE_START=======================================================
 * org.onap.aai
 * ================================================================================
 * Copyright © 2017-2018 AT&T Intellectual Property. All rights reserved.
 * Modifications Copyright (C) 2023 Deutsche Telekom SA.
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

package org.onap.aai.restclient;

import static org.junit.Assert.assertNotNull;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.boot.web.client.RestTemplateCustomizer;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.xml.MappingJackson2XmlHttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.test.web.client.ExpectedCount;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Component
public class MockRestClient extends RestClient {

    private final RestTemplate restTemplate;
    private final MockRestServiceServer mockRestServiceServer;

    public MockRestClient(String fileName) {
        // When jackson-dataformat-xml is on the classpath, the default Content-Type changes
        // from application/json to application/xml
        RestTemplateBuilder restTemplateBuilder = new RestTemplateBuilder(new RestTemplateCustomizer() {
            @Override
            public void customize(RestTemplate restTemplate) {
                restTemplate.getMessageConverters()
                    .removeIf(converter -> MappingJackson2XmlHttpMessageConverter.class.isAssignableFrom(converter.getClass()));
            }
        });
        restTemplate = restTemplateBuilder.build();
        mockRestServiceServer = MockRestServiceServer.createServer(restTemplate);

        JsonObject payload = null;
        try {
            payload = getPayload(fileName + ".json");
        } catch (IOException e) {
            e.printStackTrace();
        }
        JsonArray mockUris = payload.getAsJsonArray("mock-uri");
        
        String url = "https://localhost:8447/aai/v14";

        for (int i = 0; i < mockUris.size(); i++) {
            registerRequestStub(mockUris, url, i);
        }
    }

    private void registerRequestStub(JsonArray mockUris, String url, int i) {
        JsonObject jsonObject = mockUris.get(i).getAsJsonObject();
        String responseFile = jsonObject.get("response-file").getAsString();
        String contentTypeValue = jsonObject.get("content").getAsString();
        String uri = jsonObject.get("aai-uri").getAsString();

        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(responseFile);
        String responseBody = null;
        try {
            responseBody = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }

        mockRestServiceServer.expect(ExpectedCount.manyTimes(), requestTo(url + uri))
                .andExpect(method(HttpMethod.GET)).andExpect(content().contentType(contentTypeValue))
                .andRespond(withStatus(HttpStatus.OK).body(responseBody.toString())
                        .contentType(MediaType.valueOf(contentTypeValue)));
    }

    public MockRestClient() {
        this("mockrequests");
    }

    public JsonObject getTestDetails(String fileName) throws IOException {

        JsonObject payload = getPayload(fileName);

        return payload;
    }

    public JsonObject getPayload(String filename) throws IOException {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(filename);

        String result = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
        String message = String.format("Unable to find the %s in src/test/resources", filename);
        assertNotNull(message, inputStream);

        JsonObject payload = JsonParser.parseString(result).getAsJsonObject();
        return payload;
    }

    @Override
    public ResponseEntity<String> execute(String uri, HttpMethod method, Map<String, String> headers, String body) {

        String url = "https://localhost:8447/aai/v14/" + uri;

        HttpHeaders headersMap = new HttpHeaders();

        headersMap.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headersMap.setContentType(MediaType.APPLICATION_JSON);
        headersMap.add("Real-Time", "true");
        headersMap.add("X-FromAppId", "JUNIT");
        headersMap.add("X-TransactionId", "JUNIT");

        HttpEntity<String> httpEntity = new HttpEntity(headers);

        ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.GET, httpEntity, String.class);

        return responseEntity;
    }

    @Override
    public ResponseEntity<Resource> executeResource(String uri, HttpMethod method, Map<String, String> headers,
            String body) {

        String url = "https://localhost:8447/aai/v14/" + uri;

        HttpHeaders headersMap = new HttpHeaders();

        headersMap.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headersMap.setContentType(MediaType.APPLICATION_JSON);
        headersMap.add("Real-Time", "true");
        headersMap.add("X-FromAppId", "JUNIT");
        headersMap.add("X-TransactionId", "JUNIT");

        HttpEntity<String> httpEntity = new HttpEntity(headers);

        ResponseEntity<Resource> responseEntity =
                restTemplate.exchange(url, HttpMethod.GET, httpEntity, Resource.class);

        // mockRestServiceServer.verify();
        return responseEntity;
    }

    @Override
    public RestTemplate getRestTemplate() {
        RestTemplate restTemplate = null;
        return restTemplate;
    }

    public String getBaseUrl() {
        return "";
    }

    protected MultiValueMap<String, String> getHeaders(Map<String, String> headers) {
        return null;
    }

}
