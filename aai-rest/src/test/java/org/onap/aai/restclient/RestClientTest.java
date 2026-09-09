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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.net.URI;
import java.util.Map;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

public class RestClientTest {

  @Test
  public void executeSendsHeadersAndBody() {
    RestTemplate restTemplate = mock(RestTemplate.class);
    ResponseEntity<String> expectedResponse = new ResponseEntity<>("response", HttpStatus.OK);
    when(restTemplate.exchange(
            eq(URI.create("https://client.example/api/items")),
            eq(HttpMethod.POST),
            org.mockito.ArgumentMatchers.<HttpEntity<String>>any(),
            eq(String.class)))
        .thenReturn(expectedResponse);

    RestClient client = new TestRestClient("https://client.example/api/", restTemplate);
    ResponseEntity<String> actualResponse = client.execute("/items", HttpMethod.POST, Map.of("X-Request-Id", "123"), "payload");

    ArgumentCaptor<HttpEntity<String>> requestCaptor = ArgumentCaptor.forClass(HttpEntity.class);
    verify(restTemplate)
        .exchange(eq(URI.create("https://client.example/api/items")), eq(HttpMethod.POST), requestCaptor.capture(), eq(String.class));
    assertEquals(expectedResponse, actualResponse);
    assertEquals("payload", requestCaptor.getValue().getBody());
    assertEquals("123", requestCaptor.getValue().getHeaders().getFirst("X-Request-Id"));
  }

  @ParameterizedTest
  @MethodSource("uriBoundaryCases")
  public void executeNormalizesBaseUrlAndUriBoundary(String baseUrl, String uri, String expectedUrl) {
    RestTemplate restTemplate = mock(RestTemplate.class);
    when(restTemplate.exchange(
            eq(URI.create(expectedUrl)),
            eq(HttpMethod.GET),
            org.mockito.ArgumentMatchers.<HttpEntity<String>>any(),
            eq(String.class)))
        .thenReturn(ResponseEntity.ok("response"));

    new TestRestClient(baseUrl, restTemplate).execute(uri, HttpMethod.GET, Map.of());

    verify(restTemplate)
        .exchange(eq(URI.create(expectedUrl)), eq(HttpMethod.GET), org.mockito.ArgumentMatchers.any(HttpEntity.class), eq(String.class));
  }

  @Test
  public void executeWithStringMethodDispatchesToHttpMethodOverload() {
    RestTemplate restTemplate = mock(RestTemplate.class);
    when(restTemplate.exchange(
            eq(URI.create("https://client.example/api/items")),
            eq(HttpMethod.PATCH),
            org.mockito.ArgumentMatchers.<HttpEntity<String>>any(),
            eq(String.class)))
        .thenReturn(ResponseEntity.ok("response"));

    new TestRestClient("https://client.example/api", restTemplate)
        .execute("items", "PATCH", Map.of("X-Request-Id", "123"), "payload");

    verify(restTemplate)
        .exchange(
            eq(URI.create("https://client.example/api/items")),
            eq(HttpMethod.PATCH),
            org.mockito.ArgumentMatchers.any(HttpEntity.class),
            eq(String.class));
  }

  @ParameterizedTest
  @MethodSource("responseErrorCases")
  public void responseErrorHandlerMapsOnlyForbiddenAndServerErrors(int statusCode, boolean expectedError) throws Exception {
    ClientHttpResponse response = mock(ClientHttpResponse.class);
    when(response.getStatusCode()).thenReturn(HttpStatusCode.valueOf(statusCode));
    when(response.getRawStatusCode()).thenReturn(statusCode);
    when(response.getStatusText()).thenReturn("status");

    assertEquals(expectedError, new RestClientResponseErrorHandler().hasError(response));
  }

  @Test
  public void aaiRestClientAddsJsonAndRealtimeHeaders() {
    MultiValueMap<String, String> headers = new AAIRestClient().getHeaders(Map.of("X-Request-Id", "123"));

    assertEquals("application/json", headers.getFirst(HttpHeaders.CONTENT_TYPE));
    assertEquals("application/json", headers.getFirst(HttpHeaders.ACCEPT));
    assertEquals("true", headers.getFirst("Real-Time"));
    assertEquals("123", headers.getFirst("X-Request-Id"));
  }

  @Test
  public void restClientVariantsConfigureAnErrorHandlerAndLoggingInterceptor() throws Exception {
    assertConfigured(new TestNoAuthRestClient());
    assertConfigured(new TestOneWaySslRestClient());
    assertConfigured(new TestTwoWaySslRestClient());
  }

  private static Stream<Arguments> uriBoundaryCases() {
    return Stream.of(
        Arguments.of("https://client.example/api", "items", "https://client.example/api/items"),
        Arguments.of("https://client.example/api", "/items", "https://client.example/api/items"),
        Arguments.of("https://client.example/api/", "items", "https://client.example/api/items"),
        Arguments.of("https://client.example/api/", "/items", "https://client.example/api/items"));
  }

  private static Stream<Arguments> responseErrorCases() {
    return Stream.of(
      Arguments.of(200, false),
      Arguments.of(401, false),
      Arguments.of(403, true),
      Arguments.of(404, false),
      Arguments.of(302, false),
      Arguments.of(499, false),
      Arguments.of(500, true),
      Arguments.of(501, true));
  }

  private static void assertConfigured(RestClient client) throws Exception {
    if (client instanceof NoAuthRestClient) {
      ((NoAuthRestClient) client).init();
    } else if (client instanceof OneWaySSLRestClient) {
      ((OneWaySSLRestClient) client).init();
    } else {
      ((TwoWaySSLRestClient) client).init();
    }

    assertTrue(client.getRestTemplate().getErrorHandler() instanceof RestClientResponseErrorHandler);
    assertFalse(client.getRestTemplate().getInterceptors().isEmpty());
  }

  private static MultiValueMap<String, String> createHeaders(Map<String, String> headers) {
    HttpHeaders httpHeaders = new HttpHeaders();
    headers.forEach(httpHeaders::add);
    return httpHeaders;
  }

  private static final class TestRestClient extends RestClient {

    private final String baseUrl;
    private final RestTemplate restTemplate;

    private TestRestClient(String baseUrl, RestTemplate restTemplate) {
      this.baseUrl = baseUrl;
      this.restTemplate = restTemplate;
    }

    @Override
    public RestTemplate getRestTemplate() {
      return restTemplate;
    }

    @Override
    public String getBaseUrl() {
      return baseUrl;
    }

    @Override
    protected MultiValueMap<String, String> getHeaders(Map<String, String> headers) {
      return createHeaders(headers);
    }
  }

  private static final class TestNoAuthRestClient extends NoAuthRestClient {

    @Override
    public String getBaseUrl() {
      return "https://client.example";
    }

    @Override
    protected MultiValueMap<String, String> getHeaders(Map<String, String> headers) {
      return createHeaders(headers);
    }
  }

  private static final class TestOneWaySslRestClient extends OneWaySSLRestClient {

    @Override
    public String getBaseUrl() {
      return "https://client.example";
    }

    @Override
    protected MultiValueMap<String, String> getHeaders(Map<String, String> headers) {
      return createHeaders(headers);
    }
  }

  private static final class TestTwoWaySslRestClient extends TwoWaySSLRestClient {

    @Override
    public String getBaseUrl() {
      return "https://client.example";
    }

    @Override
    protected MultiValueMap<String, String> getHeaders(Map<String, String> headers) {
      return createHeaders(headers);
    }
  }
}
