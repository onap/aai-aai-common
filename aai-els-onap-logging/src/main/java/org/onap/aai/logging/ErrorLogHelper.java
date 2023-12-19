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

package org.onap.aai.logging;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;

import javax.ws.rs.core.MediaType;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.apache.commons.lang3.StringUtils;
import org.onap.aai.domain.errorResponse.AAIErrorResponse;
import org.onap.aai.domain.errorResponse.RequestError;
import org.onap.aai.domain.errorResponse.ServiceException;
import org.onap.aai.domain.restPolicyException.RESTResponse;
import org.onap.aai.exceptions.AAIException;
import org.onap.aai.util.AAIConstants;
import org.onap.logging.filter.base.Constants;
import org.onap.logging.filter.base.MDCSetup;
import org.onap.logging.ref.slf4j.ONAPLogConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import com.fasterxml.jackson.databind.ObjectMapper;

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

    public static AAIErrorResponse getAaiErrorResponse(AAIException aaiException) {
        ServiceException serviceException = ServiceException.builder()
            .messageId(null)
            .text(null)
            .variables(null)
            .build();
        return new AAIErrorResponse(new RequestError(serviceException));
    }

    /**
     * Determines whether category is policy or not. If policy (1), this is a POL error, else it's a SVC error.
     * The AAIRESTException may contain a different ErrorObject than that created with the REST error key.
     * This allows lower level exception detail to be returned to the client to help troubleshoot the problem.
     * If no error object is embedded in the AAIException, one will be created using the error object from the
     * AAIException.
     *
     * @param acceptHeaders the accept headers orig
     * @param aaiException must have a restError value whose numeric value must match what should be returned in the REST API
     * @param variables optional list of variables to flesh out text in error string
     * @return appropriately formatted JSON response per the REST API spec.
     */
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

        final ErrorObject errorObject = aaiException.getErrorObject();
        ErrorObject restErrorObject = parseErrorObject(errorObject);

        final StringBuilder text = new StringBuilder();
        text.append(restErrorObject.getErrorText());

        // We want to always append the (msg=%n) (ec=%n+1) to the text, but have to find value of n
        // This assumes that the variables in the ArrayList, which might be more than are needed to flesh out the
        // error, are ordered based on the error string.
        int localDataIndex = StringUtils.countMatches(restErrorObject.getErrorText(), "%");
        text.append(" (msg=%").append(localDataIndex + 1).append(") (ec=%").append(localDataIndex + 2).append(")");

        variables = checkAndEnrichVariables(aaiException, variables, errorObject, localDataIndex);

        String response = null;
        for (MediaType mediaType : validAcceptHeaders) {
            if (MediaType.APPLICATION_XML_TYPE.isCompatible(mediaType)) {
                try {
                    if (errorObject.getCategory().equals("1")) {
                        org.onap.aai.domain.restPolicyException.Fault fault = createPolicyExceptionFault(variables, errorObject,
                                text);
                        response = marshallXml(fault);
                    } else {
                        org.onap.aai.domain.restServiceException.Fault fault = createServiceExceptionFault(variables,
                                errorObject, text);
                        response = marshallXml(fault);
                    }
                } catch (Exception ex) {
                    LOGGER.error(
                            "We were unable to create a rest exception to return on an API because of a parsing error "
                                    + ex.getMessage());
                }
            } else {
                try {
                    if (errorObject.getCategory().equals("1")) {
                        RESTResponse policyRESTResponse = createPolicyRESTResponse(variables, text, errorObject);
                        response = objectMapper.writeValueAsString(policyRESTResponse);
                    } else {
                        org.onap.aai.domain.restServiceException.RESTResponse serviceRESTResponse = createServiceErrorResponse(variables, text, errorObject);
                        response = objectMapper.writeValueAsString(serviceRESTResponse);
                    }
                } catch (Exception ex) {
                    LOGGER.error(
                            "We were unable to create a rest exception to return on an API because of a parsing error "
                                    + ex.getMessage());
                }
            }
        }

        return response;
    }

    private static org.onap.aai.domain.restPolicyException.Fault createPolicyExceptionFault(ArrayList<String> variables,
            final ErrorObject errorObject, final StringBuilder text) {
        org.onap.aai.domain.restPolicyException.ObjectFactory factory =
                new org.onap.aai.domain.restPolicyException.ObjectFactory();
        org.onap.aai.domain.restPolicyException.Fault fault = factory.createFault();
        org.onap.aai.domain.restPolicyException.Fault.RequestError requestError =
                factory.createFaultRequestError();
        org.onap.aai.domain.restPolicyException.Fault.RequestError.PolicyException policyException =
                factory.createFaultRequestErrorPolicyException();
        org.onap.aai.domain.restPolicyException.Fault.RequestError.PolicyException.Variables policyVariables =
                factory.createFaultRequestErrorPolicyExceptionVariables();

        policyException.setMessageId("POL" + errorObject.getRESTErrorCode());
        policyException.setText(text.toString());
        for (int i = 0; i < variables.size(); i++) {
            policyVariables.getVariable().add(variables.get(i));
        }
        policyException.setVariables(policyVariables);
        requestError.setPolicyException(policyException);
        fault.setRequestError(requestError);
        return fault;
    }

    private static <T> String marshallXml(T toMarshal) throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(toMarshal.getClass());
        Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");

        StringWriter stringWriter = new StringWriter();
        marshaller.marshal(toMarshal, stringWriter);

        return stringWriter.toString();
    }

    private static org.onap.aai.domain.restServiceException.Fault createServiceExceptionFault(ArrayList<String> variables,
            final ErrorObject errorObject, final StringBuilder text) {
        org.onap.aai.domain.restServiceException.ObjectFactory objectFactory =
                new org.onap.aai.domain.restServiceException.ObjectFactory();
        org.onap.aai.domain.restServiceException.Fault fault = objectFactory.createFault();
        org.onap.aai.domain.restServiceException.Fault.RequestError requestError =
                objectFactory.createFaultRequestError();
        org.onap.aai.domain.restServiceException.Fault.RequestError.ServiceException serviceException =
                objectFactory.createFaultRequestErrorServiceException();
        org.onap.aai.domain.restServiceException.Fault.RequestError.ServiceException.Variables serviceVariables =
                objectFactory.createFaultRequestErrorServiceExceptionVariables();
        serviceException.setMessageId("SVC" + errorObject.getRESTErrorCode());
        serviceException.setText(text.toString());
        for (int i = 0; i < variables.size(); i++) {
            serviceVariables.getVariable().add(variables.get(i));
        }
        serviceException.setVariables(serviceVariables);
        requestError.setServiceException(serviceException);
        fault.setRequestError(requestError);
        return fault;
    }

    private static ErrorObject parseErrorObject(ErrorObject errorObject) {
        final int restErrorCode = Integer.parseInt(errorObject.getRESTErrorCode());
        return ErrorLogHelper.getErrorObject("AAI_" + restErrorCode);
    }

    private static org.onap.aai.domain.restServiceException.RESTResponse createServiceErrorResponse(ArrayList<String> variables, StringBuilder text, ErrorObject errorObject)
            throws AAIException {
        org.onap.aai.domain.restServiceException.RequestError serviceRequestError =
                new org.onap.aai.domain.restServiceException.RequestError();
        org.onap.aai.domain.restServiceException.ServiceException serviceException =
                new org.onap.aai.domain.restServiceException.ServiceException();
        serviceException.setMessageId("SVC" + errorObject.getRESTErrorCode());
        serviceException.setText(text.toString());
        serviceException.setVariables(variables);
        serviceRequestError.setServiceException(serviceException);
        return new org.onap.aai.domain.restServiceException.RESTResponse(serviceRequestError);
    }

    private static RESTResponse createPolicyRESTResponse(List<String> variables, StringBuilder text, ErrorObject errorObject)
            throws AAIException {
        org.onap.aai.domain.restPolicyException.RequestError policyRequestError = createPolicyRequestError(variables, text,
                errorObject);
        return new RESTResponse(policyRequestError);
    }

    private static org.onap.aai.domain.restPolicyException.RequestError createPolicyRequestError(List<String> variables,
            StringBuilder text, ErrorObject errorObject) {
        org.onap.aai.domain.restPolicyException.RequestError policyRequestError =
        new org.onap.aai.domain.restPolicyException.RequestError();
        org.onap.aai.domain.restPolicyException.PolicyException policyException =
        new org.onap.aai.domain.restPolicyException.PolicyException();
        policyException.setMessageId("POL" + errorObject.getRESTErrorCode());
        policyException.setText(text.toString());
        policyException.setVariables(variables);
        policyRequestError.setPolicyException(policyException);
        return policyRequestError;
    }

    private static ArrayList<String> checkAndEnrichVariables(AAIException aaiException, ArrayList<String> variables, ErrorObject errorObject,
            int localDataIndex) {
        if (variables == null) {
            variables = new ArrayList<String>();
        }

        if (variables.size() < localDataIndex) {
            ErrorLogHelper.logError("AAI_4011", "data missing for rest error");
            while (variables.size() < localDataIndex) {
                variables.add("null");
            }
        }

        // This will put the error code and error text into the right positions
        if (aaiException.getMessage() == null || aaiException.getMessage().length() == 0) {
            variables.add(localDataIndex++, errorObject.getErrorText());
        } else {
            variables.add(localDataIndex++, errorObject.getErrorText() + ":" + aaiException.getMessage());
        }
        variables.add(localDataIndex, errorObject.getErrorCodeString());
        return variables;
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
    public static Object getRESTAPIInfoResponse(ArrayList<MediaType> acceptHeaders,
            HashMap<AAIException, ArrayList<String>> aaiExceptionsMap) {

        Object respObj = null;

        org.onap.aai.domain.restResponseInfo.ObjectFactory factory =
                new org.onap.aai.domain.restResponseInfo.ObjectFactory();
        org.onap.aai.domain.restResponseInfo.Info info = factory.createInfo();
        org.onap.aai.domain.restResponseInfo.Info.ResponseMessages responseMessages =
                factory.createInfoResponseMessages();
        Iterator<Entry<AAIException, ArrayList<String>>> it = aaiExceptionsMap.entrySet().iterator();

        while (it.hasNext()) {
            Entry<AAIException, ArrayList<String>> pair = (Entry<AAIException, ArrayList<String>>) it.next();
            AAIException aaiException = pair.getKey();
            ArrayList<String> variables = pair.getValue();

            StringBuilder text = new StringBuilder();

            ErrorObject errorObject = aaiException.getErrorObject();

            int restErrorCode = Integer.parseInt(errorObject.getRESTErrorCode());
            ErrorObject restErrorObject = ErrorLogHelper.getErrorObject("AAI_" + restErrorCode);
            text.append(restErrorObject.getErrorText());

            // We want to always append the (msg=%n) (ec=%n+1) to the text, but have to find value of n
            // This assumes that the variables in the ArrayList, which might be more than are needed to flesh out the
            // error, are ordered based on the error string.
            int localDataIndex = StringUtils.countMatches(restErrorObject.getErrorText(), "%");
            text.append(" (msg=%").append(localDataIndex + 1).append(") (rc=%").append(localDataIndex + 2).append(")");

            if (variables == null) {
                variables = new ArrayList<String>();
            }

            if (variables.size() < localDataIndex) {
                ErrorLogHelper.logError("AAI_4011", "data missing for rest error");
                while (variables.size() < localDataIndex) {
                    variables.add("null");
                }
            }

            // This will put the error code and error text into the right positions
            if (aaiException.getMessage() == null) {
                variables.add(localDataIndex++, errorObject.getErrorText());
            } else {
                variables.add(localDataIndex++, errorObject.getErrorText() + ":" + aaiException.getMessage());
            }
            variables.add(localDataIndex, errorObject.getErrorCodeString());

            try {
                org.onap.aai.domain.restResponseInfo.Info.ResponseMessages.ResponseMessage responseMessage =
                        factory.createInfoResponseMessagesResponseMessage();
                org.onap.aai.domain.restResponseInfo.Info.ResponseMessages.ResponseMessage.Variables infovariables =
                        factory.createInfoResponseMessagesResponseMessageVariables();

                responseMessage.setMessageId("INF" + errorObject.getRESTErrorCode());
                responseMessage.setText(text.toString());
                for (int i = 0; i < variables.size(); i++) {
                    infovariables.getVariable().add(variables.get(i));
                }

                responseMessage.setVariables(infovariables);
                responseMessages.getResponseMessage().add(responseMessage);

            } catch (Exception ex) {
                LOGGER.error("We were unable to create a rest exception to return on an API because of a parsing error "
                        + ex.getMessage());
            }
        }

        info.setResponseMessages(responseMessages);
        respObj = (Object) info;

        return respObj;
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

        String response = null;
        JAXBContext context = null;
        
        ErrorObject errorObject = aaiException.getErrorObject();
        
        int restErrorCode = Integer.parseInt(errorObject.getRESTErrorCode());
        ErrorObject restErrorObject;
        restErrorObject = ErrorLogHelper.getErrorObject("AAI_" + restErrorCode);
        
        StringBuilder text = new StringBuilder();
        text.append(restErrorObject.getErrorText());

        // We want to always append the (msg=%n) (ec=%n+1) to the text, but have to find value of n
        // This assumes that the variables in the ArrayList, which might be more than are needed to flesh out the
        // error, are ordered based on the error string.
        int localDataIndex = StringUtils.countMatches(restErrorObject.getErrorText(), "%");
        text.append(" (msg=%").append(localDataIndex + 1).append(") (ec=%").append(localDataIndex + 2).append(")");

        if (variables == null) {
            variables = new ArrayList<String>();
        }

        if (variables.size() < localDataIndex) {
            ErrorLogHelper.logError("AAI_4011", "data missing for rest error");
            while (variables.size() < localDataIndex) {
                variables.add("null");
            }
        }

        // This will put the error code and error text into the right positions
        if (aaiException.getMessage() == null) {
            variables.add(localDataIndex++, errorObject.getErrorText());
        } else {
            variables.add(localDataIndex++, errorObject.getErrorText() + ":" + aaiException.getMessage());
        }
        variables.add(localDataIndex, errorObject.getErrorCodeString());

        try {
            if (errorObject.getCategory().equals("1")) {

                context = JAXBContext.newInstance(org.onap.aai.domain.restPolicyException.Fault.class);
                Marshaller marshaller = context.createMarshaller();
                marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
                marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");

                org.onap.aai.domain.restPolicyException.ObjectFactory factory =
                        new org.onap.aai.domain.restPolicyException.ObjectFactory();
                org.onap.aai.domain.restPolicyException.Fault fault = factory.createFault();
                org.onap.aai.domain.restPolicyException.Fault.RequestError requestError =
                        factory.createFaultRequestError();
                org.onap.aai.domain.restPolicyException.Fault.RequestError.PolicyException policyException =
                        factory.createFaultRequestErrorPolicyException();
                org.onap.aai.domain.restPolicyException.Fault.RequestError.PolicyException.Variables policyVariables =
                        factory.createFaultRequestErrorPolicyExceptionVariables();

                policyException.setMessageId("POL" + errorObject.getRESTErrorCode());
                policyException.setText(text.toString());
                for (int i = 0; i < variables.size(); i++) {
                    policyVariables.getVariable().add(variables.get(i));
                }
                policyException.setVariables(policyVariables);
                requestError.setPolicyException(policyException);
                fault.setRequestError(requestError);

                StringWriter stringWriter = new StringWriter();
                marshaller.marshal(fault, stringWriter);

                response = stringWriter.toString();

            } else {

                context = JAXBContext.newInstance(org.onap.aai.domain.restServiceException.Fault.class);
                Marshaller marshaller = context.createMarshaller();
                marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
                marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");

                org.onap.aai.domain.restServiceException.ObjectFactory factory =
                        new org.onap.aai.domain.restServiceException.ObjectFactory();
                org.onap.aai.domain.restServiceException.Fault fault = factory.createFault();
                org.onap.aai.domain.restServiceException.Fault.RequestError requestError =
                        factory.createFaultRequestError();
                org.onap.aai.domain.restServiceException.Fault.RequestError.ServiceException serviceException =
                        factory.createFaultRequestErrorServiceException();
                org.onap.aai.domain.restServiceException.Fault.RequestError.ServiceException.Variables serviceVariables =
                        factory.createFaultRequestErrorServiceExceptionVariables();
                serviceException.setMessageId("POL" + errorObject.getRESTErrorCode());
                serviceException.setText(text.toString());
                for (int i = 0; i < variables.size(); i++) {
                    serviceVariables.getVariable().add(variables.get(i));
                }
                serviceException.setVariables(serviceVariables);
                requestError.setServiceException(serviceException);
                fault.setRequestError(requestError);

                StringWriter stringWriter = new StringWriter();
                marshaller.marshal(fault, stringWriter);

                response = stringWriter.toString();
            }
        } catch (Exception ex) {
            LOGGER.error("We were unable to create a rest exception to return on an API because of a parsing error "
                    + ex.getMessage());
        }
        return response;
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
}
