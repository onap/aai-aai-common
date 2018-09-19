/**
 * ============LICENSE_START=======================================================
 * org.onap.aai
 * ================================================================================
 * Copyright © 2017-2018 AT&T Intellectual Property. All rights reserved.
 * ================================================================================
 * Modifications Copyright © 2018 IBM.
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
package org.onap.aai.domain.translog;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;
import org.onap.aai.AAISetup;

public class TransactionLogEntryTest {
    private TransactionLogEntry logEntry;
    
    @Before
    public void setup() {
        logEntry = new TransactionLogEntry();
    }

    @Test
    public void testGetNotificationAction() throws Exception {
    	logEntry.setNotificationAction("testaction");
        assertEquals(logEntry.getNotificationAction(), "testaction");
    }
    
    @Test
    public void testGetNotificationEntityLink() throws Exception {
    	logEntry.setNotificationEntityLink("entityLink");
        assertEquals(logEntry.getNotificationEntityLink(), "entityLink");
    }
    
    @Test
    public void testGetNotificationId() throws Exception {
    	logEntry.setNotificationId("notificationid");
        assertEquals(logEntry.getNotificationId(), "notificationid");
    }
    
    @Test
    public void testGetNotificationPayload() throws Exception {
    	logEntry.setNotificationPayload("payload");
        assertEquals(logEntry.getNotificationPayload(), "payload");
    }
    
    @Test
    public void testGetNotificationStatus() throws Exception {
    	logEntry.setNotificationStatus("status");
        assertEquals(logEntry.getNotificationStatus(), "status");
    }
    
    @Test
    public void testGetNotificationTopic() throws Exception {
    	logEntry.setNotificationTopic("topic");
        assertEquals(logEntry.getNotificationTopic(), "topic");
    }
}
