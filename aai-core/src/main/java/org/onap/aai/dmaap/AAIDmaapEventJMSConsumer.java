/**
 * ============LICENSE_START=======================================================
 * org.onap.aai
 * ================================================================================
 * Copyright © 2017-2018 AT&T Intellectual Property. All rights reserved.
 * ================================================================================
 *  Modifications Copyright © 2018 IBM.
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
package org.onap.aai.dmaap;

import com.att.eelf.configuration.EELFLogger;
import com.att.eelf.configuration.EELFManager;
import java.util.Objects;
import java.util.UUID;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import org.apache.log4j.MDC;
import org.json.JSONException;
import org.json.JSONObject;
import org.onap.aai.logging.LogFormatTools;
import org.onap.aai.logging.LoggingContext;
import org.onap.aai.logging.LoggingContext.LoggingField;
import org.onap.aai.logging.LoggingContext.StatusCode;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;

public class AAIDmaapEventJMSConsumer implements MessageListener {

    private static final String EVENT_TOPIC = "event-topic";

    private static final EELFLogger LOGGER = EELFManager.getInstance().getLogger(AAIDmaapEventJMSConsumer.class);

    private RestTemplate restTemplate;

    private HttpHeaders httpHeaders;

    private Environment environment;

    public AAIDmaapEventJMSConsumer(Environment environment, RestTemplate restTemplate, HttpHeaders httpHeaders) {
        Objects.nonNull(environment);
        Objects.nonNull(restTemplate);
        Objects.nonNull(httpHeaders);
        this.environment = environment;
        this.restTemplate = restTemplate;
        this.httpHeaders = httpHeaders;
    }

    @Override
    public void onMessage(Message message) {

        if(restTemplate == null){
            return;
        }

        String jsmMessageTxt = "";
        String aaiEvent = "";
        String eventName = "";
        LoggingContext.save();
        LoggingContext.init();
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
                    LoggingContext.requestId(jo.getString("transId"));
                } else {
                    final UUID generatedRequestUuid = UUID.randomUUID();
                    LoggingContext.requestId(generatedRequestUuid.toString());
                }
                if (jo.getString("fromAppId") != null) {
                    LoggingContext.partnerName(jo.getString("fromAppId"));
                }
                if (jo.getString(EVENT_TOPIC) != null) {
                    eventName = jo.getString(EVENT_TOPIC);
                }

                LoggingContext.targetEntity ("DMAAP");
                if (jo.getString(EVENT_TOPIC) != null) {
                    eventName = jo.getString(EVENT_TOPIC);
                    LoggingContext.targetServiceName(eventName);
                }
                LoggingContext.serviceName("AAI");
                LoggingContext.statusCode(StatusCode.COMPLETE);
                LoggingContext.responseCode(LoggingContext.SUCCESS);
                LOGGER.info(eventName + "|" + aaiEvent);
                
                HttpEntity httpEntity = new HttpEntity(aaiEvent, httpHeaders);

                String transportType = environment.getProperty("dmaap.ribbon.transportType", "http");
                String baseUrl  = transportType + "://" + environment.getProperty("dmaap.ribbon.listOfServers");
                String endpoint = "/events/" + eventName;

                if ("AAI-EVENT".equals(eventName)) {
                    restTemplate.exchange(baseUrl + endpoint, HttpMethod.POST, httpEntity, String.class);
                } else {
                    LoggingContext.statusCode(StatusCode.ERROR);
                    LOGGER.error(eventName + "|Event Topic invalid.");
                }
            } catch (JMSException | JSONException e) {
                LoggingContext.statusCode(StatusCode.ERROR);
                LoggingContext.responseCode(LoggingContext.DATA_ERROR);
                LOGGER.error("AAI_7350 Error parsing aaievent jsm message for sending to dmaap. {} {}", jsmMessageTxt, LogFormatTools.getStackTop(e));
            } catch (Exception e) {
                LoggingContext.statusCode(StatusCode.ERROR);
                LoggingContext.responseCode(LoggingContext.AVAILABILITY_TIMEOUT_ERROR);
                LOGGER.error("AAI_7350 Error sending message to dmaap. {} {}" , jsmMessageTxt, LogFormatTools.getStackTop(e));
            }
        }

    }
}
