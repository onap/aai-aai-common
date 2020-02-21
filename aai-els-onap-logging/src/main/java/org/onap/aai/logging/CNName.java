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

import ch.qos.logback.access.pattern.AccessConverter;
import ch.qos.logback.access.spi.IAccessEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.x500.X500Principal;
import javax.servlet.http.HttpServletRequest;
import java.security.cert.X509Certificate;

import static java.util.Base64.getDecoder;

public class CNName extends AccessConverter {
    protected static final Logger LOGGER = LoggerFactory.getLogger(CNName.class);

    /**
     * Converts access events to String response codes
     *
     * @param accessEvent the IAccessEvent
     */
    public String convert(IAccessEvent accessEvent) {
        if (!isStarted()) {
            return "INACTIVE_HEADER_CONV";
        }

        String cipherSuite = (String) accessEvent.getRequest().getAttribute("javax.servlet.request.cipher_suite");
        String authUser = null;
        if (cipherSuite != null) {
            try {
                X509Certificate certChain[] = (X509Certificate[]) accessEvent.getRequest()
                        .getAttribute("javax.servlet.request.X509Certificate");
                if (certChain == null || certChain.length == 0) {

                    HttpServletRequest request = accessEvent.getRequest();

                    String authorization = request.getHeader("Authorization");

                    // Set the auth user to "-" so if the authorization header is not found
                    // Or if the decoded basic auth credentials are not found in the format required
                    // it should return "-"
                    // If the decoded string is in the right format, find the index of ":"
                    // Then get the substring of the starting point to the colon not including the colon

                    authUser = "-";

                    if (authorization != null && authorization.startsWith("Basic ")) {
                        String credentials = authorization.replace("Basic ", "");
                        byte[] userCredentials = getDecoder().decode(credentials.getBytes("utf-8"));
                        credentials = new String(userCredentials);

                        int codePoint = credentials.indexOf(':');

                        if (codePoint != -1) {
                            authUser = credentials.substring(0, codePoint);
                        }

                    }

                    return authUser;

                } else {
                    X509Certificate clientCert = certChain[0];
                    X500Principal subjectDN = clientCert.getSubjectX500Principal();
                    authUser = subjectDN.toString();
                    return authUser;
                }
            } catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
                return "-";
            }
        } else {
            return "-";
        }
    }

}
