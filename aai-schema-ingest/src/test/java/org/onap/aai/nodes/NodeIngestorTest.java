/** 
 * ============LICENSE_START=======================================================
 * org.onap.aai
 * ================================================================================
 * Copyright © 2017-18 AT&T Intellectual Property. All rights reserved.
 * ================================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ============LICENSE_END=========================================================
 */

package org.onap.aai.nodes;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
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
import org.eclipse.persistence.jaxb.JAXBContext;
import org.eclipse.persistence.jaxb.dynamic.DynamicJAXBContext;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.onap.aai.setup.SchemaLocationsBean;
import org.onap.aai.setup.SchemaVersion;
import org.onap.aai.setup.SchemaVersions;
import org.onap.aai.testutils.TestUtilConfigTranslator;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.w3c.dom.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {SchemaLocationsBean.class, SchemaVersions.class, TestUtilConfigTranslator.class, NodeIngestor.class})
@TestPropertySource(properties = { "schema.ingest.file = src/test/resources/forWiringTests/schema-ingest-wiring-test.properties" })
@SpringBootTest
public class NodeIngestorTest {
    @Autowired
    NodeIngestor ni;
    
    //set thrown.expect to whatever a specific test needs
    //this establishes a default of expecting no exceptions to be thrown
    @Rule
    public ExpectedException thrown = ExpectedException.none();
    
    @Test
    public void testGetContextForVersion() {
        DynamicJAXBContext ctx10 = ni.getContextForVersion(new SchemaVersion("v10"));
        
        //should work bc Foo is valid in test_network_v10 schema
        DynamicEntity foo10 = ctx10.newDynamicEntity("Foo");

        foo10.set("fooId","bar");
        assertTrue("bar".equals(foo10.get("fooId")));
        
        //should work bc Bar is valid in test_business_v10 schema
        DynamicEntity bar10 = ctx10.newDynamicEntity("Bar");
        bar10.set("barId","bar2");
        assertTrue("bar2".equals(bar10.get("barId")));
        XSDOutputResolver outputResolver10 = new XSDOutputResolver();
        ctx10.generateSchema(outputResolver10);
        
        DynamicJAXBContext ctx11 = ni.getContextForVersion(new SchemaVersion("v11"));
        
        //should work bc Foo.quantity is valid in test_network_v11 schema
        DynamicEntity foo11 = ctx11.newDynamicEntity("Foo");
        foo11.set("quantity","12");
        assertTrue("12".equals(foo11.get("quantity")));
        
        DynamicEntity quux11 = ctx11.newDynamicEntity("Quux");
        quux11.set("qManagerName","some guy");
        assertTrue("some guy".equals(quux11.get("qManagerName")));
        XSDOutputResolver outputResolver11 = new XSDOutputResolver();
        ctx11.generateSchema(outputResolver11);

        
        thrown.expect(IllegalArgumentException.class);
        //should fail bc Quux not in v10 test schema
        ctx10.newDynamicEntity("Quux");
    }
    
    @Test
    public void testHasNodeType() {
        assertTrue(ni.hasNodeType("foo", new SchemaVersion("v11")));
        assertTrue(ni.hasNodeType("quux", new SchemaVersion("v11")));
        assertFalse(ni.hasNodeType("quux", new SchemaVersion("v10")));
    }
    @Test
    public void testCombinedSchema() throws TransformerException, IOException {
        DynamicJAXBContext ctx13 = ni.getContextForVersion(new SchemaVersion("v13"));
        XSDOutputResolver outputResolver13 = new XSDOutputResolver();
        ctx13.generateSchema(outputResolver13);
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        printDocument(ni.getSchema(new SchemaVersion("v13")),buffer);
        String content = new String(Files.readAllBytes(Paths.get("src/test/resources/forWiringTests/aai_oxm_v13.xml")));
        content = content.replaceAll("\\s+", "");
        String expected = buffer.toString().replaceAll("\\s+", "");
        assertThat("OXM:\n"+expected,expected, is(content));
    }

    public static void printDocument(Document doc, OutputStream out) throws IOException, TransformerException {
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer = tf.newTransformer();
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
        transformer.setOutputProperty(OutputKeys.METHOD, "xml");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

        transformer.transform(new DOMSource(doc), 
             new StreamResult(new OutputStreamWriter(out, "UTF-8")));
    }
    
    private class XSDOutputResolver extends SchemaOutputResolver {

           @Override
           public Result createOutput(String namespaceUri, String suggestedFileName)
              throws IOException {
              
              // create new file
              // create stream result
              File temp = File.createTempFile("schema", ".xsd"); 
              StreamResult result = new StreamResult(temp);
              System.out.println("Schema file: "+temp.getAbsolutePath());
              
              // set system id
              result.setSystemId(temp.toURI().toURL().toString());
              
              // return result
              return result;
           }
    }
}


