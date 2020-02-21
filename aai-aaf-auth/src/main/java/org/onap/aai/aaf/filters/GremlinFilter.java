/**
 * ============LICENSE_START=======================================================
 * org.onap.aai
 * ================================================================================
 * Copyright © 2017-2019 AT&T Intellectual Property. All rights reserved.
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
package org.onap.aai.aaf.filters;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.commons.io.IOUtils;
import org.onap.aai.aaf.auth.ResponseFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

@Component
@Profile({
    AafProfiles.AAF_CERT_AUTHENTICATION,
    AafProfiles.AAF_AUTHENTICATION
})
public class GremlinFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(GremlinFilter.class);

    private static final String ADVANCED = "advanced";
    private static final String BASIC = "basic";
    private static final Pattern ECHO_ENDPOINT = Pattern.compile("^.*/util/echo$");

    String type;

    String instance;

    private CadiProps cadiProps;

    @Autowired
    public GremlinFilter(
        @Value("${permission.type}") String type,
        @Value("${permission.instance}") String instance,
        CadiProps cadiProps
    ) {
        this.type = type;
        this.instance = instance;
        this.cadiProps = cadiProps;
    }

    public void doBasicAuthFilter(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        PayloadBufferingRequestWrapper requestBufferWrapper = new PayloadBufferingRequestWrapper(request);

        if(ECHO_ENDPOINT.matcher(request.getRequestURI()).matches()){
            filterChain.doFilter(requestBufferWrapper, response);
        }

        String payload = IOUtils.toString(requestBufferWrapper.getInputStream(), StandardCharsets.UTF_8.name());
        boolean containsWordGremlin = payload.contains("\"gremlin\"");

        //if the requestBufferWrapper contains the word "gremlin" it's an "advanced" query needing an "advanced" role
        String permissionBasic = String.format("%s|%s|%s", type, instance, BASIC);
        String permissionAdvanced = String.format("%s|%s|%s", type, instance, ADVANCED);

        boolean isAuthorized;

        if(containsWordGremlin){
            isAuthorized = requestBufferWrapper.isUserInRole(permissionAdvanced);
        }else{
            isAuthorized = requestBufferWrapper.isUserInRole(permissionAdvanced) || requestBufferWrapper.isUserInRole(permissionBasic);
        }

        if(!isAuthorized){
            String name = requestBufferWrapper.getUserPrincipal() != null ? requestBufferWrapper.getUserPrincipal().getName() : "unknown";
            LOGGER.info("User " + name + " does not have a role for " + (containsWordGremlin ? "gremlin" : "non-gremlin") + " query" );
            ResponseFormatter.errorResponse(request, response);
        } else {
            filterChain.doFilter(requestBufferWrapper,response);
        }
    }
}
