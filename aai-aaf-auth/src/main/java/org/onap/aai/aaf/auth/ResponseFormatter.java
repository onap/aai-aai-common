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

package org.onap.aai.aaf.auth;

import org.onap.aai.exceptions.AAIException;
import org.onap.aai.logging.ErrorLogHelper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

public class ResponseFormatter {

    private static final String ACCEPT_HEADER = "accept";

    public static void errorResponse(HttpServletRequest request, HttpServletResponse response) throws IOException {
        errorResponse(new AAIException("AAI_3300"), request, response);
    }

    public static void errorResponse(AAIException exception, HttpServletRequest request, HttpServletResponse response) throws IOException {

        if(response.isCommitted()){
            return;
        }

        String accept = request.getHeader(ACCEPT_HEADER) == null ? MediaType.APPLICATION_XML : request.getHeader(ACCEPT_HEADER);

        response.setStatus(exception.getErrorObject().getHTTPResponseCode().getStatusCode());
        response.setHeader("Content-Type", accept);
        response.resetBuffer();

        String resp = ErrorLogHelper.getRESTAPIErrorResponse(Collections.singletonList(MediaType.valueOf(accept)), exception, new ArrayList<>());
        response.getOutputStream().print(resp);
        response.flushBuffer();
    }
}
