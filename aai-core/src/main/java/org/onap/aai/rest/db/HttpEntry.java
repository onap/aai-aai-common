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
package org.onap.aai.rest.db;


import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriBuilder;

import com.att.eelf.configuration.EELFLogger;
import com.att.eelf.configuration.EELFManager;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import org.apache.commons.lang.StringUtils;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.apache.tinkerpop.gremlin.structure.VertexProperty;
import org.janusgraph.core.JanusGraphException;
import org.javatuples.Pair;
import org.json.JSONException;
import org.json.JSONObject;
import org.onap.aai.db.props.AAIProperties;
import org.onap.aai.dbmap.DBConnectionType;
import org.onap.aai.domain.responseMessage.AAIResponseMessage;
import org.onap.aai.domain.responseMessage.AAIResponseMessageDatum;
import org.onap.aai.exceptions.AAIException;
import org.onap.aai.extensions.AAIExtensionMap;
import org.onap.aai.extensions.ExtensionController;
import org.onap.aai.introspection.*;
import org.onap.aai.introspection.exceptions.AAIUnknownObjectException;
import org.onap.aai.logging.ErrorLogHelper;
import org.onap.aai.logging.LoggingContext;
import org.onap.aai.nodes.NodeIngestor;
import org.onap.aai.parsers.query.QueryParser;
import org.onap.aai.parsers.uri.URIToExtensionInformation;

import org.onap.aai.parsers.uri.URIToObject;
import org.onap.aai.rest.ueb.UEBNotification;
import org.onap.aai.restcore.HttpMethod;
import org.onap.aai.schema.enums.ObjectMetadata;
import org.onap.aai.serialization.db.DBSerializer;
import org.onap.aai.serialization.engines.JanusGraphDBEngine;
import org.onap.aai.serialization.engines.QueryStyle;
import org.onap.aai.serialization.engines.TransactionalGraphEngine;
import org.onap.aai.serialization.engines.query.QueryEngine;
import org.onap.aai.serialization.queryformats.Format;
import org.onap.aai.serialization.queryformats.FormatFactory;
import org.onap.aai.serialization.queryformats.Formatter;
import org.onap.aai.setup.SchemaVersion;
import org.onap.aai.setup.SchemaVersions;
import org.onap.aai.util.AAIConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

/**
 * The Class HttpEntry.
 */
public class HttpEntry {

	private static final EELFLogger LOGGER = EELFManager.getInstance().getLogger(HttpEntry.class);
	private static final String TARGET_ENTITY = "DB";

	private ModelType introspectorFactoryType;

	private QueryStyle queryStyle;

	private SchemaVersion version;

	private Loader loader;

	private TransactionalGraphEngine dbEngine;

	private boolean processSingle = true;

	private int paginationBucket = -1;
	private int paginationIndex = -1;
	private int totalVertices = 0;
	private int totalPaginationBuckets = 0;

	@Autowired
	private NodeIngestor nodeIngestor;

	@Autowired
	private LoaderFactory loaderFactory;

	@Autowired
	private SchemaVersions schemaVersions;

	@Value("${schema.uri.base.path}")
	private String basePath;

	private UEBNotification notification;

	/**
	 * Instantiates a new http entry.
	 *
	 * @param modelType the model type
	 * @param queryStyle the query style
	 */
	public HttpEntry(ModelType modelType, QueryStyle queryStyle) {
		this.introspectorFactoryType = modelType;
		this.queryStyle = queryStyle;
	}

	public HttpEntry setHttpEntryProperties(SchemaVersion version, DBConnectionType connectionType) {
		this.version = version;
		this.loader = loaderFactory.createLoaderForVersion(introspectorFactoryType, version);
		this.dbEngine = new JanusGraphDBEngine(
			queryStyle,
			connectionType,
			loader);

		getDbEngine().startTransaction();
		this.notification = new UEBNotification(loader, loaderFactory, schemaVersions);
		return this;
	}


	public HttpEntry setHttpEntryProperties(SchemaVersion version, DBConnectionType connectionType, UEBNotification notification) {
		this.version = version;
		this.loader = loaderFactory.createLoaderForVersion(introspectorFactoryType, version);
		this.dbEngine = new JanusGraphDBEngine(
			queryStyle,
			connectionType,
			loader);

		this.notification = notification;
		//start transaction on creation
		getDbEngine().startTransaction();
		return this;
	}


	/**
	 * Gets the introspector factory type.
	 *
	 * @return the introspector factory type
	 */
	public ModelType getIntrospectorFactoryType() {
		return introspectorFactoryType;
	}

	/**
	 * Gets the query style.
	 *
	 * @return the query style
	 */
	public QueryStyle getQueryStyle() {
		return queryStyle;
	}

	/**
	 * Gets the version.
	 *
	 * @return the version
	 */
	public SchemaVersion getVersion() {
		return version;
	}

	/**
	 * Gets the loader.
	 *
	 * @return the loader
	 */
	public Loader getLoader() {
		return loader;
	}

	/**
	 * Gets the db engine.
	 *
	 * @return the db engine
	 */
	public TransactionalGraphEngine getDbEngine() {
		return dbEngine;
	}

	public Pair<Boolean, List<Pair<URI, Response>>> process(List<DBRequest> requests, String sourceOfTruth) throws AAIException {
		return this.process(requests, sourceOfTruth, true);
	}


	/**
	 * Checks the pagination bucket and pagination index variables to determine whether or not the user
	 * requested paginated results
	 *
	 * @return a boolean true/false of whether the user requested paginated results
	 */
	public boolean isPaginated() {
		return this.paginationBucket > -1 && this.paginationIndex > -1;
	}

	/**
	 * Returns the pagination size
	 * @return integer of the size of results to be returned when paginated
	 */
	public int getPaginationBucket() {
		return this.paginationBucket;
	}

	/**
	 * Setter for the pagination bucket variable which stores in this object the size of results to return
	 * @param pb
	 */
	public void setPaginationBucket(int pb) {
		this.paginationBucket = pb;
	}

	/**
	 * Getter to return the pagination index requested by the user when requesting paginated results
	 * @return
	 */
	public int getPaginationIndex() {
		return this.paginationIndex;
	}

	/**
	 * Sets the pagination index that was passed in by the user, to determine which index or results to retrieve when
	 * paginated
	 * @param pi
	 */
	public void setPaginationIndex(int pi) {
		if (pi == 0) {
			pi = 1;
		}
		this.paginationIndex = pi;
	}

	/**
	 * Sets the total vertices variables and calculates the amount of pages based on size and total vertices
	 * @param totalVertices
	 * @param paginationBucketSize
	 */
	public void setTotalsForPaging(int totalVertices, int paginationBucketSize) {
		this.totalVertices = totalVertices;
		//set total number of buckets equal to full pages
		this.totalPaginationBuckets = totalVertices / paginationBucketSize;
		//conditionally add a page for the remainder
		if (totalVertices % paginationBucketSize > 0) {
			this.totalPaginationBuckets++;
		}
	}

	/**
	 * @return the total amount of pages
	 */
	public int getTotalPaginationBuckets() {
		return this.totalPaginationBuckets;
	}

	/**
	 *
	 * @return the total number of vertices when paginated
	 */
	public int getTotalVertices() {
		return this.totalVertices;
	}

	/**
	 * Process.
	 * @param requests the requests
	 * @param sourceOfTruth the source of truth
	 *
	 * @return the pair
	 * @throws AAIException the AAI exception
	 */
	public Pair<Boolean, List<Pair<URI, Response>>> process(List<DBRequest> requests, String sourceOfTruth, boolean enableResourceVersion) throws AAIException {

		DBSerializer serializer = new DBSerializer(version, dbEngine, introspectorFactoryType, sourceOfTruth);
		String methodName = "process";
		Response response;
		Introspector obj = null;
		QueryParser query = null;
		URI uri = null;
		String transactionId = null;
		int depth = AAIProperties.MAXIMUM_DEPTH;
		Format format = null;
		List<Pair<URI, Response>> responses = new ArrayList<>();
		MultivaluedMap<String, String> params = null;
		HttpMethod method = null;
		String uriTemp = "";
		Boolean success = true;
		QueryEngine queryEngine = dbEngine.getQueryEngine();
		int maxRetries = 10;
		int retry = 0;

		LoggingContext.save();
		for (DBRequest request : requests) {
			response = null;
			Status status = Status.NOT_FOUND;
			method = request.getMethod();
			try {
				for (retry = 0; retry < maxRetries; ++retry) {
					try {

						LoggingContext.targetEntity(TARGET_ENTITY);
						LoggingContext.targetServiceName(methodName + " " + method);

						obj = request.getIntrospector();
						query = request.getParser();
						transactionId = request.getTransactionId();
						uriTemp = request.getUri().getRawPath().replaceFirst("^v\\d+/", "");
						uri = UriBuilder.fromPath(uriTemp).build();
						LoggingContext.startTime();
						List<Vertex> vertTemp;
						List<Vertex> vertices;
						if (this.isPaginated()) {
							vertTemp = query.getQueryBuilder().toList();
							this.setTotalsForPaging(vertTemp.size(), this.paginationBucket);
							vertices = vertTemp.subList(((this.paginationIndex - 1) * this.paginationBucket), Math.min((this.paginationBucket * this.paginationIndex), vertTemp.size()));
						} else {
							vertices = query.getQueryBuilder().toList();
						}
						boolean isNewVertex = false;
						String outputMediaType = getMediaType(request.getHeaders().getAcceptableMediaTypes());
						String result = null;
						params = request.getInfo().getQueryParameters(false);
						depth = setDepth(obj, params.getFirst("depth"));
						if (params.containsKey("format")) {
							format = Format.getFormat(params.getFirst("format"));
						}
						String cleanUp = params.getFirst("cleanup");
						String requestContext = "";
						List<String> requestContextList = request.getHeaders().getRequestHeader("aai-request-context");
						if (requestContextList != null) {
							requestContext = requestContextList.get(0);
						}

						if (cleanUp == null) {
							cleanUp = "false";
						}
						if (vertices.size() > 1 && processSingle && !(method.equals(HttpMethod.GET) || method.equals(HttpMethod.GET_RELATIONSHIP))) {
							if (method.equals(HttpMethod.DELETE)) {
								LoggingContext.restoreIfPossible();
								throw new AAIException("AAI_6138");
							} else {
								LoggingContext.restoreIfPossible();
								throw new AAIException("AAI_6137");
							}
						}
						if (method.equals(HttpMethod.PUT)) {
							String resourceVersion = (String) obj.getValue("resource-version");
							if (vertices.isEmpty()) {
								if (enableResourceVersion) {
									serializer.verifyResourceVersion("create", query.getResultType(), "", resourceVersion, obj.getURI());
								}
								isNewVertex = true;
							} else {
								if (enableResourceVersion) {
									serializer.verifyResourceVersion("update", query.getResultType(), vertices.get(0).<String>property("resource-version").orElse(null), resourceVersion, obj.getURI());
								}
								isNewVertex = false;
							}
						} else {
							if (vertices.isEmpty()) {
								String msg = createNotFoundMessage(query.getResultType(), request.getUri());
								throw new AAIException("AAI_6114", msg);
							} else {
								isNewVertex = false;
							}
						}
						Vertex v = null;
						if (!isNewVertex) {
							v = vertices.get(0);
						}
						HashMap<String, Introspector> relatedObjects = new HashMap<>();
						String nodeOnly = params.getFirst("nodes-only");
						boolean isNodeOnly = nodeOnly != null;
						switch (method) {
							case GET:

								if (format == null) {
									obj = this.getObjectFromDb(vertices, serializer, query, obj, request.getUri(), depth, isNodeOnly, cleanUp);


									LoggingContext.elapsedTime((long) serializer.getDBTimeMsecs(), TimeUnit.MILLISECONDS);
									LOGGER.info("Completed");
									LoggingContext.restoreIfPossible();

									if (obj != null) {
										status = Status.OK;
										MarshallerProperties properties;
										if (!request.getMarshallerProperties().isPresent()) {
											properties = new MarshallerProperties.Builder(org.onap.aai.restcore.MediaType.getEnum(outputMediaType)).build();
										} else {
											properties = request.getMarshallerProperties().get();
										}
										result = obj.marshal(properties);
									}
								} else {
									FormatFactory ff = new FormatFactory(loader, serializer, schemaVersions, basePath + "/");
									Formatter formatter = ff.get(format, params);
									result = formatter.output(vertices.stream().map(vertex -> (Object) vertex).collect(Collectors.toList())).toString();
									status = Status.OK;
								}

								break;
							case GET_RELATIONSHIP:
								if (format == null) {
									obj = this.getRelationshipObjectFromDb(vertices, serializer, query, request.getInfo().getRequestUri());

									LoggingContext.elapsedTime((long) serializer.getDBTimeMsecs(), TimeUnit.MILLISECONDS);
									LOGGER.info("Completed");
									LoggingContext.restoreIfPossible();

									if (obj != null) {
										status = Status.OK;
										MarshallerProperties properties;
										if (!request.getMarshallerProperties().isPresent()) {
											properties = new MarshallerProperties.Builder(org.onap.aai.restcore.MediaType.getEnum(outputMediaType)).build();
										} else {
											properties = request.getMarshallerProperties().get();
										}
										result = obj.marshal(properties);
									} else {
										String msg = createRelationshipNotFoundMessage(query.getResultType(), request.getUri());
										throw new AAIException("AAI_6149", msg);
									}
								} else {
									FormatFactory ff = new FormatFactory(loader, serializer, schemaVersions, basePath + "/");
									Formatter formatter = ff.get(format, params);
									result = formatter.output(vertices.stream().map(vertex -> (Object) vertex).collect(Collectors.toList())).toString();
									status = Status.OK;
								}
								break;
							case PUT:
								response = this.invokeExtension(dbEngine, this.dbEngine.tx(), method, request, sourceOfTruth, version, loader, obj, uri, true);
								if (isNewVertex) {
									v = serializer.createNewVertex(obj);
								}
								serializer.serializeToDb(obj, v, query, uri.getRawPath(), requestContext);
								this.invokeExtension(dbEngine, this.dbEngine.tx(), HttpMethod.PUT, request, sourceOfTruth, version, loader, obj, uri, false);
								status = Status.OK;
								if (isNewVertex) {
									status = Status.CREATED;
								}
								obj = serializer.getLatestVersionView(v);
								if (query.isDependent()) {
									relatedObjects = this.getRelatedObjects(serializer, queryEngine, v, obj, this.loader);
								}
								LoggingContext.elapsedTime((long) serializer.getDBTimeMsecs() +
									(long) queryEngine.getDBTimeMsecs(), TimeUnit.MILLISECONDS);
								LOGGER.info("Completed ");
								LoggingContext.restoreIfPossible();
								notification.createNotificationEvent(transactionId, sourceOfTruth, status, uri, obj, relatedObjects, basePath);

								break;
							case PUT_EDGE:
								serializer.touchStandardVertexProperties(v, false);
								this.invokeExtension(dbEngine, this.dbEngine.tx(), method, request, sourceOfTruth, version, loader, obj, uri, true);
								serializer.createEdge(obj, v);

								LoggingContext.elapsedTime((long) serializer.getDBTimeMsecs(), TimeUnit.MILLISECONDS);
								LOGGER.info("Completed");
								LoggingContext.restoreIfPossible();
								status = Status.OK;
								notification.createNotificationEvent(transactionId, sourceOfTruth, status, new URI(uri.toString().replace("/relationship-list/relationship", "")), serializer.getLatestVersionView(v), relatedObjects, basePath);
								break;
							case MERGE_PATCH:
								Introspector existingObj = loader.introspectorFromName(obj.getDbName());
								existingObj = this.getObjectFromDb(vertices, serializer, query, existingObj, request.getUri(), 0, false, cleanUp);
								String existingJson = existingObj.marshal(false);
								String newJson;

								if (request.getRawRequestContent().isPresent()) {
									newJson = request.getRawRequestContent().get();
								} else {
									newJson = "";
								}
								Object relationshipList = request.getIntrospector().getValue("relationship-list");
								ObjectMapper mapper = new ObjectMapper();
								try {
									JsonNode existingNode = mapper.readTree(existingJson);
									JsonNode newNode = mapper.readTree(newJson);
									JsonMergePatch patch = JsonMergePatch.fromJson(newNode);
									JsonNode completed = patch.apply(existingNode);
									String patched = mapper.writeValueAsString(completed);
									Introspector patchedObj = loader.unmarshal(existingObj.getName(), patched);
									if (relationshipList == null) {
										//if the caller didn't touch the relationship-list, we shouldn't either
										patchedObj.setValue("relationship-list", null);
									}
									serializer.serializeToDb(patchedObj, v, query, uri.getRawPath(), requestContext);
									status = Status.OK;
									patchedObj = serializer.getLatestVersionView(v);
									if (query.isDependent()) {
										relatedObjects = this.getRelatedObjects(serializer, queryEngine, v, patchedObj, this.loader);
									}
									LoggingContext.elapsedTime((long) serializer.getDBTimeMsecs() +
										(long) queryEngine.getDBTimeMsecs(), TimeUnit.MILLISECONDS);
									LOGGER.info("Completed");
									LoggingContext.restoreIfPossible();
									notification.createNotificationEvent(transactionId, sourceOfTruth, status, uri, patchedObj, relatedObjects, basePath);
								} catch (IOException | JsonPatchException e) {

									LOGGER.info("Caught exception: " + e.getMessage());
									LoggingContext.restoreIfPossible();
									throw new AAIException("AAI_3000", "could not perform patch operation");
								}
								break;
							case DELETE:
								String resourceVersion = params.getFirst("resource-version");
								obj = serializer.getLatestVersionView(v);
								if (query.isDependent()) {
									relatedObjects = this.getRelatedObjects(serializer, queryEngine, v, obj, this.loader);
								}
								/*
								 * Find all Delete-other-vertex vertices and create structure for notify
								 * findDeleatble also returns the startVertex v and we dont want to create
								 * duplicate notification events for the same
								 * So remove the startvertex first
								 */

								List<Vertex> deletableVertices = dbEngine.getQueryEngine().findDeletable(v);
								Long vId = (Long) v.id();

								/*
								 * I am assuming vertexId cant be null
								 */
								deletableVertices.removeIf(s -> vId.equals(s.id()));
								boolean isDelVerticesPresent = !deletableVertices.isEmpty();
								Map<Vertex, Introspector> deleteObjects = new HashMap<>();
								Map<String, URI> uriMap = new HashMap<>();
								Map<String, HashMap<String, Introspector>> deleteRelatedObjects = new HashMap<>();

								if (isDelVerticesPresent) {
									deleteObjects = this.buildIntrospectorObjects(serializer, deletableVertices);

									uriMap = this.buildURIMap(serializer, deleteObjects);
									deleteRelatedObjects = this.buildRelatedObjects(serializer, queryEngine, deleteObjects);
								}

								this.invokeExtension(dbEngine, this.dbEngine.tx(), method, request, sourceOfTruth, version, loader, obj, uri, true);
								serializer.delete(v, deletableVertices, resourceVersion, enableResourceVersion);
								this.invokeExtension(dbEngine, this.dbEngine.tx(), method, request, sourceOfTruth, version, loader, obj, uri, false);

								LoggingContext.elapsedTime((long) serializer.getDBTimeMsecs() +
									(long) queryEngine.getDBTimeMsecs(), TimeUnit.MILLISECONDS);
								LOGGER.info("Completed");
								LoggingContext.restoreIfPossible();
								status = Status.NO_CONTENT;
								notification.createNotificationEvent(transactionId, sourceOfTruth, status, uri, obj, relatedObjects, basePath);

								/*
								 * Notify delete-other-v candidates
								 */

								if (isDelVerticesPresent) {
									this.buildNotificationEvent(sourceOfTruth, status, transactionId, notification, deleteObjects,
										uriMap, deleteRelatedObjects, basePath);
								}

								break;
							case DELETE_EDGE:
								serializer.touchStandardVertexProperties(v, false);
								serializer.deleteEdge(obj, v);

								LoggingContext.elapsedTime((long) serializer.getDBTimeMsecs(), TimeUnit.MILLISECONDS);
								LOGGER.info("Completed");
								LoggingContext.restoreIfPossible();
								status = Status.NO_CONTENT;
								notification.createNotificationEvent(transactionId, sourceOfTruth, Status.OK, new URI(uri.toString().replace("/relationship-list/relationship", "")), serializer.getLatestVersionView(v), relatedObjects, basePath);
								break;
							default:
								break;
						}


						/* temporarily adding vertex id to the headers
						 * to be able to use for testing the vertex id endpoint functionality
						 * since we presently have no other way of generating those id urls
						 */
						if (response == null && v != null && (
							method.equals(HttpMethod.PUT)
								|| method.equals(HttpMethod.GET)
								|| method.equals(HttpMethod.MERGE_PATCH)
								|| method.equals(HttpMethod.GET_RELATIONSHIP))

							) {
							String myvertid = v.id().toString();
							if (this.isPaginated()) {
								response = Response.status(status)
									.header("vertex-id", myvertid)
									.header("total-results", this.getTotalVertices())
									.header("total-pages", this.getTotalPaginationBuckets())
									.entity(result)
									.type(outputMediaType).build();
							} else {
								response = Response.status(status)
									.header("vertex-id", myvertid)
									.entity(result)
									.type(outputMediaType).build();
							}
						} else if (response == null) {
							response = Response.status(status)
								.type(outputMediaType).build();
						} else {
							//response already set to something
						}
						Pair<URI, Response> pairedResp = Pair.with(request.getUri(), response);
						responses.add(pairedResp);
						//break out of retry loop
						break;
					} catch (JanusGraphException e) {
						this.dbEngine.rollback();

						LOGGER.info("Caught exception: " + e.getMessage());
						LoggingContext.restoreIfPossible();
						AAIException ex = new AAIException("AAI_6142", e);
						ErrorLogHelper.logException(ex);
						Thread.sleep((retry + 1) * 20L);
						this.dbEngine.startTransaction();
						queryEngine = dbEngine.getQueryEngine();
						serializer = new DBSerializer(version, dbEngine, introspectorFactoryType, sourceOfTruth);
					}
					if (retry == maxRetries) {
						throw new AAIException("AAI_6134");
					}
				}
			} catch (AAIException e) {
				success = false;
				ArrayList<String> templateVars = new ArrayList<>();
				templateVars.add(request.getMethod().toString()); //GET, PUT, etc
				templateVars.add(request.getUri().getPath());
				templateVars.addAll(e.getTemplateVars());
				ErrorLogHelper.logException(e);
				response = Response
					.status(e.getErrorObject().getHTTPResponseCode())
					.entity(ErrorLogHelper.getRESTAPIErrorResponse(request.getHeaders().getAcceptableMediaTypes(), e, templateVars))
					.build();
				Pair<URI, Response> pairedResp = Pair.with(request.getUri(), response);
				responses.add(pairedResp);
				continue;
			} catch (Exception e) {
				success = false;
				LOGGER.info("Error occured in process"+e);
				AAIException ex = new AAIException("AAI_4000", e);
				ArrayList<String> templateVars = new ArrayList<String>();
				templateVars.add(request.getMethod().toString()); //GET, PUT, etc
				templateVars.add(request.getUri().getPath().toString());
				ErrorLogHelper.logException(ex);
				response = Response
					.status(ex.getErrorObject().getHTTPResponseCode())
					.entity(ErrorLogHelper.getRESTAPIErrorResponse(request.getHeaders().getAcceptableMediaTypes(), ex, templateVars))
					.build();
				Pair<URI, Response> pairedResp = Pair.with(request.getUri(), response);
				responses.add(pairedResp);
				continue;
			}
		}
		notification.triggerEvents();
		return Pair.with(success, responses);
	}


	/**
	 * Gets the media type.
	 *
	 * @param mediaTypeList the media type list
	 * @return the media type
	 */
	private String getMediaType(List<MediaType> mediaTypeList) {
		String mediaType = MediaType.APPLICATION_JSON;  // json is the default
		for (MediaType mt : mediaTypeList) {
			if (MediaType.APPLICATION_XML_TYPE.isCompatible(mt)) {
				mediaType = MediaType.APPLICATION_XML;
			}
		}
		return mediaType;
	}

	/**
	 * Gets the object from db.
	 *
	 * @param serializer the serializer
	 * @param query the query
	 * @param obj the obj
	 * @param uri the uri
	 * @param depth the depth
	 * @param cleanUp the clean up
	 * @return the object from db
	 * @throws AAIException the AAI exception
	 * @throws IllegalAccessException the illegal access exception
	 * @throws IllegalArgumentException the illegal argument exception
	 * @throws InvocationTargetException the invocation target exception
	 * @throws SecurityException the security exception
	 * @throws InstantiationException the instantiation exception
	 * @throws NoSuchMethodException the no such method exception
	 * @throws UnsupportedEncodingException the unsupported encoding exception
	 * @throws MalformedURLException the malformed URL exception
	 * @throws AAIUnknownObjectException
	 * @throws URISyntaxException
	 */
	private Introspector getObjectFromDb(List<Vertex> results, DBSerializer serializer, QueryParser query, Introspector obj, URI uri, int depth, boolean nodeOnly, String cleanUp) throws AAIException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, SecurityException, InstantiationException, NoSuchMethodException, UnsupportedEncodingException, AAIUnknownObjectException, URISyntaxException {

		//nothing found
		if (results.isEmpty()) {
			String msg = createNotFoundMessage(query.getResultType(), uri);
			throw new AAIException("AAI_6114", msg);
		}

		return serializer.dbToObject(results, obj, depth, nodeOnly, cleanUp);

	}

	/**
	 * Gets the object from db.
	 *
	 * @param serializer the serializer
	 * @param query the query
	 * @param obj the obj
	 * @param uri the uri
	 * @param depth the depth
	 * @param cleanUp the clean up
	 * @return the object from db
	 * @throws AAIException the AAI exception
	 * @throws IllegalAccessException the illegal access exception
	 * @throws IllegalArgumentException the illegal argument exception
	 * @throws InvocationTargetException the invocation target exception
	 * @throws SecurityException the security exception
	 * @throws InstantiationException the instantiation exception
	 * @throws NoSuchMethodException the no such method exception
	 * @throws UnsupportedEncodingException the unsupported encoding exception
	 * @throws MalformedURLException the malformed URL exception
	 * @throws AAIUnknownObjectException
	 * @throws URISyntaxException
	 */
	private Introspector getRelationshipObjectFromDb(List<Vertex> results, DBSerializer serializer, QueryParser query, URI uri) throws AAIException, IllegalArgumentException, SecurityException, UnsupportedEncodingException, AAIUnknownObjectException {

		//nothing found
		if (results.isEmpty()) {
			String msg = createNotFoundMessage(query.getResultType(), uri);
			throw new AAIException("AAI_6114", msg);
		}

		if (results.size() > 1) {
			throw new AAIException("AAI_6148", uri.getPath());
		}

		Vertex v = results.get(0);
		return serializer.dbToRelationshipObject(v);
	}

	/**
	 * Invoke extension.
	 *
	 * @param dbEngine the db engine
	 * @param g the g
	 * @param httpMethod the http method
	 * @param fromAppId the from app id
	 * @param apiVersion the api version
	 * @param loader the loader
	 * @param obj the obj
	 * @param uri the uri
	 * @param isPreprocess the is preprocess
	 * @return the response
	 * @throws IllegalArgumentException the illegal argument exception
	 * @throws UnsupportedEncodingException the unsupported encoding exception
	 * @throws AAIException the AAI exception
	 */
	private Response invokeExtension(TransactionalGraphEngine dbEngine, Graph g, HttpMethod httpMethod, DBRequest request, String fromAppId, SchemaVersion apiVersion, Loader loader, Introspector obj, URI uri, boolean isPreprocess) throws IllegalArgumentException, UnsupportedEncodingException, AAIException {
		AAIExtensionMap aaiExtMap = new AAIExtensionMap();
		//ModelInjestor injestor = ModelInjestor.getInstance();
		Response response = null;
		URIToExtensionInformation extensionInformation = new URIToExtensionInformation(loader, uri);
		aaiExtMap.setHttpEntry(this);
		aaiExtMap.setDbRequest(request);
		aaiExtMap.setTransId(request.getTransactionId());
		aaiExtMap.setFromAppId(fromAppId);
		aaiExtMap.setGraph(g);
		aaiExtMap.setApiVersion(apiVersion.toString());
		aaiExtMap.setObjectFromRequest(obj);
		aaiExtMap.setObjectFromRequestType(obj.getJavaClassName());
		aaiExtMap.setObjectFromResponse(obj);
		aaiExtMap.setObjectFromResponseType(obj.getJavaClassName());
		aaiExtMap.setJaxbContext(nodeIngestor.getContextForVersion(apiVersion));
		aaiExtMap.setUri(uri.getRawPath());
		aaiExtMap.setTransactionalGraphEngine(dbEngine);
		aaiExtMap.setLoader(loader);
		aaiExtMap.setNamespace(extensionInformation.getNamespace());

		ExtensionController ext = new ExtensionController();
		ext.runExtension(aaiExtMap.getApiVersion(),
			extensionInformation.getNamespace(),
			extensionInformation.getTopObject(),
			extensionInformation.getMethodName(httpMethod, isPreprocess),
			aaiExtMap,
			isPreprocess);

		if (aaiExtMap.getPrecheckAddedList().size() > 0) {
			response = notifyOnSkeletonCreation(aaiExtMap, obj, request.getHeaders());
		}

		return response;
	}

	/**
	 * Notify on skeleton creation.
	 *
	 * @param aaiExtMap the aai ext map
	 * @param input the input
	 * @param headers the headers
	 * @return the response
	 */
	//Legacy support
	private Response notifyOnSkeletonCreation(AAIExtensionMap aaiExtMap, Introspector input, HttpHeaders headers) {
		Response response = null;
		HashMap<AAIException, ArrayList<String>> exceptionList = new HashMap<AAIException, ArrayList<String>>();

		StringBuilder keyString = new StringBuilder();

		Set<String> resourceKeys = input.getKeys();
		for (String key : resourceKeys) {
			keyString.append(key).append("=").append(input.getValue(key).toString()).append(" ");
		}

		for (AAIResponseMessage msg : aaiExtMap.getPrecheckResponseMessages().getAAIResponseMessage()) {
			ArrayList<String> templateVars = new ArrayList<>();

			templateVars.add("PUT " + input.getDbName());
			templateVars.add(keyString.toString());
			List<String> keys = new ArrayList<>();
			templateVars.add(msg.getAaiResponseMessageResourceType());
			for (AAIResponseMessageDatum dat : msg.getAaiResponseMessageData().getAAIResponseMessageDatum()) {
				keys.add(dat.getAaiResponseMessageDatumKey() + "=" + dat.getAaiResponseMessageDatumValue());
			}
			templateVars.add(StringUtils.join(keys, ", "));
			exceptionList.put(new AAIException("AAI_0004", msg.getAaiResponseMessageResourceType()),
				templateVars);
		}
		response = Response
			.status(Status.ACCEPTED).entity(ErrorLogHelper
				.getRESTAPIInfoResponse(headers.getAcceptableMediaTypes(), exceptionList))
			.build();

		return response;
	}

	/**
	 * Creates the not found message.
	 *
	 * @param resultType the result type
	 * @param uri the uri
	 * @return the string
	 */
	private String createNotFoundMessage(String resultType, URI uri) {

		String msg = "No Node of type " + resultType + " found at: " + uri.getPath();

		return msg;
	}

	/**
	 * Creates the not found message.
	 *
	 * @param resultType the result type
	 * @param uri the uri
	 * @return the string
	 */
	private String createRelationshipNotFoundMessage(String resultType, URI uri) {

		String msg = "No relationship found of type " + resultType + " at the given URI: " + uri.getPath() + "/relationship-list";

		return msg;
	}

	/**
	 * Sets the depth.
	 *
	 * @param depthParam the depth param
	 * @return the int
	 * @throws AAIException the AAI exception
	 */
	protected int setDepth(Introspector obj, String depthParam) throws AAIException {
		int depth = AAIProperties.MAXIMUM_DEPTH;

		String getAllRandomStr = AAIConfig.get("aai.rest.getall.depthparam", "");
		if (depthParam != null && getAllRandomStr != null && !getAllRandomStr.isEmpty()
			&& getAllRandomStr.equals(depthParam)) {
			return depth;
		}

		if (depthParam == null) {
			if (this.version.compareTo(schemaVersions.getDepthVersion()) >= 0) {
				depth = 0;
			} else {
				depth = AAIProperties.MAXIMUM_DEPTH;
			}
		} else {
			if (!depthParam.isEmpty() && !"all".equals(depthParam)) {
				try {
					depth = Integer.parseInt(depthParam);
				} catch (Exception e) {
					throw new AAIException("AAI_4016");
				}

			}
		}
		String maxDepth = obj.getMetadata(ObjectMetadata.MAXIMUM_DEPTH);

		int maximumDepth = AAIProperties.MAXIMUM_DEPTH;

		if (maxDepth != null) {
			try {
				maximumDepth = Integer.parseInt(maxDepth);
			} catch (Exception ex) {
				throw new AAIException("AAI_4018");
			}
		}

		if (depth > maximumDepth) {
			throw new AAIException("AAI_3303");
		}

		return depth;
	}

	/**
	 * Checks if is modification method.
	 *
	 * @param method the method
	 * @return true, if is modification method
	 */
	private boolean isModificationMethod(HttpMethod method) {
		boolean result = false;

		if (method.equals(HttpMethod.PUT) || method.equals(HttpMethod.PUT_EDGE) || method.equals(HttpMethod.DELETE_EDGE) || method.equals(HttpMethod.MERGE_PATCH)) {
			result = true;
		}

		return result;

	}

	/**
	 * Given an uri, introspector object and loader object
	 * it will check if the obj is top level object if it is,
	 * it will return immediately returning the uri passed in
	 * If it isn't, it will go through, get the uriTemplate
	 * from the introspector object and get the count of "/"s
	 * and remove that part of the uri using substring
	 * and keep doing that until the current object is top level
	 * Also added the max depth just so worst case scenario
	 * Then keep adding aai-uri to the list include the aai-uri passed in
	 * Convert that list into an array and return it
	 * <p>
	 *
	 * Example:
	 *
	 * <blockquote>
	 * aai-uri -> /cloud-infrastructure/cloud-regions/cloud-region/cloud-owner/cloud-region-id/tenants/tenant/tenant1/vservers/vserver/v1
	 *
	 * Given the uriTemplate vserver -> /vservers/vserver/{vserver-id}
	 * it converts to /vservers/vserver
	 *
	 * lastIndexOf /vservers/vserver in /cloud-infrastructure/cloud-regions/cloud-region/cloud-owner/cloud-region-id/tenants/tenant/tenant1/vservers/vserver/v1
	 *																																	^
	 *																																	|
	 *																																	|
	 *																																lastIndexOf
	 * Use substring to get the string from 0 to that lastIndexOf
	 * aai-uri -> /cloud-infrastructure/cloud-regions/cloud-region/cloud-owner/cloud-region-id/tenants/tenant/tenant1
	 *
	 * From this new aai-uri, generate a introspector from the URITOObject class
	 * and keep doing this until you
	 *
	 * </blockquote>
	 *
	 * @param aaiUri - aai-uri of the vertex representating the unique id of a given vertex
	 * @param obj	- introspector object of the given starting vertex
	 * @param loader - Type of loader which will always be MoxyLoader to support model driven
	 * @return an array of strings which can be used to get the vertexes of parent and grand parents from a given vertex
	 * @throws UnsupportedEncodingException
	 * @throws AAIException
	 */
	String[] convertIntrospectorToUriList(String aaiUri, Introspector obj, Loader loader) throws UnsupportedEncodingException, AAIException {

		List<String> uriList = new ArrayList<>();
		String template = StringUtils.EMPTY;
		String truncatedUri = aaiUri;
		int depth = AAIProperties.MAXIMUM_DEPTH;
		uriList.add(truncatedUri);

		while (depth >= 0 && !obj.isTopLevel()) {
			template = obj.getMetadata(ObjectMetadata.URI_TEMPLATE);

			if (template == null) {
				LOGGER.warn("Unable to find the uriTemplate for the object {}", obj.getDbName());
				return null;
			}

			int templateCount = StringUtils.countMatches(template, "/");
			int truncatedUriCount = StringUtils.countMatches(truncatedUri, "/");

			if (templateCount > truncatedUriCount) {
				LOGGER.warn("Template uri {} contains more slashes than truncatedUri {}", template, truncatedUri);
				return null;
			}

			int cutIndex = StringUtils.ordinalIndexOf(truncatedUri, "/", truncatedUriCount - templateCount + 1);
			truncatedUri = StringUtils.substring(truncatedUri, 0, cutIndex);
			uriList.add(truncatedUri);
			obj = new URIToObject(loader, UriBuilder.fromPath(truncatedUri).build()).getEntity();
			depth--;
		}

		return uriList.toArray(new String[uriList.size()]);
	}

	/**
	 *
	 * @param serializer
	 * @param queryEngine
	 * @param v
	 * @param obj
	 * @param loader
	 * @return
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 * @throws SecurityException
	 * @throws InstantiationException
	 * @throws NoSuchMethodException
	 * @throws UnsupportedEncodingException
	 * @throws AAIException
	 * @throws URISyntaxException
	 */
	private HashMap<String, Introspector> getRelatedObjects(DBSerializer serializer, QueryEngine queryEngine, Vertex v, Introspector obj, Loader loader) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, SecurityException, InstantiationException, NoSuchMethodException, UnsupportedEncodingException, AAIException, URISyntaxException {

		HashMap<String, Introspector> relatedVertices = new HashMap<>();
		VertexProperty aaiUriProperty = v.property(AAIProperties.AAI_URI);

		if (!aaiUriProperty.isPresent()) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("For the given vertex {}, it seems aai-uri is not present so not getting related objects", v.id().toString());
			} else {
				LOGGER.info("It seems aai-uri is not present in vertex, so not getting related objects, for more info enable debug log");
			}
			return relatedVertices;
		}

		String aaiUri = aaiUriProperty.value().toString();

		if (!obj.isTopLevel()) {
			String[] uriList = convertIntrospectorToUriList(aaiUri, obj, loader);
			List<Vertex> vertexChain = null;
			// If the uriList is null then there is something wrong with converting the uri
			// into a list of aai-uris so falling back to the old mechanism for finding parents
			if (uriList == null) {
				LOGGER.info("Falling back to the old mechanism due to unable to convert aai-uri to list of uris but this is not optimal");
				vertexChain = queryEngine.findParents(v);
			} else {
				vertexChain = queryEngine.findParents(uriList);
			}
			for (Vertex vertex : vertexChain) {
				try {
					final Introspector vertexObj = serializer.getVertexProperties(vertex);
					relatedVertices.put(vertexObj.getObjectId(), vertexObj);
				} catch (AAIUnknownObjectException e) {
					LOGGER.warn("Unable to get vertex properties, partial list of related vertices returned");
				}
			}
		} else {
			try {
				final Introspector vertexObj = serializer.getVertexProperties(v);
				relatedVertices.put(vertexObj.getObjectId(), vertexObj);
			} catch (AAIUnknownObjectException e) {
				LOGGER.warn("Unable to get vertex properties, partial list of related vertices returned");
			}
		}

		return relatedVertices;
	}

	private Map<Vertex, Introspector> buildIntrospectorObjects(DBSerializer serializer, Iterable<Vertex> vertices) {
		Map<Vertex, Introspector> deleteObjectMap = new HashMap<>();
		for (Vertex vertex : vertices) {
			try {
				// deleteObjectMap.computeIfAbsent(vertex, s ->
				// serializer.getLatestVersionView(vertex));
				Introspector deleteObj = serializer.getLatestVersionView(vertex);
				deleteObjectMap.put(vertex, deleteObj);
			} catch (UnsupportedEncodingException | AAIException e) {
				LOGGER.warn("Unable to get Introspctor Objects, Just continue");
				continue;
			}

		}

		return deleteObjectMap;

	}

	private Map<String, URI> buildURIMap(DBSerializer serializer, Map<Vertex, Introspector> introSpector) {
		Map<String, URI> uriMap = new HashMap<>();
		for (Map.Entry<Vertex, Introspector> entry : introSpector.entrySet()) {
			URI uri;
			try {
				uri = serializer.getURIForVertex(entry.getKey());
				if (null != entry.getValue()) {
					uriMap.put(entry.getValue().getObjectId(), uri);
				}
			} catch (UnsupportedEncodingException e) {
				LOGGER.warn("Unable to get URIs, Just continue");
				continue;
			}

		}

		return uriMap;

	}

	private Map<String, HashMap<String, Introspector>> buildRelatedObjects(DBSerializer serializer,
																		   QueryEngine queryEngine, Map<Vertex, Introspector> introSpector) {

		Map<String, HashMap<String, Introspector>> relatedObjectsMap = new HashMap<>();
		for (Map.Entry<Vertex, Introspector> entry : introSpector.entrySet()) {
			try {
				HashMap<String, Introspector> relatedObjects = this.getRelatedObjects(serializer, queryEngine,
					entry.getKey(), entry.getValue(), this.loader);
				if (null != entry.getValue()) {
					relatedObjectsMap.put(entry.getValue().getObjectId(), relatedObjects);
				}
			} catch (IllegalAccessException | IllegalArgumentException
				| InvocationTargetException | SecurityException | InstantiationException | NoSuchMethodException
				| UnsupportedEncodingException | AAIException | URISyntaxException e) {
				LOGGER.warn("Unable to get realted Objects, Just continue");
				continue;
			}

		}

		return relatedObjectsMap;

	}

	private void buildNotificationEvent(String sourceOfTruth, Status status, String transactionId,
										UEBNotification notification, Map<Vertex, Introspector> deleteObjects, Map<String, URI> uriMap,
										Map<String, HashMap<String, Introspector>> deleteRelatedObjects, String basePath) {
		for (Map.Entry<Vertex, Introspector> entry : deleteObjects.entrySet()) {
			try {
				String vertexObjectId = "";

				if (null != entry.getValue()) {
					vertexObjectId = entry.getValue().getObjectId();

					if (uriMap.containsKey(vertexObjectId) && deleteRelatedObjects.containsKey(vertexObjectId)) {
						notification.createNotificationEvent(transactionId, sourceOfTruth, status,
							uriMap.get(vertexObjectId), entry.getValue(), deleteRelatedObjects.get(vertexObjectId), basePath);
					}
				}
			} catch (UnsupportedEncodingException | AAIException e) {

				LOGGER.warn("Error in sending notification");
			}
		}
	}

	public void setPaginationParameters(String resultIndex, String resultSize) {
		if (resultIndex != null && resultIndex != "-1" && resultSize != null && resultSize != "-1") {
			this.setPaginationIndex(Integer.parseInt(resultIndex));
			this.setPaginationBucket(Integer.parseInt(resultSize));
		}
	}

	public List<Object> getPaginatedVertexList(List<Object> vertexList) throws AAIException {
		List<Object> vertices;
		if (this.isPaginated()) {
			this.setTotalsForPaging(vertexList.size(), this.getPaginationBucket());
			int startIndex = (this.getPaginationIndex() - 1) * this.getPaginationBucket();
			int endIndex = Math.min((this.getPaginationBucket() * this.getPaginationIndex()), vertexList.size());
			if (startIndex > endIndex) {
				throw new AAIException("AAI_6150", " ResultIndex is not appropriate for the result set, Needs to be <= " + endIndex);
			}
			vertices = vertexList.subList(startIndex, endIndex);
		} else {
			vertices = vertexList;
		}
		return vertices;
	}
}
