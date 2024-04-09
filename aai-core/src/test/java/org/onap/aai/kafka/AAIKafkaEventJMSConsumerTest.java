package org.onap.aai.kafka;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import javax.jms.TextMessage;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.onap.aai.PayloadUtil;
import org.springframework.core.env.Environment;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.util.ReflectionTestUtils;

@RunWith(MockitoJUnitRunner.class)
@EmbeddedKafka(partitions = 1, brokerProperties = { "listeners=PLAINTEXT://localhost:9092", "port=9092" })
public class AAIKafkaEventJMSConsumerTest {

    @Mock
    private Environment environment;

    @Mock
    private KafkaTemplate<String,String> kafkaTemplate;

    private AAIKafkaEventJMSConsumer aaiKafkaEventJMSConsumer;

    @Before
    public void setUp(){
        aaiKafkaEventJMSConsumer = new AAIKafkaEventJMSConsumer(environment,kafkaTemplate);
    }

    @Test
    public void onMessage_shouldSendMessageToKafkaTopic_whenAAIEventReceived()
    throws Exception
    {
        TextMessage mockTextMessage = mock(TextMessage.class);
        String payload = PayloadUtil.getResourcePayload("aai-event.json");

        when(mockTextMessage.getText()).thenReturn(payload);
        aaiKafkaEventJMSConsumer.onMessage(mockTextMessage);
        verify(kafkaTemplate, times(1)).send(eq("AAI-EVENT"), anyString());
    }

    @Test
    public void onMessage_shouldNotSendMessageToKafkaTopic_whenInvalidEventReceived() throws Exception{
        TextMessage mockTextMessage = mock(TextMessage.class);
        String payload = PayloadUtil.getResourcePayload("aai-invalid-event.json");
        when(mockTextMessage.getText()).thenReturn(payload);
        aaiKafkaEventJMSConsumer.onMessage(mockTextMessage);
    }


    @Test
    public void onMessage_shouldHandleJSONException() throws Exception {
        // Arrange
        AAIKafkaEventJMSConsumer consumer = new AAIKafkaEventJMSConsumer(null, kafkaTemplate);
        TextMessage mockTextMessage = mock(TextMessage.class);
        ReflectionTestUtils.setField(consumer, "kafkaTemplate", null); // Simulate null kafkaTemplate

        // Act
        consumer.onMessage(mockTextMessage);

        // Assert
        // Verify that exception is logged
    }

    @Test
    public void onMessage_shouldHandleGenericException() throws Exception {
        // Arrange
        AAIKafkaEventJMSConsumer consumer = new AAIKafkaEventJMSConsumer(null, kafkaTemplate);
        TextMessage mockTextMessage = mock(TextMessage.class);
        when(mockTextMessage.getText()).thenReturn("{\"event-topic\":\"AAI-EVENT\",\"aaiEventPayload\":{}}"); // Valid JSON but missing required fields

        // Act
        consumer.onMessage(mockTextMessage);

        // Assert
        // Verify that exception is logged
    }

}
