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

package org.onap.aai.util;

import java.io.FileInputStream;

import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.SslConfigurator;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;

public class HttpsAuthExternalClient {

    /**
     * Gets the client.
     *
     * @param keystoreFileName the keystore file name
     * @param keystorePassword the keystore password
     * @return the client
     * @throws Exception the exception
     */
    public static Client getClient(String keystoreFileName, String keystorePassword) throws Exception {

        ClientConfig config = new ClientConfig();
        config.register(org.onap.aai.restcore.CustomJacksonJaxBJsonProvider.class);

        SSLContext sslContext = null;
        String truststorePath = AAIConstants.AAI_HOME_ETC_AUTH + AAIConfig.get(AAIConstants.AAI_TRUSTSTORE_FILENAME);
        try (FileInputStream tin = new FileInputStream(truststorePath)) {
            String truststorePassword = AAIConfig.get(AAIConstants.AAI_TRUSTSTORE_PASSWD);
            String keystorePath = AAIConstants.AAI_HOME_ETC_AUTH + keystoreFileName;

            SslConfigurator sslConfig = SslConfigurator.newInstance()
                    .trustStoreFile(truststorePath)
                    .trustStorePassword(truststorePassword)
                    .keyStoreFile(keystorePath)
                    .keyStorePassword(keystorePassword);

            sslContext = sslConfig.createSSLContext();

            HostnameVerifier hostnameVerifier = new HostnameVerifier() {
                @Override
                public boolean verify(String s, SSLSession sslSession) {
                    return true;
                }
            };

            Client client = ClientBuilder.newBuilder()
                    .withConfig(config)
                    .sslContext(sslContext)
                    .hostnameVerifier(hostnameVerifier)
                    .build();

            // Uncomment this line to get more logging for the request/response
            // client.register(new
            // LoggingFeature(Logger.getLogger(LoggingFeature.DEFAULT_LOGGER_NAME),
            // Level.INFO, LoggingFeature.Verbosity.PAYLOAD_ANY, 8192));

            return client;
        } catch (Exception e) {
            throw e;
        }
    }

}
