/**
 * ﻿============LICENSE_START=======================================================
 * org.onap.aai
 * ================================================================================
 * Copyright © 2017-2018 AT&T Intellectual Property. All rights reserved.
 * Copyright © 2017-2018 European Software Marketing Ltd.
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

package org.onap.aai.schemaif.json;


import java.io.FileInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.security.KeyStore;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.springframework.http.client.SimpleClientHttpRequestFactory;


public class SecureClientHttpRequestFactory extends SimpleClientHttpRequestFactory {

    private static final String SSL_PROTOCOL = "TLS";
    private static final String KEYSTORE_ALGORITHM = "SunX509";
    private static final String KEYSTORE_TYPE = "PKCS12";
    private JsonSchemaProviderConfig config;


    public SecureClientHttpRequestFactory(JsonSchemaProviderConfig  config) {
        super();
        this.config = config;
    }

    @Override
    protected void prepareConnection(final HttpURLConnection connection, final String httpMethod)
        throws IOException {
        if (connection instanceof HttpsURLConnection) {
            ((HttpsURLConnection) connection)
                .setSSLSocketFactory(getSSLContext().getSocketFactory());
            ((HttpsURLConnection) connection).setHostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String str, SSLSession sslSession) {
                    return true;
                }
            });
        } else {

            throw new IOException();
        }
        super.prepareConnection(connection, httpMethod);
    }

    protected SSLContext getSSLContext() throws IOException {
        try {
            TrustManager[] trustAllCerts = null;

            // We aren't validating certificates, so create a trust manager that
            // does
            // not validate certificate chains.
            trustAllCerts = new TrustManager[] {new X509TrustManager() {
                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }

                public void checkClientTrusted(X509Certificate[] certs, String authType) {
                }

                public void checkServerTrusted(X509Certificate[] certs, String authType) {
                }
            }};

            SSLContext ctx = SSLContext.getInstance(SSL_PROTOCOL);
            KeyManagerFactory kmf = KeyManagerFactory.getInstance(KEYSTORE_ALGORITHM);
            KeyStore ks = KeyStore.getInstance(KEYSTORE_TYPE);

            char[] pwd = null;
            if (config.getSchemaServiceCertPwd()!= null) {
                pwd = config.getSchemaServiceCertPwd().toCharArray();
            }

            if (config.getSchemaServiceCertFile() != null) {
                try (FileInputStream fin = new FileInputStream(config.getSchemaServiceCertFile())) {
                    // Load the keystore and initialize the key manager factory.
                    ks.load(fin, pwd);
                    kmf.init(ks, pwd);

                    ctx.init(kmf.getKeyManagers(), trustAllCerts, null);
                }
            } else {
                ctx.init(null, trustAllCerts, null);
            }

            return ctx;
        } catch (Exception e) {
            throw new IOException("Problem with getting the SSL Context::" + e.getMessage(), e);
        }

    }

}
