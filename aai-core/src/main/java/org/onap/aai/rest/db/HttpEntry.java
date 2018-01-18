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
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriBuilder;

import org.apache.commons.lang.StringUtils;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.javatuples.Pair;
import org.onap.aai.db.props.AAIProperties;
import org.onap.aai.dbmap.DBConnectionType;
import org.onap.aai.domain.responseMessage.AAIResponseMessage;
import org.onap.aai.domain.responseMessage.AAIResponseMessageDatum;
import org.onap.aai.exceptions.AAIException;
import org.onap.aai.extensions.AAIExtensionMap;
import org.onap.aai.extensions.ExtensionController;
import org.onap.aai.introspection.Introspector;
import org.onap.aai.introspection.Loader;
import org.onap.aai.introspection.LoaderFactory;
import org.onap.aai.introspection.MarshallerProperties;
import org.onap.aai.introspection.ModelInjestor;
import org.onap.aai.introspection.ModelType;
import org.onap.aai.introspection.Version;
import org.onap.aai.introspection.exceptions.AAIUnknownObjectException;
import org.onap.aai.logging.ErrorLogHelper;
import org.onap.aai.logging.LoggingContext;
import org.onap.aai.parsers.query.QueryParser;
import org.onap.aai.parsers.uri.URIToExtensionInformation;
import org.onap.aai.rest.ueb.UEBNotification;
import org.onap.aai.restcore.HttpMethod;
import org.onap.aai.schema.enums.ObjectMetadata;
import org.onap.aai.serialization.db.DBSerializer;
import org.onap.aai.serialization.engines.QueryStyle;
import org.onap.aai.serialization.engines.TitanDBEngine;
import org.onap.aai.serialization.engines.TransactionalGraphEngine;
import org.onap.aai.serialization.engines.query.QueryEngine;

import com.att.eelf.configuration.EELFLogger;
import com.att.eelf.configuration.EELFManager;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import com.thinkaurelius.titan.core.TitanException;

/**
 * The Class HttpEntry.
 */
public class HttpEntry {

	private static final EELFLogger LOGGER = EELFManager.getInstance().getLogger(HttpEntry.class);
	private static final String TARGET_ENTITY = "DB";

	private final ModelType introspectorFactoryType;
	
	private final QueryStyle queryStyle;
	
	private final Version version;
	
	private final Loader loader;
	
	private final TransactionalGraphEngine dbEngine;
		
	private boolean processSingle = true;

	/**
	 * Instantiates a new http entry.
	 *
	 * @param version the version
	 * @param modelType the model type
	 * @param queryStyle the query style
	 * @param llBuilder the ll builder
	 */
	public HttpEntry(Version version, ModelType modelType, QueryStyle queryStyle, DBConnectionType connectionType) {
		this.introspectorFactoryType = modelType;
		this.queryStyle = queryStyle;
		this.version = version;
		this.loader = LoaderFactory.createLoaderForVersion(introspectorFactoryType, version);
		this.dbEngine = new TitanDBEngine(
				queryStyle,
				connectionType,
				loader);
		//start transaction on creation
		dbEngine.startTransaction();

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
	public Version getVersion() {
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

	public Pair<Boolean, List<Pair<URI, Response>>> process (List<DBRequest> requests, String sourceOfTruth) throws AAIException {
		return this.process(requests, sourceOfTruth, true);
	}
	/**
	 * Process.
	 * @param requests the requests
	 * @param sourceOfTruth the source of truth
	 *
	 * @return the pair
	 * @throws AAIException the AAI exception
	 */
	public Pair<Boolean, List<Pair<URI, Response>>> process (List<DBRequest> requests, String sourceOfTruth, boolean enableResourceVersion) throws AAIException {
		DBSerializer serializer = new DBSerializer(version, dbEngine, introspectorFactoryType, sourceOfTruth);
		String methodName = "process";
		Response response = null;
		Status status = Status.NOT_FOUND;
		Introspector obj = null;
		QueryParser query = null;
		URI uri = null;
		String transactionId = null;
		UEBNotification	notification = new UEBNotification(loader);
		int depth = AAIProperties.MAXIMUM_DEPTH;
		List<Pair<URI,Response>> responses = new ArrayList<>();
		MultivaluedMap<String, String> params = null;
		HttpMethod method = null;
		String uriTemp = "";
		Boolean success = true;
		QueryEngine queryEngine = dbEngine.getQueryEngine();
		int maxRetries = 10;
		int retry = 0;

		LoggingContext.save();
		for (DBRequest request : requests) {
			try {
				for (retry = 0; retry < maxRetries; ++retry) {
					try {
						method = request.getMethod();

						LoggingContext.targetEntity(TARGET_ENTITY);
						LoggingContext.targetServiceName(methodName + " " + method);

						obj = request.getIntrospector();
						query = request.getParser();
						transactionId = request.getTransactionId();
						uriTemp = request.getUri().getRawPath().replaceFirst("^v\\d+/", "");
						uri = UriBuilder.fromPath(uriTemp).build();

						LoggingContext.startTime();

						List<Vertex> vertices = query.getQueryBuilder().toList();
						boolean isNewVertex = false;
						String outputMediaType = getMediaType(request.getHeaders().getAcceptableMediaTypes());
						String result = null;
						params = request.getInfo().getQueryParameters(false);
						depth = setDepth(obj, params.getFirst("depth"));
						String cleanUp = params.getFirst("cleanup");
						String requestContext = "";
						List<String> requestContextList = request.getHeaders().getRequestHeader("aai-request-context");
						if (requestContextList != null) {
							requestContext = requestContextList.get(0);
						}
					
						if (cleanUp == null) {
							cleanUp = "false";
						}
						if (vertices.size() > 1 && processSingle && !method.equals(HttpMethod.GET)) {
							if (method.equals(HttpMethod.DELETE)) {
								LoggingContext.restoreIfPossible();
								throw new AAIException("AAI_6138");
							} else {
								LoggingContext.restoreIfPossible();
								throw new AAIException("AAI_6137");
							}
						}
						if (method.equals(HttpMethod.PUT)) {
							String resourceVersion = (String)obj.getValue("resource-version");
							if (vertices.isEmpty()) {
								if (enableResourceVersion) {
									serializer.verifyResourceVersion("create", query.getResultType(), "", resourceVersion, obj.getURI());
								}
								isNewVertex = true;
							} else {
								if (enableResourceVersion) {
									serializer.verifyResourceVersion("update", query.getResultType(), (String)vertices.get(0).<String>property("resource-version").orElse(null), resourceVersion, obj.getURI());
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
						switch (method) {
							case GET:
								String nodeOnly = params.getFirst("nodes-only");
								boolean isNodeOnly = nodeOnly != null;
								obj = this.getObjectFromDb(vertices, serializer, query, obj, request.getUri(), depth, isNodeOnly, cleanUp);

								LoggingContext.elapsedTime((long)serializer.getDBTimeMsecs(),TimeUnit.MILLISECONDS);
								LOGGER.info ("Completed");
								LoggingContext.restoreIfPossible();

								if (obj != null) {
									status = Status.OK;
									MarshallerProperties properties;
									if (!request.getMarshallerProperties().isPresent()) {
										properties = 
												new MarshallerProperties.Builder(org.onap.aai.restcore.MediaType.getEnum(outputMediaType)).build();
									} else {
										properties = request.getMarshallerProperties().get();
									}
									result = obj.marshal(properties);
								}
								
								break;
							case PUT:
								if (isNewVertex) {
									v = serializer.createNewVertex(obj);
								}
								serializer.serializeToDb(obj, v, query, uri.getRawPath(), requestContext);
								status = Status.OK;
								if (isNewVertex) {
									status = Status.CREATED;
								}
								obj = serializer.getLatestVersionView(v);
								if (query.isDependent()) {
									relatedObjects = this.getRelatedObjects(serializer, queryEngine, v);
								}
								LoggingContext.elapsedTime((long)serializer.getDBTimeMsecs() +
										(long)queryEngine.getDBTimeMsecs(), TimeUnit.MILLISECONDS);
								LOGGER.info ("Completed");
								LoggingContext.restoreIfPossible();
								notification.createNotificationEvent(transactionId, sourceOfTruth, status, uri, obj, relatedObjects);

								break;
							case PUT_EDGE:
								serializer.touchStandardVertexProperties(v, false);
								serializer.createEdge(obj, v);

								LoggingContext.elapsedTime((long)serializer.getDBTimeMsecs(),TimeUnit.MILLISECONDS);
								LOGGER.info ("Completed");
								LoggingContext.restoreIfPossible();
								status = Status.OK;
								break;
							case MERGE_PATCH:
								Introspector existingObj = (Introspector) obj.clone();
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
										relatedObjects = this.getRelatedObjects(serializer, queryEngine, v);
									}
									LoggingContext.elapsedTime((long)serializer.getDBTimeMsecs() +
											(long)queryEngine.getDBTimeMsecs(), TimeUnit.MILLISECONDS);
									LOGGER.info ("Completed");
									LoggingContext.restoreIfPossible();
									notification.createNotificationEvent(transactionId, sourceOfTruth, status, uri, patchedObj, relatedObjects);
								} catch (IOException | JsonPatchException e) {

									LOGGER.info ("Caught exception: " + e.getMessage());
									LoggingContext.restoreIfPossible();
									throw new AAIException("AAI_3000", "could not perform patch operation");
								}
								break;
							case DELETE:
								String resourceVersion = params.getFirst("resource-version");
								obj = serializer.getLatestVersionView(v);
								if (query.isDependent()) {
									relatedObjects = this.getRelatedObjects(serializer, queryEngine, v);
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

								if(isDelVerticesPresent){
									deleteObjects = this.buildIntrospectorObjects(serializer, deletableVertices);

									uriMap = this.buildURIMap(serializer, deleteObjects);
									deleteRelatedObjects = this.buildRelatedObjects(serializer, queryEngine, deleteObjects);
								}

								serializer.delete(v, resourceVersion, enableResourceVersion);

								LoggingContext.elapsedTime((long)serializer.getDBTimeMsecs() +
										(long)queryEngine.getDBTimeMsecs(), TimeUnit.MILLISECONDS);
								LOGGER.info ("Completed");
								LoggingContext.restoreIfPossible();
								status = Status.NO_CONTENT;
								notification.createNotificationEvent(transactionId, sourceOfTruth, status, uri, obj, relatedObjects);

								/*
								 * Notify delete-other-v candidates
								 */

								if(isDelVerticesPresent){
									this.buildNotificationEvent(sourceOfTruth, status, transactionId, notification, deleteObjects,
									uriMap, deleteRelatedObjects);
								}

								break;
							case DELETE_EDGE:
								serializer.touchStandardVertexProperties(v, false);
								serializer.deleteEdge(obj, v);

								LoggingContext.elapsedTime((long)serializer.getDBTimeMsecs(),TimeUnit.MILLISECONDS);
								LOGGER.info ("Completed");
								LoggingContext.restoreIfPossible();
								status = Status.NO_CONTENT;
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
							|| method.equals(HttpMethod.MERGE_PATCH))
						) {
							String myvertid = v.id().toString();
							response = Response.status(status)
									.header("vertex-id", myvertid)
									.entity(result)
									.type(outputMediaType).build();
						} else if (response == null) {
							response = Response.status(status)
									.type(outputMediaType).build();
						} else {
							//response already set to something
						}
						Pair<URI,Response> pairedResp = Pair.with(request.getUri(), response);
						responses.add(pairedResp);
						//break out of retry loop
						break;
					} catch (TitanException e) {
						this.dbEngine.rollback();

						LOGGER.info ("Caught exception: " + e.getMessage());
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
				Pair<URI,Response> pairedResp = Pair.with(request.getUri(), response);
				responses.add(pairedResp);
				continue;
			} catch (Exception e) {
				success = false;
				e.printStackTrace();
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
	private String getMediaType(List <MediaType> mediaTypeList) {
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
	 * @param g the g
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
	 * Sets the depth.
	 *
	 * @param depthParam the depth param
	 * @return the int
	 * @throws AAIException the AAI exception
	 */
	protected int setDepth(Introspector obj, String depthParam) throws AAIException {
			int depth = AAIProperties.MAXIMUM_DEPTH;

        if(depthParam == null){
			if(this.version.compareTo(Version.v9) >= 0){
				depth = 0;
			} else {
                depth = AAIProperties.MAXIMUM_DEPTH;
			}
		} else {
			if (depthParam.length() > 0 && !depthParam.equals("all")){
				try {
					depth = Integer.valueOf(depthParam);
				} catch (Exception e) {
					throw new AAIException("AAI_4016");
				}

			}
		}
        String maxDepth = obj.getMetadata(ObjectMetadata.MAXIMUM_DEPTH);
        
		int maximumDepth = AAIProperties.MAXIMUM_DEPTH;

		if(maxDepth != null){
            try {
                maximumDepth = Integer.parseInt(maxDepth);
            } catch(Exception ex){
                throw new AAIException("AAI_4018");
            }
		}

		if(depth > maximumDepth){
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
	
	private HashMap<String, Introspector> getRelatedObjects(DBSerializer serializer, QueryEngine queryEngine, Vertex v) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, SecurityException, InstantiationException, NoSuchMethodException, UnsupportedEncodingException, AAIException, URISyntaxException {
		HashMap<String, Introspector> relatedVertices = new HashMap<>();
		List<Vertex> vertexChain = queryEngine.findParents(v);
		for (Vertex vertex : vertexChain) {
			try {
				final Introspector vertexObj = serializer.getVertexProperties(vertex);
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
				if (null != entry.getValue())
					uriMap.put(entry.getValue().getObjectId(), uri);
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
						entry.getKey());
				if (null != entry.getValue())
					relatedObjectsMap.put(entry.getValue().getObjectId(), relatedObjects);
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
			Map<String, HashMap<String, Introspector>> deleteRelatedObjects) {
		for (Map.Entry<Vertex, Introspector> entry : deleteObjects.entrySet()) {
			try {
				String vertexObjectId = "";

				if (null != entry.getValue()) {
					vertexObjectId = entry.getValue().getObjectId();

					if (uriMap.containsKey(vertexObjectId) && deleteRelatedObjects.containsKey(vertexObjectId))
						notification.createNotificationEvent(transactionId, sourceOfTruth, status,
								uriMap.get(vertexObjectId), entry.getValue(), deleteRelatedObjects.get(vertexObjectId));
				}
			} catch (UnsupportedEncodingException | AAIException e) {

				LOGGER.warn("Error in sending otification");
			}
		}
	}

 	
}
