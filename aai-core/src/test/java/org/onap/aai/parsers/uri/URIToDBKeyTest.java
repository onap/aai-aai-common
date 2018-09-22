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

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.onap.aai.AAISetup;
import org.onap.aai.exceptions.AAIException;
import org.onap.aai.parsers.exceptions.DoesNotStartWithValidNamespaceException;
import org.onap.aai.db.props.AAIProperties;
import org.onap.aai.introspection.*;

import javax.annotation.PostConstruct;
import javax.ws.rs.core.UriBuilder;
import javax.xml.bind.JAXBException;
import java.io.UnsupportedEncodingException;
import java.net.URI;

import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;


public class URIToDBKeyTest extends AAISetup {

    private Loader loader ;

    @Rule
    public ExpectedException thrown = ExpectedException.none();
    
    /**
     * Uri.
     *
     * @throws JAXBException the JAXB exception
     * @throws AAIException the AAI exception
     * @throws IllegalArgumentException the illegal argument exception
     * @throws UnsupportedEncodingException the unsupported encoding exception
     */
    @PostConstruct
    public void createLoader(){
        loader = loaderFactory.createLoaderForVersion(ModelType.MOXY, schemaVersions.getDefaultVersion());
    }
    
    @Test
    public void uri() throws JAXBException, AAIException, IllegalArgumentException, UnsupportedEncodingException {
        URI uri = UriBuilder.fromPath("/aai/" + loader.getVersion() + "/cloud-infrastructure/cloud-regions/cloud-region/cloudOwner-key/cloudRegion-key/tenants/tenant/tenantId-key/vservers/vserver/vserverId-key/l-interfaces/l-interface/key3").build();
        URIToDBKey parse = new URIToDBKey(loader, uri);
        Object result = parse.getResult();

        String expected = "cloud-region/tenant/vserver/l-interface";
        
        assertEquals("blah", expected, result);
        
    }
    
    /**
     * Uri no version.
     *
     * @throws JAXBException the JAXB exception
     * @throws AAIException the AAI exception
     * @throws IllegalArgumentException the illegal argument exception
     * @throws UnsupportedEncodingException the unsupported encoding exception
     */
    @Test
    public void uriNoVersion() throws JAXBException, AAIException, IllegalArgumentException, UnsupportedEncodingException {
        URI uri = UriBuilder.fromPath("/cloud-infrastructure/cloud-regions/cloud-region/cloudOwner-key/cloudRegion-key/tenants/tenant/tenantId-key/vservers/vserver/vserverId-key/l-interfaces/l-interface/key3").build();
        URIToDBKey parse = new URIToDBKey(loader, uri);
        Object result = parse.getResult();
        
        String expected = "cloud-region/tenant/vserver/l-interface";
        
        assertEquals("blah", expected, result);
        
    }
    

    /**
     * Bad URI.
     *
     * @throws JAXBException the JAXB exception
     * @throws AAIException the AAI exception
     * @throws IllegalArgumentException the illegal argument exception
     * @throws UnsupportedEncodingException the unsupported encoding exception
     */
    @Test
    public void badURI() throws JAXBException, AAIException, IllegalArgumentException, UnsupportedEncodingException {
        URI uri = UriBuilder.fromPath("/aai/" + loader.getVersion() + "/cloud-infrastructure/tenants/tenant/key1/vservers/vserver/key2/l-interadsfaces/l-interface/key3").build();
        
        thrown.expect(AAIException.class);
        thrown.expect(hasProperty("code",  is("AAI_3001")));
        
        new URIToDBKey(loader, uri);
    }
    
    /**
     * NotValid namespace.
     *
     * @throws JAXBException the JAXB exception
     * @throws DoesNotStartWithValidNamespaceException the AAI exception
     * @throws IllegalArgumentException the illegal argument exception
     * @throws UnsupportedEncodingException the unsupported encoding exception
     */
    @Test
    public void notValidNamespace() throws JAXBException, AAIException, IllegalArgumentException, UnsupportedEncodingException {
        URI uri = UriBuilder.fromPath("/cloud-region/cloud-regions/cloud-region/cloudOwner-key/cloudRegion-key/tenants/tenant/tenantId-key/vservers/vserver/vserverId-key/l-interfaces/l-interface/key3").build();
        thrown.expect(DoesNotStartWithValidNamespaceException.class);
        URIToDBKey parse = new URIToDBKey(loader, uri);
    }
    
    
    /**
     * No valid tokens.
     *
     * @throws JAXBException the JAXB exception
     * @throws AAIException the AAI exception
     * @throws IllegalArgumentException the illegal argument exception
     * @throws UnsupportedEncodingException the unsupported encoding exception
     */
    @Test
    public void noValidTokens() throws JAXBException, AAIException, IllegalArgumentException, UnsupportedEncodingException {
        URI uri = UriBuilder.fromPath("/aai/" + loader.getVersion() + "/cloud/blah/blah").build();
        
        thrown.expect(AAIException.class);
        thrown.expect(hasProperty("code",  is("AAI_3000")));
        
        new URIToDBKey(loader, uri);
    }
    
    /**
     * Starts with valid namespace.
     *
     * @throws JAXBException the JAXB exception
     * @throws AAIException the AAI exception
     * @throws IllegalArgumentException the illegal argument exception
     * @throws UnsupportedEncodingException the unsupported encoding exception
     */
    @Test
    public void startsWithValidNamespace() throws JAXBException, AAIException, IllegalArgumentException, UnsupportedEncodingException {
        URI uri = UriBuilder.fromPath("/aai/" + loader.getVersion() + "/cloud-infrastructure/cloud-regions/cloud-region/cloudOwner-key/cloudRegion-key/tenants/tenant/tenantId-key/vservers/vserver/vserverId-key/l-interfaces/l-interface/key3").build();
        
        URIToDBKey parse = new URIToDBKey(loader, uri);
        Object result = parse.getResult();

        String expected = "cloud-region/tenant/vserver/l-interface";
        
        assertEquals("blah", expected, result);
    }
    
    /**
     * Naming exceptions.
     *
     * @throws IllegalArgumentException the illegal argument exception
     * @throws AAIException the AAI exception
     * @throws UnsupportedEncodingException the unsupported encoding exception
     */
    @Test
    public void namingExceptions() throws IllegalArgumentException, AAIException, UnsupportedEncodingException {
        URI uri = UriBuilder.fromPath("network/vces/vce/key1/port-groups/port-group/key2/cvlan-tags/cvlan-tag/655").build();
        URIToDBKey parse = new URIToDBKey(loader, uri);
        Object result = parse.getResult();

        String expected = "vce/port-group/cvlan-tag";
        
        assertEquals("blah", expected, result);
        
    }
        
}
