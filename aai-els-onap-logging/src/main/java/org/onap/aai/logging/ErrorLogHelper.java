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

package org.onap.aai.logging;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.Properties;
import java.util.Map.Entry;

import javax.ws.rs.core.MediaType;

import org.apache.commons.lang3.StringUtils;
import org.onap.aai.domain.errorResponse.ErrorMessage;
import org.onap.aai.domain.errorResponse.ExceptionType;
import org.onap.aai.domain.errorResponse.Fault;
import org.onap.aai.domain.restResponseInfo.Info;
import org.onap.aai.domain.restResponseInfo.Info.ResponseMessages;
import org.onap.aai.domain.restResponseInfo.Info.ResponseMessages.ResponseMessage;
import org.onap.aai.domain.restResponseInfo.Info.ResponseMessages.ResponseMessage.Variables;
import org.onap.aai.exceptions.AAIException;
import org.onap.aai.util.AAIConstants;
import org.onap.logging.filter.base.Constants;
import org.onap.logging.filter.base.MDCSetup;
import org.onap.logging.ref.slf4j.ONAPLogConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

/**
 *
 * This classes loads the application error properties file
 * and provides a method that returns an ErrorObject
 *
 */

public class ErrorLogHelper {
    private static final Logger LOGGER = LoggerFactory.getLogger(ErrorLogHelper.class);
    private static final HashMap<String, ErrorObject> ERROR_OBJECTS = new HashMap<String, ErrorObject>();
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final XmlMapper xmlMapper = new XmlMapper();

    static {
        try {
            loadProperties();
        } catch (IOException e) {
            throw new RuntimeException("Failed to load error.properties file", e);
        } catch (ErrorObjectFormatException e) {
            throw new RuntimeException("Failed to parse error.properties file", e);
        }
    }

    /**
     * Load properties.
     * 
     * @throws IOException the exception
     * @throws ErrorObjectFormatException
     */
    public static void loadProperties() throws IOException, ErrorObjectFormatException {
        final String filePath = AAIConstants.AAI_HOME_ETC_APP_PROPERTIES + "error.properties";
        final InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("error.properties");
        final Properties properties = new Properties();

        try (final FileInputStream fileInputStream = new FileInputStream(filePath)) {
            LOGGER.info("Found the error.properties in the following location: {}",
                    AAIConstants.AAI_HOME_ETC_APP_PROPERTIES);
            properties.load(fileInputStream);
        } catch (Exception ex) {
            LOGGER.info("Unable to find the error.properties from filesystem so using file in jar");
            if (inputStream != null) {
                properties.load(inputStream);
            } else {
                LOGGER.error("Expected to find the error.properties in the jar but unable to find it");
            }
        }

        for (Entry<Object, Object> entry : properties.entrySet()) {
            final String key = (String) entry.getKey();
            final String value = (String) entry.getValue();
            final String[] errorProperties = value.split(":");

            if (errorProperties.length < 7)
                throw new ErrorObjectFormatException();

            final ErrorObject errorObject = new ErrorObject();

            errorObject.setDisposition(errorProperties[0].trim());
            errorObject.setCategory(errorProperties[1].trim());
            errorObject.setSeverity(errorProperties[2].trim());
            errorObject.setErrorCode(errorProperties[3].trim());
            errorObject.setHTTPResponseCode(errorProperties[4].trim());
            errorObject.setRESTErrorCode(errorProperties[5].trim());
            errorObject.setErrorText(errorProperties[6].trim());
            if (errorProperties.length > 7) {
                errorObject.setAaiElsErrorCode(errorProperties[7].trim());
            }

            ERROR_OBJECTS.put(key, errorObject);
        }
    }

    /**
     * Logs a known A&AI exception (i.e. one that can be found in error.properties)
     *
     * @param code for the error in the error.properties file
     * @throws IOException
     * @throws ErrorObjectNotFoundException
     */
    public static ErrorObject getErrorObject(String code) {

        if (code == null)
            throw new IllegalArgumentException("Key cannot be null");

        final ErrorObject errorObject = ERROR_OBJECTS.get(code);

        if (errorObject == null) {
            LOGGER.warn("Unknown AAIException with code=" + code + ".  Using default AAIException");
            return ERROR_OBJECTS.get(AAIException.DEFAULT_EXCEPTION_CODE);
        }

        return errorObject;
    }

    /**
     * Determines whether category is policy or not. If policy (1), this is a POL error, else it's a SVC error.
     * The AAIRESTException may contain a different ErrorObject than that created with the REST error key.
     * This allows lower level exception detail to be returned to the client to help troubleshoot the problem.
     * If no error object is embedded in the AAIException, one will be created using the error object from the
     * AAIException.
     *
     * @param aaiException must have a restError value whose numeric value must match what should be returned in the REST API
     * @param variables optional list of variables to flesh out text in error string
     * @return appropriately formatted JSON response per the REST API spec.
     * @throws IOException
     * @deprecated
     */
    public static String getRESTAPIErrorResponse(AAIException aaiException, ArrayList<String> variables) {
        List<MediaType> acceptHeaders = Collections.singletonList(MediaType.APPLICATION_JSON_TYPE);

        return getRESTAPIErrorResponse(acceptHeaders, aaiException, variables);
    }

    /**
     * Determines whether category is policy or not. If policy (1), this is a POL error, else it's a SVC error.
     * The AAIRESTException may contain a different ErrorObject than that created with the REST error key.
     * This allows lower level exception detail to be returned to the client to help troubleshoot the problem.
     * If no error object is embedded in the AAIException, one will be created using the error object from the
     * AAIException.
     * 
     * @param aaiException
     * @param variables
     * @return
     */
    public static Fault getErrorResponse(AAIException aaiException,
            ArrayList<String> variables) {
        final ErrorObject restErrorObject = getRestErrorObject(aaiException);
        final String text = createText(restErrorObject);
        final int placeholderCount = StringUtils.countMatches(restErrorObject.getErrorText(), "%");
        variables = checkAndEnrichVariables(aaiException, variables, placeholderCount);
        
        if (aaiException.getErrorObject().getCategory().equals("1")) {
            return createPolicyFault(aaiException, text, variables);
        } else {
            return createServiceFault(aaiException, text, variables);
        }
    }

    /**
     *
     * @param acceptHeaders the accept headers orig
     * @param aaiException must have a restError value whose numeric value must match what should be returned in the REST API
     * @param variables optional list of variables to flesh out text in error string
     * @return appropriately formatted JSON response per the REST API spec.
     * @deprecated in favor of {@link #getErrorResponse(AAIException, ArrayList)}
     */
    @Deprecated
    public static String getRESTAPIErrorResponse(List<MediaType> acceptHeaders, AAIException aaiException,
            ArrayList<String> variables) {

        List<MediaType> validAcceptHeaders = new ArrayList<MediaType>();
        // we might have an exception but no accept header, so we'll set default to JSON
        boolean foundValidAcceptHeader = false;
        for (MediaType mediaType : acceptHeaders) {
            if (MediaType.APPLICATION_XML_TYPE.isCompatible(mediaType) || MediaType.APPLICATION_JSON_TYPE.isCompatible(mediaType)) {
                validAcceptHeaders.add(mediaType);
                foundValidAcceptHeader = true;
            }
        }
        if (foundValidAcceptHeader == false) {
            // override the exception, client needs to set an appropriate Accept header
            aaiException = new AAIException("AAI_4014");
            validAcceptHeaders.add(MediaType.APPLICATION_JSON_TYPE);
        }

        MediaType mediaType = validAcceptHeaders.stream()
            .filter(MediaType.APPLICATION_JSON_TYPE::isCompatible)
            .findAny()
            .orElse(MediaType.APPLICATION_XML_TYPE);

        Fault fault = getErrorResponse(aaiException, variables);
        try {
            return MediaType.APPLICATION_JSON_TYPE.isCompatible(mediaType)
                ? objectMapper.writeValueAsString(fault)
                : xmlMapper.writeValueAsString(fault);
        } catch (JsonProcessingException ex) {
            LOGGER.error(
                "We were unable to create a rest exception to return on an API because of a parsing error "
                                    + ex.getMessage());
        }
        return null;
    }

    private static String createText(ErrorObject restErrorObject) {
        final StringBuilder text = new StringBuilder();
        text.append(restErrorObject.getErrorText());

        // We want to always append the (msg=%n) (ec=%n+1) to the text, but have to find value of n
        // This assumes that the variables in the ArrayList, which might be more than are needed to flesh out the
        // error, are ordered based on the error string.
        int placeholderCount = StringUtils.countMatches(restErrorObject.getErrorText(), "%");
        text.append(" (msg=%").append(placeholderCount + 1).append(") (ec=%").append(placeholderCount + 2).append(")");
        return text.toString();
    }

    /**
     * Gets the RESTAPI error response with logging.
     *
     * @param acceptHeaders the accept headers orig
     * @param aaiException the are
     * @param variables the variables
     */
    public static String getRESTAPIErrorResponseWithLogging(List<MediaType> acceptHeaders, AAIException aaiException,
            ArrayList<String> variables) {
        logException(aaiException);
        return ErrorLogHelper.getRESTAPIErrorResponse(acceptHeaders, aaiException, variables);
    }

    /**
     * Gets the RESTAPI info response.
     *
     * @param acceptHeaders the accept headers
     * @param aaiExceptionsMap the are list
     * @return the RESTAPI info response
     */
    @Deprecated
    public static Object getRESTAPIInfoResponse(ArrayList<MediaType> acceptHeaders,
            HashMap<AAIException, ArrayList<String>> aaiExceptionsMap) {
        return (Object) getRestApiInfoResponse(aaiExceptionsMap);
    }

    /**
     * Gets the RESTAPI info response.
     *
     * @param acceptHeaders the accept headers
     * @param aaiExceptionsMap the are list
     * @return the RESTAPI info response
     */
    private static Info getRestApiInfoResponse(
            HashMap<AAIException, ArrayList<String>> aaiExceptionsMap) {
        List<ResponseMessage> responseMessageList = aaiExceptionsMap.entrySet().stream()
                .map(entry -> createResponseMessage(entry))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        ResponseMessages responseMessages = new ResponseMessages(responseMessageList);
        return new Info(responseMessages);
    }

    private static ResponseMessage createResponseMessage(Entry<AAIException, ArrayList<String>> entry) {
        AAIException aaiException = entry.getKey();
        ArrayList<String> variables = entry.getValue();

        ErrorObject restErrorObject = getRestErrorObject(aaiException);
        final String text = createText(restErrorObject);
        final int placeholderCount = StringUtils.countMatches(aaiException.getErrorObject().getErrorText(), "%");
        variables = checkAndEnrichVariables(aaiException, variables, placeholderCount);

        try {
            ResponseMessage responseMessage = new ResponseMessage();
            Variables infoVariables = new Variables();

            responseMessage.setMessageId("INF" + aaiException.getErrorObject().getRESTErrorCode());
            responseMessage.setText(text.toString());
            for (int i = 0; i < variables.size(); i++) {
                infoVariables.getVariable().add(variables.get(i));
            }

            responseMessage.setVariables(infoVariables);
            return responseMessage;
        } catch (Exception ex) {
            LOGGER.error("We were unable to create a rest exception to return on an API because of a parsing error "
                    + ex.getMessage());
            return null;
        }
    }

    /**
     * Determines whether category is policy or not. If policy (1), this is a POL error, else it's a SVC error.
     * The AAIRESTException may contain a different ErrorObject than that created with the REST error key.
     * This allows lower level exception detail to be returned to the client to help troubleshoot the problem.
     * If no error object is embedded in the AAIException, one will be created using the error object from the
     * AAIException.
     *
     * @param aaiException must have a restError value whose numeric value must match what should be returned in the REST API
     * @param variables optional list of variables to flesh out text in error string
     * @return appropriately formatted JSON response per the REST API spec.
     */
    public static String getRESTAPIPolicyErrorResponseXML(AAIException aaiException, ArrayList<String> variables) {
        return getRESTAPIErrorResponse(Collections.singletonList(MediaType.APPLICATION_XML_TYPE), aaiException, variables);
    }

    public static void logException(AAIException aaiException) {
        final ErrorObject errorObject = aaiException.getErrorObject();
        /*
         * String severityCode = errorObject.getSeverityCode(errorObject.getSeverity());
         * 
         * Severify should be left empty per Logging Specification 2019.11
         * if (!StringUtils.isEmpty(severityCode)) {
         * int sevCode = Integer.parseInt(severityCode);
         * if (sevCode > 0 && sevCode <= 3) {
         * LoggingContext.severity(sevCode);
         * }
         * }
         */
        String stackTrace = "";
        try {
            stackTrace = LogFormatTools.getStackTop(aaiException);
        } catch (Exception a) {
            // ignore
        }
        final String errorMessage = new StringBuilder().append(errorObject.getErrorText()).append(":")
                .append(errorObject.getRESTErrorCode()).append(":").append(errorObject.getHTTPResponseCode())
                .append(":").append(aaiException.getMessage()).toString().replaceAll("\\n", "^");

        MDCSetup mdcSetup = new MDCSetup();
        mdcSetup.setResponseStatusCode(errorObject.getHTTPResponseCode().getStatusCode());
        mdcSetup.setErrorCode(Integer.parseInt(errorObject.getAaiElsErrorCode()));
        String serviceName = MDC.get(ONAPLogConstants.MDCs.SERVICE_NAME);
        if (serviceName == null || serviceName.isEmpty()) {
            MDC.put(ONAPLogConstants.MDCs.SERVICE_NAME, Constants.DefaultValues.UNKNOWN);
        }
        MDC.put(ONAPLogConstants.MDCs.ERROR_DESC, errorMessage);
        final String details =
                new StringBuilder().append(errorObject.getErrorCodeString()).append(" ").append(stackTrace).toString();

        if (errorObject.getSeverity().equalsIgnoreCase("WARN"))
            LOGGER.warn(details);
        else if (errorObject.getSeverity().equalsIgnoreCase("ERROR"))
            LOGGER.error(details);
        else if (errorObject.getSeverity().equalsIgnoreCase("FATAL"))
            LOGGER.error(details);
        else if (errorObject.getSeverity().equals("INFO"))
            LOGGER.info(details);
    }

    public static void logError(String code) {
        logError(code, "");
    }

    public static void logError(String code, String message) {
        logException(new AAIException(code, message));
    }

    private static ErrorObject getRestErrorObject(AAIException aaiException) {
        final int restErrorCode = Integer.parseInt(aaiException.getErrorObject().getRESTErrorCode());
        return ErrorLogHelper.getErrorObject("AAI_" + restErrorCode);
    }

    public static Fault createPolicyFault(AAIException aaiException, String text, List<String> variables) {
        return createFault(aaiException, text, variables, ExceptionType.POLICY);
    }
    public static Fault createServiceFault(AAIException aaiException, String text, List<String> variables) {
        return createFault(aaiException, text, variables, ExceptionType.SERVICE);
    }
    private static Fault createFault(AAIException aaiException, String text, List<String> variables, ExceptionType exceptionType) {
        String typePrefix = ExceptionType.POLICY.equals(exceptionType) ? "POL" : "SVC";
        Map<ExceptionType, ErrorMessage> requestError = Collections.singletonMap(exceptionType, 
            ErrorMessage.builder()
                .messageId(typePrefix+ aaiException.getErrorObject().getRESTErrorCode())
                .text(text)
                .variables(variables)
                .build());
        return new Fault(requestError);
    }

    private static ArrayList<String> checkAndEnrichVariables(AAIException aaiException, ArrayList<String> variables, int placeholderCount) {
        final ErrorObject errorObject = aaiException.getErrorObject();
        if (variables == null) {
            variables = new ArrayList<String>();
        }

        // int placeholderCount = StringUtils.countMatches(errorObject.getErrorText(), "%");
        if (variables.size() < placeholderCount) {
            ErrorLogHelper.logError("AAI_4011", "data missing for rest error");
            while (variables.size() < placeholderCount) {
                variables.add("null");
            }
        }

        // This will put the error code and error text into the right positions
        if (aaiException.getMessage() == null || aaiException.getMessage().length() == 0) {
            variables.add(placeholderCount++, errorObject.getErrorText());
        } else {
            variables.add(placeholderCount++, errorObject.getErrorText() + ":" + aaiException.getMessage());
        }
        variables.add(placeholderCount, errorObject.getErrorCodeString());
        return variables;
    }
}
