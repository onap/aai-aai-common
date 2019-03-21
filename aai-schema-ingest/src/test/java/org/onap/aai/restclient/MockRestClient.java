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

package org.onap.aai.restclient;

import static org.junit.Assert.assertNotNull;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;

import com.att.eelf.configuration.EELFLogger;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.test.web.client.ExpectedCount;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Component
public class MockRestClient extends RestClient {

    private RestTemplate restTemplate;
    private MockRestServiceServer mockRestServiceServer;

    String fileName = "mockrequests";

    public MockRestClient(String fileName) {
        /*
         * List<MockRestServiceServer> mockedAAIRequests = new ArrayList<>(aaiRequests.size());
         */
        List<MockRestServiceServer> mockedAAIRequests = new ArrayList<>();

        restTemplate = new RestTemplate();
        /*
         * MockRestServiceServer server = MockRestServiceServer
         * .bindTo(restClientFactory.getRestClient(ClientType.SchemaService).getRestTemplate())
         * .build();
         * server.expect(MockRestRequestMatchers.requestTo(url))
         * .andRespond(withSuccess("{}", MediaType.APPLICATION_JSON));
         */

        // RestTemplateBuilder mockBuilder = mock(RestTemplateBuilder.class);
        // when(mockBuilder.build()).thenReturn(restTemplate);

        JsonObject payload = null;
        try {
            payload = getPayload(fileName + ".json");
        } catch (IOException e) {
            e.printStackTrace();
        }

        JsonArray mockUris = payload.getAsJsonArray("mock-uri");

        mockRestServiceServer = MockRestServiceServer.createServer(restTemplate);
        String url = "https://localhost:8447/aai/v14";
        /*
         * mockRestServiceServer.expect(requestTo(url))
         * .andRespond(withSuccess("{}", MediaType.APPLICATION_JSON));
         */

        for (int i = 0; i < mockUris.size(); i++) {
            String responseFile = mockUris.get(i).getAsJsonObject().get("response-file").getAsString();
            String contentTypeValue = mockUris.get(i).getAsJsonObject().get("content").getAsString();

            String uri = mockUris.get(i).getAsJsonObject().get("aai-uri").getAsString();

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
    }

    public MockRestClient() {

        restTemplate = new RestTemplate();
        /*
         * MockRestServiceServer server = MockRestServiceServer
         * .bindTo(restClientFactory.getRestClient(ClientType.SchemaService).getRestTemplate())
         * .build();
         * server.expect(MockRestRequestMatchers.requestTo(url))
         * .andRespond(withSuccess("{}", MediaType.APPLICATION_JSON));
         */

        // RestTemplateBuilder mockBuilder = mock(RestTemplateBuilder.class);
        // when(mockBuilder.build()).thenReturn(restTemplate);

        JsonObject payload = null;
        try {
            payload = getPayload(fileName + ".json");
        } catch (IOException e) {
            e.printStackTrace();
        }

        JsonArray mockUris = payload.getAsJsonArray("mock-uri");

        mockRestServiceServer = MockRestServiceServer.createServer(restTemplate);
        String url = "https://localhost:8447/aai/v14";
        /*
         * mockRestServiceServer.expect(requestTo(url))
         * .andRespond(withSuccess("{}", MediaType.APPLICATION_JSON));
         */

        for (int i = 0; i < mockUris.size(); i++) {
            String responseFile = mockUris.get(i).getAsJsonObject().get("response-file").getAsString();
            String contentTypeValue = mockUris.get(i).getAsJsonObject().get("content").getAsString();

            String uri = mockUris.get(i).getAsJsonObject().get("aai-uri").getAsString();

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

    }

    public JsonObject getTestDetails(String fileName) throws IOException {

        JsonObject payload = getPayload(fileName);

        return payload;
    }

    public JsonObject getPayload(String filename) throws IOException {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(filename);

        // InputStream inputStream = new FileInputStream(filename);

        String result = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
        String message = String.format("Unable to find the %s in src/test/resources", filename);
        assertNotNull(message, inputStream);

        JsonParser parser = new JsonParser();
        JsonObject payload = parser.parse(result).getAsJsonObject();
        return payload;
    }

    @Override
    public ResponseEntity execute(String uri, HttpMethod method, Map<String, String> headers, String body) {

        String url = "https://localhost:8447/aai/v14/" + uri;

        /*
         * MockRestServiceServer server = MockRestServiceServer
         * .bindTo(restClientFactory.getRestClient(ClientType.SchemaService).getRestTemplate())
         * .build();
         * server.expect(MockRestRequestMatchers.requestTo(url))
         * .andRespond(withSuccess("{}", MediaType.APPLICATION_JSON));
         */

        // RestTemplateBuilder mockBuilder = mock(RestTemplateBuilder.class);
        // when(mockBuilder.build()).thenReturn(restTemplate);

        /*
         * MockRestServiceServer server = MockRestServiceServer.createServer(restTemplate);
         * server.expect(requestTo(url))
         * .andRespond(withSuccess("{}", MediaType.APPLICATION_JSON));
         * return new ResponseEntity("blah", HttpStatus.OK);
         * server.expect(ExpectedCount.manyTimes(), requestTo(Matchers.startsWith(aaiBaseUrl +
         * aaiRequests.get(i).get("aai-uri").asText())))
         * .andExpect(method(HttpMethod.GET))
         * .andExpect(content().contentType(MediaType.APPLICATION_JSON))
         * .andRespond(withStatus(HttpStatus.OK).body(aaiResponses.get(i).toString()).contentType(MediaType.
         * APPLICATION_JSON));
         */

        HttpHeaders headersMap = new HttpHeaders();

        headersMap.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headersMap.setContentType(MediaType.APPLICATION_JSON);
        headersMap.add("Real-Time", "true");
        headersMap.add("X-FromAppId", "JUNIT");
        headersMap.add("X-TransactionId", "JUNIT");

        HttpEntity httpEntity = new HttpEntity(headers);

        ResponseEntity responseEntity = restTemplate.exchange(url, HttpMethod.GET, httpEntity, String.class);

        // mockRestServiceServer.verify();
        return responseEntity;
    }

    @Override
    public ResponseEntity executeResource(String uri, HttpMethod method, Map<String, String> headers, String body) {

        String url = "https://localhost:8447/aai/v14/" + uri;

        /*
         * MockRestServiceServer server = MockRestServiceServer
         * .bindTo(restClientFactory.getRestClient(ClientType.SchemaService).getRestTemplate())
         * .build();
         * server.expect(MockRestRequestMatchers.requestTo(url))
         * .andRespond(withSuccess("{}", MediaType.APPLICATION_JSON));
         */

        // RestTemplateBuilder mockBuilder = mock(RestTemplateBuilder.class);
        // when(mockBuilder.build()).thenReturn(restTemplate);

        /*
         * MockRestServiceServer server = MockRestServiceServer.createServer(restTemplate);
         * server.expect(requestTo(url))
         * .andRespond(withSuccess("{}", MediaType.APPLICATION_JSON));
         * return new ResponseEntity("blah", HttpStatus.OK);
         * server.expect(ExpectedCount.manyTimes(), requestTo(Matchers.startsWith(aaiBaseUrl +
         * aaiRequests.get(i).get("aai-uri").asText())))
         * .andExpect(method(HttpMethod.GET))
         * .andExpect(content().contentType(MediaType.APPLICATION_JSON))
         * .andRespond(withStatus(HttpStatus.OK).body(aaiResponses.get(i).toString()).contentType(MediaType.
         * APPLICATION_JSON));
         */

        HttpHeaders headersMap = new HttpHeaders();

        headersMap.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headersMap.setContentType(MediaType.APPLICATION_JSON);
        headersMap.add("Real-Time", "true");
        headersMap.add("X-FromAppId", "JUNIT");
        headersMap.add("X-TransactionId", "JUNIT");

        HttpEntity httpEntity = new HttpEntity(headers);

        ResponseEntity responseEntity = restTemplate.exchange(url, HttpMethod.GET, httpEntity, Resource.class);

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

    protected EELFLogger getLogger() {
        return null;
    }

}
