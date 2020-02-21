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

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import org.apache.http.conn.ConnectTimeoutException;
import org.onap.aai.exceptions.AAIException;
import org.onap.aai.introspection.Introspector;
import org.onap.aai.rest.ueb.NotificationEvent;
import org.onap.aai.restclient.RestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * <b>ValidationService</b> routes all the writes to the database
 * excluding deletes for now to the validation service to verify
 * that the request is an valid one before committing to the database
 */
@Service
@Profile("pre-validation")
public class ValidationService {

    /**
     * Error indicating that the service trying to connect is down
     */
    static final String CONNECTION_REFUSED_STRING =
        "Connection refused to the validation microservice due to service unreachable";

    /**
     * Error indicating that the server is unable to reach the port
     * Could be server related connectivity issue
     */
    static final String CONNECTION_TIMEOUT_STRING =
        "Connection timeout to the validation microservice as this could " +
        "indicate the server is unable to reach port, " +
        "please check on server by running: nc -w10 -z -v ${VALIDATION_HOST} ${VALIDATION_PORT}";

    /**
     * Error indicating that the request exceeded the allowed time
     *
     * Note: This means that the service could be active its
     *       just taking some time to process our request
     */
    static final String REQUEST_TIMEOUT_STRING =
        "Request to validation service took longer than the currently set timeout";

    static final String VALIDATION_ENDPOINT = "/v1/app/validate";
    static final String VALIDATION_HEALTH_ENDPOINT = "/v1/core/core-service/info";

    private static final String ENTITY_TYPE = "entity-type";
    private static final String ACTION = "action";
    private static final String SOURCE_NAME = "source-name";

    private static final String DELETE = "DELETE";

    private static final Logger LOGGER = LoggerFactory.getLogger(ValidationService.class);

    private final RestClient validationRestClient;

    private final String appName;

    private final Set<String> validationNodeTypes;

    private List<Pattern> exclusionList;

    private final Gson gson;

    @Autowired
    public ValidationService(
        @Qualifier("validationRestClient") RestClient validationRestClient,
        @Value("${spring.application.name}") String appName,
        @Value("${validation.service.node-types}") String validationNodes,
        @Value("${validation.service.exclusion-regexes}") String exclusionRegexes
    ){
        this.validationRestClient = validationRestClient;
        this.appName = appName;

        this.validationNodeTypes = Arrays
            .stream(validationNodes.split(","))
            .collect(Collectors.toSet());

        if(exclusionRegexes == null || exclusionRegexes.isEmpty()){
            this.exclusionList = new ArrayList<>();
        } else {
            this.exclusionList = Arrays
                .stream(exclusionRegexes.split(","))
                .map(Pattern::compile)
                .collect(Collectors.toList());
        }
        this.gson = new Gson();
        LOGGER.info("Successfully initialized the pre validation service");
    }

    @PostConstruct
    public void initialize() throws AAIException {

        Map<String, String> httpHeaders = new HashMap<>();

        httpHeaders.put("X-FromAppId", appName);
        httpHeaders.put("X-TransactionID", UUID.randomUUID().toString());
        httpHeaders.put("Content-Type", "application/json");

        ResponseEntity<String> healthCheckResponse = null;

        try {

            healthCheckResponse = validationRestClient.execute(
                VALIDATION_HEALTH_ENDPOINT,
                HttpMethod.GET,
                httpHeaders,
                null
            );

        } catch(Exception ex){
            AAIException validationException = new AAIException("AAI_4021", ex);
            throw validationException;
        }

        if(!isSuccess(healthCheckResponse)){
            throw new AAIException("AAI_4021");
        }

        LOGGER.info("Successfully connected to the validation service endpoint");
    }

    public boolean shouldValidate(String nodeType){
        return this.validationNodeTypes.contains(nodeType);
    }

    public void validate(List<NotificationEvent> notificationEvents) throws AAIException {

        if(notificationEvents == null || notificationEvents.isEmpty()){
            return;
        }

        {
            // Get the first notification and if the source of that notification
            // is in one of the regexes then we skip sending it to validation
            NotificationEvent notification = notificationEvents.get(0);
            Introspector eventHeader = notification.getEventHeader();
            if(eventHeader != null){
                String source = eventHeader.getValue(SOURCE_NAME);
                for(Pattern pattern: exclusionList){
                    if(pattern.matcher(source).matches()){
                        return;
                    }
                }
            }

        }

        for (NotificationEvent event : notificationEvents) {

            Introspector eventHeader = event.getEventHeader();

            if(eventHeader == null){
                // Should I skip processing the request and let it continue
                // or fail the request and cause client impact
                continue;
            }

            String entityType = eventHeader.getValue(ENTITY_TYPE);
            String action = eventHeader.getValue(ACTION);

            /**
             * Skipping the delete events for now
             * Note: Might revisit this later when validation supports DELETE events
             */
            if(DELETE.equalsIgnoreCase(action)){
                continue;
            }

            if (this.shouldValidate(entityType)) {
                List<String> violations = this.preValidate(event.getNotificationEvent());
                if(!violations.isEmpty()){
                    AAIException aaiException = new AAIException("AAI_4019");
                    aaiException.getTemplateVars().addAll(violations);
                    throw aaiException;
                }
            }
        }
    }

    List<String> preValidate(String body) throws AAIException {

        Map<String, String> httpHeaders = new HashMap<>();

        httpHeaders.put("X-FromAppId", appName);
        httpHeaders.put("X-TransactionID", UUID.randomUUID().toString());
        httpHeaders.put("Content-Type", "application/json");

        List<String> violations = new ArrayList<>();
        ResponseEntity responseEntity;
        try {

            responseEntity = validationRestClient.execute(
                VALIDATION_ENDPOINT,
                HttpMethod.POST,
                httpHeaders,
                body
            );

            if(isSuccess(responseEntity)){
                LOGGER.debug("Validation Service returned following response status code {} and body {}", responseEntity.getStatusCodeValue(), responseEntity.getBody());
            } else {
                Validation validation = null;
                try {
                    validation = gson.fromJson(responseEntity.getBody().toString(), Validation.class);
                } catch(JsonSyntaxException jsonException){
                    LOGGER.warn("Unable to convert the response body {}", jsonException.getMessage());
                }

                if(validation == null){
                    LOGGER.debug(
                        "Validation Service following status code {} with body {}",
                        responseEntity.getStatusCodeValue(),
                        responseEntity.getBody()
                    );
                } else {
                    violations.addAll(extractViolations(validation));
                }
            }
        } catch(Exception e){
            // If the exception cause is client side timeout
            // then proceed as if it passed validation
            // resources microservice shouldn't be blocked because of validation service
            // is taking too long or if the validation service is down
            // Any other exception it should block the request from passing?
            if(e.getCause() instanceof SocketTimeoutException){
                LOGGER.error(REQUEST_TIMEOUT_STRING, e.getCause());
            } else if(e.getCause() instanceof ConnectException){
                LOGGER.error(CONNECTION_REFUSED_STRING, e.getCause());
            } else if(e.getCause() instanceof ConnectTimeoutException){
                LOGGER.error(CONNECTION_TIMEOUT_STRING, e.getCause());
            } else {
                LOGGER.error("Unknown exception thrown please investigate", e.getCause());
            }
        }
        return violations;
    }

    boolean isSuccess(ResponseEntity responseEntity){
        return responseEntity != null && responseEntity.getStatusCode().is2xxSuccessful();
    }

    List<String> extractViolations(Validation validation) {

        List<String> errorMessages = new ArrayList<>();

        if(validation == null){
            return errorMessages;
        }

        List<Violation> violations = validation.getViolations();

        if (violations != null && !violations.isEmpty()) {
            for (Violation violation : validation.getViolations()) {
                LOGGER.info(violation.getErrorMessage());
                errorMessages.add(violation.getErrorMessage());
            }
        }

        return errorMessages;
    }
}
