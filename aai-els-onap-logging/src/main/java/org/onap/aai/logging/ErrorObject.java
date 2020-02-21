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

import javax.ws.rs.core.Response.Status;

/**
 * 
 * Contains the definition of all error message fields to be mapped from the Error
 * properties file
 * 
 */
public class ErrorObject {

    private String disposition;
    private String category;
    private String severity;
    private Status httpResponseCode = Status.INTERNAL_SERVER_ERROR; // default
    private String restErrorCode = "3002";
    private String errorCode;
    private String errorText;
    private String details;
    private String aaiElsErrorCode = AaiElsErrorCode.UNKNOWN_ERROR;

    /**
     * Instantiates a new error object.
     */
    public ErrorObject() {
        super();
    }

    /**
     * Creates an error object
     *
     * @param disposition the disposition
     * @param category the category
     * @param severity the severity
     * @param httpResponseCode the http response code
     * @param restErrorCode the rest error code
     * @param errorCode the error code
     * @param errorText the error text
     */
    public ErrorObject(String disposition, String category, String severity, Integer httpResponseCode,
            String restErrorCode, String errorCode, String errorText) {
        super();
        this.setDisposition(disposition);
        this.setCategory(category);
        this.severity = severity;
        this.setHTTPResponseCode(httpResponseCode);
        this.setRESTErrorCode(restErrorCode);
        this.setErrorCode(errorCode);
        this.setErrorText(errorText);
        this.setAaiElsErrorCode(AaiElsErrorCode.UNKNOWN_ERROR);
    }

    // OLD STARTS HERE

    /**
     * Instantiates a new error object.
     * @param severity the severity
     * @param errorCode the error code
     * @param errorText the error text
     * @param disposition the disposition
     * @param category the category
     */
    public ErrorObject(String severity, String errorCode, String errorText, String disposition, String category) {
        this(severity, Status.INTERNAL_SERVER_ERROR, errorCode, errorText, disposition, category);
    }

    /**
     * Instantiates a new error object.
     *
     * @param severity the severity
     * @param httpResponseCode the http response code
     * @param errorCode the error code
     * @param errorText the error text
     * @param disposition the disposition
     * @param category the category
     */
    public ErrorObject(String severity, Integer httpResponseCode, String errorCode, String errorText,
            String disposition, String category) {
        super();
        this.severity = severity;
        this.setHTTPResponseCode(httpResponseCode);
        this.setErrorCode(errorCode);
        this.setErrorText(errorText);
        this.setDisposition(disposition);
        this.setCategory(category);
        this.setAaiElsErrorCode(AaiElsErrorCode.UNKNOWN_ERROR);
    }

    /**
     * Instantiates a new error object.
     *
     * @param severity the severity
     * @param httpResponseCode the http response code
     * @param errorCode the error code
     * @param errorText the error text
     * @param disposition the disposition
     * @param category the category
     */
    public ErrorObject(String severity, Status httpResponseCode, String errorCode, String errorText, String disposition,
            String category) {
        super();
        this.severity = severity;
        this.setHTTPResponseCode(httpResponseCode);
        this.setErrorCode(errorCode);
        this.setErrorText(errorText);
        this.setDisposition(disposition);
        this.setCategory(category);
        this.setAaiElsErrorCode(AaiElsErrorCode.UNKNOWN_ERROR);
    }

    /**
     * Gets the disposition.
     *
     * @return the disposition
     */
    public String getDisposition() {
        return disposition;
    }

    /**
     * Sets the disposition.
     *
     * @param disposition the new disposition
     */
    public void setDisposition(String disposition) {
        this.disposition = disposition;
    }

    /**
     * Gets the category.
     *
     * @return the category
     */
    public String getCategory() {
        return category;
    }

    /**
     * Sets the category.
     *
     * @param category the new category
     */
    public void setCategory(String category) {
        this.category = category;
    }

    /**
     * Gets the severity.
     *
     * @return the severity
     */
    public String getSeverity() {
        return severity;
    }

    /**
     * Sets the severity.
     *
     * @param severity the new severity
     */
    public void setSeverity(String severity) {
        this.severity = severity;
    }

    /**
     * Gets the error code.
     *
     * @return the error code
     */
    public String getErrorCode() {
        return errorCode;
    }

    /**
     * Sets the error code.
     *
     * @param errorCode the new error code
     */
    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    /**
     * Gets the HTTP response code.
     *
     * @return the HTTP response code
     */
    public Status getHTTPResponseCode() {
        return httpResponseCode;
    }

    /**
     * Sets the HTTP response code.
     *
     * @param httpResponseCode the new HTTP response code
     */
    public void setHTTPResponseCode(Integer httpResponseCode) {
        this.httpResponseCode = Status.fromStatusCode(httpResponseCode);
        if (this.httpResponseCode == null) {
            throw new IllegalArgumentException(
                    "setHTTPResponseCode was passed an invalid Integer value, fix error.properties or your code "
                            + httpResponseCode);
        }
    }

    /**
     * Sets the HTTP response code.
     *
     * @param httpResponseCode the new HTTP response code
     */
    public void setHTTPResponseCode(String httpResponseCode) {
        this.httpResponseCode = Status.fromStatusCode(Integer.valueOf(httpResponseCode));
        if (this.httpResponseCode == null) {
            throw new IllegalArgumentException(
                    "setHTTPResponseCode was passed an invalid String value, fix error.properties or your code "
                            + httpResponseCode);
        }
    }

    /**
     * Sets the REST error code.
     *
     * @param restErrorCode the new REST error code
     */
    public void setRESTErrorCode(String restErrorCode) {
        this.restErrorCode = restErrorCode;
    }

    /**
     * Gets the REST error code.
     *
     * @return the REST error code
     */
    public String getRESTErrorCode() {
        return this.restErrorCode;
    }

    /**
     * Sets the HTTP response code.
     *
     * @param httpResponseCode the new HTTP response code
     */
    public void setHTTPResponseCode(Status httpResponseCode) {
        this.httpResponseCode = httpResponseCode;
        if (this.httpResponseCode == null) {
            throw new IllegalArgumentException(
                    "setHTTPResponseCode was passed an invalid String value, fix error.properties or your code "
                            + httpResponseCode);
        }
    }

    /**
     * Gets the error text.
     *
     * @return the error text
     */
    public String getErrorText() {
        return errorText;
    }

    /**
     * Sets the error text.
     *
     * @param errorText the new error text
     */
    public void setErrorText(String errorText) {
        this.errorText = errorText;
    }

    /**
     * Gets the details.
     *
     * @return the details
     */
    public String getDetails() {
        return details;
    }

    /**
     * Sets the details.
     *
     * @param details the new details
     */
    public void setDetails(String details) {
        this.details = details == null ? "" : details;
    }
    /**
     * Sets the aai els error code.
     *
     * @param elsErrorCode the new code
     */
    public void setAaiElsErrorCode(String elsErrorCode) {
        aaiElsErrorCode = elsErrorCode;
    }
    /**
     * Gets the aai els error code.
     *
     * @return the code
     */
    public String getAaiElsErrorCode() {
        return (aaiElsErrorCode);
    }
    /**
     * Gets the error code string. This is also the string
     * configured in Nagios to alert on
     *
     * @return the error code string
     */
    // Get the X.Y.Z representation of the error code
    public String getErrorCodeString() {
        String prefix = null;
        switch (disposition) {
            default:
                prefix = "";
                break;
            case "5":
                prefix = "ERR.";
                break;
        }
        return prefix + disposition + "." + category + "." + errorCode;
    }

    /**
     * Gets the severity Code. This is also the string
     * configured in Nagios to alert on
     *
     * @return the severity
     */
    // Get the numerical value of severity
    public String getSeverityCode(String severity) {
        String severityCode = "";
        switch (severity) {
            case "WARN":
                severityCode = "1";
                break;
            case "ERROR":
                severityCode = "2";
                break;
            case "FATAL":
                severityCode = "3";
                break;
        }
        return severityCode;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "ErrorObject [errorCode=" + errorCode + ", errorText=" + errorText + ", restErrorCode=" + restErrorCode
                + ", httpResponseCode=" + httpResponseCode + ", severity=" + severity + ", disposition=" + disposition
                + ", category=" + category + "]";
    }

}
