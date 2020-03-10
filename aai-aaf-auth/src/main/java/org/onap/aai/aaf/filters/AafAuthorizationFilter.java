/**
 * ============LICENSE_START=======================================================
 * org.onap.aai
 * ================================================================================
 * Copyright © 2017-2018 AT&T Intellectual Property. All rights reserved.
 * ================================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ============LICENSE_END=========================================================
 */

package org.onap.aai.aaf.filters;

import org.onap.aai.aaf.auth.ResponseFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.filter.OrderedRequestContextFilter;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * AAF authorization filter
 */

@Component
@Profile(AafProfiles.AAF_AUTHENTICATION)
@PropertySource(value = "file:${CONFIG_HOME}/aaf/permissions.properties", ignoreResourceNotFound = true)
@PropertySource(value = "file:${server.local.startpath}/aaf/permissions.properties", ignoreResourceNotFound = true)
public class AafAuthorizationFilter extends OrderedRequestContextFilter {

    private static final String ADVANCED = "advanced";
    private static final String BASIC = "basic";

    private final String type;
    private final String instance;

    private GremlinFilter gremlinFilter;

    private List<String> advancedKeywordsList;

    @Autowired
    public AafAuthorizationFilter(
        GremlinFilter gremlinFilter,
        @Value("${permission.type}") String type,
        @Value("${permission.instance}") String instance,
        @Value("${advanced.keywords.list:}") String advancedKeys
    ) {
        this.gremlinFilter = gremlinFilter;
        this.type = type;
        this.instance = instance;
        if(advancedKeys == null || advancedKeys.isEmpty()){
            this.advancedKeywordsList = new ArrayList<>();
        } else {
            this.advancedKeywordsList = Arrays.stream(advancedKeys.split(","))
                .collect(Collectors.toList());
        }
        this.setOrder(FilterPriority.AAF_AUTHORIZATION.getPriority());
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {
	if(request.getRequestURI().matches("^.*/util/echo$")){
	    filterChain.doFilter(request, response);
	}
        if(request.getRequestURI().endsWith("/query")){
            gremlinFilter.doBasicAuthFilter(request, response, filterChain);
        } else {

            String permission = null;

            if(advancedKeywordsList == null || advancedKeywordsList.size() == 0) {
                permission = String.format("%s|%s|%s", type, instance, request.getMethod().toLowerCase());
            } else {

                boolean isAdvanced = this.containsAdvancedKeywords(request);

                //if the URI contains advanced.keywords it's an advanced query
                String queryType = isAdvanced ? ADVANCED : BASIC;
                permission = String.format("%s|%s|%s", type, instance, queryType);
            }

            boolean isAuthorized = request.isUserInRole(permission);

            if(!isAuthorized){
                ResponseFormatter.errorResponse(request, response);
            } else {
                filterChain.doFilter(request,response);
            }

        }
    }

    private boolean containsAdvancedKeywords(HttpServletRequest request) {
        String uri = request.getRequestURI();
        for (String keyword: advancedKeywordsList) {
            if (uri.contains(keyword)) {
                return true;
            }
        }
        return false;
    }
}
