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

import static org.junit.Assert.assertEquals;

import java.io.UnsupportedEncodingException;
import java.net.URI;

import javax.annotation.PostConstruct;
import javax.ws.rs.core.UriBuilder;
import javax.xml.bind.JAXBException;

import org.junit.Test;
import org.onap.aai.AAISetup;
import org.onap.aai.exceptions.AAIException;
import org.onap.aai.introspection.Loader;
import org.onap.aai.introspection.ModelType;
import org.onap.aai.restcore.HttpMethod;
import org.onap.aai.setup.SchemaVersion;

public class URIToExtensionInformationTest extends AAISetup {

    private Loader specificLoader;

    /**
     * Vservers V 7.
     *
     * @throws JAXBException the JAXB exception
     * @throws AAIException the AAI exception
     * @throws IllegalArgumentException the illegal argument exception
     * @throws UnsupportedEncodingException the unsupported encoding exception
     */

    @PostConstruct
    public void createLoader() {
        specificLoader = loaderFactory.createLoaderForVersion(ModelType.MOXY, new SchemaVersion("v10"));
    }

    @Test
    public void vserversV8()
            throws JAXBException, AAIException, IllegalArgumentException, UnsupportedEncodingException {
        URI uri = UriBuilder.fromPath("/aai/" + specificLoader.getVersion()
                + "/cloud-infrastructure/cloud-regions/cloud-region/testOwner1/testRegion1/tenants/tenant/key1/vservers/vserver/key2")
                .build();
        URIToExtensionInformation parse = new URIToExtensionInformation(specificLoader, uri);

        String namespace = "cloudInfrastructure";
        String preMethodName =
                "DynamicAddCloudInfrastructureCloudRegionsCloudRegionTenantsTenantVserversVserverPreProc";
        String postMethodName =
                "DynamicAddCloudInfrastructureCloudRegionsCloudRegionTenantsTenantVserversVserverPostProc";
        String topLevel = "CloudRegion";
        testSpec(parse, HttpMethod.PUT, namespace, preMethodName, postMethodName, topLevel);

    }

    /**
     * Test spec.
     *
     * @param info the info
     * @param httpMethod the http method
     * @param namespace the namespace
     * @param preMethodName the pre method name
     * @param postMethodName the post method name
     * @param topLevel the top level
     */
    private void testSpec(URIToExtensionInformation info, HttpMethod httpMethod, String namespace, String preMethodName,
            String postMethodName, String topLevel) {

        String namespaceResult = info.getNamespace();
        String methodNameResult = info.getMethodName(httpMethod, true);

        assertEquals("namespace", namespace, namespaceResult);
        assertEquals("preprocess method name", preMethodName, methodNameResult);
        methodNameResult = info.getMethodName(httpMethod, false);

        assertEquals("postprocess method name", postMethodName, methodNameResult);

        String topLevelResult = info.getTopObject();

        assertEquals("topLevel", topLevel, topLevelResult);
    }

}
