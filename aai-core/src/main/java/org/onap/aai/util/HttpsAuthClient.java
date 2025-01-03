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
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.client.ClientConfig;
import org.onap.aai.aailog.filter.RestControllerClientRequestLoggingInterceptor;
import org.onap.aai.aailog.filter.RestControllerClientResponseLoggingInterceptor;
import org.onap.aai.exceptions.AAIException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpsAuthClient {

    private static final Logger logger = LoggerFactory.getLogger(HttpsAuthClient.class);

    /**
     * The main method.
     *
     * @param args the arguments
     */
    public static void main(String[] args) {
        try {
            String url = AAIConfig.get(AAIConstants.AAI_SERVER_URL) + "business/customers";
            System.out.println("Making Jersey https call...");
            Client client = HttpsAuthClient.getClient();

            Response res = client.target(url)
                    .request(MediaType.APPLICATION_JSON)
                    .header("X-TransactionId", "PROV001")
                    .header("X-FromAppId", "AAI").get();
        } catch (KeyManagementException e) {
            logger.debug("HttpsAuthClient KeyManagement error : {}", e.getMessage());
        } catch (Exception e) {
            logger.debug("HttpsAuthClient error : {}", e.getMessage());
        }
    }

    /**
     * Gets the client.
     *
     * @param truststorePath the truststore path
     * @param truststorePassword the truststore password
     * @param keystorePath the keystore path
     * @param keystorePassword the keystore password
     * @return the client
     * @throws KeyManagementException the key management exception
     */
    public static Client getClient(String truststorePath, String truststorePassword, String keystorePath,
            String keystorePassword) throws KeyManagementException, UnrecoverableKeyException, CertificateException,
            NoSuchAlgorithmException, KeyStoreException, IOException {

        ClientConfig config = new ClientConfig();
        config.register(org.onap.aai.restcore.CustomJacksonJaxBJsonProvider.class);

        SSLContext ctx = null;
        try {
            System.setProperty("javax.net.ssl.trustStore", truststorePath);
            System.setProperty("javax.net.ssl.trustStorePassword", truststorePassword);
            HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
                public boolean verify(String string, SSLSession ssls) {
                    return true;
                }
            });

            ctx = SSLContext.getInstance("TLSv1.2");
            KeyManagerFactory kmf = null;

            try (FileInputStream fin = new FileInputStream(keystorePath)) {
                kmf = KeyManagerFactory.getInstance("SunX509");
                KeyStore ks = KeyStore.getInstance("PKCS12");
                char[] pwd = keystorePassword.toCharArray();
                ks.load(fin, pwd);
                kmf.init(ks, pwd);
            } catch (Exception e) {
                System.out.println("Error setting up kmf: exiting " + e.getMessage());
                throw e;
            }

            ctx.init(kmf.getKeyManagers(), null, null);
            config.property("jersey.config.client.ssl.context", ctx);
            config.property("jersey.config.client.hostname.verifier", (HostnameVerifier) (s, sslSession) -> true);
        } catch (Exception e) {
            System.out.println("Error setting up config: exiting " + e.getMessage());
            throw e;
        }

        Client client = ClientBuilder.newClient(config);
        client.register(new RestControllerClientRequestLoggingInterceptor());
        client.register(new RestControllerClientResponseLoggingInterceptor());
        // uncomment this line to get more logging for the request/response
        // client.addFilter(new LoggingFilter(System.out));

        return client;
    }

    /**
     * Gets the client.
     *
     * @return the client
     * @throws KeyManagementException the key management exception
     */
    public static Client getClient() throws KeyManagementException, AAIException, UnrecoverableKeyException,
            CertificateException, NoSuchAlgorithmException, KeyStoreException, IOException {
        String truststorePath = null;
        String truststorePassword = null;
        String keystorePath = null;
        String keystorePassword = null;
        truststorePath = AAIConstants.AAI_HOME_ETC_AUTH + AAIConfig.get(AAIConstants.AAI_TRUSTSTORE_FILENAME);
        truststorePassword = AAIConfig.get(AAIConstants.AAI_TRUSTSTORE_PASSWD);
        keystorePath = AAIConstants.AAI_HOME_ETC_AUTH + AAIConfig.get(AAIConstants.AAI_KEYSTORE_FILENAME);
        keystorePassword = AAIConfig.get(AAIConstants.AAI_KEYSTORE_PASSWD);
        return getClient(truststorePath, truststorePassword, keystorePath, keystorePassword);
    }

}
