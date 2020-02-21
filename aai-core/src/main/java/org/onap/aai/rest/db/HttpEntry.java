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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.janusgraph.core.JanusGraphException;
import org.javatuples.Pair;
import org.onap.aai.aailog.logs.AaiDBMetricLog;
import org.onap.aai.db.props.AAIProperties;
import org.onap.aai.exceptions.AAIException;
import org.onap.aai.introspection.*;
import org.onap.aai.introspection.exceptions.AAIUnknownObjectException;
import org.onap.aai.logging.ErrorLogHelper;
import org.onap.aai.nodes.NodeIngestor;
import org.onap.aai.parsers.query.QueryParser;
import org.onap.aai.prevalidation.ValidationService;
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
import org.onap.aai.transforms.XmlFormatTransformer;
import org.onap.aai.util.AAIConfig;
import org.onap.aai.util.AAIConstants;
import org.onap.aai.util.delta.DeltaEvents;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;

import javax.ws.rs.core.*;
import javax.ws.rs.core.Response.Status;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * The Class HttpEntry.
 */
public class HttpEntry {

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpEntry.class);

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

    @Value("${delta.events.enabled:false}")
    private boolean isDeltaEventsEnabled;

    @Autowired
    private XmlFormatTransformer xmlFormatTransformer;

    /**
     * Inject the validation service if the profile pre-valiation is enabled,
     * Otherwise this variable will be set to null and thats why required=false
     * so that it can continue even if pre validation isn't enabled
     */
    @Autowired(required = false)
    private ValidationService validationService;

    private UEBNotification notification;

    private int notificationDepth;

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

    public HttpEntry setHttpEntryProperties(SchemaVersion version) {
        this.version = version;
        this.loader = loaderFactory.createLoaderForVersion(introspectorFactoryType, version);
        this.dbEngine = new JanusGraphDBEngine(queryStyle, loader);

        getDbEngine().startTransaction();
        this.notification = new UEBNotification(loader, loaderFactory, schemaVersions);
        if("true".equals(AAIConfig.get("aai.notification.depth.all.enabled", "true"))){
            this.notificationDepth = AAIProperties.MAXIMUM_DEPTH;
        } else {
            this.notificationDepth = AAIProperties.MINIMUM_DEPTH;
        }
        return this;
    }

    public HttpEntry setHttpEntryProperties(SchemaVersion version, UEBNotification notification) {
        this.version = version;
        this.loader = loaderFactory.createLoaderForVersion(introspectorFactoryType, version);
        this.dbEngine = new JanusGraphDBEngine(queryStyle, loader);

        this.notification = notification;

        if("true".equals(AAIConfig.get("aai.notification.depth.all.enabled", "true"))){
            this.notificationDepth = AAIProperties.MAXIMUM_DEPTH;
        } else {
            this.notificationDepth = AAIProperties.MINIMUM_DEPTH;
        }
        // start transaction on creation
        getDbEngine().startTransaction();
        return this;
    }

    public HttpEntry setHttpEntryProperties(SchemaVersion version, UEBNotification notification, int notificationDepth) {
        this.version = version;
        this.loader = loaderFactory.createLoaderForVersion(introspectorFactoryType, version);
        this.dbEngine = new JanusGraphDBEngine(queryStyle, loader);

        this.notification = notification;
        this.notificationDepth = notificationDepth;
        // start transaction on creation
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

    public Pair<Boolean, List<Pair<URI, Response>>> process(List<DBRequest> requests, String sourceOfTruth)
            throws AAIException {
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
     *
     * @return integer of the size of results to be returned when paginated
     */
    public int getPaginationBucket() {
        return this.paginationBucket;
    }

    /**
     * Setter for the pagination bucket variable which stores in this object the size of results to return
     *
     * @param pb
     */
    public void setPaginationBucket(int pb) {
        this.paginationBucket = pb;
    }

    /**
     * Getter to return the pagination index requested by the user when requesting paginated results
     *
     * @return
     */
    public int getPaginationIndex() {
        return this.paginationIndex;
    }

    /**
     * Sets the pagination index that was passed in by the user, to determine which index or results to retrieve when
     * paginated
     *
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
     *
     * @param totalVertices
     * @param paginationBucketSize
     */
    public void setTotalsForPaging(int totalVertices, int paginationBucketSize) {
        this.totalVertices = totalVertices;
        // set total number of buckets equal to full pages
        this.totalPaginationBuckets = totalVertices / paginationBucketSize;
        // conditionally add a page for the remainder
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
     *
     * @param requests the requests
     * @param sourceOfTruth the source of truth
     *
     * @return the pair
     * @throws AAIException the AAI exception
     */
    public Pair<Boolean, List<Pair<URI, Response>>> process(List<DBRequest> requests, String sourceOfTruth,
            boolean enableResourceVersion) throws AAIException {

        DBSerializer serializer = new DBSerializer(version, dbEngine, introspectorFactoryType, sourceOfTruth, notificationDepth);
        Response response;
        Introspector obj;
        QueryParser query;
        URI uri;
        String transactionId = null;
        int depth;
        Format format = null;
        List<Pair<URI, Response>> responses = new ArrayList<>();
        MultivaluedMap<String, String> params;
        HttpMethod method;
        String uriTemp;
        boolean success = true;
        QueryEngine queryEngine = dbEngine.getQueryEngine();
        Set<Vertex> mainVertexesToNotifyOn = new LinkedHashSet<>();

        AaiDBMetricLog metricLog = new AaiDBMetricLog(AAIConstants.AAI_RESOURCES_MS);

        String outputMediaType = null;

        if(requests != null && !requests.isEmpty()){
            HttpHeaders headers = requests.get(0).getHeaders();
            outputMediaType = getMediaType(headers.getAcceptableMediaTypes());
        }

        for (DBRequest request : requests) {
            response = null;
            Status status = Status.NOT_FOUND;
            method = request.getMethod();
            metricLog.pre(request);
            try {
                try {

                    obj = request.getIntrospector();
                    query = request.getParser();
                    transactionId = request.getTransactionId();
                    uriTemp = request.getUri().getRawPath().replaceFirst("^v\\d+/", "");
                    uri = UriBuilder.fromPath(uriTemp).build();
                    List<Vertex> vertTemp;
                    List<Vertex> vertices;
                    if (this.isPaginated()) {
                        vertTemp = query.getQueryBuilder().toList();
                        this.setTotalsForPaging(vertTemp.size(), this.paginationBucket);
                        vertices = vertTemp.subList(((this.paginationIndex - 1) * this.paginationBucket),
                                Math.min((this.paginationBucket * this.paginationIndex), vertTemp.size()));
                    } else {
                        vertices = query.getQueryBuilder().toList();
                    }
                    boolean isNewVertex;
                    HttpHeaders headers = request.getHeaders();
                    outputMediaType = getMediaType(headers.getAcceptableMediaTypes());
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
                    if (vertices.size() > 1 && processSingle
                            && !(method.equals(HttpMethod.GET) || method.equals(HttpMethod.GET_RELATIONSHIP))) {
                        if (method.equals(HttpMethod.DELETE)) {

                            throw new AAIException("AAI_6138");
                        } else {
                            throw new AAIException("AAI_6137");
                        }
                    }
                    if (method.equals(HttpMethod.PUT)) {
                        String resourceVersion = obj.getValue(AAIProperties.RESOURCE_VERSION);
                        if (vertices.isEmpty()) {
                            if (enableResourceVersion) {
                                serializer.verifyResourceVersion("create", query.getResultType(), "",
                                        resourceVersion, obj.getURI());
                            }
                            isNewVertex = true;
                        } else {
                            if (enableResourceVersion) {
                                serializer.verifyResourceVersion("update", query.getResultType(),
                                        vertices.get(0).<String>property(AAIProperties.RESOURCE_VERSION).orElse(null),
                                        resourceVersion, obj.getURI());
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

                    /*
                     * This skip-related-to query parameter is used to determine if the relationships object will omit the related-to-property
                     * If a GET is sent to resources without a format, if format=resource, or if format=resource_and_url with this param set to false
                     * then behavior will be keep the related-to properties. By default, set to true.
                     * Otherwise, for any other case, when the skip-related-to parameter exists, has value=true, or some unfamiliar input (e.g. skip-related-to=bogusvalue), the value is true.
                     */
                    boolean isSkipRelatedTo = true;
                    if (params.containsKey("skip-related-to")) {
                        String skipRelatedTo = params.getFirst("skip-related-to");
                        isSkipRelatedTo = !(skipRelatedTo != null && skipRelatedTo.equals("false"));
                    } else {
                        // if skip-related-to param is missing, then default it to false;
                        isSkipRelatedTo = false;
                    }

                    HashMap<String, Introspector> relatedObjects = new HashMap<>();
                    String nodeOnly = params.getFirst("nodes-only");
                    boolean isNodeOnly = nodeOnly != null;
                    switch (method) {
                        case GET:

                            if (format == null) {
                                obj = this.getObjectFromDb(vertices, serializer, query, obj, request.getUri(),
                                        depth, isNodeOnly, cleanUp, isSkipRelatedTo);

                                if (obj != null) {
                                    status = Status.OK;
                                    MarshallerProperties properties;
                                    if (!request.getMarshallerProperties().isPresent()) {
                                        properties = new MarshallerProperties.Builder(
                                                org.onap.aai.restcore.MediaType.getEnum(outputMediaType)).build();
                                    } else {
                                        properties = request.getMarshallerProperties().get();
                                    }
                                    result = obj.marshal(properties);
                                }
                            } else {
                                FormatFactory ff =
                                        new FormatFactory(loader, serializer, schemaVersions, basePath + "/");
                                Formatter formatter = ff.get(format, params);
                                result = formatter.output(vertices.stream().map(vertex -> (Object) vertex)
                                        .collect(Collectors.toList())).toString();

                                if(outputMediaType == null){
                                    outputMediaType = MediaType.APPLICATION_JSON;
                                }

                                if(MediaType.APPLICATION_XML_TYPE.isCompatible(MediaType.valueOf(outputMediaType))){
                                    result = xmlFormatTransformer.transform(result);
                                }
                                status = Status.OK;
                            }

                            break;
                        case GET_RELATIONSHIP:
                            if (format == null) {
                                obj = this.getRelationshipObjectFromDb(vertices, serializer, query,
                                        request.getInfo().getRequestUri(), isSkipRelatedTo);

                                if (obj != null) {
                                    status = Status.OK;
                                    MarshallerProperties properties;
                                    if (!request.getMarshallerProperties().isPresent()) {
                                        properties = new MarshallerProperties.Builder(
                                                org.onap.aai.restcore.MediaType.getEnum(outputMediaType)).build();
                                    } else {
                                        properties = request.getMarshallerProperties().get();
                                    }
                                    result = obj.marshal(properties);
                                } else {
                                    String msg = createRelationshipNotFoundMessage(query.getResultType(),
                                            request.getUri());
                                    throw new AAIException("AAI_6149", msg);
                                }
                            } else {
                                FormatFactory ff =
                                        new FormatFactory(loader, serializer, schemaVersions, basePath + "/");
                                Formatter formatter = ff.get(format, params);
                                result = formatter.output(vertices.stream().map(vertex -> (Object) vertex)
                                        .collect(Collectors.toList())).toString();

                                if(outputMediaType == null){
                                    outputMediaType = MediaType.APPLICATION_JSON;
                                }

                                if(MediaType.APPLICATION_XML_TYPE.isCompatible(MediaType.valueOf(outputMediaType))){
                                    result = xmlFormatTransformer.transform(result);
                                }
                                status = Status.OK;
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

                            mainVertexesToNotifyOn.add(v);
                            if (notificationDepth == AAIProperties.MINIMUM_DEPTH) {
                                Map<String, Pair<Introspector, LinkedHashMap<String,Introspector>>> allImpliedDeleteObjs = serializer.getImpliedDeleteUriObjectPair();

                                for(Map.Entry<String, Pair<Introspector, LinkedHashMap<String,Introspector>>> entry: allImpliedDeleteObjs.entrySet()){
                                    // The format is purposefully %s/%s%s due to the fact
                                    // that every aai-uri will have a slash at the beginning
                                    // If that assumption isn't true, then its best to change this code
                                    String curUri = String.format("%s/%s%s", basePath , version , entry.getKey());
                                    Introspector curObj = entry.getValue().getValue0();
                                    HashMap<String, Introspector> curObjRelated = entry.getValue().getValue1();
                                    notification.createNotificationEvent(transactionId, sourceOfTruth, Status.NO_CONTENT, URI.create(curUri), curObj, curObjRelated, basePath);
                                }
                            }

                            break;
                        case PUT_EDGE:
                            serializer.touchStandardVertexProperties(v, false);
                            Vertex relatedVertex = serializer.createEdge(obj, v);
                            status = Status.OK;

                            mainVertexesToNotifyOn.add(v);
                            serializer.addVertexToEdgeVertexes(relatedVertex);
                            break;
                        case MERGE_PATCH:
                            Introspector existingObj = loader.introspectorFromName(obj.getDbName());
                            existingObj = this.getObjectFromDb(vertices, serializer, query, existingObj,
                                    request.getUri(), 0, false, cleanUp);
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
                                if (relationshipList == null && patchedObj.hasProperty("relationship-list")) {
                                    // if the caller didn't touch the relationship-list, we shouldn't either
                                    patchedObj.setValue("relationship-list", null);
                                }
                                serializer.serializeToDb(patchedObj, v, query, uri.getRawPath(), requestContext);
                                status = Status.OK;
                                mainVertexesToNotifyOn.add(v);
                            } catch (IOException | JsonPatchException e) {
                                throw new AAIException("AAI_3000", "could not perform patch operation");
                            }
                            break;
                        case DELETE:
                            String resourceVersion = params.getFirst(AAIProperties.RESOURCE_VERSION);
                            obj = serializer.getLatestVersionView(v, notificationDepth);
                            if (query.isDependent()) {
                                relatedObjects =
                                        serializer.getRelatedObjects(queryEngine, v, obj, this.loader);
                            }
                            /*
                             * Find all Delete-other-vertex vertices and create structure for notify
                             * findDeleatble also returns the startVertex v and we dont want to create
                             * duplicate notification events for the same
                             * So remove the startvertex first
                             */

                            List<Vertex> deletableVertices = dbEngine.getQueryEngine().findDeletable(v);
                            Object vId = v.id();

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
                                deleteRelatedObjects =
                                        this.buildRelatedObjects(serializer, queryEngine, deleteObjects);
                            }

                            serializer.delete(v, deletableVertices, resourceVersion, enableResourceVersion);
                            status = Status.NO_CONTENT;
                            notification.createNotificationEvent(transactionId, sourceOfTruth, status, uri, obj,
                                    relatedObjects, basePath);

                            /*
                             * Notify delete-other-v candidates
                             */

                            if (isDelVerticesPresent) {
                                this.buildNotificationEvent(sourceOfTruth, status, transactionId, notification,
                                        deleteObjects, uriMap, deleteRelatedObjects, basePath);
                            }
                            break;
                        case DELETE_EDGE:
                            serializer.touchStandardVertexProperties(v, false);
                            Optional<Vertex> otherV = serializer.deleteEdge(obj, v);

                            status = Status.NO_CONTENT;
                            if (otherV.isPresent()) {
                                mainVertexesToNotifyOn.add(v);
                                serializer.addVertexToEdgeVertexes(otherV.get());
                            }
                            break;
                        default:
                            break;
                    }

                    /*
                     * temporarily adding vertex id to the headers
                     * to be able to use for testing the vertex id endpoint functionality
                     * since we presently have no other way of generating those id urls
                     */
                    if (response == null && v != null
                            && (method.equals(HttpMethod.PUT) || method.equals(HttpMethod.GET)
                                    || method.equals(HttpMethod.MERGE_PATCH)
                                    || method.equals(HttpMethod.GET_RELATIONSHIP))

                    ) {
                        String myvertid = v.id().toString();
                        if (this.isPaginated()) {
                            response = Response.status(status).header("vertex-id", myvertid)
                                    .header("total-results", this.getTotalVertices())
                                    .header("total-pages", this.getTotalPaginationBuckets()).entity(result)
                                    .type(outputMediaType).build();
                        } else {
                            response = Response.status(status).header("vertex-id", myvertid).entity(result)
                                    .type(outputMediaType).build();
                        }
                    } else if (response == null) {
                        response = Response.status(status).type(outputMediaType).build();
                    } // else, response already set to something

                    Pair<URI, Response> pairedResp = Pair.with(request.getUri(), response);
                    responses.add(pairedResp);
                } catch (JanusGraphException e) {
                    this.dbEngine.rollback();
                    throw new AAIException("AAI_6134", e);
                }
            } catch (AAIException e) {
                success = false;
                ArrayList<String> templateVars = new ArrayList<>();
                templateVars.add(request.getMethod().toString()); // GET, PUT, etc
                templateVars.add(request.getUri().getPath());
                templateVars.addAll(e.getTemplateVars());
                ErrorLogHelper.logException(e);
                response = Response.status(e.getErrorObject().getHTTPResponseCode()).entity(ErrorLogHelper
                        .getRESTAPIErrorResponse(request.getHeaders().getAcceptableMediaTypes(), e, templateVars))
                        .type(outputMediaType)
                        .build();
                Pair<URI, Response> pairedResp = Pair.with(request.getUri(), response);
                responses.add(pairedResp);
            } catch (Exception e) {
                success = false;
                AAIException ex = new AAIException("AAI_4000", e);
                ArrayList<String> templateVars = new ArrayList<>();
                templateVars.add(request.getMethod().toString()); // GET, PUT, etc
                templateVars.add(request.getUri().getPath());
                ErrorLogHelper.logException(ex);
                response = Response.status(ex.getErrorObject().getHTTPResponseCode()).entity(ErrorLogHelper
                        .getRESTAPIErrorResponse(request.getHeaders().getAcceptableMediaTypes(), ex, templateVars))
                        .type(outputMediaType)
                        .build();
                Pair<URI, Response> pairedResp = Pair.with(request.getUri(), response);
                responses.add(pairedResp);
            }
            finally {
                if (response != null) {
                    metricLog.post(request, response);
                }
            }
        }

       if (success) {
           generateEvents(sourceOfTruth, serializer, transactionId, queryEngine, mainVertexesToNotifyOn);
       } else {
            notification.clearEvents();
        }

        return Pair.with(success, responses);
    }

    /**
     * Generate notification events for the resulting db requests.
     */
    private void generateEvents(String sourceOfTruth, DBSerializer serializer, String transactionId, QueryEngine queryEngine, Set<Vertex> mainVertexesToNotifyOn) throws AAIException {
        if (notificationDepth == AAIProperties.MINIMUM_DEPTH) {
            serializer.getUpdatedVertexes().entrySet().stream().filter(Map.Entry::getValue).map(Map.Entry::getKey).forEach(mainVertexesToNotifyOn::add);
        }
        Set<Vertex> edgeVertexes = serializer.touchStandardVertexPropertiesForEdges().stream()
            .filter(v -> !mainVertexesToNotifyOn.contains(v)).collect(Collectors.toSet());
        try {
            createNotificationEvents(mainVertexesToNotifyOn, sourceOfTruth, serializer, transactionId, queryEngine, notificationDepth);
            if("true".equals(AAIConfig.get("aai.notification.both.sides.enabled", "true"))){
                createNotificationEvents(edgeVertexes, sourceOfTruth, serializer, transactionId, queryEngine, AAIProperties.MINIMUM_DEPTH);
            }
        } catch (UnsupportedEncodingException e) {
            LOGGER.warn("Encountered exception generating events", e);
        }

        // Since @Autowired required is set to false, we need to do a null check
        // for the existence of the validationService since its only enabled if profile is enabled
        if(validationService != null){
            validationService.validate(notification.getEvents());
        }
        notification.triggerEvents();
        if (isDeltaEventsEnabled) {
            try {
                DeltaEvents deltaEvents = new DeltaEvents(transactionId, sourceOfTruth, version.toString(), serializer.getObjectDeltas());
                deltaEvents.triggerEvents();
            } catch (Exception e) {
                LOGGER.error("Error sending Delta Events", e);
            }
        }
    }

    /**
     * Generate notification events for provided set of vertexes at the specified depth
     */
    private void createNotificationEvents(Set<Vertex> vertexesToNotifyOn, String sourceOfTruth, DBSerializer serializer,
                                          String transactionId, QueryEngine queryEngine, int eventDepth) throws AAIException, UnsupportedEncodingException {
        for(Vertex vertex : vertexesToNotifyOn){
            if (canGenerateEvent(vertex)) {
                boolean isCurVertexNew = vertex.value(AAIProperties.CREATED_TS).equals(vertex.value(AAIProperties.LAST_MOD_TS));
                Status curObjStatus = (isCurVertexNew) ? Status.CREATED : Status.OK;

                Introspector curObj = serializer.getLatestVersionView(vertex, eventDepth);
                String aaiUri = vertex.<String>property(AAIProperties.AAI_URI).value();
                String uri = String.format("%s/%s%s", basePath, version, aaiUri);
                HashMap<String, Introspector> curRelatedObjs = new HashMap<>();
                if (!curObj.isTopLevel()) {
                    curRelatedObjs = serializer.getRelatedObjects(queryEngine, vertex, curObj, this.loader);
                }
                notification.createNotificationEvent(transactionId, sourceOfTruth, curObjStatus, URI.create(uri), curObj, curRelatedObjs, basePath);
            }
        }
    }

    /**
     * Verifies that vertex has needed properties to generate on
     * @param vertex Vertex to be verified
     * @return <code>true</code> if vertex has necessary properties and exists
     */
    private boolean canGenerateEvent(Vertex vertex) {
        boolean canGenerate = true;
        try {
            if(!vertex.property(AAIProperties.AAI_URI).isPresent()){
                LOGGER.debug("Encountered an vertex {} with missing aai-uri", vertex.id());
                canGenerate = false;
            } else if(!vertex.property(AAIProperties.CREATED_TS).isPresent() || !vertex.property(AAIProperties.LAST_MOD_TS).isPresent()){
                LOGGER.debug("Encountered an vertex {} with missing timestamp", vertex.id());
                canGenerate = false;
            }
        } catch (IllegalStateException e) {
            if (e.getMessage().contains(" was removed")) {
                LOGGER.warn("Attempted to generate event for non existent vertex", e);
            } else {
                LOGGER.warn("Encountered exception generating events", e);
            }
            canGenerate = false;
        }
        return canGenerate;
    }

    /**
     * Gets the media type.
     *
     * @param mediaTypeList the media type list
     * @return the media type
     */
    private String getMediaType(List<MediaType> mediaTypeList) {
        String mediaType = MediaType.APPLICATION_JSON; // json is the default
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
    private Introspector getObjectFromDb(List<Vertex> results, DBSerializer serializer, QueryParser query,
            Introspector obj, URI uri, int depth, boolean nodeOnly, String cleanUp)
            throws AAIException, IllegalAccessException, IllegalArgumentException, InvocationTargetException,
            SecurityException, InstantiationException, NoSuchMethodException, UnsupportedEncodingException,
            AAIUnknownObjectException, URISyntaxException {

        // nothing found
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
     * @param isSkipRelatedTo include related to flag
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
    private Introspector getObjectFromDb(List<Vertex> results, DBSerializer serializer, QueryParser query,
            Introspector obj, URI uri, int depth, boolean nodeOnly, String cleanUp, boolean isSkipRelatedTo)
            throws AAIException, IllegalAccessException, IllegalArgumentException, InvocationTargetException,
            SecurityException, InstantiationException, NoSuchMethodException, UnsupportedEncodingException,
            AAIUnknownObjectException, URISyntaxException {

        // nothing found
        if (results.isEmpty()) {
            String msg = createNotFoundMessage(query.getResultType(), uri);
            throw new AAIException("AAI_6114", msg);
        }

        return serializer.dbToObject(results, obj, depth, nodeOnly, cleanUp, isSkipRelatedTo);

    }

    /**
     * Gets the object from db.
     *
     * @param serializer the serializer
     * @param query the query
     * @param uri the uri
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
    private Introspector getRelationshipObjectFromDb(List<Vertex> results, DBSerializer serializer, QueryParser query,
            URI uri, boolean isSkipRelatedTo) throws AAIException, IllegalArgumentException, SecurityException, UnsupportedEncodingException,
            AAIUnknownObjectException {

        // nothing found
        if (results.isEmpty()) {
            String msg = createNotFoundMessage(query.getResultType(), uri);
            throw new AAIException("AAI_6114", msg);
        }

        if (results.size() > 1) {
            throw new AAIException("AAI_6148", uri.getPath());
        }

        Vertex v = results.get(0);
        return serializer.dbToRelationshipObject(v, isSkipRelatedTo);
    }

    /**
     * Creates the not found message.
     *
     * @param resultType the result type
     * @param uri the uri
     * @return the string
     */
    private String createNotFoundMessage(String resultType, URI uri) {
        return  "No Node of type " + resultType + " found at: " + uri.getPath();
    }

    /**
     * Creates the not found message.
     *
     * @param resultType the result type
     * @param uri the uri
     * @return the string
     */
    private String createRelationshipNotFoundMessage(String resultType, URI uri) {
        return  "No relationship found of type " + resultType + " at the given URI: " + uri.getPath()
                + "/relationship-list";
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
        if (getAllRandomStr != null && !getAllRandomStr.isEmpty() && getAllRandomStr.equals(depthParam)) {
            return depth;
        }

        if (depthParam == null) {
            if (this.version.compareTo(schemaVersions.getDepthVersion()) >= 0) {
                depth = 0;
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

    private Map<Vertex, Introspector> buildIntrospectorObjects(DBSerializer serializer, Iterable<Vertex> vertices) {
        Map<Vertex, Introspector> deleteObjectMap = new HashMap<>();
        for (Vertex vertex : vertices) {
            try {
                Introspector deleteObj = serializer.getLatestVersionView(vertex, notificationDepth);
                deleteObjectMap.put(vertex, deleteObj);
            } catch (UnsupportedEncodingException | AAIException e) {
                LOGGER.warn("Unable to get Introspctor Objects, Just continue");
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
            }

        }

        return uriMap;

    }

    private Map<String, HashMap<String, Introspector>> buildRelatedObjects(DBSerializer serializer,
            QueryEngine queryEngine, Map<Vertex, Introspector> introSpector) {

        Map<String, HashMap<String, Introspector>> relatedObjectsMap = new HashMap<>();
        for (Map.Entry<Vertex, Introspector> entry : introSpector.entrySet()) {
            try {
                HashMap<String, Introspector> relatedObjects =
                        serializer.getRelatedObjects(queryEngine, entry.getKey(), entry.getValue(), this.loader);
                if (null != entry.getValue()) {
                    relatedObjectsMap.put(entry.getValue().getObjectId(), relatedObjects);
                }
            } catch (IllegalArgumentException | SecurityException
                    | UnsupportedEncodingException | AAIException e) {
                LOGGER.warn("Unable to get realted Objects, Just continue");
            }

        }

        return relatedObjectsMap;

    }

    private void buildNotificationEvent(String sourceOfTruth, Status status, String transactionId,
            UEBNotification notification, Map<Vertex, Introspector> deleteObjects, Map<String, URI> uriMap,
            Map<String, HashMap<String, Introspector>> deleteRelatedObjects, String basePath) {
        for (Map.Entry<Vertex, Introspector> entry : deleteObjects.entrySet()) {
            try {
                if (null != entry.getValue()) {
                    String vertexObjectId = entry.getValue().getObjectId();

                    if (uriMap.containsKey(vertexObjectId) && deleteRelatedObjects.containsKey(vertexObjectId)) {
                        notification.createNotificationEvent(transactionId, sourceOfTruth, status,
                                uriMap.get(vertexObjectId), entry.getValue(), deleteRelatedObjects.get(vertexObjectId),
                                basePath);
                    }
                }
            } catch (UnsupportedEncodingException | AAIException e) {

                LOGGER.warn("Error in sending notification");
            }
        }
    }

    public void setPaginationParameters(String resultIndex, String resultSize) {
        if (resultIndex != null && !"-1".equals(resultIndex) && resultSize != null && !"-1".equals(resultSize)) {
            this.setPaginationIndex(Integer.parseInt(resultIndex));
            this.setPaginationBucket(Integer.parseInt(resultSize));
        }
    }

    public List<Object> getPaginatedVertexListForAggregateFormat(List<Object> aggregateVertexList) throws AAIException {
        List<Object> finalList = new Vector<>();
        if (this.isPaginated()) {
            if (aggregateVertexList != null && !aggregateVertexList.isEmpty()) {
                int listSize = aggregateVertexList.size();
                if (listSize == 1) {
                    List<Object> vertexList = (List<Object>) aggregateVertexList.get(0);
                    this.setTotalsForPaging(vertexList.size(), this.getPaginationBucket());
                    int startIndex = (this.getPaginationIndex() - 1) * this.getPaginationBucket();
                    int endIndex = Math.min((this.getPaginationBucket() * this.getPaginationIndex()), vertexList.size());
                    if (startIndex > endIndex) {
                        throw new AAIException("AAI_6150",
                            " ResultIndex is not appropriate for the result set, Needs to be <= " + endIndex);
                    }
                    finalList.add(new ArrayList<Object>());
                    for (int i = startIndex; i < endIndex; i++) {
                        ((ArrayList<Object>) finalList.get(0)).add(((ArrayList<Object>) aggregateVertexList.get(0)).get(i));
                    }
                    return finalList;
                }
            }
        }
        // If the list size is greater than 1 or if pagination is not needed, return the original list.
        return aggregateVertexList;
    }

    public List<Object> getPaginatedVertexList(List<Object> vertexList) throws AAIException {
        List<Object> vertices;
        if (this.isPaginated()) {
            this.setTotalsForPaging(vertexList.size(), this.getPaginationBucket());
            int startIndex = (this.getPaginationIndex() - 1) * this.getPaginationBucket();
            int endIndex = Math.min((this.getPaginationBucket() * this.getPaginationIndex()), vertexList.size());
            if (startIndex > endIndex) {
                throw new AAIException("AAI_6150",
                        " ResultIndex is not appropriate for the result set, Needs to be <= " + endIndex);
            }
            vertices = vertexList.subList(startIndex, endIndex);
        } else {
            vertices = vertexList;
        }
        return vertices;
    }
}
