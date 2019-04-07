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
package org.onap.aai.util.genxsd;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.onap.aai.config.ConfigConfiguration;
import org.onap.aai.config.SwaggerGenerationConfiguration;
import org.onap.aai.setup.SchemaVersion;
import org.onap.aai.setup.SchemaVersions;
import org.junit.runner.RunWith;
import org.onap.aai.edges.EdgeIngestor;
import org.onap.aai.nodes.NodeIngestor;
import org.onap.aai.serialization.queryformats.QueryFormatTestHelper;
import org.onap.aai.setup.SchemaLocationsBean;
import org.onap.aai.testutils.TestUtilConfigTranslatorforBusiness;
import org.onap.aai.util.AAIConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.w3c.dom.Element;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {
		ConfigConfiguration.class,
        TestUtilConfigTranslatorforBusiness.class,
        EdgeIngestor.class,
        NodeIngestor.class,
		SwaggerGenerationConfiguration.class

})
@TestPropertySource(properties = {
		"schema.uri.base.path = /aai",
		"schema.xsd.maxoccurs = 5000"
})
public class HTMLfromOXMTest {
	private static final Logger logger = LoggerFactory.getLogger("HTMLfromOXMTest.class");
	private static final String OXMFILENAME = "src/test/resources/oxm/business_oxm_v11.xml";
	public static AnnotationConfigApplicationContext ctx = null;
	private static String testXML;
	protected static final String SERVICE_NAME = "JUNIT";
	
	
	@Autowired
	HTMLfromOXM htmlFromOxm;
	
	@Autowired
	SchemaVersions schemaVersions;
	
	@BeforeClass
	public static void setUpContext() throws Exception {
		
	}
	@BeforeClass
    public static void setupBundleconfig() throws Exception {
        System.setProperty("AJSC_HOME", ".");
        System.setProperty("BUNDLECONFIG_DIR", "src/test/resources/bundleconfig-local");
        System.setProperty("aai.service.name", SERVICE_NAME);
        QueryFormatTestHelper.setFinalStatic(AAIConstants.class.getField("AAI_HOME_ETC_OXM"), "src/test/resources/bundleconfig-local/etc/oxm/");
    }
	
	@Before
	public void setUp() throws Exception {
		setUp(0);    
	}
	
	public void setUp(int sbopt) throws Exception
	{
		XSDElementTest x = new XSDElementTest();
		x.setUp(sbopt);
		testXML = x.testXML;
		logger.debug(testXML);
		BufferedWriter bw = new BufferedWriter(new FileWriter(OXMFILENAME));
		bw.write(testXML);
		bw.close();
	}

	@Test
	public void testGetDocumentHeader() {
		SchemaVersion v = schemaVersions.getAppRootVersion();
		String header = null;
		try {
			htmlFromOxm.setXmlVersion(testXML, v);
			htmlFromOxm.setSchemaVersions(schemaVersions);
			header = htmlFromOxm.getDocumentHeader();
		} catch(Exception e) {
			e.printStackTrace();
		}
		logger.debug("Header:");
		logger.debug(header);
		assertThat(header, is(HTMLheader()));
	}

	@Test
	public void testProcess() {
		SchemaVersion v = schemaVersions.getAppRootVersion();
		String fileContent = null;
		try {
			htmlFromOxm.setXmlVersion(testXML, v);
			fileContent = htmlFromOxm.process();
		} catch(Exception e) {
			e.printStackTrace();
		}
		logger.debug("FileContent-I:");
		logger.debug(fileContent);
		assertThat(fileContent, is(HTMLresult(0)));
	}

	@Test
	public void testProcessWithCombiningJavaTypes() {
		SchemaVersion v = schemaVersions.getAppRootVersion();
		String fileContent = null;
		try {
			setUp(1);
			htmlFromOxm.setXmlVersion(testXML, v);
			fileContent = htmlFromOxm.process();
		} catch(Exception e) {
			e.printStackTrace();
		}
		logger.debug("FileContent-I:");
		logger.debug(fileContent);
		assertThat(fileContent, is(HTMLresult(1)));
	}

	@Test
	public void testHTMLfromOXMFileVersion() throws IOException {
		String outfileName = "testXML.xml";
		File XMLfile = new File(outfileName);
		XMLfile.createNewFile();
		BufferedWriter bw = null;
		Charset charset = Charset.forName("UTF-8");
		Path path = Paths.get(outfileName);
		bw = Files.newBufferedWriter(path, charset);
		bw.write(testXML);
		bw.close();
		SchemaVersion v = schemaVersions.getAppRootVersion();
		String fileContent = null;
		try {
			htmlFromOxm.setXmlVersion(testXML, v);
			fileContent = htmlFromOxm.process();
		} catch(Exception e) {
			e.printStackTrace();
		}
		XMLfile.delete();
		logger.debug("FileContent-I:");
		logger.debug(fileContent);
		assertThat(fileContent, is(HTMLresult(0)));
	}

	@Test
	public void testHTMLfromOXMStringVersion() {
		SchemaVersion v = schemaVersions.getAppRootVersion();
		String fileContent = null;
		try {
			htmlFromOxm.setXmlVersion(testXML, v);
			fileContent = htmlFromOxm.process();
		} catch(Exception e) {
			e.printStackTrace();
		}
		logger.debug("FileContent-II:");
		logger.debug(fileContent);
		assertThat(fileContent, is(HTMLresult(0)));
	}

	@Test
	public void testProcessJavaTypeElement() {
		String target = "Element=java-type/Customer";
		SchemaVersion v = schemaVersions.getAppRootVersion();
		Element customer = null;
		try {
			htmlFromOxm.setXmlVersion(testXML, v);
			htmlFromOxm.process();
			customer = htmlFromOxm.getJavaTypeElementSwagger("Customer");
		} catch(Exception e) {
			e.printStackTrace();
		}
		logger.debug("Element:");
		logger.debug("Element="+customer.getNodeName()+"/"+customer.getAttribute("name"));
		assertThat("Element="+customer.getNodeName()+"/"+customer.getAttribute("name"), is(target));	}
	
	public String HTMLresult() {
		return HTMLresult(0);
	}
	
	public String HTMLresult(int sbopt) {
		StringBuilder sb = new StringBuilder(32368);
		sb.append(HTMLheader());
		sb.append(HTMLdefs(sbopt));
		return sb.toString();
	}
	
	public String HTMLheader() {
		StringBuilder sb = new StringBuilder(1500);
		 sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" + OxmFileProcessor.LINE_SEPARATOR);
		 sb.append("<xs:schema elementFormDefault=\"qualified\" version=\"1.0\" targetNamespace=\"http://org.onap.aai.inventory/v11\" xmlns:tns=\"http://org.onap.aai.inventory/v11\" xmlns:xs=\"http://www.w3.org/2001/XMLSchema\""  + OxmFileProcessor.LINE_SEPARATOR + "xmlns:jaxb=\"http://java.sun.com/xml/ns/jaxb\"" + OxmFileProcessor.LINE_SEPARATOR);
		 sb.append("    jaxb:version=\"2.1\"" + OxmFileProcessor.LINE_SEPARATOR);
		 sb.append("    xmlns:annox=\"http://annox.dev.java.net\"" + OxmFileProcessor.LINE_SEPARATOR);
		 sb.append("    jaxb:extensionBindingPrefixes=\"annox\">" + OxmFileProcessor.DOUBLE_LINE_SEPARATOR);
		return sb.toString();
	}
	
	public String HTMLdefs() {
		return HTMLdefs(0);
	}
	public String HTMLdefs(int sbopt) {
		StringBuilder sb = new StringBuilder(1500);
		sb.append("  <xs:element name=\"service-subscription\">" + OxmFileProcessor.LINE_SEPARATOR);
		sb.append("    <xs:complexType>" + OxmFileProcessor.LINE_SEPARATOR);
		sb.append("      <xs:annotation>" + OxmFileProcessor.LINE_SEPARATOR);
		sb.append("        <xs:appinfo>" + OxmFileProcessor.LINE_SEPARATOR);
		sb.append("          <annox:annotate target=\"class\">@org.onap.aai.annotations.Metadata(description=\"Object that group service instances.\",indexedProps=\"service-type\",dependentOn=\"customer\",container=\"service-subscriptions\",crossEntityReference=\"service-instance,service-type\")</annox:annotate>" + OxmFileProcessor.LINE_SEPARATOR);
		sb.append("        </xs:appinfo>" + OxmFileProcessor.LINE_SEPARATOR);
		sb.append("      </xs:annotation>" + OxmFileProcessor.LINE_SEPARATOR);
		sb.append("      <xs:sequence>" + OxmFileProcessor.LINE_SEPARATOR);
		sb.append("        <xs:element name=\"service-type\" type=\"xs:string\" minOccurs=\"0\">" + OxmFileProcessor.LINE_SEPARATOR);
		sb.append("          <xs:annotation>" + OxmFileProcessor.LINE_SEPARATOR);
		sb.append("            <xs:appinfo>" + OxmFileProcessor.LINE_SEPARATOR);
		sb.append("              <annox:annotate target=\"field\">@org.onap.aai.annotations.Metadata(isKey=true,description=\"Value defined by orchestration to identify this service.\")</annox:annotate>" + OxmFileProcessor.LINE_SEPARATOR);
		sb.append("            </xs:appinfo>" + OxmFileProcessor.LINE_SEPARATOR);
		sb.append("          </xs:annotation>" + OxmFileProcessor.LINE_SEPARATOR);
		sb.append("        </xs:element>" + OxmFileProcessor.LINE_SEPARATOR);
		sb.append("        <xs:element name=\"temp-ub-sub-account-id\" type=\"xs:string\" minOccurs=\"0\">" + OxmFileProcessor.LINE_SEPARATOR);
		sb.append("          <xs:annotation>" + OxmFileProcessor.LINE_SEPARATOR);
		sb.append("            <xs:appinfo>" + OxmFileProcessor.LINE_SEPARATOR);
		sb.append("              <annox:annotate target=\"field\">@org.onap.aai.annotations.Metadata(description=\"This property will be deleted from A&amp;AI in the near future. Only stop gap solution.\")</annox:annotate>" + OxmFileProcessor.LINE_SEPARATOR);
		sb.append("            </xs:appinfo>" + OxmFileProcessor.LINE_SEPARATOR);
		sb.append("          </xs:annotation>" + OxmFileProcessor.LINE_SEPARATOR);
		sb.append("        </xs:element>" + OxmFileProcessor.LINE_SEPARATOR);
		sb.append("        <xs:element name=\"resource-version\" type=\"xs:string\" minOccurs=\"0\">" + OxmFileProcessor.LINE_SEPARATOR);
		sb.append("          <xs:annotation>" + OxmFileProcessor.LINE_SEPARATOR);
		sb.append("            <xs:appinfo>" + OxmFileProcessor.LINE_SEPARATOR);
		sb.append("              <annox:annotate target=\"field\">@org.onap.aai.annotations.Metadata(description=\"Used for optimistic concurrency.  Must be empty on create, valid on update and delete.\")</annox:annotate>" + OxmFileProcessor.LINE_SEPARATOR);
		sb.append("            </xs:appinfo>" + OxmFileProcessor.LINE_SEPARATOR);
		sb.append("          </xs:annotation>" + OxmFileProcessor.LINE_SEPARATOR);
		sb.append("        </xs:element>" + OxmFileProcessor.LINE_SEPARATOR);
		sb.append("      </xs:sequence>" + OxmFileProcessor.LINE_SEPARATOR);
		sb.append("    </xs:complexType>" + OxmFileProcessor.LINE_SEPARATOR);
		sb.append("  </xs:element>" + OxmFileProcessor.LINE_SEPARATOR);
		sb.append("  <xs:element name=\"service-subscriptions\">" + OxmFileProcessor.LINE_SEPARATOR);
		sb.append("    <xs:complexType>" + OxmFileProcessor.LINE_SEPARATOR);
		sb.append("      <xs:annotation>" + OxmFileProcessor.LINE_SEPARATOR);
		sb.append("        <xs:appinfo>" + OxmFileProcessor.LINE_SEPARATOR);
		sb.append("          <annox:annotate target=\"class\">@org.onap.aai.annotations.Metadata(description=\"Collection of objects that group service instances.\")</annox:annotate>" + OxmFileProcessor.LINE_SEPARATOR);
		sb.append("        </xs:appinfo>" + OxmFileProcessor.LINE_SEPARATOR);
		sb.append("      </xs:annotation>" + OxmFileProcessor.LINE_SEPARATOR);
		sb.append("      <xs:sequence>" + OxmFileProcessor.LINE_SEPARATOR);
		sb.append("        <xs:element ref=\"tns:service-subscription\" minOccurs=\"0\" maxOccurs=\"5000\"/>" + OxmFileProcessor.LINE_SEPARATOR);
		sb.append("      </xs:sequence>" + OxmFileProcessor.LINE_SEPARATOR);
		sb.append("    </xs:complexType>" + OxmFileProcessor.LINE_SEPARATOR);
		sb.append("  </xs:element>" + OxmFileProcessor.LINE_SEPARATOR);
		sb.append("  <xs:element name=\"customer\">" + OxmFileProcessor.LINE_SEPARATOR);
		sb.append("    <xs:complexType>" + OxmFileProcessor.LINE_SEPARATOR);
		sb.append("      <xs:annotation>" + OxmFileProcessor.LINE_SEPARATOR);
		sb.append("        <xs:appinfo>" + OxmFileProcessor.LINE_SEPARATOR);
		if ( sbopt == 0 ) {
			sb.append("          <annox:annotate target=\"class\">@org.onap.aai.annotations.Metadata(description=\"customer identifiers to provide linkage back to BSS information.\",nameProps=\"subscriber-name\",indexedProps=\"subscriber-name,global-customer-id,subscriber-type\",searchable=\"global-customer-id,subscriber-name\",uniqueProps=\"global-customer-id\",container=\"customers\",namespace=\"business\")</annox:annotate>" + OxmFileProcessor.LINE_SEPARATOR);
		} else {
			sb.append("          <annox:annotate target=\"class\">@org.onap.aai.annotations.Metadata(description=\"customer identifiers to provide linkage back to BSS information.\",nameProps=\"subscriber-name\",indexedProps=\"subscriber-type,subscriber-name,global-customer-id\",searchable=\"global-customer-id,subscriber-name\",uniqueProps=\"global-customer-id\",container=\"customers\",namespace=\"business\")</annox:annotate>" + OxmFileProcessor.LINE_SEPARATOR);
		}
		sb.append("        </xs:appinfo>" + OxmFileProcessor.LINE_SEPARATOR);
		sb.append("      </xs:annotation>" + OxmFileProcessor.LINE_SEPARATOR);
		sb.append("      <xs:sequence>" + OxmFileProcessor.LINE_SEPARATOR);
		sb.append("        <xs:element name=\"global-customer-id\" type=\"xs:string\" minOccurs=\"0\">" + OxmFileProcessor.LINE_SEPARATOR);
		sb.append("          <xs:annotation>" + OxmFileProcessor.LINE_SEPARATOR);
		sb.append("            <xs:appinfo>" + OxmFileProcessor.LINE_SEPARATOR);
		sb.append("              <annox:annotate target=\"field\">@org.onap.aai.annotations.Metadata(isKey=true,description=\"Global customer id used across to uniquely identify customer.\")</annox:annotate>" + OxmFileProcessor.LINE_SEPARATOR);
		sb.append("            </xs:appinfo>" + OxmFileProcessor.LINE_SEPARATOR);
		sb.append("          </xs:annotation>" + OxmFileProcessor.LINE_SEPARATOR);
		sb.append("        </xs:element>" + OxmFileProcessor.LINE_SEPARATOR);
		sb.append("        <xs:element name=\"subscriber-name\" type=\"xs:string\" minOccurs=\"0\">" + OxmFileProcessor.LINE_SEPARATOR);
		sb.append("          <xs:annotation>" + OxmFileProcessor.LINE_SEPARATOR);
		sb.append("            <xs:appinfo>" + OxmFileProcessor.LINE_SEPARATOR);
		sb.append("              <annox:annotate target=\"field\">@org.onap.aai.annotations.Metadata(description=\"Subscriber name, an alternate way to retrieve a customer.\")</annox:annotate>" + OxmFileProcessor.LINE_SEPARATOR);
		sb.append("            </xs:appinfo>" + OxmFileProcessor.LINE_SEPARATOR);
		sb.append("          </xs:annotation>" + OxmFileProcessor.LINE_SEPARATOR);
		sb.append("        </xs:element>" + OxmFileProcessor.LINE_SEPARATOR);
		if ( sbopt == 0 ) {
			sb.append("        <xs:element name=\"subscriber-type\" type=\"xs:string\" minOccurs=\"0\">" + OxmFileProcessor.LINE_SEPARATOR);
			sb.append("          <xs:annotation>" + OxmFileProcessor.LINE_SEPARATOR);
			sb.append("            <xs:appinfo>" + OxmFileProcessor.LINE_SEPARATOR);
			sb.append("              <annox:annotate target=\"field\">@org.onap.aai.annotations.Metadata(description=\"Subscriber type, a way to provide VID with only the INFRA customers.\",defaultValue=\"CUST\")</annox:annotate>" + OxmFileProcessor.LINE_SEPARATOR);
			sb.append("            </xs:appinfo>" + OxmFileProcessor.LINE_SEPARATOR);
			sb.append("          </xs:annotation>" + OxmFileProcessor.LINE_SEPARATOR);
			sb.append("        </xs:element>" + OxmFileProcessor.LINE_SEPARATOR);
			sb.append("        <xs:element name=\"resource-version\" type=\"xs:string\" minOccurs=\"0\">" + OxmFileProcessor.LINE_SEPARATOR);
			sb.append("          <xs:annotation>" + OxmFileProcessor.LINE_SEPARATOR);
			sb.append("            <xs:appinfo>" + OxmFileProcessor.LINE_SEPARATOR);
			sb.append("              <annox:annotate target=\"field\">@org.onap.aai.annotations.Metadata(description=\"Used for optimistic concurrency.  Must be empty on create, valid on update and delete.\")</annox:annotate>" + OxmFileProcessor.LINE_SEPARATOR);
			sb.append("            </xs:appinfo>" + OxmFileProcessor.LINE_SEPARATOR);
			sb.append("          </xs:annotation>" + OxmFileProcessor.LINE_SEPARATOR);
			sb.append("        </xs:element>" + OxmFileProcessor.LINE_SEPARATOR);
		} else {
			sb.append("        <xs:element name=\"resource-version\" type=\"xs:string\" minOccurs=\"0\">" + OxmFileProcessor.LINE_SEPARATOR);
			sb.append("          <xs:annotation>" + OxmFileProcessor.LINE_SEPARATOR);
			sb.append("            <xs:appinfo>" + OxmFileProcessor.LINE_SEPARATOR);
			sb.append("              <annox:annotate target=\"field\">@org.onap.aai.annotations.Metadata(description=\"Used for optimistic concurrency.  Must be empty on create, valid on update and delete.\")</annox:annotate>" + OxmFileProcessor.LINE_SEPARATOR);
			sb.append("            </xs:appinfo>" + OxmFileProcessor.LINE_SEPARATOR);
			sb.append("          </xs:annotation>" + OxmFileProcessor.LINE_SEPARATOR);
			sb.append("        </xs:element>" + OxmFileProcessor.LINE_SEPARATOR);
			sb.append("        <xs:element name=\"subscriber-type\" type=\"xs:string\" minOccurs=\"0\">" + OxmFileProcessor.LINE_SEPARATOR);
			sb.append("          <xs:annotation>" + OxmFileProcessor.LINE_SEPARATOR);
			sb.append("            <xs:appinfo>" + OxmFileProcessor.LINE_SEPARATOR);
			sb.append("              <annox:annotate target=\"field\">@org.onap.aai.annotations.Metadata(description=\"Subscriber type, a way to provide VID with only the INFRA customers.\",defaultValue=\"CUST\")</annox:annotate>" + OxmFileProcessor.LINE_SEPARATOR);
			sb.append("            </xs:appinfo>" + OxmFileProcessor.LINE_SEPARATOR);
			sb.append("          </xs:annotation>" + OxmFileProcessor.LINE_SEPARATOR);
			sb.append("        </xs:element>" + OxmFileProcessor.LINE_SEPARATOR);			
			
		}
		sb.append("        <xs:element ref=\"tns:service-subscriptions\" minOccurs=\"0\"/>" + OxmFileProcessor.LINE_SEPARATOR);
		sb.append("      </xs:sequence>" + OxmFileProcessor.LINE_SEPARATOR);
		sb.append("    </xs:complexType>" + OxmFileProcessor.LINE_SEPARATOR);
		sb.append("  </xs:element>" + OxmFileProcessor.LINE_SEPARATOR);
		sb.append("  <xs:element name=\"customers\">" + OxmFileProcessor.LINE_SEPARATOR);
		sb.append("    <xs:complexType>" + OxmFileProcessor.LINE_SEPARATOR);
		sb.append("      <xs:annotation>" + OxmFileProcessor.LINE_SEPARATOR);
		sb.append("        <xs:appinfo>" + OxmFileProcessor.LINE_SEPARATOR);
		sb.append("          <annox:annotate target=\"class\">@org.onap.aai.annotations.Metadata(description=\"Collection of customer identifiers to provide linkage back to BSS information.\")</annox:annotate>" + OxmFileProcessor.LINE_SEPARATOR);
		sb.append("        </xs:appinfo>" + OxmFileProcessor.LINE_SEPARATOR);
		sb.append("      </xs:annotation>" + OxmFileProcessor.LINE_SEPARATOR);
		sb.append("      <xs:sequence>" + OxmFileProcessor.LINE_SEPARATOR);
		sb.append("        <xs:element ref=\"tns:customer\" minOccurs=\"0\" maxOccurs=\"5000\"/>" + OxmFileProcessor.LINE_SEPARATOR);
		sb.append("      </xs:sequence>" + OxmFileProcessor.LINE_SEPARATOR);
		sb.append("    </xs:complexType>" + OxmFileProcessor.LINE_SEPARATOR);
		sb.append("  </xs:element>" + OxmFileProcessor.LINE_SEPARATOR);
		sb.append("  <xs:element name=\"business\">" + OxmFileProcessor.LINE_SEPARATOR);
		sb.append("    <xs:complexType>" + OxmFileProcessor.LINE_SEPARATOR);
		sb.append("      <xs:annotation>" + OxmFileProcessor.LINE_SEPARATOR);
		sb.append("        <xs:appinfo>" + OxmFileProcessor.LINE_SEPARATOR);
		sb.append("          <annox:annotate target=\"class\">@org.onap.aai.annotations.Metadata(description=\"Namespace for business related constructs\")</annox:annotate>" + OxmFileProcessor.LINE_SEPARATOR);
		sb.append("        </xs:appinfo>" + OxmFileProcessor.LINE_SEPARATOR);
		sb.append("      </xs:annotation>" + OxmFileProcessor.LINE_SEPARATOR);
		sb.append("      <xs:sequence>" + OxmFileProcessor.LINE_SEPARATOR);
		sb.append("        <xs:element ref=\"tns:customers\" minOccurs=\"0\"/>" + OxmFileProcessor.LINE_SEPARATOR);
		sb.append("      </xs:sequence>" + OxmFileProcessor.LINE_SEPARATOR);
		sb.append("    </xs:complexType>" + OxmFileProcessor.LINE_SEPARATOR);
		sb.append("  </xs:element>" + OxmFileProcessor.LINE_SEPARATOR);
		sb.append("  <xs:element name=\"inventory\">" + OxmFileProcessor.LINE_SEPARATOR);
		sb.append("    <xs:complexType>" + OxmFileProcessor.LINE_SEPARATOR);
		sb.append("      <xs:sequence>" + OxmFileProcessor.LINE_SEPARATOR);
		sb.append("        <xs:element ref=\"tns:business\" minOccurs=\"0\"/>" + OxmFileProcessor.LINE_SEPARATOR);
		sb.append("      </xs:sequence>" + OxmFileProcessor.LINE_SEPARATOR);
		sb.append("    </xs:complexType>" + OxmFileProcessor.LINE_SEPARATOR);
		sb.append("  </xs:element>" + OxmFileProcessor.LINE_SEPARATOR);
		sb.append("</xs:schema>" + OxmFileProcessor.LINE_SEPARATOR);
		return sb.toString();
	}
}

