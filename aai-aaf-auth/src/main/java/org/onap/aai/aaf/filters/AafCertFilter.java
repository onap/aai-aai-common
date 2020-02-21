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
import org.onap.aai.aaf.auth.AafRequestFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.filter.OrderedRequestContextFilter;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * AAF with client cert authentication filter
 */

@Component
@Profile(AafProfiles.AAF_CERT_AUTHENTICATION)
@PropertySource(value = "file:${CONFIG_HOME}/aaf/permissions.properties", ignoreResourceNotFound = true)
@PropertySource(value = "file:${server.local.startpath}/aaf/permissions.properties", ignoreResourceNotFound = true)
public class AafCertFilter extends OrderedRequestContextFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(AafCertFilter.class);

    String aafUserChainPattern;

    private final CadiFilter cadiFilter;

    private final CadiProps cadiProps;

    @Autowired
    public AafCertFilter( @Value("${aaf.userchain.pattern}") String aafUserChainPattern,
                          CadiProps cadiProps) throws IOException, ServletException {

        this.aafUserChainPattern = aafUserChainPattern;
        this.cadiProps = cadiProps;
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
        this.setOrder(FilterPriority.AAF_CERT_AUTHENTICATION.getPriority());
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        AafRequestFilter.authenticationFilter(request, response, filterChain, cadiFilter, cadiProps.getCadiProperties(), aafUserChainPattern);
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
