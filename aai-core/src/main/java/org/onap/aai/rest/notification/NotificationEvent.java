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

package org.onap.aai.rest.notification;

import org.onap.aai.exceptions.AAIException;
import org.onap.aai.introspection.Introspector;
import org.onap.aai.introspection.Loader;
import org.onap.aai.setup.SchemaVersion;
import org.onap.aai.util.StoreNotificationEvent;

/**
 * The Class NotificationEvent.
 */
public class NotificationEvent {

    private final Loader loader;

    private final Introspector eventHeader;

    private final Introspector obj;
    private final String transactionId;
    private final String sourceOfTruth;

    /**
     * Instantiates a new notification event.
     *
     * @param eventHeader the event header
     * @param obj the obj
     */
    public NotificationEvent(Loader loader, Introspector eventHeader, Introspector obj, String transactionId,
            String sourceOfTruth) {
        this.loader = loader;
        this.eventHeader = eventHeader;
        this.obj = obj;
        this.transactionId = transactionId;
        this.sourceOfTruth = sourceOfTruth;
    }

    /**
     * Trigger.
     *
     * @throws AAIException the AAI exception
     */
    public void trigger() throws AAIException {

        StoreNotificationEvent sne = new StoreNotificationEvent(transactionId, sourceOfTruth);

        sne.storeEventAndSendToJms(loader, eventHeader, obj);

    }

    public String getNotificationEvent() throws AAIException {
        return new StoreNotificationEvent(transactionId, sourceOfTruth).storeEventOnly(loader, eventHeader, obj);
    }

    /**
     * Gets the notification version.
     *
     * @return the notification version
     */
    public SchemaVersion getNotificationVersion() {
        return loader.getVersion();
    }

    /**
     * Gets the event header.
     *
     * @return the event header
     */
    public Introspector getEventHeader() {
        return eventHeader;
    }

    /**
     * Gets the obj.
     *
     * @return the obj
     */
    public Introspector getObj() {
        return obj;
    }

}
