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
package org.onap.aai.auth.aaf;

import java.io.IOException;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;

import com.att.eelf.configuration.EELFLogger;
import com.att.eelf.configuration.EELFManager;
import org.onap.aaf.cadi.filter.CadiFilter;

import static org.onap.aai.auth.aaf.ResponseFormatter.errorResponse;

/**
 * The Class AafRequestFilter provides common auth filter methods
 */
public class AafRequestFilter {

    private static final EELFLogger LOGGER = EELFManager.getInstance().getLogger(AafRequestFilter.class);

    public static void authenticationFilter(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain,
                       CadiFilter cadiFilter, Properties props, String userChainPattern) throws IOException, ServletException {
        if (!request.getRequestURI().matches("^.*/util/echo$")) {

            List<String> cadiConfiguredIssuers = CertUtil.getCadiCertIssuers(props);
            String issuer = CertUtil.getCertIssuer(request);
            if (issuer == null || issuer.isEmpty()) {
                errorResponse(request, response);
                return;
            }
            issuer = issuer.replaceAll("\\s+", "").toUpperCase();

            if (cadiConfiguredIssuers.contains(issuer)) {
                LOGGER.debug("authenticationFilter CADI issuer " + issuer);
                if (CertUtil.isHaProxy(request)) {
                    // get the end user/client mechid and use it in the user chain header value
                    String user = CertUtil.getMechId(request);
                    LOGGER.debug("authenticationFilter haProxy sent end user/mechid " + user );
                    if (user == null || user.isEmpty()) {
                        errorResponse(request, response);
                        return;
                    }
                    AafRequestWrapper reqWrapper = new AafRequestWrapper(request);
                    String userChainHdr = CertUtil.buildUserChainHeader(user, userChainPattern);
                    LOGGER.debug("User chain header value: " + userChainHdr );
                    reqWrapper.putHeader(CertUtil.AAF_USER_CHAIN_HDR, userChainHdr);
                    cadiFilter.doFilter(reqWrapper, response, filterChain);
                } else {
                    cadiFilter.doFilter(request, response, filterChain);
                }
                if (response.getStatus() >= 400 && response.getStatus() < 500) {
                    LOGGER.debug("authenticationFilter failed CADI authentication" );
                    errorResponse(request, response);
                    return;
                }
            } else {
                filterChain.doFilter(request, response);
            }
        } else {
            filterChain.doFilter(request, response);
        }
    }
    public static void authorizationFilter(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain, String permission,
                                       Properties props) throws IOException, ServletException {
        if(request.getRequestURI().matches("^.*/util/echo$")){
            filterChain.doFilter(request, response);
        }
        List<String> cadiConfiguredIssuers = CertUtil.getCadiCertIssuers(props);
        String issuer = CertUtil.getCertIssuer(request);
        if (issuer == null || issuer.isEmpty()) {
            errorResponse(request, response);
            return;
        }
        issuer = issuer.replaceAll("\\s+","").toUpperCase();
        Enumeration hdrs = request.getHeaders(CertUtil.AAF_USER_CHAIN_HDR);
        while (hdrs.hasMoreElements()) {
            String headerValue = (String)hdrs.nextElement();
            LOGGER.debug("authorizationFilter user chain headerValue=" + headerValue);
        }
        if ((cadiConfiguredIssuers.contains(issuer)) && (!request.isUserInRole(permission))) {
            LOGGER.debug("authorizationFilter failed CADI authorization issuer=" + issuer + " permission=" + permission);
            errorResponse(request, response);
        }
        else{
            filterChain.doFilter(request,response);
        }
    }
}
