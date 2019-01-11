/**
 * ============LICENSE_START=======================================================
 * org.onap.aai
 * ================================================================================
 * Copyright © 2017-2018 AT&T Intellectual Property. All rights reserved.
 * ================================================================================
 * Modifications Copyright © 2018 IBM.
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

package org.onap.aai.nodes;

import com.att.eelf.configuration.EELFLogger;
import com.att.eelf.configuration.EELFManager;
import com.google.common.base.CaseFormat;
import org.eclipse.persistence.jaxb.JAXBContextProperties;
import org.eclipse.persistence.jaxb.dynamic.DynamicJAXBContext;
import org.eclipse.persistence.jaxb.dynamic.DynamicJAXBContextFactory;
import org.onap.aai.setup.ConfigTranslator;
import org.onap.aai.setup.SchemaVersion;
import org.onap.aai.setup.SchemaVersions;
import org.onap.aai.setup.Translator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.annotation.PostConstruct;
import javax.xml.XMLConstants;
import javax.xml.bind.JAXBException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
/*
  NodeIngestor - ingests A&AI OXM files per given config, serves DynamicJAXBContext per version
 */
@PropertySource(value = "classpath:schema-ingest.properties", ignoreResourceNotFound=true)
@PropertySource(value = "file:${schema.ingest.file}", ignoreResourceNotFound=true)
public class NodeIngestor {

    private static final EELFLogger LOGGER = EELFManager.getInstance().getLogger(NodeIngestor.class);
    private static final Pattern classNamePattern = Pattern.compile("\\.(v\\d+)\\.");
    Map<SchemaVersion, List<String>> filesToIngest;
    private Map<SchemaVersion, DynamicJAXBContext> versionContextMap = new TreeMap<>();
    private Map<SchemaVersion, Set<String>> typesPerVersion = new TreeMap<>();
    private Map<SchemaVersion, Document> schemaPerVersion = new TreeMap<>();
    private String localSchema;
    private SchemaVersions schemaVersions;
    private Set<Translator> translators;
    
    //TODO : See if you can get rid of InputStream resets
     /**
     * Instantiates the NodeIngestor bean.
     *
     * @param  - ConfigTranslator autowired in by Spring framework which
     * contains the configuration information needed to ingest the desired files.
     */

     @Autowired
    public NodeIngestor(Set<Translator> translatorSet) {
        LOGGER.debug("Local Schema files will be fetched");
        this.translators = translatorSet;
    }

    @PostConstruct
    public void initialize() {

        for (Translator translator : translators) {
            try {
                LOGGER.debug("Processing the translator");
                translateAll(translator);

            } catch (Exception e) {
                LOGGER.info("Error while Processing the translator" + e.getMessage());
                continue;
            }
        }
        if (versionContextMap.isEmpty() || schemaPerVersion.isEmpty() || typesPerVersion.isEmpty()) {
            throw new ExceptionInInitializerError("NodeIngestor could not ingest schema");
        }
    }

    private void translateAll(Translator translator) throws ExceptionInInitializerError {
        if (translator instanceof ConfigTranslator) {
            this.localSchema = "true";
        }

	    Boolean retrieveLocalSchema = Boolean.parseBoolean(this.localSchema);
        /*
         * Set this to default schemaVersion
         */
        this.schemaVersions = translator.getSchemaVersions();
        List<SchemaVersion> schemaVersionList = translator.getSchemaVersions().getVersions();

        try {
            for (SchemaVersion version : schemaVersionList) {
                LOGGER.debug("Version being processed" + version);
                List<InputStream> inputStreams = retrieveOXM(version, translator);
                LOGGER.debug("Retrieved OXMs from SchemaService");
                /*
                IOUtils.copy and copy the inputstream
                */
                if (inputStreams.isEmpty()) {
                    continue;
                }

                final DynamicJAXBContext ctx = ingest(inputStreams);
                versionContextMap.put(version, ctx);
                typesPerVersion.put(version, getAllNodeTypes(inputStreams));
                schemaPerVersion.put(version, createCombinedSchema(inputStreams, version, retrieveLocalSchema));
            }
        } catch (JAXBException | ParserConfigurationException | SAXException | IOException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    /**
     * Ingests the given OXM files into DynamicJAXBContext
     *
     * @param inputStreams - inputStrean of oxms from SchemaService to be ingested
     *
     * @return DynamicJAXBContext including schema information from all given files
     *
     * @throws JAXBException if there's an error creating the DynamicJAXBContext
     */
    private DynamicJAXBContext ingest(List<InputStream> inputStreams) throws JAXBException {
        Map<String, Object> properties = new HashMap<>();
        properties.put(JAXBContextProperties.OXM_METADATA_SOURCE, inputStreams);
        LOGGER.debug("Ingested the InputStream");
        return DynamicJAXBContextFactory.createContextFromOXM(this.getClass().getClassLoader(), properties);
    }

    private Set<String> getAllNodeTypes(List<InputStream> inputStreams) throws ParserConfigurationException, SAXException, IOException {
        //Reset the InputStream to reset the offset to inital position
        Set<String> types = new HashSet<>();
        final DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        docFactory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
        final DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

        for (InputStream inputStream : inputStreams) {
            //TODO Change this
            inputStream.reset();
            final Document doc = docBuilder.parse(inputStream);
            final NodeList list = doc.getElementsByTagName("java-type");

            for (int i = 0; i < list.getLength(); i++) {
                String type = list.item(i).getAttributes().getNamedItem("name").getNodeValue();
                types.add(CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_HYPHEN, type));
            }
        }

        LOGGER.debug("Types size" + types.size());
        return types;
    }

    private Document createCombinedSchema(List<InputStream> inputStreams, SchemaVersion version, boolean localSchema) throws ParserConfigurationException, SAXException, IOException {
        if (localSchema) {
            return createCombinedSchema(inputStreams, version);
        }

        InputStream inputStream = inputStreams.get(0);
        inputStream.reset();
        final DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        docFactory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
        DocumentBuilder masterDocBuilder = docFactory.newDocumentBuilder();
        return masterDocBuilder.parse(inputStream);
    }

    private Document createCombinedSchema(List<InputStream> inputStreams, SchemaVersion version) throws ParserConfigurationException, SAXException, IOException {
        final DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        docFactory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
        final DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
        DocumentBuilder masterDocBuilder = docFactory.newDocumentBuilder();
        Document combinedDoc = masterDocBuilder.parse(getShell(version));
        NodeList masterList = combinedDoc.getElementsByTagName("java-types");
        Node javaTypesContainer = masterList.getLength() == 0 ? combinedDoc.getDocumentElement() : masterList.item(0);

        for (InputStream inputStream : inputStreams) {
            inputStream.reset();
            final Document doc = docBuilder.parse(inputStream);
            final NodeList list = doc.getElementsByTagName("java-type");
            for (int i = 0; i < list.getLength(); i++) {
                Node copy = combinedDoc.importNode(list.item(i), true);
                javaTypesContainer.appendChild(copy);
            }
        }
        return combinedDoc;
    }

    /**
     * Gets the DynamicJAXBContext for the given version
     *
     * @param v - schema version to retrieve the context
     * @return DynamicJAXBContext
     */
    public DynamicJAXBContext getContextForVersion(SchemaVersion v) {
        return versionContextMap.get(v);
    }

    /**
     * Determines if the given version contains the given node type
     *
     * @param nodeType - node type to check, must be in lower hyphen form (ie "type-name")
     * @param v - schema version to check against
     * @return boolean
     */
    public boolean hasNodeType(String nodeType, SchemaVersion v) {
        return typesPerVersion.get(v).contains(nodeType);
    }

    public Set<String> getObjectsInVersion(SchemaVersion v) {
        return typesPerVersion.get(v);
    }

    /**
     * Determines if the given version contains the given node type
     *
     * @param v - Schemaversion to retrieve the schema
     * @return Document
     */
    public Document getSchema(SchemaVersion v) {
        return schemaPerVersion.get(v);
    }


    public SchemaVersion getVersionFromClassName(String classname) {
        Matcher m = classNamePattern.matcher(classname);
        if (m.find()) {
            String version = m.group(1);
            return new SchemaVersion(version);
        } else {
            return this.schemaVersions.getDefaultVersion();
        }
    }

    private List<InputStream> retrieveOXM(SchemaVersion version, Translator translator) throws IOException {
	    /*
	    Call Schema MS to get versions using RestTemplate or Local
	     */
        return translator.getVersionNodeStream(version);

    }

    private InputStream getShell(SchemaVersion v) {
        String source = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<xml-bindings xmlns=\"http://www.eclipse.org/eclipselink/xsds/persistence/oxm\" package-name=\"inventory.aai.onap.org." + v.toString().toLowerCase() + "\" xml-mapping-metadata-complete=\"true\">\n" +
            "	<xml-schema element-form-default=\"QUALIFIED\">\n" +
            "		<xml-ns namespace-uri=\"http://org.onap.aai.inventory/" + v.toString().toLowerCase() + "\" />\n" +
            "	</xml-schema>\n" +
            "	<java-types>\n" +
            "	</java-types>\n" +
            "</xml-bindings>";
        return new ByteArrayInputStream(source.getBytes(StandardCharsets.UTF_8));
    }
}
