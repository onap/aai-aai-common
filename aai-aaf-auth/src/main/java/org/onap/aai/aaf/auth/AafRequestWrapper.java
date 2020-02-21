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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.util.*;

/**
 * The AafRequestWrapper sets the user in the principal name
 */
public class AafRequestWrapper extends HttpServletRequestWrapper {

    private final Map<String, String> customHeaders;

    public AafRequestWrapper(HttpServletRequest request) {
        super(request);
        this.customHeaders = new HashMap<String, String>();
    }

    public void putHeader(String name, String value) {
        this.customHeaders.put(name, value);
    }

    @Override
    public String getHeader(String name) {
        String headerValue = customHeaders.get(name);
        if (headerValue != null) {
            return headerValue;
        }
        return (((HttpServletRequest) getRequest()).getHeader(name));
    }

    @Override
    public Enumeration<String> getHeaderNames() {
        Set<String> nameSet = new HashSet<String>(customHeaders.keySet());

        Enumeration<String> e = ((HttpServletRequest) getRequest()).getHeaderNames();
        while (e.hasMoreElements()) {
            String headerName = e.nextElement();
            nameSet.add(headerName);
        }
        return Collections.enumeration(nameSet);
    }

    @Override
    public Enumeration<String> getHeaders(String name) {
        String myHeaderValue = customHeaders.get(name);
        Set<String> headerValueSet = new HashSet<String>();
        if (myHeaderValue != null) {
            headerValueSet.add(myHeaderValue);
        }
        Enumeration<String> e = ((HttpServletRequest) getRequest()).getHeaders(name);
        while (e.hasMoreElements()) {
            String headerValue = e.nextElement();
            headerValueSet.add(headerValue);
        }
        return Collections.enumeration(headerValueSet);
    }
}
