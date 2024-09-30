/**
 * ============LICENSE_START=======================================================
 * org.onap.aai
 * ================================================================================
 * Copyright © 2024 Deutsche Telekom. All rights reserved.
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

package org.onap.aai.kafka;

import org.json.JSONObject;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AAIKafkaEventProducer implements MessageProducer {

  private static final String TOPIC = "AAI-EVENT";
  private final KafkaTemplate<String, String> kafkaTemplate;

  @Override
  public void sendMessageToDefaultDestination(JSONObject notificationEvent) {
    kafkaTemplate.send(TOPIC, notificationEvent.toString());
  }

  @Override
  public void sendMessageToDefaultDestination(String notificationEvent) {
    kafkaTemplate.send(TOPIC, notificationEvent);
  }

}
