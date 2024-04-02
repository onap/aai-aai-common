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

package org.onap.aai.domain.translog;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.onap.aai.AAISetup;

public class TransactionLogEntryTest extends AAISetup {
    private TransactionLogEntry transactionLogEntry;

    @BeforeEach
    public void setUp() {
        transactionLogEntry = new TransactionLogEntry();
    }

    @Test
    public void testGetterSetterTransactionLogEntryId() {
        String testTransactionLogEntryId = "testTransactionLogEntryId";
        transactionLogEntry.setTransactionLogEntryId(testTransactionLogEntryId);
        Assertions.assertEquals(testTransactionLogEntryId, transactionLogEntry.getTransactionLogEntryId());
    }

    @Test
    public void testGetterSetterStatus() {
        String testStatus = "testStatus";
        transactionLogEntry.setStatus(testStatus);
        Assertions.assertEquals(testStatus, transactionLogEntry.getStatus());
    }

    @Test
    public void testGetterSetterRqstDate() {
        String testRqstDate = "testRqstDate";
        transactionLogEntry.setRqstDate(testRqstDate);
        Assertions.assertEquals(testRqstDate, transactionLogEntry.getRqstDate());
    }

    @Test
    public void testGetterSetterRespDate() {
        String testRespDate = "testRespDate";
        transactionLogEntry.setRespDate(testRespDate);
        Assertions.assertEquals(testRespDate, transactionLogEntry.getRespDate());
    }

    @Test
    public void testGetterSetterSourceId() {
        String testSourceId = "testSourceId";
        transactionLogEntry.setSourceId(testSourceId);
        Assertions.assertEquals(testSourceId, transactionLogEntry.getSourceId());
    }

    @Test
    public void testGetterSetterResourceId() {
        String testResourceId = "testResourceId";
        transactionLogEntry.setResourceId(testResourceId);
        Assertions.assertEquals(testResourceId, transactionLogEntry.getResourceId());
    }

    @Test
    public void testGetterSetterResourceType() {
        String testResourceType = "testResourceType";
        transactionLogEntry.setResourceType(testResourceType);
        Assertions.assertEquals(testResourceType, transactionLogEntry.getResourceType());
    }

    @Test
    public void testGetterSetterRqstBuf() {
        String testRqstBuf = "testRqstBuf";
        transactionLogEntry.setRqstBuf(testRqstBuf);
        Assertions.assertEquals(testRqstBuf, transactionLogEntry.getRqstBuf());
    }

    @Test
    public void testGetterSetterRespBuf() {
        String testRespBuf = "testRespBuf";
        transactionLogEntry.setrespBuf(testRespBuf);
        Assertions.assertEquals(testRespBuf, transactionLogEntry.getrespBuf());
    }

    @Test
    public void testGetterSetterNotificationPayload() {
        String testNotificationPayload = "testNotificationPayload";
        transactionLogEntry.setNotificationPayload(testNotificationPayload);
        Assertions.assertEquals(testNotificationPayload, transactionLogEntry.getNotificationPayload());
    }

    @Test
    public void testGetterSetterNotificationId() {
        String testNotificationId = "testNotificationId";
        transactionLogEntry.setNotificationId(testNotificationId);
        Assertions.assertEquals(testNotificationId, transactionLogEntry.getNotificationId());
    }

    @Test
    public void testGetterSetterNotificationStatus() {
        String testNotificationStatus = "testNotificationStatus";
        transactionLogEntry.setNotificationStatus(testNotificationStatus);
        Assertions.assertEquals(testNotificationStatus, transactionLogEntry.getNotificationStatus());
    }

    @Test
    public void testGetterSetterNotificationTopic() {
        String testNotificationTopic = "testNotificationTopic";
        transactionLogEntry.setNotificationTopic(testNotificationTopic);
        Assertions.assertEquals(testNotificationTopic, transactionLogEntry.getNotificationTopic());
    }

    @Test
    public void testGetterSetterNotificationEntityLink() {
        String testNotificationEntityLink = "testNotificationEntityLink";
        transactionLogEntry.setNotificationEntityLink(testNotificationEntityLink);
        Assertions.assertEquals(testNotificationEntityLink, transactionLogEntry.getNotificationEntityLink());
    }

    @Test
    public void testGetterSetterNotificationAction() {
        String testNotificationAction = "testNotificationAction";
        transactionLogEntry.setNotificationAction(testNotificationAction);
        Assertions.assertEquals(testNotificationAction, transactionLogEntry.getNotificationAction());
    }
}
