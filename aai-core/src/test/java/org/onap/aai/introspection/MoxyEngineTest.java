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

package org.onap.aai.introspection;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;

import org.eclipse.persistence.dynamic.DynamicEntity;
import org.eclipse.persistence.jaxb.dynamic.DynamicJAXBContext;
import org.junit.Assert;
import org.junit.Test;
import org.onap.aai.introspection.exceptions.AAIUnknownObjectException;
import org.onap.aai.introspection.exceptions.AAIUnmarshallingException;
import org.onap.aai.setup.SchemaVersion;
import org.springframework.test.annotation.DirtiesContext;

@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
public class MoxyEngineTest extends IntrospectorTestSpec {

    @Test
    public void castValueAccordingToSchemaTest() throws AAIUnknownObjectException {

        Loader loader = loaderFactory.createLoaderForVersion(ModelType.MOXY, schemaVersions.getDepthVersion());
        Introspector introspector = loader.introspectorFromName("pserver");
        Object test1 = "name1";
        Object result = introspector.castValueAccordingToSchema("hostname", test1);
        Assert.assertTrue(result instanceof java.lang.String);

        Object test2 = "4";
        Object result2 = introspector.castValueAccordingToSchema("number-of-cpus", test2);
        Assert.assertTrue(result2 instanceof java.lang.Integer);
    }

    /**
     * Container object.
     *
     * @throws AAIUnknownObjectException
     */
    @Test
    public void containerObject() throws AAIUnknownObjectException {

        Loader loader = loaderFactory.createLoaderForVersion(ModelType.MOXY, schemaVersions.getDepthVersion());
        Introspector obj = loader.introspectorFromName("port-groups");
        this.containerTestSet(obj);
        Assert.assertTrue(true);
    }

    @Test
    public void testDslStartNodeProps() throws AAIUnknownObjectException {
        Loader loader = loaderFactory.createLoaderForVersion(ModelType.MOXY, schemaVersions.getDepthVersion());
        Introspector obj = loader.introspectorFromName("pserver");
        Assert.assertFalse(obj.getDslStartNodeProperties().contains("in-maint"));
        Assert.assertTrue(obj.getDslStartNodeProperties().contains("pserver-name2"));

    }

    @Test
    public void testDslStartNodePropsDefault() throws AAIUnknownObjectException {
        /*
         * Use indexedprops when there is no dslStartNodeProps
         */
        Loader loader = loaderFactory.createLoaderForVersion(ModelType.MOXY, schemaVersions.getDepthVersion());
        Introspector obj = loader.introspectorFromName("vserver");
        Assert.assertTrue(obj.getDslStartNodeProperties().contains("in-maint"));
    }

    @Test
    public void thatObjectsCanBeUnmarshalled() throws IOException, AAIUnmarshallingException {
        Loader loader = loaderFactory.getMoxyLoaderInstance().get(new SchemaVersion("v14"));
        String xmlModelPayload = new String(Files.readAllBytes(Paths.get("src/test/resources/payloads/resource/network-service.xml")));
        Introspector obj = loader.unmarshal("model", xmlModelPayload,
                org.onap.aai.restcore.MediaType.APPLICATION_XML_TYPE);

        assertEquals("d821d1aa-8a69-47a4-aa63-3dae1742c47c", obj.get("modelInvariantId"));
        assertEquals("service", obj.get("modelType"));
    }

    @Test
    public void moxyTest() throws IOException, JAXBException{
        String xmlModelPayload = new String(Files.readAllBytes(Paths.get("src/test/resources/payloads/resource/network-service.xml")));
        DynamicJAXBContext jaxbContext = nodeIngestor.getContextForVersion(new SchemaVersion("v14"));
        final Object clazz = jaxbContext.newDynamicEntity("Model");
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        DynamicEntity entity = (DynamicEntity)unmarshaller.unmarshal(new StreamSource(new StringReader(xmlModelPayload)), clazz.getClass()).getValue();
        assertEquals("d821d1aa-8a69-47a4-aa63-3dae1742c47c", entity.get("modelInvariantId"));
        assertEquals("service", entity.get("modelType"));
    }

}
