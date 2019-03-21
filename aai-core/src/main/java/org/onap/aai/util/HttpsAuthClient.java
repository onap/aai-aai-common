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
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ============LICENSE_END=========================================================
 */

package org.onap.aai.util;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.client.filter.LoggingFilter;
import com.sun.jersey.api.json.JSONConfiguration;
import com.sun.jersey.client.urlconnection.HTTPSProperties;

import java.io.FileInputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;

public class HttpsAuthClient {

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

            ClientResponse res = client.resource(url).accept("application/json")
                .header("X-TransactionId", "PROV001").header("X-FromAppId", "AAI")
                .type("application/json").get(ClientResponse.class);

            // System.out.println("Jersey result: ");
            // System.out.println(res.getEntity(String.class).toString());

        } catch (KeyManagementException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets the client.
     *
     * @return the client
     * @throws KeyManagementException the key management exception
     */
    public static Client getClient() throws KeyManagementException {

        ClientConfig config = new DefaultClientConfig();
        config.getFeatures().put(JSONConfiguration.FEATURE_POJO_MAPPING, Boolean.TRUE);
        config.getClasses().add(org.onap.aai.restcore.CustomJacksonJaxBJsonProvider.class);

        SSLContext ctx = null;
        try {
            String truststore_path = AAIConstants.AAI_HOME_ETC_AUTH
                + AAIConfig.get(AAIConstants.AAI_TRUSTSTORE_FILENAME);
            String truststore_password = AAIConfig.get(AAIConstants.AAI_TRUSTSTORE_PASSWD);
            String keystore_path =
                AAIConstants.AAI_HOME_ETC_AUTH + AAIConfig.get(AAIConstants.AAI_KEYSTORE_FILENAME);
            String keystore_password = AAIConfig.get(AAIConstants.AAI_KEYSTORE_PASSWD);

            System.setProperty("javax.net.ssl.trustStore", truststore_path);
            System.setProperty("javax.net.ssl.trustStorePassword", truststore_password);
            HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
                public boolean verify(String string, SSLSession ssls) {
                    return true;
                }
            });

            ctx = SSLContext.getInstance("TLSv1.2");
            KeyManagerFactory kmf = null;
            try {
                kmf = KeyManagerFactory.getInstance("SunX509");
                FileInputStream fin = new FileInputStream(keystore_path);
                KeyStore ks = KeyStore.getInstance("PKCS12");
                char[] pwd = keystore_password.toCharArray();
                ks.load(fin, pwd);
                kmf.init(ks, pwd);
            } catch (Exception e) {
                System.out.println("Error setting up kmf: exiting");
                e.printStackTrace();
                System.exit(1);
            }

            ctx.init(kmf.getKeyManagers(), null, null);
            config.getProperties().put(HTTPSProperties.PROPERTY_HTTPS_PROPERTIES,
                new HTTPSProperties(new HostnameVerifier() {
                    @Override
                    public boolean verify(String s, SSLSession sslSession) {
                        return true;
                    }
                }, ctx));
        } catch (Exception e) {
            System.out.println("Error setting up config: exiting");
            e.printStackTrace();
            System.exit(1);
        }

        Client client = Client.create(config);
        // uncomment this line to get more logging for the request/response
        // client.addFilter(new LoggingFilter(System.out));

        return client;
    }

}
