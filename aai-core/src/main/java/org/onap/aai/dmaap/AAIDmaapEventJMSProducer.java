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

import java.util.Optional;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQQueue;
import org.json.JSONObject;
import org.onap.aai.util.AAIConfig;
import org.springframework.jms.connection.CachingConnectionFactory;
import org.springframework.jms.core.JmsTemplate;

public class AAIDmaapEventJMSProducer implements MessageProducer {

	private final Optional<JmsTemplate> jmsTemplate;

	AAIDmaapEventJMSProducer(Optional<JmsTemplate> jmsTemplate) {
		this.jmsTemplate = jmsTemplate;
	}

	public void sendMessageToDefaultDestination(JSONObject finalJson) {
		jmsTemplate.ifPresent( jmsTemplate -> {
			jmsTemplate.convertAndSend(finalJson.toString());
			CachingConnectionFactory ccf = (CachingConnectionFactory) jmsTemplate.getConnectionFactory();
			ccf.destroy();
		});
	}

	public static class Factory {

		public static AAIDmaapEventJMSProducer createInstance() {
			JmsTemplate jmsTemplate = null;
			if (AAIConfig.get("aai.jms.enable", "true").equals("true")) {
				jmsTemplate = new JmsTemplate();
				String activeMqTcpUrl = System.getProperty("activemq.tcp.url", "tcp://localhost:61547");
				jmsTemplate.setConnectionFactory(new CachingConnectionFactory(new ActiveMQConnectionFactory(activeMqTcpUrl)));
				jmsTemplate.setDefaultDestination(new ActiveMQQueue("IN_QUEUE"));
			}

			return new AAIDmaapEventJMSProducer(Optional.ofNullable(jmsTemplate));
		}
	}
}
