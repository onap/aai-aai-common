/**
 * ============LICENSE_START=======================================================
 * org.onap.aai
 * ================================================================================
 * Copyright © 2017 AT&T Intellectual Property. All rights reserved.
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
 *
 * ECOMP is a trademark and service mark of AT&T Intellectual Property.
 */
package org.onap.aai.dbgen;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.tinkerpop.gremlin.process.traversal.step.util.Tree;
import org.apache.tinkerpop.gremlin.structure.Element;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.onap.aai.db.props.AAIProperties;
import org.onap.aai.dbmap.DBConnectionType;
import org.onap.aai.dbmap.InMemoryGraph;
import org.onap.aai.exceptions.AAIException;
import org.onap.aai.introspection.Introspector;
import org.onap.aai.introspection.Loader;
import org.onap.aai.introspection.LoaderFactory;
import org.onap.aai.introspection.ModelType;
import org.onap.aai.introspection.Version;
import org.onap.aai.introspection.exceptions.AAIUnknownObjectException;
import org.onap.aai.logging.LogFormatTools;
import org.onap.aai.parsers.uri.URIToObject;
import org.onap.aai.serialization.engines.InMemoryDBEngine;
import org.onap.aai.serialization.engines.QueryStyle;
import org.onap.aai.serialization.db.DBSerializer;

import com.att.eelf.configuration.EELFLogger;
import com.att.eelf.configuration.EELFManager;

import org.slf4j.MDC;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ObjectNode;
import org.codehaus.jackson.type.TypeReference;

import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import org.onap.aai.serialization.tinkerpop.TreeBackedVertex;
import org.onap.aai.util.AAIConfig;
import org.onap.aai.util.AAIConstants;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;

/**
 * The Class ListEndpoints.
 */
public class DynamicPayloadGenerator {

	/*
	 * Create a Dynamic memory graph instance which should not affect the
	 * AAIGraph
	 */
	private InMemoryGraph inMemGraph = null;
	private InMemoryDBEngine dbEngine;

	/*
	 * Loader, QueryStyle, ConnectionType for the Serializer
	 */
	private Loader loader;
	private String urlBase;
	private BufferedWriter bw = null;

	private CommandLineArgs cArgs;

	private static final EELFLogger LOGGER = EELFManager.getInstance().getLogger(DynamicPayloadGenerator.class);

	private static final QueryStyle queryStyle = QueryStyle.TRAVERSAL;
	private static final DBConnectionType type = DBConnectionType.CACHED;
	private static final ModelType introspectorFactoryType = ModelType.MOXY;

	/*
	 * Version
	 */
	private static final Version version = Version.getLatest();

	/**
	 * The main method.
	 *
	 * @param args
	 *            the arguments
	 * @throws AAIException
	 * @throws Exception
	 */
	public static void main(String[] args) {

		MDC.put("logFilenameAppender", DynamicPayloadGenerator.class.getSimpleName());
		DynamicPayloadGenerator payloadgen = new DynamicPayloadGenerator();
		try {
			payloadgen.init(args);

			payloadgen.generatePayloads();
		} catch (AAIException e) {
			LOGGER.error("Exception " + LogFormatTools.getStackTop(e));
		} catch (IOException e) {
			LOGGER.error("Exception " + LogFormatTools.getStackTop(e));
		}
		System.exit(0);
	}

	public void init(String[] args) throws AAIException {
		cArgs = new CommandLineArgs();
		JCommander jCommander = new JCommander(cArgs, args);
		jCommander.setProgramName(DynamicPayloadGenerator.class.getSimpleName());
		LOGGER.info("Snapshot file" + cArgs.dataSnapshot);
		//TODO- How to add dynamic.properties
		
		
		LOGGER.info("output file" + cArgs.output);
		
		
		LOGGER.info("format file" + cArgs.format);
		LOGGER.info("format file" + cArgs.schemaEnabled);
		if(cArgs.config.isEmpty())
			cArgs.config = AAIConstants.AAI_HOME_ETC_APP_PROPERTIES + "dynamic.properties";
		
		LOGGER.info("config file" + cArgs.config);
		if(cArgs.nodePropertyFile.isEmpty())
			cArgs.nodePropertyFile = AAIConstants.AAI_HOME_ETC_SCRIPT + "/tenant_isolation/nodes.json";
		LOGGER.info("nodePropertyFile file" + cArgs.nodePropertyFile);
		AAIConfig.init();

		urlBase = AAIConfig.get("aai.server.url.base", "");

	}

	public void generatePayloads() throws AAIException, IOException{
		
			List<Map<String, List<String>>> nodeFilters = readFile(cArgs.nodePropertyFile);
			LOGGER.info("Load the Graph");

			this.loadGraph();
			LOGGER.info("Generate payload");
			this.generatePayload(nodeFilters);
			LOGGER.info("Close graph");
			this.closeGraph();
		
	}

	private List<Map<String, List<String>>> readFile(String inputFile) throws IOException {

		InputStream is = new FileInputStream(inputFile);
		Scanner scanner = new Scanner(is);
		String jsonFile = scanner.useDelimiter("\\Z").next();
		scanner.close();

		List<Map<String, List<String>>> allNodes = new ArrayList<>();
		Map<String, List<String>> filterCousins = new HashMap<>();
		Map<String, List<String>> filterParents = new HashMap<>();

		ObjectMapper mapper = new ObjectMapper();

		JsonNode rootNode = mapper.readTree(jsonFile);

		Iterator<Entry<String, JsonNode>> nodeFields = rootNode.getFields();

		while (nodeFields.hasNext()) {
			Entry<String, JsonNode> entry = nodeFields.next();
			String nodeType = entry.getKey();
			JsonNode nodeProperty = entry.getValue();

			JsonNode cousinFilter = nodeProperty.path("cousins");
			JsonNode parentFilter = nodeProperty.path("parents");
			List<String> cousins = new ObjectMapper().readValue(cousinFilter.traverse(),
					new TypeReference<ArrayList<String>>() {
					});

			List<String> parents = new ObjectMapper().readValue(parentFilter.traverse(),
					new TypeReference<ArrayList<String>>() {
					});
			for (String cousin : cousins) {
				LOGGER.info("Cousins-Filtered" + cousin);
			}
			for (String parent : parents) {
				LOGGER.info("Parents-Filtered" + parent);
			}
			filterCousins.put(nodeType, cousins);
			filterParents.put(nodeType, parents);

		}

		allNodes.add(filterCousins);
		allNodes.add(filterParents);
		return allNodes;

	}

	private void loadGraph() throws IOException {

		loadGraphIntoMemory();
		buildDbEngine();

	}

	private void loadGraphIntoMemory() throws  IOException {

		inMemGraph = new InMemoryGraph.Builder().build(cArgs.dataSnapshot, cArgs.config, cArgs.schemaEnabled);

	}

	private void buildDbEngine() {
		// TODO : parametrise version
		loader = LoaderFactory.createLoaderForVersion(introspectorFactoryType, version);

		dbEngine = new InMemoryDBEngine(queryStyle, type, loader, inMemGraph.getGraph());
		dbEngine.startTransaction();
	}

	private void generatePayload(List<Map<String, List<String>>> nodeFilters) throws AAIException, IOException {

		Map<String, List<String>> filterCousinsMap = nodeFilters.get(0);
		Map<String, List<String>> filterParentsMap = nodeFilters.get(1);

		Set<String> nodeTypes = filterCousinsMap.keySet();

		for (String nodeType : nodeTypes) {
			if ("DMAAP-MR".equals(cArgs.format)) {
				bw = createFile(nodeType + ".json");
			}
			List<String> filterCousins = filterCousinsMap.get(nodeType);
			List<String> filterParents = filterParentsMap.get(nodeType);
			readVertices(nodeType, filterCousins, filterParents);
			bw.close();
			LOGGER.info("All Done-" + nodeType);
		}

	}

	private BufferedWriter createFile(String outfileName) throws IOException {
		// FileLocation
		String fileName = outfileName;

		File outFile = new File(fileName);
		LOGGER.info(" Will write to " + outFile);
		FileWriter fw = new FileWriter(outFile.getAbsoluteFile());
		return new BufferedWriter(fw);

	}

	private void createDirectory(String dirName) throws IOException {
		// FileLocation
		Path pathDir = Paths.get(dirName);
		Files.createDirectories(pathDir);
		
	}

	public void readVertices(String nodeType, List<String> filterCousins, List<String> filterParents)
			throws AAIException, IOException {

		DBSerializer serializer = new DBSerializer(version, dbEngine, introspectorFactoryType, "sourceOfTruth");
		List<Vertex> nodes = inMemGraph.getGraph().traversal().V().has("aai-node-type", nodeType).toList();

		LOGGER.info("Number of nodes" + nodes.size());
		String dirName = cArgs.output + AAIConstants.AAI_FILESEP + nodeType + AAIConstants.AAI_FILESEP;
		createDirectory( dirName);
		// TODO: Formatter
		if ("DMAAP-MR".equals(cArgs.format)) {
			for (Vertex node : nodes) {

				Introspector nodeObj = serializer.getLatestVersionView(node);
				createPayloadForDmaap(node, nodeObj);

			}
		}
		int counter = 0;
		if ("PAYLOAD".equals(cArgs.format)) {
			for (Vertex node : nodes) {

				counter++;
				String filename = dirName + counter + "-" + nodeType + ".json";
				bw = createFile(filename);
				Introspector obj = loader.introspectorFromName(nodeType);
				Set<Vertex> seen = new HashSet<>();
				int depth = AAIProperties.MAXIMUM_DEPTH;
				boolean nodeOnly = false;

				Tree<Element> tree = dbEngine.getQueryEngine().findSubGraph(node, depth, nodeOnly);
				TreeBackedVertex treeVertex = new TreeBackedVertex(node, tree);
				serializer.dbToObjectWithFilters(obj, treeVertex, seen, depth, nodeOnly, filterCousins, filterParents);
				createPayloadForPut(obj);
				bw.close();

				URI uri = serializer.getURIForVertex(node);
				String filenameWithUri = dirName + counter + "-" + nodeType + ".txt";
				bw = createFile(filenameWithUri);
				bw.write(uri.toString());
				bw.newLine();
				bw.close();
			}
		}

	}

	public void createPayloadForPut(Introspector nodeObj) throws IOException {

		String entityJson = nodeObj.marshal(false);
		ObjectMapper mapper = new ObjectMapper();

		ObjectNode rootNode = (ObjectNode) mapper.readTree(entityJson);
		rootNode.remove("resource-version");

		bw.newLine();
		bw.write(rootNode.toString());
		bw.newLine();
	}

	public void createPayloadForDmaap(Vertex node, Introspector nodeObj)
			throws AAIException, UnsupportedEncodingException {

		DBSerializer serializer = new DBSerializer(version, dbEngine, introspectorFactoryType, "sourceOfTruth");

		URI uri = serializer.getURIForVertex(node);

		String sourceOfTruth = "";
		HashMap<String, Introspector> relatedVertices = new HashMap<>();
		List<Vertex> vertexChain = dbEngine.getQueryEngine().findParents(node);

		for (Vertex vertex : vertexChain) {
			try {

				Introspector vertexObj = serializer.getVertexProperties(vertex);

				relatedVertices.put(vertexObj.getObjectId(), vertexObj);
			} catch (AAIUnknownObjectException e) {
				LOGGER.warn("Unable to get vertex properties, partial list of related vertices returned");
			}

		}

		String transactionId = "TXID";
		createNotificationEvent(transactionId, sourceOfTruth, uri, nodeObj, relatedVertices);

	}

	public void createNotificationEvent(String transactionId, String sourceOfTruth, URI uri, Introspector obj,
			Map<String, Introspector> relatedObjects) throws AAIException, UnsupportedEncodingException {

		String action = "CREATE";
		final Introspector notificationEvent = loader.introspectorFromName("notification-event");

		try {
			Introspector eventHeader = loader.introspectorFromName("notification-event-header");
			URIToObject parser = new URIToObject(loader, uri, (HashMap) relatedObjects);

			String entityLink = urlBase + version + uri;

			notificationEvent.setValue("cambria-partition", "AAI");

			eventHeader.setValue("entity-link", entityLink);
			eventHeader.setValue("action", action);
			eventHeader.setValue("entity-type", obj.getDbName());
			eventHeader.setValue("top-entity-type", parser.getTopEntityName());
			eventHeader.setValue("source-name", sourceOfTruth);
			eventHeader.setValue("version", version.toString());
			eventHeader.setValue("id", transactionId);
			eventHeader.setValue("event-type", "AAI-BASELINE");
			if (eventHeader.getValue("domain") == null) {
				eventHeader.setValue("domain", AAIConfig.get("aai.notificationEvent.default.domain", "UNK"));
			}

			if (eventHeader.getValue("sequence-number") == null) {
				eventHeader.setValue("sequence-number",
						AAIConfig.get("aai.notificationEvent.default.sequenceNumber", "UNK"));
			}

			if (eventHeader.getValue("severity") == null) {
				eventHeader.setValue("severity", AAIConfig.get("aai.notificationEvent.default.severity", "UNK"));
			}

			if (eventHeader.getValue("id") == null) {
				eventHeader.setValue("id", genDate2() + "-" + UUID.randomUUID().toString());

			}

			if (eventHeader.getValue("timestamp") == null) {
				eventHeader.setValue("timestamp", genDate());
			}

			List<Object> parentList = parser.getParentList();
			parentList.clear();

			if (!parser.getTopEntity().equals(parser.getEntity())) {
				Introspector child;
				String json = obj.marshal(false);
				child = parser.getLoader().unmarshal(parser.getEntity().getName(), json);
				parentList.add(child.getUnderlyingObject());
			}

			final Introspector eventObject;

			String json = "";
			if (parser.getTopEntity().equals(parser.getEntity())) {
				json = obj.marshal(false);
				eventObject = loader.unmarshal(obj.getName(), json);
			} else {
				json = parser.getTopEntity().marshal(false);

				eventObject = loader.unmarshal(parser.getTopEntity().getName(), json);
			}
			notificationEvent.setValue("event-header", eventHeader.getUnderlyingObject());
			notificationEvent.setValue("entity", eventObject.getUnderlyingObject());

			String entityJson = notificationEvent.marshal(false);

			bw.newLine();
			bw.write(entityJson);

		} catch (AAIUnknownObjectException e) {
			LOGGER.error("Fatal error - notification-event-header object not found!");
		} catch (Exception e) {
			LOGGER.error("Unmarshalling error occurred while generating Notification " + LogFormatTools.getStackTop(e));
		}
	}

	private void closeGraph() {
		inMemGraph.getGraph().tx().rollback();
		inMemGraph.getGraph().close();
	}

	public static String genDate() {
		Date date = new Date();
		DateFormat formatter = new SimpleDateFormat("yyyyMMdd-HH:mm:ss:SSS");
		return formatter.format(date);
	}

	public static String genDate2() {
		Date date = new Date();
		DateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
		return formatter.format(date);
	}

}

class CommandLineArgs {

	@Parameter(names = "--help", help = true)
	public boolean help;

	@Parameter(names = "-d", description = "snapshot file to be loaded")
	public String dataSnapshot;

	@Parameter(names = "-s", description = "is schema to be enabled ", arity = 1)
	public boolean schemaEnabled = true;

	@Parameter(names = "-c", description = "location of configuration file")
	public String config = "";

	@Parameter(names = "-o", description = "output location")
	public String output = "";

	@Parameter(names = "-f", description = "format of output")
	public String format = "PAYLOAD";

	@Parameter(names = "-n", description = "Node input file")
	public String nodePropertyFile = "";

}
