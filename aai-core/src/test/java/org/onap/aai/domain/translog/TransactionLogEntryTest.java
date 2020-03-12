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

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.onap.aai.AAISetup;
import org.onap.aai.domain.model.AAIResource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TransactionLogEntryTest extends AAISetup {
    private TransactionLogEntry transactionLogEntry;

    @Before
    public void setUp() {
        transactionLogEntry = new TransactionLogEntry();
    }

    @Test
    public void testGetterSetterTransactionLogEntryId() {
        String testTransactionLogEntryId = "testTransactionLogEntryId";
        transactionLogEntry.setTransactionLogEntryId(testTransactionLogEntryId);
        Assert.assertEquals(testTransactionLogEntryId, transactionLogEntry.getTransactionLogEntryId());
    }

    @Test
    public void testGetterSetterStatus() {
        String testStatus = "testStatus";
        transactionLogEntry.setStatus(testStatus);
        Assert.assertEquals(testStatus, transactionLogEntry.getStatus());
    }

    @Test
    public void testGetterSetterRqstDate() {
        String testRqstDate = "testRqstDate";
        transactionLogEntry.setRqstDate(testRqstDate);
        Assert.assertEquals(testRqstDate, transactionLogEntry.getRqstDate());
    }

    @Test
    public void testGetterSetterRespDate() {
        String testRespDate = "testRespDate";
        transactionLogEntry.setRespDate(testRespDate);
        Assert.assertEquals(testRespDate, transactionLogEntry.getRespDate());
    }

    @Test
    public void testGetterSetterSourceId() {
        String testSourceId = "testSourceId";
        transactionLogEntry.setSourceId(testSourceId);
        Assert.assertEquals(testSourceId, transactionLogEntry.getSourceId());
    }

    @Test
    public void testGetterSetterResourceId() {
        String testResourceId = "testResourceId";
        transactionLogEntry.setResourceId(testResourceId);
        Assert.assertEquals(testResourceId, transactionLogEntry.getResourceId());
    }

    @Test
    public void testGetterSetterResourceType() {
        String testResourceType = "testResourceType";
        transactionLogEntry.setResourceType(testResourceType);
        Assert.assertEquals(testResourceType, transactionLogEntry.getResourceType());
    }

    @Test
    public void testGetterSetterRqstBuf() {
        String testRqstBuf = "testRqstBuf";
        transactionLogEntry.setRqstBuf(testRqstBuf);
        Assert.assertEquals(testRqstBuf, transactionLogEntry.getRqstBuf());
    }

    @Test
    public void testGetterSetterRespBuf() {
        String testRespBuf = "testRespBuf";
        transactionLogEntry.setrespBuf(testRespBuf);
        Assert.assertEquals(testRespBuf, transactionLogEntry.getrespBuf());
    }

    @Test
    public void testGetterSetterNotificationPayload() {
        String testNotificationPayload = "testNotificationPayload";
        transactionLogEntry.setNotificationPayload(testNotificationPayload);
        Assert.assertEquals(testNotificationPayload, transactionLogEntry.getNotificationPayload());
    }

    @Test
    public void testGetterSetterNotificationId() {
        String testNotificationId = "testNotificationId";
        transactionLogEntry.setNotificationId(testNotificationId);
        Assert.assertEquals(testNotificationId, transactionLogEntry.getNotificationId());
    }

    @Test
    public void testGetterSetterNotificationStatus() {
        String testNotificationStatus = "testNotificationStatus";
        transactionLogEntry.setNotificationStatus(testNotificationStatus);
        Assert.assertEquals(testNotificationStatus, transactionLogEntry.getNotificationStatus());
    }

    @Test
    public void testGetterSetterNotificationTopic() {
        String testNotificationTopic = "testNotificationTopic";
        transactionLogEntry.setNotificationTopic(testNotificationTopic);
        Assert.assertEquals(testNotificationTopic, transactionLogEntry.getNotificationTopic());
    }

    @Test
    public void testGetterSetterNotificationEntityLink() {
        String testNotificationEntityLink = "testNotificationEntityLink";
        transactionLogEntry.setNotificationEntityLink(testNotificationEntityLink);
        Assert.assertEquals(testNotificationEntityLink, transactionLogEntry.getNotificationEntityLink());
    }

    @Test
    public void testGetterSetterNotificationAction() {
        String testNotificationAction = "testNotificationAction";
        transactionLogEntry.setNotificationAction(testNotificationAction);
        Assert.assertEquals(testNotificationAction, transactionLogEntry.getNotificationAction());
    }
}
