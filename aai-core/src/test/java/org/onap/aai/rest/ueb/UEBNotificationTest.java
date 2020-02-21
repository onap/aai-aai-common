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

package org.onap.aai.rest.ueb;

import org.junit.Before;
import org.junit.Test;
import org.onap.aai.AAISetup;
import org.onap.aai.edges.EdgeIngestor;
import org.onap.aai.exceptions.AAIException;
import org.onap.aai.introspection.Introspector;
import org.onap.aai.introspection.Loader;
import org.onap.aai.introspection.ModelType;
import org.onap.aai.serialization.db.EdgeSerializer;
import org.onap.aai.serialization.engines.QueryStyle;
import org.onap.aai.setup.SchemaVersion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;

import javax.ws.rs.core.Response;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.UUID;

import static org.junit.Assert.assertEquals;

@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
public class UEBNotificationTest extends AAISetup {

    public static final String BASE_PATH = "/aai";
    @Autowired
    protected EdgeSerializer edgeSer;
    @Autowired
    protected EdgeIngestor ei;

    private SchemaVersion version;
    private final ModelType introspectorFactoryType = ModelType.MOXY;
    private Loader loader;

    public QueryStyle queryStyle = QueryStyle.TRAVERSAL_URI;


    @Before
    public void setup() throws Exception {
        version = schemaVersions.getDefaultVersion();
        loader = loaderFactory.createLoaderForVersion(introspectorFactoryType, version);
    }

    @Test
    public void verifyUriNoIssues() throws AAIException, URISyntaxException, UnsupportedEncodingException {

        Introspector pserver = loader.introspectorFromName("pserver");
        pserver.setValue("hostname", "hn");
        URI uri = new URI("/cloud-infrastructure/pservers/pserver/hn");
        UEBNotification uebNotification = new UEBNotification(loader, loaderFactory, schemaVersions);
        uebNotification.createNotificationEvent(
            UUID.randomUUID().toString(),
            "JUNIT-SOT",
            Response.Status.CREATED,
            uri,
            pserver,
            new HashMap<>(),
            BASE_PATH);

        assertEquals("One event created", 1, uebNotification.getEvents().size());
        assertEquals(
            "Uri is correct",
            BASE_PATH + "/" + schemaVersions.getDefaultVersion() + "/cloud-infrastructure/pservers/pserver/hn",
            uebNotification.getEvents().get(0).getEventHeader().getValue("entity-link").toString());
    }

    @Test
    public void verifyUriWithBaseAndUri() throws AAIException, URISyntaxException, UnsupportedEncodingException {

        Introspector pserver = loader.introspectorFromName("pserver");
        pserver.setValue("hostname", "hn");
        URI uri = new URI(BASE_PATH + "/v12/cloud-infrastructure/pservers/pserver/hn");
        UEBNotification uebNotification = new UEBNotification(loader, loaderFactory, schemaVersions);
        uebNotification.createNotificationEvent(
            UUID.randomUUID().toString(),
            "JUNIT-SOT",
            Response.Status.CREATED,
            uri,
            pserver,
            new HashMap<>(), BASE_PATH);

        assertEquals("One event created", 1, uebNotification.getEvents().size());
        assertEquals(
            "Uri is correct",
            BASE_PATH + "/" + schemaVersions.getDefaultVersion() + "/cloud-infrastructure/pservers/pserver/hn",
            uebNotification.getEvents().get(0).getEventHeader().getValue("entity-link").toString());
    }
}
