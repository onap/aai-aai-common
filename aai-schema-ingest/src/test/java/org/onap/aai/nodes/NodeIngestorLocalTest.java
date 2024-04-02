/*
 * ============LICENSE_START=======================================================
 * org.onap.aai
 * ================================================================================
 * Copyright © 2017-18 AT&T Intellectual Property. All rights reserved.
 * ================================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ============LICENSE_END=========================================================
 */

package org.onap.aai.nodes;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.xml.bind.SchemaOutputResolver;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.eclipse.persistence.dynamic.DynamicEntity;
import org.eclipse.persistence.jaxb.dynamic.DynamicJAXBContext;
import org.junit.jupiter.api.Test;
import org.onap.aai.config.NodesConfiguration;
import org.onap.aai.setup.SchemaVersion;
import org.onap.aai.testutils.TestUtilConfigTranslator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.w3c.dom.Document;

@TestPropertySource(
        properties = {
                "schema.ingest.file = src/test/resources/forWiringTests/schema-ingest-wiring-test-local-node.properties"})
@ContextConfiguration(classes = {TestUtilConfigTranslator.class, NodesConfiguration.class})

@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)

@SpringBootTest
public class NodeIngestorLocalTest {
    @Autowired
    NodeIngestor nodeIngestor;

    public static void printDocument(Document doc, OutputStream out) throws TransformerException {
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer = tf.newTransformer();
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
        transformer.setOutputProperty(OutputKeys.METHOD, "xml");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

        transformer.transform(new DOMSource(doc), new StreamResult(new OutputStreamWriter(out, UTF_8)));
    }

    @Test
    public void testGetContextForVersion11() {
        assertThrows(IllegalArgumentException.class, () -> {
            DynamicJAXBContext ctx10 = nodeIngestor.getContextForVersion(new SchemaVersion("v10"));

            // should work bc Foo is valid in test_network_v10 schema
            DynamicEntity foo10 = ctx10.newDynamicEntity("Foo");

            foo10.set("fooId", "bar");
            assertEquals("bar", foo10.get("fooId"));

            // should work bc Bar is valid in test_business_v10 schema
            DynamicEntity bar10 = ctx10.newDynamicEntity("Bar");
            bar10.set("barId", "bar2");
            assertEquals("bar2", bar10.get("barId"));
            XSDOutputResolver outputResolver10 = new XSDOutputResolver();
            ctx10.generateSchema(outputResolver10);

            DynamicJAXBContext ctx11 = nodeIngestor.getContextForVersion(new SchemaVersion("v11"));

            // should work bc Foo.quantity is valid in test_network_v11 schema
            DynamicEntity foo11 = ctx11.newDynamicEntity("Foo");
            foo11.set("quantity", "12");
            assertEquals("12", foo11.get("quantity"));

            DynamicEntity quux11 = ctx11.newDynamicEntity("Quux");
            quux11.set("qManagerName", "some guy");
            assertEquals("some guy", quux11.get("qManagerName"));
            XSDOutputResolver outputResolver11 = new XSDOutputResolver();
            ctx11.generateSchema(outputResolver11);
            // should fail bc Quux not in v10 test schema
            ctx10.newDynamicEntity("Quux");
        });
    }

    @Test
    public void testHasNodeType() {
        assertTrue(nodeIngestor.hasNodeType("foo", new SchemaVersion("v11")));
        assertTrue(nodeIngestor.hasNodeType("quux", new SchemaVersion("v11")));
        assertFalse(nodeIngestor.hasNodeType("quux", new SchemaVersion("v10")));
    }

    @Test
    public void testGetVersionFromClassName() {
        assertEquals(nodeIngestor.getVersionFromClassName("inventory.aai.onap.org.v13.Evc"), new SchemaVersion("v13"));
    }

    @Test
    public void testGetVersionFromClassNameNull() {
        assertEquals(nodeIngestor.getVersionFromClassName("blah"), new SchemaVersion("v15"));
    }

    @Test
    public void testGetObjectsInVersion() {
        assertEquals(nodeIngestor.getObjectsInVersion(new SchemaVersion("v13")).size(), 148);
    }

    @Test
    public void testCombinedSchema() throws TransformerException, IOException {
        DynamicJAXBContext ctx13 = nodeIngestor.getContextForVersion(new SchemaVersion("v13"));
        XSDOutputResolver outputResolver13 = new XSDOutputResolver();
        ctx13.generateSchema(outputResolver13);
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        printDocument(nodeIngestor.getSchema(new SchemaVersion("v13")), buffer);
        String content = new String(Files.readAllBytes(Paths.get("src/test/resources/forWiringTests/aai_oxm_v13.xml")));
        content = content.replaceAll("\\s+", "");
        String expected = buffer.toString().replaceAll("\\s+", "");

        assertThat("OXM:\n" + expected, expected, is(content));
    }

    private static class XSDOutputResolver extends SchemaOutputResolver {

        @Override
        public Result createOutput(String namespaceUri, String suggestedFileName) throws IOException {

            // create new file
            // create stream result
            File temp = File.createTempFile("schema", ".xsd");
            StreamResult result = new StreamResult(temp);
            System.out.println("Schema file: " + temp.getAbsolutePath());

            // set system id
            result.setSystemId(temp.toURI().toURL().toString());

            // return result
            return result;
        }
    }
}
