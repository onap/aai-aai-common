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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.onap.aaf.cadi.PropAccess;
import org.onap.aaf.cadi.filter.CadiFilter;
import org.onap.aai.aaf.auth.ResponseFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.filter.OrderedRequestContextFilter;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


/**
 * AAF authentication filter
 */

@Component
@Profile(AafProfiles.AAF_AUTHENTICATION)
public class AafFilter extends OrderedRequestContextFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(AafCertFilter.class);

    private final CadiFilter cadiFilter;

    @Autowired
    public AafFilter(CadiProps cadiProps) throws IOException, ServletException {
        cadiFilter = new CadiFilter(new PropAccess((level,element)->{
            switch (level) {
                case DEBUG:
                    LOGGER.debug(buildMsg(element));
                    break;
                case INFO:
                case AUDIT:
                    LOGGER.info(buildMsg(element));
                    break;
                case WARN:
                    LOGGER.warn(buildMsg(element));
                    break;
                case ERROR:
                    LOGGER.error(buildMsg(element));
                    break;
                case INIT:
                    LOGGER.info(buildMsg(element));
                    break;
                case TRACE:
                    LOGGER.trace(buildMsg(element));
                    break;
                case NONE:
                    break;
            }
        }, new String[]{"cadi_prop_files=" + cadiProps.getCadiFileName()} ));
        this.setOrder(FilterPriority.AAF_AUTHENTICATION.getPriority());
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        if (!request.getRequestURI().matches("^.*/util/echo$")) {
            cadiFilter.doFilter(request, response, filterChain);
            if (response.getStatus() == 401 || response.getStatus() == 403) {
                ResponseFormatter.errorResponse(request, response);
            }
        } else {
            filterChain.doFilter(request, response);
        }
    }

    private String buildMsg(Object[] objects) {
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for ( Object o: objects ) {
            if (first) {
                first = false;
            }
            else {
                sb.append(' ');
            }
            sb.append(o.toString());
        }
        return (sb.toString());
    }
}
