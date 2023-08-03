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

package org.onap.aai.parsers.uri;

import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;

import javax.annotation.PostConstruct;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.UriBuilder;
import javax.xml.bind.JAXBException;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.onap.aai.AAISetup;
import org.onap.aai.edges.enums.EdgeType;
import org.onap.aai.exceptions.AAIException;
import org.onap.aai.introspection.Introspector;
import org.onap.aai.introspection.Loader;
import org.onap.aai.introspection.ModelType;
import org.onap.aai.introspection.exceptions.AAIUnknownObjectException;
import org.onap.aai.setup.SchemaVersion;


public class URIParserTest extends AAISetup {

    private Loader loader;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    /**
     * Invalid path.
     *
     * @throws JAXBException the JAXB exception
     * @throws AAIException the AAI exception
     * @throws IllegalArgumentException the illegal argument exception
     * @throws UnsupportedEncodingException the unsupported encoding exception
     */
    @PostConstruct
    public void createLoader() {
        loader = loaderFactory.createLoaderForVersion(ModelType.MOXY, new SchemaVersion("v10"));
    }

    @Test
    public void invalidPath()
            throws JAXBException, AAIException, IllegalArgumentException, UnsupportedEncodingException {
        URI uri =
                UriBuilder
                        .fromPath("/aai/" + loader.getVersion()
                                + "/network/tenants/tenant/key1/vservers/vserver/key2/l-interfaces/l-interface/key3")
                        .build();

        thrown.expect(AAIException.class);
        thrown.expect(hasProperty("code", is("AAI_3001")));

        new URIToDBKey(loader, uri);
    }

    /**
     * Invalid path no name space.
     *
     * @throws JAXBException the JAXB exception
     * @throws AAIException the AAI exception
     * @throws IllegalArgumentException the illegal argument exception
     * @throws UnsupportedEncodingException the unsupported encoding exception
     */
    @Test
    public void invalidPathNoNameSpace()
            throws JAXBException, AAIException, IllegalArgumentException, UnsupportedEncodingException {
        URI uri = UriBuilder.fromPath("/aai/" + loader.getVersion()
                + "/tenants/tenant/key1/vservers/vserver/key2/l-interfaces/l-interface/key3").build();

        thrown.expect(AAIException.class);
        thrown.expect(hasProperty("code", is("AAI_3000")));

        new URIToDBKey(loader, uri);
    }

    /**
     * Invalid path partial.
     *
     * @throws JAXBException the JAXB exception
     * @throws AAIException the AAI exception
     * @throws IllegalArgumentException the illegal argument exception
     * @throws UnsupportedEncodingException the unsupported encoding exception
     */
    @Test
    public void invalidPathPartial()
            throws JAXBException, AAIException, IllegalArgumentException, UnsupportedEncodingException {
        URI uri = UriBuilder.fromPath("vservers/vserver/key2/l-interfaces/l-interface/key3").build();

        thrown.expect(AAIException.class);
        thrown.expect(hasProperty("code", is("AAI_3000")));

        new URIToDBKey(loader, uri);
    }

    @Test
    public void verifyParsableInvokation() throws UnsupportedEncodingException, AAIException {
        ArgumentCaptor<Introspector> cloudInfrastructureArgument = ArgumentCaptor.forClass(Introspector.class);
        ArgumentCaptor<Introspector> containersArgument = ArgumentCaptor.forClass(Introspector.class);
        ArgumentCaptor<Introspector> objectArgument = ArgumentCaptor.forClass(Introspector.class);
        URI uri = UriBuilder.fromPath("cloud-infrastructure/complexes/complex/key1/ctag-pools/ctag-pool/key2/key3")
        .build();
        Parsable parsable = mock(Parsable.class);

        URIParser uriParser = new URIParser(loader, uri);
        uriParser.parse(parsable);

        verify(parsable).processNamespace(cloudInfrastructureArgument.capture());
        verify(parsable, times(2)).processContainer(containersArgument.capture(),eq(EdgeType.TREE),eq(new MultivaluedHashMap<>()),eq(false));
        verify(parsable, times(2)).processObject(objectArgument.capture(),eq(EdgeType.TREE),eq(new MultivaluedHashMap<>()));
        assertEquals("cloud-infrastructure", cloudInfrastructureArgument.getValue().getName());
        assertEquals("complexes", containersArgument.getAllValues().get(0).getName());
        assertEquals("ctag-pools", containersArgument.getAllValues().get(1).getName());
        assertEquals("complex", objectArgument.getAllValues().get(0).getName());
        assertEquals("ctag-pool", objectArgument.getAllValues().get(1).getName());
    }
}
