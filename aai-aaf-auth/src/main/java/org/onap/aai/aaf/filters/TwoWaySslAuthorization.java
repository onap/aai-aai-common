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
import org.onap.aai.aaf.auth.AAIAuthCore;
import org.onap.aai.aaf.auth.CertUtil;
import org.onap.aai.aaf.auth.ResponseFormatter;
import org.onap.aai.exceptions.AAIException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.filter.OrderedRequestContextFilter;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.security.auth.x500.X500Principal;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.cert.X509Certificate;
import java.util.*;

@Component
@Profile("two-way-ssl")
public class TwoWaySslAuthorization extends OrderedRequestContextFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(TwoWaySslAuthorization.class);

    public static final String HTTP_METHOD_OVERRIDE = "X-HTTP-Method-Override";

    public static final String MERGE_PATCH = "MERGE_PATCH";

    @Autowired
    private Environment environment;

    @Autowired
    private AAIAuthCore aaiAuthCore;

    @Autowired
    private CadiProps cadiProps;

    public TwoWaySslAuthorization(){
        this.setOrder(FilterPriority.TWO_WAY_SSL_AUTH.getPriority());
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {

        String uri = request.getRequestURI();
        String httpMethod = getHttpMethod(request);

        Optional<String> authUser = getUser(request);

        if (authUser.isPresent()) {
            Properties cadiProperties = cadiProps.getCadiProperties();

            String issuer = CertUtil.getCertIssuer(request);
            if (issuer == null || issuer.isEmpty()) {
                AAIException aaie = new AAIException("AAI_9107");
                ResponseFormatter.errorResponse(aaie, request, response);
                return;
            }
            issuer = issuer.replaceAll("\\s+","").toUpperCase();

            List<String> cadiConfiguredIssuers = CertUtil.getCadiCertIssuers(cadiProperties);
            boolean isAafAuthProfileActive = this.isAafAuthProfileActive();
            if ((!isAafAuthProfileActive) || (!cadiConfiguredIssuers.contains(issuer))  ) {
                try {
                    this.authorize(uri, httpMethod, authUser.get(), this.getHaProxyUser(request), issuer);
                } catch (AAIException e) {
                    ResponseFormatter.errorResponse(e, request, response);
                    return;
                }
            }
        } else {
            AAIException aaie = new AAIException("AAI_9107");
            ResponseFormatter.errorResponse(aaie, request, response);
            return;
        }
        filterChain.doFilter(request, response);
    }


    private String getHttpMethod(HttpServletRequest request) {
        String httpMethod = request.getMethod();
        if ("POST".equalsIgnoreCase(httpMethod)
            && "PATCH".equals(request.getHeader(HTTP_METHOD_OVERRIDE))) {
            httpMethod = MERGE_PATCH;
        }
        if (httpMethod.equalsIgnoreCase(MERGE_PATCH) || "patch".equalsIgnoreCase(httpMethod)) {
            httpMethod = "PUT";
        }
        return httpMethod;
    }

    private Optional<String> getUser(HttpServletRequest hsr) {
        String authUser = null;
        if (hsr.getAttribute("javax.servlet.request.cipher_suite") != null) {
            X509Certificate[] certChain = (X509Certificate[]) hsr.getAttribute("javax.servlet.request.X509Certificate");

            /*
             * If the certificate is null or the certificate chain length is zero Then
             * retrieve the authorization in the request header Authorization Check that it
             * is not null and that it starts with Basic and then strip the basic portion to
             * get the base64 credentials Check if this is contained in the AAIBasicAuth
             * Singleton class If it is, retrieve the username associated with that
             * credentials and set to authUser Otherwise, get the principal from certificate
             * and use that authUser
             */

            if (certChain == null || certChain.length == 0) {

                String authorization = hsr.getHeader("Authorization");

                if (authorization != null && authorization.startsWith("Basic ")) {
                    authUser = authorization.replace("Basic ", "");
                }

            } else {
                X509Certificate clientCert = certChain[0];
                X500Principal subjectDN = clientCert.getSubjectX500Principal();
                authUser = subjectDN.toString().toLowerCase();
            }
        }

        return Optional.ofNullable(authUser);
    }

    private String getHaProxyUser(HttpServletRequest hsr) {
        String haProxyUser;
        if (Objects.isNull(hsr.getHeader("X-AAI-SSL-Client-CN"))
            || Objects.isNull(hsr.getHeader("X-AAI-SSL-Client-OU"))
            || Objects.isNull(hsr.getHeader("X-AAI-SSL-Client-O"))
            || Objects.isNull(hsr.getHeader("X-AAI-SSL-Client-L"))
            || Objects.isNull(hsr.getHeader("X-AAI-SSL-Client-ST"))
            || Objects.isNull(hsr.getHeader("X-AAI-SSL-Client-C"))) {
            haProxyUser = "";
        } else {
            haProxyUser = String.format("CN=%s, OU=%s, O=\"%s\", L=%s, ST=%s, C=%s",
                Objects.toString(hsr.getHeader("X-AAI-SSL-Client-CN"), ""),
                Objects.toString(hsr.getHeader("X-AAI-SSL-Client-OU"), ""),
                Objects.toString(hsr.getHeader("X-AAI-SSL-Client-O"), ""),
                Objects.toString(hsr.getHeader("X-AAI-SSL-Client-L"), ""),
                Objects.toString(hsr.getHeader("X-AAI-SSL-Client-ST"), ""),
                Objects.toString(hsr.getHeader("X-AAI-SSL-Client-C"), "")).toLowerCase();
        }
        return haProxyUser;
    }

    private void authorize(String uri, String httpMethod, String authUser, String haProxyUser, String issuer) throws AAIException {
        if (!aaiAuthCore.authorize(authUser, uri, httpMethod, haProxyUser, issuer)) {
            throw new AAIException("AAI_9101", "Request on " + httpMethod + " " + uri + " status is not OK");
        }
    }

    private boolean isAafAuthProfileActive() {
        String[] profiles = environment.getActiveProfiles();
        if (profiles != null) {
            if (Arrays.stream(profiles).anyMatch(
                env -> (env.equalsIgnoreCase(AafProfiles.AAF_CERT_AUTHENTICATION)))) {
                return true;
            }
        }
        return false;
    }
}
