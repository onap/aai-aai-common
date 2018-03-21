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
import java.security.KeyManagementException;
import java.security.KeyStore;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;

import org.onap.aai.domain.yang.Customers;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.json.JSONConfiguration;
import com.sun.jersey.client.urlconnection.HTTPSProperties;

public class HttpsAuthExternalClient {

	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public static void main(String[] args) {
		try {
			String url = AAIConfig.get(AAIConstants.AAI_SERVER_URL) + "business/customers";
			System.out.println("Making Jersey https call...");
			String keystore = args[0];
			String keypasswd = args[1];
			Client client = HttpsAuthExternalClient.getClient(keystore, keypasswd);
		
			ClientResponse res = client.resource(url)
					.accept("application/json")
					.header("X-TransactionId", "PROV001")
					.header("X-FromAppId",  "AAI")
					.type("application/json")
					.get(ClientResponse.class);
			
//			System.out.println("Jersey result: ");
//			System.out.println(res.getEntity(String.class).toString());
			
			Customers customers = res.getEntity(Customers.class);
			System.out.println("Jersey result: ");
			System.out.println("Number of customers: " + customers.getCustomer().size());	
			
		} catch (KeyManagementException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	/**
	 * Gets the client.
	 *
	 * @param keystoreFileName the keystore file name
	 * @param keystorePassword the keystore password
	 * @return the client
	 * @throws Exception the exception
	 */
	public static Client getClient ( String keystoreFileName, String keystorePassword ) throws Exception {
		
		ClientConfig config = new DefaultClientConfig();
		config.getFeatures().put(JSONConfiguration.FEATURE_POJO_MAPPING, Boolean.TRUE);
		config.getClasses().add(org.onap.aai.restcore.CustomJacksonJaxBJsonProvider.class);
		Client client = null;
		SSLContext ctx = null;
		
		try {
			String truststore_path = AAIConstants.AAI_HOME_ETC_AUTH + AAIConfig.get(AAIConstants.AAI_TRUSTSTORE_FILENAME);
			String truststore_password = AAIConfig.get(AAIConstants.AAI_TRUSTSTORE_PASSWD);
			String keystore_path = AAIConstants.AAI_HOME_ETC_AUTH + keystoreFileName;
			String keystore_password = keystorePassword;

		    //System.setProperty("javax.net.ssl.trustStore", truststore_path);
		    //System.setProperty("javax.net.ssl.trustStorePassword", truststore_password);
			HttpsURLConnection.setDefaultHostnameVerifier( new HostnameVerifier(){
			    public boolean verify(String string,SSLSession ssls) {
			        return true;
			    }
			});
						
			ctx = SSLContext.getInstance("TLS");
			KeyManagerFactory kmf = null;

			
			/**** kmf = KeyManagerFactory.getInstance("SunX509");
			FileInputStream fin = new FileInputStream(keystore_path);
			KeyStore ks = KeyStore.getInstance("PKCS12");
			char[] pwd = keystore_password.toCharArray();
			ks.load(fin, pwd);
			kmf.init(ks, pwd);
			***/
			
			String alg = TrustManagerFactory.getDefaultAlgorithm();
			TrustManagerFactory tmf = TrustManagerFactory.getInstance(alg);
			FileInputStream tin = new FileInputStream(truststore_path);
			KeyStore ts = KeyStore.getInstance("PKCS12");
			char[] tpwd = truststore_password.toCharArray();
			ts.load(tin, tpwd);
			tmf.init(ts);
	
			//ctx.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);
			// Updating key manager to null, to disable two way SSL
			ctx.init(null, tmf.getTrustManagers(), null);
			
			config.getProperties().put(HTTPSProperties.PROPERTY_HTTPS_PROPERTIES, 
									new HTTPSProperties( new HostnameVerifier() {
				@Override
				public boolean verify( String s, SSLSession sslSession ) {
					return true;
				}
			}, ctx));
			
			client = Client.create(config);
			// uncomment this line to get more logging for the request/response
			// client.addFilter(new LoggingFilter(System.out));
		} catch (Exception e) {
			throw e;
		}
		return client;
	}
	
}  
