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
import org.onap.aai.introspection.*;
import org.onap.aai.setup.SchemaVersion;

import javax.annotation.PostConstruct;
import javax.ws.rs.core.UriBuilder;
import javax.xml.bind.JAXBException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;

import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertTrue;

public class URIToRelationshipObjectTest extends AAISetup {

    private SchemaVersion latest ;
    private Loader loader;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @PostConstruct
    public void createLoader(){
        latest = schemaVersions.getDefaultVersion();
        loader = loaderFactory.createLoaderForVersion(ModelType.MOXY, latest);
    }
    /**
     * Uri.
     *
     * @throws JAXBException the JAXB exception
     * @throws AAIException the AAI exception
     * @throws IllegalArgumentException the illegal argument exception
     * @throws UnsupportedEncodingException the unsupported encoding exception
     * @throws URISyntaxException 
     * @throws MalformedURLException the malformed URL exception
     */
    @Test
    public void uri() throws JAXBException, AAIException, IllegalArgumentException, UnsupportedEncodingException, URISyntaxException {
        
        URI uri = UriBuilder.fromPath("/aai/" + loader.getVersion() + "/cloud-infrastructure/cloud-regions/cloud-region/mycloudregionowner/mycloudregionid/tenants/tenant/key1/vservers/vserver/key2/l-interfaces/l-interface/key3").build();
        URIToRelationshipObject parse = new URIToRelationshipObject(loader, uri);
        Introspector result = parse.getResult();
        String expected = "\\{\"related-to\":\"l-interface\",\"related-link\":\"/aai/" + latest + "/cloud-infrastructure/cloud-regions/cloud-region/mycloudregionowner/mycloudregionid/tenants/tenant/key1/vservers/vserver/key2/l-interfaces/l-interface/key3\",\"relationship-data\":\\[\\{\"relationship-key\":\"cloud-region.cloud-owner\",\"relationship-value\":\"mycloudregionowner\"\\},\\{\"relationship-key\":\"cloud-region.cloud-region-id\",\"relationship-value\":\"mycloudregionid\"\\},\\{\"relationship-key\":\"tenant.tenant-id\",\"relationship-value\":\"key1\"\\},\\{\"relationship-key\":\"vserver.vserver-id\",\"relationship-value\":\"key2\"\\},\\{\"relationship-key\":\"l-interface.interface-name\",\"relationship-value\":\"key3\"\\}\\]\\}";
        assertTrue("blah", result.marshal(false).matches(expected));
        
    }
    
    /**
     * Uri no version.
     *
     * @throws JAXBException the JAXB exception
     * @throws AAIException the AAI exception
     * @throws IllegalArgumentException the illegal argument exception
     * @throws UnsupportedEncodingException the unsupported encoding exception
     * @throws URISyntaxException 
     * @throws MalformedURLException the malformed URL exception
     */
    @Test
    public void uriNoVersion() throws JAXBException, AAIException, IllegalArgumentException, UnsupportedEncodingException, URISyntaxException {
        URI uri = UriBuilder.fromPath("/cloud-infrastructure/cloud-regions/cloud-region/mycloudregionowner/mycloudregionid/tenants/tenant/key1/vservers/vserver/key2/l-interfaces/l-interface/key3").build();
        URIToRelationshipObject parse = new URIToRelationshipObject(loader, uri);
        Introspector result = parse.getResult();
        String expected = "\\{\"related-to\":\"l-interface\",\"related-link\":\"/aai/" + latest + "/cloud-infrastructure/cloud-regions/cloud-region/mycloudregionowner/mycloudregionid/tenants/tenant/key1/vservers/vserver/key2/l-interfaces/l-interface/key3\",\"relationship-data\":\\[\\{\"relationship-key\":\"cloud-region.cloud-owner\",\"relationship-value\":\"mycloudregionowner\"\\},\\{\"relationship-key\":\"cloud-region.cloud-region-id\",\"relationship-value\":\"mycloudregionid\"\\},\\{\"relationship-key\":\"tenant.tenant-id\",\"relationship-value\":\"key1\"\\},\\{\"relationship-key\":\"vserver.vserver-id\",\"relationship-value\":\"key2\"\\},\\{\"relationship-key\":\"l-interface.interface-name\",\"relationship-value\":\"key3\"\\}\\]\\}";
        assertTrue("blah", result.marshal(false).matches(expected));

        
    }

    /**
     * Double key relationship.
     *
     * @throws JAXBException the JAXB exception
     * @throws AAIException the AAI exception
     * @throws IllegalArgumentException the illegal argument exception
     * @throws UnsupportedEncodingException the unsupported encoding exception
     * @throws URISyntaxException 
     * @throws MalformedURLException the malformed URL exception
     */
    @Test
    public void doubleKeyRelationship() throws JAXBException, AAIException, IllegalArgumentException, UnsupportedEncodingException, URISyntaxException {
        URI uri = UriBuilder.fromPath("/aai/" + latest + "/cloud-infrastructure/complexes/complex/key1/ctag-pools/ctag-pool/key2/key3/").build();
        URIToRelationshipObject parse = new URIToRelationshipObject(loader, uri);
        Introspector result = parse.getResult();
        String expected = "\\{\"related-to\":\"ctag-pool\",\"related-link\":\"/aai/" + latest + "/cloud-infrastructure/complexes/complex/key1/ctag-pools/ctag-pool/key2/key3\",\"relationship-data\":\\[\\{\"relationship-key\":\"complex.physical-location-id\",\"relationship-value\":\"key1\"\\},\\{\"relationship-key\":\"ctag-pool.target-pe\",\"relationship-value\":\"key2\"\\},\\{\"relationship-key\":\"ctag-pool.availability-zone-name\",\"relationship-value\":\"key3\"\\}\\]\\}";
        assertTrue("blah", result.marshal(false).matches(expected));

    }
    
    /**
     * Uri with non string key.
     *
     * @throws JAXBException the JAXB exception
     * @throws AAIException the AAI exception
     * @throws IllegalArgumentException the illegal argument exception
     * @throws UnsupportedEncodingException the unsupported encoding exception
     * @throws URISyntaxException 
     * @throws MalformedURLException the malformed URL exception
     */
    @Test
    public void uriWithNonStringKey() throws JAXBException, AAIException, IllegalArgumentException, UnsupportedEncodingException, URISyntaxException {
        URI uri = UriBuilder.fromPath("/aai/" + latest + "/network/vces/vce/key1/port-groups/port-group/key2/cvlan-tags/cvlan-tag/144").build();
        URIToRelationshipObject parse = new URIToRelationshipObject(loader, uri);
        Introspector result = parse.getResult();
        String expected = "\\{\"related-to\":\"cvlan-tag\",\"related-link\":\"/aai/" + latest + "/network/vces/vce/key1/port-groups/port-group/key2/cvlan-tags/cvlan-tag/144\",\"relationship-data\":\\[\\{\"relationship-key\":\"vce.vnf-id\",\"relationship-value\":\"key1\"\\},\\{\"relationship-key\":\"port-group.interface-id\",\"relationship-value\":\"key2\"\\},\\{\"relationship-key\":\"cvlan-tag.cvlan-tag\",\"relationship-value\":\"144\"\\}\\]\\}";
        assertTrue("blah", result.marshal(false).matches(expected));
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
        URI uri = UriBuilder.fromPath("/aai/" + loader.getVersion() + "/cloud-infrastructure/cloud-regions/cloud-region/mycloudregionowner/mycloudregionid/tenants/tenant/key1/vservers/vserver/key2/l-interadsfaces/l-interface/key3").build();
        
        thrown.expect(AAIException.class);
        thrown.expect(hasProperty("code",  is("AAI_3000")));
        
        URIToObject parse = new URIToObject(loader, uri);
        
    }
}
