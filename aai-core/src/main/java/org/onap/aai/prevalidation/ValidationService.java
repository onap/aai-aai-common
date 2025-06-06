/**
 * ============LICENSE_START=======================================================
 * org.onap.aai
 * ================================================================================
 * Copyright © 2017-2018 AT&T Intellectual Property. All rights reserved.
 * ================================================================================
 *  Modifications Copyright © 2018 IBM.
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

package org.onap.aai.prevalidation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import jakarta.annotation.PostConstruct;

import org.apache.hc.client5.http.ConnectTimeoutException;
import org.onap.aai.domain.notificationEvent.NotificationEvent;
import org.onap.aai.domain.notificationEvent.NotificationEvent.EventHeader;
import org.onap.aai.exceptions.AAIException;

import org.onap.aai.restclient.RestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

/**
 * <b>ValidationService</b> routes all the writes to the database
 * excluding deletes for now to the validation service to verify
 * that the request is an valid one before committing to the database
 */
@Service
@Profile("pre-validation")
public class ValidationService {

    static final String CONNECTION_REFUSED_STRING =
            "Connection refused to the validation microservice due to service unreachable";
    static final String CONNECTION_TIMEOUT_STRING = "Connection timeout to the validation microservice as this could "
            + "indicate the server is unable to reach port, "
            + "please check on server by running: nc -w10 -z -v ${VALIDATION_HOST} ${VALIDATION_PORT}";
    static final String REQUEST_TIMEOUT_STRING =
            "Request to validation service took longer than the currently set timeout";
    static final String VALIDATION_ENDPOINT = "/v1/validate";
    static final String VALIDATION_HEALTH_ENDPOINT = "/v1/info";

    private static final Logger LOGGER = LoggerFactory.getLogger(ValidationService.class);
    private static final String DELETE = "DELETE";

    private final RestClient validationRestClient;
    private final String appName;
    private final Set<String> validationNodeTypes;
    private final ObjectMapper mapper;
    private final List<Pattern> exclusionList;

    public ValidationService(@Qualifier("validationRestClient") RestClient validationRestClient,
            @Value("${spring.application.name}") String appName,
            @Value("${validation.service.node-types}") String validationNodes,
            @Value("${validation.service.exclusion-regexes:#{null}}") String exclusionRegexes,
            ObjectMapper mapper) {
        this.validationRestClient = validationRestClient;
        this.appName = appName;

        this.validationNodeTypes = Arrays.stream(validationNodes.split(",")).collect(Collectors.toSet());

        if (exclusionRegexes == null || exclusionRegexes.isEmpty()) {
            this.exclusionList = new ArrayList<>();
        } else {
            this.exclusionList =
                    Arrays.stream(exclusionRegexes.split(",")).map(Pattern::compile).collect(Collectors.toList());
        }
        this.mapper = mapper;
        LOGGER.info("Successfully initialized the pre validation service");
    }

    @PostConstruct
    public void initialize() throws AAIException {
        doHealthCheckRequest();
    }

    private void doHealthCheckRequest() throws AAIException {
        Map<String, String> httpHeaders = new HashMap<>();
        httpHeaders.put("X-FromAppId", appName);
        httpHeaders.put("X-TransactionID", UUID.randomUUID().toString());
        httpHeaders.put("Content-Type", "application/json");

        ResponseEntity<String> healthCheckResponse = null;
        try {
            healthCheckResponse =
                    validationRestClient.execute(VALIDATION_HEALTH_ENDPOINT, HttpMethod.GET, httpHeaders);
        } catch (Exception ex) {
            AAIException validationException = new AAIException("AAI_4021", ex);
            throw validationException;
        }

        if (!isSuccess(healthCheckResponse)) {
            throw new AAIException("AAI_4021");
        }

        LOGGER.info("Successfully connected to the validation service endpoint");
    }

    public boolean shouldValidate(String nodeType) {
        return this.validationNodeTypes.contains(nodeType);
    }

    public void validate(List<NotificationEvent> notificationEvents) throws AAIException {
        if (notificationEvents == null || notificationEvents.isEmpty() || isSourceExcluded(notificationEvents)) {
            return;
        }

        for (NotificationEvent event : notificationEvents) {
            EventHeader eventHeader = event.getEventHeader();
            if (eventHeader == null) {
                // Should I skip processing the request and let it continue
                // or fail the request and cause client impact
                continue;
            }

            /*
             * Skipping the delete events for now
             * Note: Might revisit this later when validation supports DELETE events
             */
            if (isDelete(eventHeader)) {
                continue;
            }
            String entityType = eventHeader.getEntityType();

            if (this.shouldValidate(entityType)) {
                List<String> violations = preValidate(event);
                if (!violations.isEmpty()) {
                    AAIException aaiException = new AAIException("AAI_4019");
                    aaiException.getTemplateVars().addAll(violations);
                    throw aaiException;
                }
            }
        }
    }

    /**
     * Determine if event is of type delete
     */
    private boolean isDelete(EventHeader eventHeader) {
        String action = eventHeader.getAction();
        return DELETE.equalsIgnoreCase(action);
    }

    /**
     * Checks the `source` attribute of the first event to determine if validation should be skipped
     * @param notificationEvents
     * @return
     */
    private boolean isSourceExcluded(List<NotificationEvent> notificationEvents) {
        // Get the first notification and if the source of that notification
        // is in one of the regexes then we skip sending it to validation
        NotificationEvent notification = notificationEvents.get(0);
        EventHeader eventHeader = notification.getEventHeader();
        if (eventHeader != null) {
            String source = eventHeader.getSourceName();
            return exclusionList.stream().anyMatch(pattern -> pattern.matcher(source).matches());
        }
        return false;
    }

    public List<String> preValidate(NotificationEvent notificationEvent) throws AAIException {
        Map<String, String> httpHeaders = new HashMap<>();
        httpHeaders.put("X-FromAppId", appName);
        httpHeaders.put("X-TransactionID", UUID.randomUUID().toString());
        httpHeaders.put("Content-Type", "application/json");

        List<String> violations = new ArrayList<>();
        ResponseEntity<String> responseEntity;
        try {
            String requestBody = mapper.writeValueAsString(notificationEvent);
            responseEntity = validationRestClient.execute(VALIDATION_ENDPOINT, HttpMethod.POST, httpHeaders, requestBody);
            Object responseBody = responseEntity.getBody();
            if (isSuccess(responseEntity)) {
                LOGGER.debug("Validation Service returned following response status code {} and body {}",
                        responseEntity.getStatusCodeValue(), responseEntity.getBody());
            } else if (responseBody != null) {
                Validation validation = getValidation(responseBody);

                if (validation == null) {
                    LOGGER.debug("Validation Service following status code {} with body {}",
                            responseEntity.getStatusCodeValue(), responseEntity.getBody());
                } else {
                    violations = extractViolations(validation);
                }
            } else {
                LOGGER.warn("Unable to convert the response body null");
            }
        } catch (Exception e) {
            // If the exception cause is client side timeout
            // then proceed as if it passed validation
            // resources microservice shouldn't be blocked because of validation service
            // is taking too long or if the validation service is down
            // Any other exception it should block the request from passing?
            if (e.getCause() instanceof ConnectTimeoutException) {
                LOGGER.error(CONNECTION_TIMEOUT_STRING, e.getCause());
            } else if (e.getCause() instanceof SocketTimeoutException) {
                LOGGER.error(REQUEST_TIMEOUT_STRING, e.getCause());
            } else if (e.getCause() instanceof ConnectException) {
                LOGGER.error(CONNECTION_REFUSED_STRING, e.getCause());
            } else {
                LOGGER.error("Unknown exception thrown please investigate", e.getCause());
            }
        }
        return violations;
    }

    private Validation getValidation(Object responseBody) {
        Validation validation = null;
        try {
            validation = mapper.readValue(responseBody.toString(), Validation.class);
        } catch (JsonProcessingException jsonException) {
            LOGGER.warn("Unable to convert the response body {}", jsonException.getMessage());
        }
        return validation;
    }

    boolean isSuccess(ResponseEntity<String> responseEntity) {
        return responseEntity != null && responseEntity.getStatusCode().is2xxSuccessful();
    }

    public List<String> extractViolations(Validation validation) {
        if (validation == null || validation.getViolations() == null) {
            return Collections.emptyList();
        }
        return validation.getViolations().stream()
            .map(Violation::getErrorMessage)
            .peek(LOGGER::info)
            .collect(Collectors.toList());
    }
}
