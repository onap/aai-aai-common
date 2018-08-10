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
package org.onap.aai.schema;


//import org.junit.Test;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.RegexFileFilter;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class ValidateOXM {

	@Test
	public void testFindXmlPropContainingSpace() throws XPathExpressionException, IOException, SAXException, ParserConfigurationException {
		boolean foundIssue = false;
		List<File> fileList = getFiles();

		StringBuilder msg = new StringBuilder();
		for (File file : fileList) {
			msg.append(file.getAbsolutePath().replaceAll(".*aai-schema", ""));
			msg.append("\n");
			Document xmlDocument = getDocument(file);
			XPath xPath = XPathFactory.newInstance().newXPath();
			String expression = "/xml-bindings/java-types/java-type/xml-properties/xml-property[@name!='description' and contains(@value,' ')]";
			NodeList nodeList = (NodeList) xPath.compile(expression).evaluate(xmlDocument, XPathConstants.NODESET);

			for (int i = 0; i < nodeList.getLength(); i++) {
				foundIssue = true;
				msg.append("\t");
				msg.append(nodeList.item(i).getParentNode().getParentNode().getAttributes().getNamedItem("name").getNodeValue());
				msg.append("\n");
				msg.append("\t");
				msg.append("\n");
			}

		}

		if (foundIssue) {
			System.out.println(msg.toString());
			fail("Node type xml-property should have space.");
		}

	}

	/**
	 * Verifies that all of the node types in the oxm's have their uri templates.
	 * @throws XPathExpressionException
	 * @throws IOException
	 * @throws SAXException
	 * @throws ParserConfigurationException
	 */
	@Test
	public void allNodeTypesHaveAAIUriTemplate() throws XPathExpressionException, IOException, SAXException, ParserConfigurationException {
		boolean foundIssue = false;
		List<File> fileList = getFiles();

		StringBuilder msg = new StringBuilder();
		for (File file : fileList) {
			msg.append(file.getAbsolutePath().replaceAll(".*aai-schema", ""));
			msg.append("\n");
			Document xmlDocument = getDocument(file);
			XPath xPath = XPathFactory.newInstance().newXPath();
			String expression = "/xml-bindings/java-types/java-type[count(xml-properties/xml-property[@name='container']) > 0 and count(xml-properties/xml-property[@name='uriTemplate']) = 0 ]";
			NodeList nodeList = (NodeList) xPath.compile(expression).evaluate(xmlDocument, XPathConstants.NODESET);

			for (int i = 0; i < nodeList.getLength(); i++) {
				String name = nodeList.item(i).getAttributes().getNamedItem("name").getNodeValue();
				if (name.equals("InstanceFilter") || name.equals("InventoryResponseItems") || name.equals("InventoryResponseItem")) {
					continue;
				}
				foundIssue = true;
				msg.append("\t");
				msg.append(name);
				msg.append("\n");
			}
		}
		if (foundIssue) {
			System.out.println(msg.toString());
			fail("Missing uriTemplate in oxm.");
		}

	}

	private List<File> getFiles() {
		Path currentRelativePath = Paths.get("../aai-schema/src/main/resources/").toAbsolutePath();
		return FileUtils.listFiles(
				currentRelativePath.toFile(),
				new RegexFileFilter(".*\\.xml"),
				DirectoryFileFilter.DIRECTORY)
				.stream().filter(file -> file.getAbsolutePath().contains("oxm"))
				.collect(Collectors.toList());
	}

	//TODO test that all oxm xml are valid xml



	public String printNodeList(NodeList nodeList, Document doc) throws IOException {
		StringBuilder stringBuilder = new StringBuilder();
		for (int i = 0; i < nodeList.getLength(); i++) {
			stringBuilder.append(printNode(nodeList.item(i), doc)).append("\n");
		}
		return stringBuilder.toString();
	}

	public String printNode(Node node, Document document) throws IOException {
		StringWriter stringWriter = new StringWriter();
		return stringWriter.toString();

	}

	private Document getDocument(File file) throws ParserConfigurationException, SAXException, IOException {
		InputStream fileIS = new FileInputStream(file);
		DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = builderFactory.newDocumentBuilder();
		return builder.parse(fileIS);
	}

}
