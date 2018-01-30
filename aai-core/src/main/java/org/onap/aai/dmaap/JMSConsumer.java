/**
 * ============LICENSE_START=======================================================
 * org.onap.aai
 * ================================================================================
 * Copyright © 2017 AT&T Intellectual Property. All rights reserved.
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
 *
 * ECOMP is a trademark and service mark of AT&T Intellectual Property.
 */
package org.onap.aai.dmaap;

import com.att.eelf.configuration.EELFLogger;
import com.att.eelf.configuration.EELFManager;
import org.apache.log4j.MDC;
import org.json.JSONException;
import org.json.JSONObject;
import org.onap.aai.config.SpringContextAware;
import org.onap.aai.logging.LoggingContext.LoggingField;
import org.onap.aai.logging.LoggingContext.StatusCode;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import java.util.Base64;
import java.util.Collections;

public class JMSConsumer implements MessageListener {

    private static final EELFLogger LOGGER = EELFManager.getInstance().getLogger(JMSConsumer.class);

    private static final int HTTPS_PORT = 3905;
    private static final Base64.Encoder base64Encoder = Base64.getEncoder();

    private HttpHeaders httpHeaders;
    private RestTemplate restTemplate;

    private Environment environment;
    private LoadBalancerClient loadBalancerClient;

    public JMSConsumer() throws Exception {
        this((LoadBalancerClient)SpringContextAware.getApplicationContext().getBean("loadBalancerClient"));
    }

    public JMSConsumer(LoadBalancerClient loadBalancerClient) throws Exception {
        this.loadBalancerClient = loadBalancerClient;
        this.httpHeaders  = new HttpHeaders();
        this.httpHeaders.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        this.environment  = SpringContextAware.getApplicationContext().getEnvironment();

        String username = this.environment.getProperty("dmaap.ribbon.username");
        String password = this.environment.getProperty("dmaap.ribbon.password");

        if(username == null || password == null){
            throw new Exception("Unable to retrive username/password from the application properties");
        }

        String auth = String.format("%s:%s", username, password);
        String authString = "Basic " + base64Encoder.encodeToString(auth.getBytes());
        httpHeaders.add("Authorization", authString);

        restTemplate = new RestTemplate();
    }

    @Override
    public void onMessage(Message message) {

        String jsmMessageTxt = "";
        String aaiEvent = "";
        String eventName = "";

        String environment = System.getProperty("lrmRO");
        if (environment == null) {
            environment = "";
        }

        if (message instanceof TextMessage) {
            try {
                jsmMessageTxt = ((TextMessage) message).getText();
                JSONObject jo = new JSONObject(jsmMessageTxt);

                if (jo.has("aaiEventPayload")) {
                    aaiEvent = jo.getJSONObject("aaiEventPayload").toString();
                } else {
                    return;
                }
                if (jo.getString("transId") != null) {
                    MDC.put("requestId", jo.getString("transId"));
                }
                if (jo.getString("fromAppId") != null) {
                    MDC.put("partnerName", jo.getString("fromAppId"));
                }
                MDC.put("targetEntity", "DMAAP");
                if (jo.getString("event-topic") != null) {
                    eventName = jo.getString("event-topic");
                    MDC.put("targetServiceName", eventName);
                }
                MDC.put("serviceName", "AAI");
                MDC.put(LoggingField.STATUS_CODE.toString(), StatusCode.COMPLETE.toString());
                MDC.put(LoggingField.RESPONSE_CODE.toString(), "0");
                LOGGER.info(eventName + "|" + aaiEvent);

                HttpEntity<String> httpEntity = new HttpEntity<>(aaiEvent, httpHeaders);
                ServiceInstance serviceInstance = loadBalancerClient.choose("dmaap");
                String url = serviceInstance.getHost() + ":" + serviceInstance.getPort();

                if(serviceInstance.getPort() == HTTPS_PORT){
                    url = "https://" + url;
                } else {
                    url = "http://" + url;
                }

                url += "/events/" + eventName;

                if ("AAI-EVENT".equals(eventName)) {
                    restTemplate.exchange(url, HttpMethod.POST, httpEntity, String.class);
                    LOGGER.info(eventName + "|Event sent.");
                } else if ("AAI-VCE-INTERFACE-DATA".equals(eventName)) {
                    restTemplate.exchange(url, HttpMethod.POST, httpEntity, String.class);
                    String msg = "";
                    LOGGER.info(eventName + "|Event sent. " + msg);
                } else {
                    MDC.put(LoggingField.STATUS_CODE.toString(), StatusCode.ERROR.toString());
                    MDC.put(LoggingField.RESPONSE_CODE.toString(), "900");
                    LOGGER.error(eventName + "|Event Topic invalid.");
                }
            } catch (JMSException | JSONException e) {
                MDC.put(LoggingField.STATUS_CODE.toString(), StatusCode.ERROR.toString());
                MDC.put(LoggingField.RESPONSE_CODE.toString(), "200");
                LOGGER.error("AAI_7350 Error parsing aaievent jms message for sending to dmaap. " + jsmMessageTxt, e);
            } catch (Exception e) {
                MDC.put(LoggingField.STATUS_CODE.toString(), StatusCode.ERROR.toString());
                MDC.put(LoggingField.RESPONSE_CODE.toString(), "200");
                LOGGER.error("AAI_7350 Error sending message to dmaap. " + jsmMessageTxt, e);
            }
        }

    }

}