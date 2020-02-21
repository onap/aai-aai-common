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

package org.onap.aai.restcore;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.onap.aai.db.props.AAIProperties;
import org.onap.aai.exceptions.AAIException;
import org.onap.aai.introspection.Introspector;
import org.onap.aai.introspection.Loader;
import org.onap.aai.introspection.tools.*;
import org.onap.aai.logging.ErrorLogHelper;
import org.onap.aai.util.AAIConfig;
import org.onap.aai.util.FormatDate;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

/**
 * Base class for AAI REST API classes.
 * Provides method to validate header information
 * TODO should authenticate caller and authorize them for the API they are calling
 * TODO should store the transaction
 *
 *
 */
public class RESTAPI {

    private static final Logger LOGGER = LoggerFactory.getLogger(RESTAPI.class);

    /**
     * The Enum Action.
     */
    public enum Action {
        GET, PUT, POST, DELETE
    }

    /**
     * Gets the from app id.
     *
     * @param headers the headers
     * @return the from app id
     * @throws AAIException the AAI exception
     */
    protected String getFromAppId(HttpHeaders headers) throws AAIException {
        String fromAppId = null;
        if (headers != null) {
            List<String> fromAppIdHeader = headers.getRequestHeader("X-FromAppId");
            if (fromAppIdHeader != null) {
                for (String fromAppIdValue : fromAppIdHeader) {
                    fromAppId = fromAppIdValue;
                }
            }
        }

        if (fromAppId == null) {
            throw new AAIException("AAI_4009");
        }

        return fromAppId;
    }

    /**
     * Gets the trans id.
     *
     * @param headers the headers
     * @return the trans id
     * @throws AAIException the AAI exception
     */
    protected String getTransId(HttpHeaders headers) throws AAIException {
        String transId = null;
        if (headers != null) {
            List<String> transIdHeader = headers.getRequestHeader("X-TransactionId");
            if (transIdHeader != null) {
                for (String transIdValue : transIdHeader) {
                    transId = transIdValue;
                }
            }
        }

        if (transId == null) {
            throw new AAIException("AAI_4010");
        }

        return transId;
    }

    /**
     * Gen date.
     *
     * @return the string
     */
    protected String genDate() {
        FormatDate fd = new FormatDate("YYMMdd-HH:mm:ss:SSS");

        return fd.getDateTime();
    }

    /**
     * Gets the media type.
     *
     * @param mediaTypeList the media type list
     * @return the media type
     */
    protected String getMediaType(List<MediaType> mediaTypeList) {
        String mediaType = MediaType.APPLICATION_JSON; // json is the default
        for (MediaType mt : mediaTypeList) {
            if (MediaType.APPLICATION_XML_TYPE.isCompatible(mt)) {
                mediaType = MediaType.APPLICATION_XML;
            }
        }
        return mediaType;
    }

    /* ----------helpers for common consumer actions ----------- */

    /**
     * Sets the depth.
     *
     * @param depthParam the depth param
     * @return the int
     * @throws AAIException the AAI exception
     */
    protected int setDepth(String depthParam) throws AAIException {
        int depth = AAIProperties.MAXIMUM_DEPTH; // default
        if (depthParam != null && depthParam.length() > 0 && !depthParam.equals("all")) {
            try {
                depth = Integer.parseInt(depthParam);
            } catch (Exception e) {
                throw new AAIException("AAI_4016");
            }
        }
        return depth;
    }

    /**
     * Consumer exception response generator.
     *
     * @param headers the headers
     * @param info the info
     * @param templateAction the template action
     * @param e the e
     * @return the response
     */
    protected Response consumerExceptionResponseGenerator(HttpHeaders headers, UriInfo info, HttpMethod templateAction,
            AAIException e) {
        ArrayList<String> templateVars = new ArrayList<>();
        templateVars.add(templateAction.toString()); // GET, PUT, etc
        templateVars.add(info.getPath());
        templateVars.addAll(e.getTemplateVars());

        ErrorLogHelper.logException(e);
        return Response
                .status(e.getErrorObject().getHTTPResponseCode()).entity(ErrorLogHelper
                        .getRESTAPIErrorResponseWithLogging(headers.getAcceptableMediaTypes(), e, templateVars))
                .build();
    }

    /**
     * Validate introspector.
     *
     * @param obj the obj
     * @param loader the loader
     * @param uri the uri
     * @throws AAIException the AAI exception
     * @throws UnsupportedEncodingException the unsupported encoding exception
     */
    protected void validateIntrospector(Introspector obj, Loader loader, URI uri, HttpMethod method)
            throws AAIException, UnsupportedEncodingException {

        int maximumDepth = AAIProperties.MAXIMUM_DEPTH;
        boolean validateRequired = true;
        if (method.equals(HttpMethod.MERGE_PATCH)) {
            validateRequired = false;
            maximumDepth = 0;
        }
        IntrospectorValidator validator = new IntrospectorValidator.Builder().validateRequired(validateRequired)
                .restrictDepth(maximumDepth).addResolver(new RemoveNonVisibleProperty()).addResolver(new CreateUUID())
                .addResolver(new DefaultFields()).addResolver(new InjectKeysFromURI(loader, uri)).build();
        boolean result = validator.validate(obj);
        if (!result) {
            result = validator.resolveIssues();
        }
        if (!result) {
            List<String> messages = new ArrayList<>();
            for (Issue issue : validator.getIssues()) {
                if (!issue.isResolved()) {
                    messages.add(issue.getDetail());
                }
            }
            String errors = String.join(",", messages);
            throw new AAIException("AAI_3000", errors);
        }
        // check that key in payload and key in request uri are the same
        String objURI = obj.getURI();
        // if requested object is a parent objURI will have a leading slash the input uri will lack
        // this adds that leading slash for the comparison
        String testURI = "/" + uri.getRawPath();
        if (!testURI.endsWith(objURI)) {
            throw new AAIException("AAI_3000", "uri and payload keys don't match");
        }
    }

    /**
     * Gets the input media type.
     *
     * @param mediaType the media type
     * @return the input media type
     */
    protected String getInputMediaType(MediaType mediaType) {
        return mediaType.getType() + "/" + mediaType.getSubtype();
    }

    /**
     * Returns the app specific timeout in milliseconds, -1 overrides the timeout for an app
     *
     * @param sot
     * @param appTimeouts
     * @param defaultTimeout
     * @return integer timeout in or -1 to bypass
     * @throws AAIException
     */

    public int getTimeoutLimit(String sot, String appTimeouts, String defaultTimeout) {
        String[] ignoreAppIds = (appTimeouts).split("\\|");
        int appLimit = Integer.parseInt(defaultTimeout);
        final Map<String, Integer> m = new HashMap<>();
        for (int i = 0; i < ignoreAppIds.length; i++) {
            String[] vals = ignoreAppIds[i].split(",");
            m.put(vals[0], Integer.parseInt(vals[1]));
        }
        if (m.get(sot) != null) {
            appLimit = m.get(sot);
        }
        return appLimit;
    }

    /**
     * Returns whether time out is enabled
     *
     * @param sot
     * @param isEnabled
     * @param appTimeouts
     * @param defaultTimeout
     * @return boolean of whether the timeout is enabled
     * @throws AAIException
     */
    public boolean isTimeoutEnabled(String sot, String isEnabled, String appTimeouts, String defaultTimeout) {
        boolean isTimeoutEnabled = Boolean.parseBoolean(isEnabled);
        int ata = -1;
        if (isTimeoutEnabled) {
            ata = getTimeoutLimit(sot, appTimeouts, defaultTimeout);
        }
        return isTimeoutEnabled && (ata > -1);
    }

    /**
     * Executes the process thread and watches the future for the timeout
     *
     * @param handler
     * @param sourceOfTruth
     * @param appTimeoutLimit
     * @param defaultTimeoutLimit
     * @param method
     * @param headers
     * @param info
     * @return the response
     */

    public Response executeProcess(Future<Response> handler, String sourceOfTruth, String appTimeoutLimit,
            String defaultTimeoutLimit, HttpMethod method, HttpHeaders headers, UriInfo info) {
        Response response = null;
        int timeoutLimit = 0;
        try {
            timeoutLimit = getTimeoutLimit(sourceOfTruth, appTimeoutLimit, defaultTimeoutLimit);
            response = handler.get(timeoutLimit, TimeUnit.MILLISECONDS);
        } catch (TimeoutException e) {
            AAIException ex = new AAIException("AAI_7406",
                    String.format("Timeout limit of %s seconds reached.", timeoutLimit / 1000));
            response = consumerExceptionResponseGenerator(headers, info, method, ex);
            handler.cancel(true);
        } catch (Exception e) {
            AAIException ex = new AAIException("AAI_4000", e);
            response = consumerExceptionResponseGenerator(headers, info, method, ex);
        }
        return response;
    }

    /**
     * runner sets up the timer logic and invokes it
     *
     * @param toe
     * @param tba
     * @param tdl
     * @param headers
     * @param info
     * @param httpMethod
     * @param c
     * @return the response
     */
    public Response runner(String toe, String tba, String tdl, HttpHeaders headers, UriInfo info, HttpMethod httpMethod,
            Callable c) {
        Response response = null;
        Future<Response> handler = null;
        ExecutorService executor = null;
        try {
            String timeoutEnabled = AAIConfig.get(toe);
            String timeoutByApp = AAIConfig.get(tba);
            String timeoutDefaultLimit = AAIConfig.get(tdl);
            String sourceOfTruth = headers.getRequestHeaders().getFirst("X-FromAppId");
            if (isTimeoutEnabled(sourceOfTruth, timeoutEnabled, timeoutByApp, timeoutDefaultLimit)) {
                executor = Executors.newSingleThreadExecutor();
                handler = executor.submit(c);
                response = executeProcess(handler, sourceOfTruth, timeoutByApp, timeoutDefaultLimit, httpMethod,
                        headers, info);
            } else {
                response = (Response) c.call();
            }
        } catch (Exception e) {
            AAIException ex = new AAIException("AAI_4000", e);
            response = consumerExceptionResponseGenerator(headers, info, httpMethod, ex);
        } finally {
            if (executor != null && handler != null) {
                executor.shutdownNow();
            }
        }
        return response;
    }

}
