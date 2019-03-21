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

import com.att.eelf.configuration.EELFLogger;
import com.att.eelf.configuration.EELFManager;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import javax.ws.rs.core.MediaType;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import org.apache.commons.lang.StringUtils;
import org.onap.aai.exceptions.AAIException;
import org.onap.aai.logging.LoggingContext.StatusCode;
import org.onap.aai.util.AAIConfig;
import org.onap.aai.util.AAIConstants;
import org.onap.aai.util.MapperUtil;
import org.slf4j.MDC;

/**
 * 
 * This classes loads the application error properties file
 * and provides a method that returns an ErrorObject
 * 
 */

public class ErrorLogHelper {

    private static final EELFLogger LOGGER = EELFManager.getInstance().getLogger(ErrorLogHelper.class);
    private static final HashMap<String, ErrorObject> ERROR_OBJECTS = new HashMap<String, ErrorObject>();

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
     * @throws ErrorObjectFormatException
     * @throws Exception the exception
     */
    public static void loadProperties() throws IOException, ErrorObjectFormatException {
        final String filePath = AAIConstants.AAI_HOME_ETC_APP_PROPERTIES + "error.properties";
        final InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(filePath);
        final Properties properties = new Properties();

        if (is != null) {
            properties.load(is);
        } else {
            try (final FileInputStream fis = new FileInputStream(filePath)) {
                properties.load(fis);
            }
        }

        for (Entry<Object, Object> entry : properties.entrySet()) {
            final String key = (String) entry.getKey();
            final String value = (String) entry.getValue();
            final String[] errorProperties = value.split(":");

            if (errorProperties.length != 7)
                throw new ErrorObjectFormatException();

            final ErrorObject errorObject = new ErrorObject();

            errorObject.setDisposition(errorProperties[0].trim());
            errorObject.setCategory(errorProperties[1].trim());
            errorObject.setSeverity(errorProperties[2].trim());
            errorObject.setErrorCode(errorProperties[3].trim());
            errorObject.setHTTPResponseCode(errorProperties[4].trim());
            errorObject.setRESTErrorCode(errorProperties[5].trim());
            errorObject.setErrorText(errorProperties[6].trim());

            ERROR_OBJECTS.put(key, errorObject);
        }
    }

    /**
     * Logs a known A&AI exception (i.e. one that can be found in error.properties)
     *
     * @param key The key for the error in the error.properties file
     * @throws IOException
     * @throws ErrorObjectNotFoundException
     * @throws ErrorObjectFormatException
     */
    public static ErrorObject getErrorObject(String code) throws ErrorObjectNotFoundException {

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
     * @param are must have a restError value whose numeric value must match what should be returned in the REST API
     * @param variables optional list of variables to flesh out text in error string
     * @return appropriately formatted JSON response per the REST API spec.
     * @throws ErrorObjectFormatException
     * @throws ErrorObjectNotFoundException
     * @throws IOException
     * @deprecated
     */
    public static String getRESTAPIErrorResponse(AAIException are, ArrayList<String> variables) {
        List<MediaType> acceptHeaders = new ArrayList<MediaType>();
        acceptHeaders.add(MediaType.APPLICATION_JSON_TYPE);

        return getRESTAPIErrorResponse(acceptHeaders, are, variables);
    }

    /**
     * Determines whether category is policy or not. If policy (1), this is a POL error, else it's a SVC error.
     * The AAIRESTException may contain a different ErrorObject than that created with the REST error key.
     * This allows lower level exception detail to be returned to the client to help troubleshoot the problem.
     * If no error object is embedded in the AAIException, one will be created using the error object from the
     * AAIException.
     *
     * @param acceptHeadersOrig the accept headers orig
     * @param are must have a restError value whose numeric value must match what should be returned in the REST API
     * @param variables optional list of variables to flesh out text in error string
     * @return appropriately formatted JSON response per the REST API spec.
     * @throws ErrorObjectFormatException
     * @throws ErrorObjectNotFoundException
     * @throws IOException
     */
    public static String getRESTAPIErrorResponse(List<MediaType> acceptHeadersOrig, AAIException are,
            ArrayList<String> variables) {

        StringBuilder text = new StringBuilder();
        String response = null;

        List<MediaType> acceptHeaders = new ArrayList<MediaType>();
        // we might have an exception but no accept header, so we'll set default to JSON
        boolean foundValidAcceptHeader = false;
        for (MediaType mt : acceptHeadersOrig) {
            if (MediaType.APPLICATION_XML_TYPE.isCompatible(mt) || MediaType.APPLICATION_JSON_TYPE.isCompatible(mt)) {
                acceptHeaders.add(mt);
                foundValidAcceptHeader = true;
            }
        }
        if (foundValidAcceptHeader == false) {
            // override the exception, client needs to set an appropriate Accept header
            are = new AAIException("AAI_4014");
            acceptHeaders.add(MediaType.APPLICATION_JSON_TYPE);
        }

        final ErrorObject eo = are.getErrorObject();

        int restErrorCode = Integer.parseInt(eo.getRESTErrorCode());

        ErrorObject restErrorObject;

        try {
            restErrorObject = ErrorLogHelper.getErrorObject("AAI_" + restErrorCode);
        } catch (ErrorObjectNotFoundException e) {
            LOGGER.warn("Failed to find related error object AAI_" + restErrorCode + " for error object "
                    + eo.getErrorCode() + "; using AAI_" + restErrorCode);
            restErrorObject = eo;
        }

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
        if (are.getMessage() == null || are.getMessage().length() == 0) {
            variables.add(localDataIndex++, eo.getErrorText());
        } else {
            variables.add(localDataIndex++, eo.getErrorText() + ":" + are.getMessage());
        }
        variables.add(localDataIndex, eo.getErrorCodeString());

        for (MediaType mediaType : acceptHeaders) {
            if (MediaType.APPLICATION_XML_TYPE.isCompatible(mediaType)) {
                JAXBContext context = null;
                try {
                    if (eo.getCategory().equals("1")) {

                        context = JAXBContext.newInstance(org.onap.aai.domain.restPolicyException.Fault.class);
                        Marshaller m = context.createMarshaller();
                        m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
                        m.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");

                        org.onap.aai.domain.restPolicyException.ObjectFactory factory =
                                new org.onap.aai.domain.restPolicyException.ObjectFactory();
                        org.onap.aai.domain.restPolicyException.Fault fault = factory.createFault();
                        org.onap.aai.domain.restPolicyException.Fault.RequestError requestError =
                                factory.createFaultRequestError();
                        org.onap.aai.domain.restPolicyException.Fault.RequestError.PolicyException policyException =
                                factory.createFaultRequestErrorPolicyException();
                        org.onap.aai.domain.restPolicyException.Fault.RequestError.PolicyException.Variables polvariables =
                                factory.createFaultRequestErrorPolicyExceptionVariables();

                        policyException.setMessageId("POL" + eo.getRESTErrorCode());
                        policyException.setText(text.toString());
                        for (int i = 0; i < variables.size(); i++) {
                            polvariables.getVariable().add(variables.get(i));
                        }
                        policyException.setVariables(polvariables);
                        requestError.setPolicyException(policyException);
                        fault.setRequestError(requestError);

                        StringWriter sw = new StringWriter();
                        m.marshal(fault, sw);

                        response = sw.toString();

                    } else {

                        context = JAXBContext.newInstance(org.onap.aai.domain.restServiceException.Fault.class);
                        Marshaller m = context.createMarshaller();
                        m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
                        m.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");

                        org.onap.aai.domain.restServiceException.ObjectFactory factory =
                                new org.onap.aai.domain.restServiceException.ObjectFactory();
                        org.onap.aai.domain.restServiceException.Fault fault = factory.createFault();
                        org.onap.aai.domain.restServiceException.Fault.RequestError requestError =
                                factory.createFaultRequestError();
                        org.onap.aai.domain.restServiceException.Fault.RequestError.ServiceException serviceException =
                                factory.createFaultRequestErrorServiceException();
                        org.onap.aai.domain.restServiceException.Fault.RequestError.ServiceException.Variables svcvariables =
                                factory.createFaultRequestErrorServiceExceptionVariables();
                        serviceException.setMessageId("SVC" + eo.getRESTErrorCode());
                        serviceException.setText(text.toString());
                        for (int i = 0; i < variables.size(); i++) {
                            svcvariables.getVariable().add(variables.get(i));
                        }
                        serviceException.setVariables(svcvariables);
                        requestError.setServiceException(serviceException);
                        fault.setRequestError(requestError);

                        StringWriter sw = new StringWriter();
                        m.marshal(fault, sw);

                        response = sw.toString();

                    }
                } catch (Exception ex) {
                    LOGGER.error(
                            "We were unable to create a rest exception to return on an API because of a parsing error "
                                    + ex.getMessage());
                }
            } else {
                try {
                    if (eo.getCategory().equals("1")) {
                        org.onap.aai.domain.restPolicyException.RESTResponse restresp =
                                new org.onap.aai.domain.restPolicyException.RESTResponse();
                        org.onap.aai.domain.restPolicyException.RequestError reqerr =
                                new org.onap.aai.domain.restPolicyException.RequestError();
                        org.onap.aai.domain.restPolicyException.PolicyException polexc =
                                new org.onap.aai.domain.restPolicyException.PolicyException();
                        polexc.setMessageId("POL" + eo.getRESTErrorCode());
                        polexc.setText(text.toString());
                        polexc.setVariables(variables);
                        reqerr.setPolicyException(polexc);
                        restresp.setRequestError(reqerr);
                        response = (MapperUtil.writeAsJSONString((Object) restresp));

                    } else {
                        org.onap.aai.domain.restServiceException.RESTResponse restresp =
                                new org.onap.aai.domain.restServiceException.RESTResponse();
                        org.onap.aai.domain.restServiceException.RequestError reqerr =
                                new org.onap.aai.domain.restServiceException.RequestError();
                        org.onap.aai.domain.restServiceException.ServiceException svcexc =
                                new org.onap.aai.domain.restServiceException.ServiceException();
                        svcexc.setMessageId("SVC" + eo.getRESTErrorCode());
                        svcexc.setText(text.toString());
                        svcexc.setVariables(variables);
                        reqerr.setServiceException(svcexc);
                        restresp.setRequestError(reqerr);
                        response = (MapperUtil.writeAsJSONString((Object) restresp));
                    }
                } catch (AAIException ex) {
                    LOGGER.error(
                            "We were unable to create a rest exception to return on an API because of a parsing error "
                                    + ex.getMessage());
                }
            }
        }

        return response;
    }

    /**
     * Gets the RESTAPI error response with logging.
     *
     * @param acceptHeadersOrig the accept headers orig
     * @param are the are
     * @param variables the variables
     * @param logline the logline
     * @return the RESTAPI error response with logging
     * @throws ErrorObjectFormatException
     * @throws ErrorObjectNotFoundException
     * @throws IOException
     */
    public static String getRESTAPIErrorResponseWithLogging(List<MediaType> acceptHeadersOrig, AAIException are,
            ArrayList<String> variables) {
        String response = ErrorLogHelper.getRESTAPIErrorResponse(acceptHeadersOrig, are, variables);

        LOGGER.error(are.getMessage() + " " + LogFormatTools.getStackTop(are));

        return response;
    }

    /**
     * Gets the RESTAPI info response.
     *
     * @param acceptHeaders the accept headers
     * @param areList the are list
     * @return the RESTAPI info response
     * @throws ErrorObjectFormatException
     * @throws ErrorObjectNotFoundException
     * @throws IOException
     */
    public static Object getRESTAPIInfoResponse(List<MediaType> acceptHeaders,
            HashMap<AAIException, ArrayList<String>> areList) {

        Object respObj = null;

        org.onap.aai.domain.restResponseInfo.ObjectFactory factory =
                new org.onap.aai.domain.restResponseInfo.ObjectFactory();
        org.onap.aai.domain.restResponseInfo.Info info = factory.createInfo();
        org.onap.aai.domain.restResponseInfo.Info.ResponseMessages responseMessages =
                factory.createInfoResponseMessages();
        Iterator<Entry<AAIException, ArrayList<String>>> it = areList.entrySet().iterator();

        while (it.hasNext()) {
            Entry<AAIException, ArrayList<String>> pair = (Entry<AAIException, ArrayList<String>>) it.next();
            AAIException are = pair.getKey();
            ArrayList<String> variables = pair.getValue();

            StringBuilder text = new StringBuilder();

            ErrorObject eo = are.getErrorObject();

            int restErrorCode = Integer.parseInt(eo.getRESTErrorCode());
            ErrorObject restErrorObject;
            try {
                restErrorObject = ErrorLogHelper.getErrorObject("AAI_" + String.format("%04d", restErrorCode));
            } catch (ErrorObjectNotFoundException e) {
                restErrorObject = eo;
            }
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
            if (are.getMessage() == null) {
                variables.add(localDataIndex++, eo.getErrorText());
            } else {
                variables.add(localDataIndex++, eo.getErrorText() + ":" + are.getMessage());
            }
            variables.add(localDataIndex, eo.getErrorCodeString());

            try {
                org.onap.aai.domain.restResponseInfo.Info.ResponseMessages.ResponseMessage responseMessage =
                        factory.createInfoResponseMessagesResponseMessage();
                org.onap.aai.domain.restResponseInfo.Info.ResponseMessages.ResponseMessage.Variables infovariables =
                        factory.createInfoResponseMessagesResponseMessageVariables();

                responseMessage.setMessageId("INF" + eo.getRESTErrorCode());
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
     * @param are must have a restError value whose numeric value must match what should be returned in the REST API
     * @param variables optional list of variables to flesh out text in error string
     * @return appropriately formatted JSON response per the REST API spec.
     * @throws ErrorObjectFormatException
     * @throws ErrorObjectNotFoundException
     * @throws IOException
     */
    public static String getRESTAPIPolicyErrorResponseXML(AAIException are, ArrayList<String> variables) {

        StringBuilder text = new StringBuilder();
        String response = null;
        JAXBContext context = null;

        ErrorObject eo = are.getErrorObject();

        int restErrorCode = Integer.parseInt(eo.getRESTErrorCode());
        ErrorObject restErrorObject;
        try {
            restErrorObject = ErrorLogHelper.getErrorObject("AAI_" + restErrorCode);
        } catch (ErrorObjectNotFoundException e) {
            restErrorObject = eo;
        }

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
        if (are.getMessage() == null) {
            variables.add(localDataIndex++, eo.getErrorText());
        } else {
            variables.add(localDataIndex++, eo.getErrorText() + ":" + are.getMessage());
        }
        variables.add(localDataIndex, eo.getErrorCodeString());

        try {
            if (eo.getCategory().equals("1")) {

                context = JAXBContext.newInstance(org.onap.aai.domain.restPolicyException.Fault.class);
                Marshaller m = context.createMarshaller();
                m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
                m.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");

                org.onap.aai.domain.restPolicyException.ObjectFactory factory =
                        new org.onap.aai.domain.restPolicyException.ObjectFactory();
                org.onap.aai.domain.restPolicyException.Fault fault = factory.createFault();
                org.onap.aai.domain.restPolicyException.Fault.RequestError requestError =
                        factory.createFaultRequestError();
                org.onap.aai.domain.restPolicyException.Fault.RequestError.PolicyException policyException =
                        factory.createFaultRequestErrorPolicyException();
                org.onap.aai.domain.restPolicyException.Fault.RequestError.PolicyException.Variables polvariables =
                        factory.createFaultRequestErrorPolicyExceptionVariables();

                policyException.setMessageId("POL" + eo.getRESTErrorCode());
                policyException.setText(text.toString());
                for (int i = 0; i < variables.size(); i++) {
                    polvariables.getVariable().add(variables.get(i));
                }
                policyException.setVariables(polvariables);
                requestError.setPolicyException(policyException);
                fault.setRequestError(requestError);

                StringWriter sw = new StringWriter();
                m.marshal(fault, sw);

                response = sw.toString();

            } else {

                context = JAXBContext.newInstance(org.onap.aai.domain.restServiceException.Fault.class);
                Marshaller m = context.createMarshaller();
                m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
                m.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");

                org.onap.aai.domain.restServiceException.ObjectFactory factory =
                        new org.onap.aai.domain.restServiceException.ObjectFactory();
                org.onap.aai.domain.restServiceException.Fault fault = factory.createFault();
                org.onap.aai.domain.restServiceException.Fault.RequestError requestError =
                        factory.createFaultRequestError();
                org.onap.aai.domain.restServiceException.Fault.RequestError.ServiceException serviceException =
                        factory.createFaultRequestErrorServiceException();
                org.onap.aai.domain.restServiceException.Fault.RequestError.ServiceException.Variables svcvariables =
                        factory.createFaultRequestErrorServiceExceptionVariables();
                serviceException.setMessageId("POL" + eo.getRESTErrorCode());
                serviceException.setText(text.toString());
                for (int i = 0; i < variables.size(); i++) {
                    svcvariables.getVariable().add(variables.get(i));
                }
                serviceException.setVariables(svcvariables);
                requestError.setServiceException(serviceException);
                fault.setRequestError(requestError);

                StringWriter sw = new StringWriter();
                m.marshal(fault, sw);

                response = sw.toString();

            }
        } catch (Exception ex) {
            LOGGER.error("We were unable to create a rest exception to return on an API because of a parsing error "
                    + ex.getMessage());
        }
        return response;
    }

    public static void logException(AAIException e) {
        final ErrorObject errorObject = e.getErrorObject();

        // MDC.put("severity", errorObject.getSeverity()); //TODO Use LoggingContext.severity(int severity)
        String severityCode = errorObject.getSeverityCode(errorObject.getSeverity());

        if (!AAIConfig.isEmpty(severityCode)) {
            int sevCode = Integer.parseInt(severityCode);
            if (sevCode > 0 && sevCode <= 3) {
                LoggingContext.severity(sevCode);
            }
        }
        String stackTrace = "";
        try {
            stackTrace = LogFormatTools.getStackTop(e);
        } catch (Exception a) {
            // ignore
        }
        final String errorMessage = new StringBuilder().append(errorObject.getErrorText()).append(":")
                .append(errorObject.getRESTErrorCode()).append(":").append(errorObject.getHTTPResponseCode())
                .append(":").append(e.getMessage()).toString().replaceAll("\\n", "^");

        LoggingContext.responseCode(Integer.toString(errorObject.getHTTPResponseCode().getStatusCode()));
        LoggingContext.responseDescription(errorMessage);
        LoggingContext.statusCode(StatusCode.ERROR);

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
