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

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.onap.aai.introspection.Version;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class HTMLfromOXMTest {
	private static final Logger logger = LoggerFactory.getLogger("HTMLfromOXMTest.class");
	private String testXML;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		XSDElementTest x = new XSDElementTest();
		x.setUp();
		testXML = x.testXML;
	}

	@Test
	public void testGetDocumentHeader() {
		Version v = Version.v11;
		String header = null;
		try {
			HTMLfromOXM swagger = new HTMLfromOXM(testXML, v);
			header = swagger.getDocumentHeader();
		} catch(Exception e) {
			e.printStackTrace();
		}
		logger.debug("Header:");
		logger.debug(header);
		assertThat(header, is(HTMLheader()));
	}

	@Test
	public void testProcess() {
		Version v = Version.v11;
		String fileContent = null;
		try {
			HTMLfromOXM xsd = new HTMLfromOXM(testXML, v);
			fileContent = xsd.process();
		} catch(Exception e) {
			e.printStackTrace();
		}
		logger.debug("FileContent-I:");
		logger.debug(fileContent);
		assertThat(fileContent, is(HTMLresult()));
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
		Version v = Version.v11;
		String fileContent = null;
		try {
			HTMLfromOXM xsd = new HTMLfromOXM(testXML, v);
			fileContent = xsd.process();
		} catch(Exception e) {
			e.printStackTrace();
		}
		XMLfile.delete();
		logger.debug("FileContent-I:");
		logger.debug(fileContent);
		assertThat(fileContent, is(HTMLresult()));
	}

	@Test
	public void testHTMLfromOXMStringVersion() {
		Version v = Version.v11;
		String fileContent = null;
		try {
			HTMLfromOXM xsd = new HTMLfromOXM(testXML, v);
			fileContent = xsd.process();
		} catch(Exception e) {
			e.printStackTrace();
		}
		logger.debug("FileContent-II:");
		logger.debug(fileContent);
		assertThat(fileContent, is(HTMLresult()));
	}

	@Test
	public void testProcessJavaTypeElement() {
		String target = "Element=java-type/Customer";
		Version v = Version.v11;
		Element customer = null;
		try {
			HTMLfromOXM xsd = new HTMLfromOXM(testXML, v);
			xsd.process();
			customer = xsd.getJavaTypeElementSwagger("Customer");
		} catch(Exception e) {
			e.printStackTrace();
		}
		logger.debug("Element:");
		logger.debug("Element="+customer.getNodeName()+"/"+customer.getAttribute("name"));
		assertThat("Element="+customer.getNodeName()+"/"+customer.getAttribute("name"), is(target));	}
	
	public String HTMLresult() {
		StringBuilder sb = new StringBuilder(32368);
		sb.append(HTMLheader());
		sb.append(HTMLdefs());
		return sb.toString();
	}
	
	public String HTMLheader() {
		StringBuilder sb = new StringBuilder(1500);
		 sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n");
		 sb.append("<xs:schema elementFormDefault=\"qualified\" version=\"1.0\" targetNamespace=\"http://org.openecomp.aai.inventory/v11\" xmlns:tns=\"http://org.openecomp.aai.inventory/v11\" xmlns:xs=\"http://www.w3.org/2001/XMLSchema\"\nxmlns:jaxb=\"http://java.sun.com/xml/ns/jaxb\"\r\n");
		 sb.append("    jaxb:version=\"2.1\"\r\n");
		 sb.append("    xmlns:annox=\"http://annox.dev.java.net\"\r\n");
		 sb.append("    jaxb:extensionBindingPrefixes=\"annox\">\n\n");
		return sb.toString();
	}
	public String HTMLdefs() {
		StringBuilder sb = new StringBuilder(1500);
		sb.append("  <xs:element name=\"service-subscription\">\n");
		sb.append("    <xs:complexType>\n");
		sb.append("      <xs:annotation>\r\n");
		sb.append("        <xs:appinfo>\r\n");
		sb.append("          <annox:annotate target=\"class\">@org.onap.aai.annotations.Metadata(description=\"Object that group service instances.\",indexedProps=\"service-type\",dependentOn=\"customer\",container=\"service-subscriptions\",crossEntityReference=\"service-instance,service-type\")</annox:annotate>\r\n");
		sb.append("        </xs:appinfo>\r\n");
		sb.append("      </xs:annotation>\r\n");
		sb.append("      <xs:sequence>\n");
		sb.append("        <xs:element name=\"service-type\" type=\"xs:string\">\n");
		sb.append("          <xs:annotation>\r\n");
		sb.append("            <xs:appinfo>\r\n");
		sb.append("              <annox:annotate target=\"field\">@org.onap.aai.annotations.Metadata(isKey=true,description=\"Value defined by orchestration to identify this service across ECOMP.\")</annox:annotate>\r\n");
		sb.append("            </xs:appinfo>\r\n");
		sb.append("          </xs:annotation>\r\n");
		sb.append("        </xs:element>\n");
		sb.append("        <xs:element name=\"temp-ub-sub-account-id\" type=\"xs:string\" minOccurs=\"0\">\n");
		sb.append("          <xs:annotation>\r\n");
		sb.append("            <xs:appinfo>\r\n");
		sb.append("              <annox:annotate target=\"field\">@org.onap.aai.annotations.Metadata(description=\"This property will be deleted from A&amp;AI in the near future. Only stop gap solution.\")</annox:annotate>\r\n");
		sb.append("            </xs:appinfo>\r\n");
		sb.append("          </xs:annotation>\r\n");
		sb.append("        </xs:element>\n");
		sb.append("        <xs:element name=\"resource-version\" type=\"xs:string\" minOccurs=\"0\">\n");
		sb.append("          <xs:annotation>\r\n");
		sb.append("            <xs:appinfo>\r\n");
		sb.append("              <annox:annotate target=\"field\">@org.onap.aai.annotations.Metadata(description=\"Used for optimistic concurrency.  Must be empty on create, valid on update and delete.\")</annox:annotate>\r\n");
		sb.append("            </xs:appinfo>\r\n");
		sb.append("          </xs:annotation>\r\n");
		sb.append("        </xs:element>\n");
		sb.append("      </xs:sequence>\n");
		sb.append("    </xs:complexType>\n");
		sb.append("  </xs:element>\n");
		sb.append("  <xs:element name=\"service-subscriptions\">\n");
		sb.append("    <xs:complexType>\n");
		sb.append("      <xs:annotation>\r\n");
		sb.append("        <xs:appinfo>\r\n");
		sb.append("          <annox:annotate target=\"class\">@org.onap.aai.annotations.Metadata(description=\"Collection of objects that group service instances.\")</annox:annotate>\r\n");
		sb.append("        </xs:appinfo>\r\n");
		sb.append("      </xs:annotation>\r\n");
		sb.append("      <xs:sequence>\n");
		sb.append("        <xs:element ref=\"tns:service-subscription\" minOccurs=\"0\" maxOccurs=\"unbounded\"/>\n");
		sb.append("      </xs:sequence>\n");
		sb.append("    </xs:complexType>\n");
		sb.append("  </xs:element>\n");
		sb.append("  <xs:element name=\"customer\">\n");
		sb.append("    <xs:complexType>\n");
		sb.append("      <xs:annotation>\r\n");
		sb.append("        <xs:appinfo>\r\n");
		sb.append("          <annox:annotate target=\"class\">@org.onap.aai.annotations.Metadata(description=\"customer identifiers to provide linkage back to BSS information.\",nameProps=\"subscriber-name\",indexedProps=\"subscriber-name,global-customer-id,subscriber-type\",searchable=\"global-customer-id,subscriber-name\",uniqueProps=\"global-customer-id\",container=\"customers\",namespace=\"business\")</annox:annotate>\r\n");
		sb.append("        </xs:appinfo>\r\n");
		sb.append("      </xs:annotation>\r\n");
		sb.append("      <xs:sequence>\n");
		sb.append("        <xs:element name=\"global-customer-id\" type=\"xs:string\">\n");
		sb.append("          <xs:annotation>\r\n");
		sb.append("            <xs:appinfo>\r\n");
		sb.append("              <annox:annotate target=\"field\">@org.onap.aai.annotations.Metadata(isKey=true,description=\"Global customer id used across ECOMP to uniquely identify customer.\")</annox:annotate>\r\n");
		sb.append("            </xs:appinfo>\r\n");
		sb.append("          </xs:annotation>\r\n");
		sb.append("        </xs:element>\n");
		sb.append("        <xs:element name=\"subscriber-name\" type=\"xs:string\">\n");
		sb.append("          <xs:annotation>\r\n");
		sb.append("            <xs:appinfo>\r\n");
		sb.append("              <annox:annotate target=\"field\">@org.onap.aai.annotations.Metadata(description=\"Subscriber name, an alternate way to retrieve a customer.\")</annox:annotate>\r\n");
		sb.append("            </xs:appinfo>\r\n");
		sb.append("          </xs:annotation>\r\n");
		sb.append("        </xs:element>\n");
		sb.append("        <xs:element name=\"subscriber-type\" type=\"xs:string\">\n");
		sb.append("          <xs:annotation>\r\n");
		sb.append("            <xs:appinfo>\r\n");
		sb.append("              <annox:annotate target=\"field\">@org.onap.aai.annotations.Metadata(description=\"Subscriber type, a way to provide VID with only the INFRA customers.\",defaultValue=\"CUST\")</annox:annotate>\r\n");
		sb.append("            </xs:appinfo>\r\n");
		sb.append("          </xs:annotation>\r\n");
		sb.append("        </xs:element>\n");
		sb.append("        <xs:element name=\"resource-version\" type=\"xs:string\" minOccurs=\"0\">\n");
		sb.append("          <xs:annotation>\r\n");
		sb.append("            <xs:appinfo>\r\n");
		sb.append("              <annox:annotate target=\"field\">@org.onap.aai.annotations.Metadata(description=\"Used for optimistic concurrency.  Must be empty on create, valid on update and delete.\")</annox:annotate>\r\n");
		sb.append("            </xs:appinfo>\r\n");
		sb.append("          </xs:annotation>\r\n");
		sb.append("        </xs:element>\n");
		sb.append("        <xs:element ref=\"tns:service-subscriptions\" minOccurs=\"0\"/>\n");
		sb.append("      </xs:sequence>\n");
		sb.append("    </xs:complexType>\n");
		sb.append("  </xs:element>\n");
		sb.append("  <xs:element name=\"customers\">\n");
		sb.append("    <xs:complexType>\n");
		sb.append("      <xs:annotation>\r\n");
		sb.append("        <xs:appinfo>\r\n");
		sb.append("          <annox:annotate target=\"class\">@org.onap.aai.annotations.Metadata(description=\"Collection of customer identifiers to provide linkage back to BSS information.\")</annox:annotate>\r\n");
		sb.append("        </xs:appinfo>\r\n");
		sb.append("      </xs:annotation>\r\n");
		sb.append("      <xs:sequence>\n");
		sb.append("        <xs:element ref=\"tns:customer\" minOccurs=\"0\" maxOccurs=\"unbounded\"/>\n");
		sb.append("      </xs:sequence>\n");
		sb.append("    </xs:complexType>\n");
		sb.append("  </xs:element>\n");
		sb.append("  <xs:element name=\"business\">\n");
		sb.append("    <xs:complexType>\n");
		sb.append("      <xs:annotation>\r\n");
		sb.append("        <xs:appinfo>\r\n");
		sb.append("          <annox:annotate target=\"class\">@org.onap.aai.annotations.Metadata(description=\"Namespace for business related constructs\")</annox:annotate>\r\n");
		sb.append("        </xs:appinfo>\r\n");
		sb.append("      </xs:annotation>\r\n");
		sb.append("      <xs:sequence>\n");
		sb.append("        <xs:element ref=\"tns:customers\" minOccurs=\"0\"/>\n");
		sb.append("      </xs:sequence>\n");
		sb.append("    </xs:complexType>\n");
		sb.append("  </xs:element>\n");
		sb.append("  <xs:element name=\"inventory\">\n");
		sb.append("    <xs:complexType>\n");
		sb.append("      <xs:sequence>\n");
		sb.append("        <xs:element ref=\"tns:business\" minOccurs=\"0\"/>\n");
		sb.append("      </xs:sequence>\n");
		sb.append("    </xs:complexType>\n");
		sb.append("  </xs:element>\n");
		sb.append("</xs:schema>\n");
		return sb.toString();
	}
}
