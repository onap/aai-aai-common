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
package org.onap.aai.dmaap;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQQueue;
import org.json.JSONObject;
import org.onap.aai.config.SpringContextAware;
import org.onap.aai.util.AAIConfig;
import org.springframework.context.ApplicationContext;
import org.springframework.jms.connection.CachingConnectionFactory;
import org.springframework.jms.core.JmsTemplate;

public class AAIDmaapEventJMSProducer implements MessageProducer {

	private JmsTemplate jmsTemplate;

	private ApplicationContext applicationContext;

	public AAIDmaapEventJMSProducer() {
		if(AAIConfig.get("aai.jms.enable", "true").equals("true")){
            this.jmsTemplate = new JmsTemplate();
            String activeMqTcpUrl = System.getProperty("activemq.tcp.url", "tcp://localhost:61547");
            this.jmsTemplate.setConnectionFactory(new CachingConnectionFactory(new ActiveMQConnectionFactory(activeMqTcpUrl)));
            this.jmsTemplate.setDefaultDestination(new ActiveMQQueue("IN_QUEUE"));
		}
	}

	public void sendMessageToDefaultDestination(JSONObject finalJson) {
		if(jmsTemplate != null){
			jmsTemplate.convertAndSend(finalJson.toString());
			CachingConnectionFactory ccf = (CachingConnectionFactory) this.jmsTemplate.getConnectionFactory();
			ccf.destroy();
		}
	}
}
