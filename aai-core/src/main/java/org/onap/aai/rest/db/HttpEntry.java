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

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.stream.Collectors;

import javax.ws.rs.core.*;
import javax.ws.rs.core.Response.Status;

import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.janusgraph.core.JanusGraphException;
import org.javatuples.Pair;
import org.onap.aai.aailog.logs.AaiDBMetricLog;
import org.onap.aai.db.props.AAIProperties;
import org.onap.aai.exceptions.AAIException;
import org.onap.aai.introspection.*;
import org.onap.aai.introspection.exceptions.AAIUnknownObjectException;
import org.onap.aai.introspection.sideeffect.OwnerCheck;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

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
    private LoaderFactory loaderFactory;

    @Autowired
    private SchemaVersions schemaVersions;

    @Value("${schema.uri.base.path}")
    private String basePath;

    @Value("${delta.events.enabled:false}")
    private boolean isDeltaEventsEnabled;

    private String serverBase;

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
     * @param modelType  the model type
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
        if ("true".equals(AAIConfig.get("aai.notification.depth.all.enabled", "true"))) {
            this.notificationDepth = AAIProperties.MAXIMUM_DEPTH;
        } else {
            this.notificationDepth = AAIProperties.MINIMUM_DEPTH;
        }
        return this;
    }

    public HttpEntry setHttpEntryProperties(SchemaVersion version, String serverBase) {
        this.version = version;
        this.loader = loaderFactory.createLoaderForVersion(introspectorFactoryType, version);
        this.dbEngine = new JanusGraphDBEngine(queryStyle, loader);

        getDbEngine().startTransaction();
        this.notification = new UEBNotification(loader, loaderFactory, schemaVersions);
        if ("true".equals(AAIConfig.get("aai.notification.depth.all.enabled", "true"))) {
            this.notificationDepth = AAIProperties.MAXIMUM_DEPTH;
        } else {
            this.notificationDepth = AAIProperties.MINIMUM_DEPTH;
        }

        this.serverBase = serverBase;
        return this;
    }

    public HttpEntry setHttpEntryProperties(SchemaVersion version, UEBNotification notification) {
        this.version = version;
        this.loader = loaderFactory.createLoaderForVersion(introspectorFactoryType, version);
        this.dbEngine = new JanusGraphDBEngine(queryStyle, loader);

        this.notification = notification;

        if ("true".equals(AAIConfig.get("aai.notification.depth.all.enabled", "true"))) {
            this.notificationDepth = AAIProperties.MAXIMUM_DEPTH;
        } else {
            this.notificationDepth = AAIProperties.MINIMUM_DEPTH;
        }
        // start transaction on creation
        getDbEngine().startTransaction();
        return this;
    }

    public HttpEntry setHttpEntryProperties(SchemaVersion version, UEBNotification notification,
            int notificationDepth) {
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

    public Pair<Boolean, List<Pair<URI, Response>>> process(List<DBRequest> requests, String sourceOfTruth,
            Set<String> groups) throws AAIException {
        return this.process(requests, sourceOfTruth, groups, true);
    }

    public Pair<Boolean, List<Pair<URI, Response>>> process(List<DBRequest> requests, String sourceOfTruth)
            throws AAIException {
        return this.process(requests, sourceOfTruth, true);
    }

    /**
     * Checks the pagination bucket and pagination index variables to determine
     * whether or not the user
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
     * Setter for the pagination bucket variable which stores in this object the
     * size of results to return
     *
     * @param pb
     */
    public void setPaginationBucket(int pb) {
        this.paginationBucket = pb;
    }

    /**
     * Getter to return the pagination index requested by the user when requesting
     * paginated results
     *
     * @return
     */
    public int getPaginationIndex() {
        return this.paginationIndex;
    }

    /**
     * Sets the pagination index that was passed in by the user, to determine which
     * index or results to retrieve when
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
     * Sets the total vertices variables and calculates the amount of pages based on
     * size and total vertices
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
     * @param requests      the requests
     * @param sourceOfTruth the source of truth
     *
     * @return the pair
     * @throws AAIException the AAI exception
     */
    public Pair<Boolean, List<Pair<URI, Response>>> process(List<DBRequest> requests, String sourceOfTruth,
            boolean enableResourceVersion) throws AAIException {
        return this.process(requests, sourceOfTruth, Collections.EMPTY_SET, enableResourceVersion);
    }

    private Pair<Boolean, List<Pair<URI, Response>>> process(List<DBRequest> requests, String sourceOfTruth,
            Set<String> groups, boolean enableResourceVersion) {
        DBSerializer serializer = null;
        try {
            serializer = serverBase != null
                    ? new DBSerializer(version, dbEngine, introspectorFactoryType, sourceOfTruth, groups,
                            notificationDepth, serverBase)
                    : new DBSerializer(version, dbEngine, introspectorFactoryType, sourceOfTruth, groups,
                            notificationDepth);
        } catch (AAIException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        List<Pair<URI, Response>> responses = new ArrayList<>();
        // requests.stream()
        // .map(dbRequest -> processRequest(dbRequest, sourceOfTruth, groups,
        // enableResourceVersion))
        // .collect(Collectors.toList());
        boolean success = true;
        for (DBRequest request : requests) {
            try {
                Response response = processRequest(request, serializer, sourceOfTruth, groups, enableResourceVersion);
                responses.add(Pair.with(request.getUri(), response));
            } catch (Exception e) {
                // Handle exceptions and log errors
                e.printStackTrace();
                // Add error response to responses list
                responses.add(Pair.with(request.getUri(), createErrorResponse(request, e)));
                success = false;
            }
        }
        return Pair.with(success, responses);
    }

    private Response processRequest(DBRequest request, DBSerializer serializer, String sourceOfTruth,
            Set<String> groups,
            boolean enableResourceVersion) throws AAIException {
        // Extract necessary components from the request
        HttpMethod method = request.getMethod();
        Introspector obj = request.getIntrospector();
        QueryParser query = request.getParser();
        URI uri = buildProcessedUri(request.getUri());
        String outputMediaType = getMediaType(request.getHeaders().getAcceptableMediaTypes());
        MarshallerProperties marshallerProperties = request.getMarshallerProperties()
                .orElse(new MarshallerProperties.Builder(
                        org.onap.aai.restcore.MediaType.getEnum(outputMediaType)).build());
        MultivaluedMap<String, String> params = request.getInfo().getQueryParameters(false);
        String transactionId = request.getTransactionId();

        List<Vertex> queryResult = query.getQueryBuilder().toList();
        List<Vertex> vertices = filterByAuthorizationGroup(groups, serializer, queryResult);

        if (this.isPaginated()) {
            vertices = paginate(vertices);
        }

        List<String> requestContextList = request.getHeaders().getRequestHeader("aai-request-context");
        String requestContext = requestContextList != null ? requestContextList.get(0) : "";

        // Perform operation based on the HTTP method
        switch (method) {
            case GET:
                return processGetRequest(request, obj, query, uri, serializer, vertices, params, marshallerProperties);
            case GET_RELATIONSHIP:
                return null;
            case PUT:
                return processPutRequest(obj, query, uri, serializer, vertices, params, marshallerProperties,
                        requestContext);
            case PUT_EDGE:
                return null;
            case MERGE_PATCH:
                return null;
            case DELETE:
                return null;
            case DELETE_EDGE:
                return null;
            // case PUT:
            // return processPutRequest(obj, query, uri, serializer);
            // Handle other HTTP methods here...
            default:
                return createInvalidMethodResponse(method);
        }
    }

    public <T> List<T> paginate(List<T> vertices) {
        this.setTotalsForPaging(vertices.size(), this.paginationBucket);
        int fromIndex = (this.paginationIndex - 1) * this.paginationBucket;
        int toIndex = Math.min(this.paginationBucket * this.paginationIndex, vertices.size());
        return vertices.subList(fromIndex, toIndex);
    }

    private List<Vertex> filterByAuthorizationGroup(Set<String> groups, DBSerializer serializer,
            List<Vertex> queryResult) {
        boolean groupsAvailable = serializer.getGroups() != null && !serializer.getGroups().isEmpty();
        return groupsAvailable
                ? queryResult.stream()
                        .filter(vx -> OwnerCheck.isAuthorized(groups, vx))
                        .collect(Collectors.toList())
                : queryResult;
    }

    private Response processPutRequest(Introspector obj, QueryParser query, URI uri, DBSerializer serializer,
            List<Vertex> vertices, MultivaluedMap<String, String> params, MarshallerProperties marshallerProperties,
            String requestContext) throws AAIException {
        boolean isCreate = vertices.isEmpty();
        boolean enableResourceVersion = true; // TODO: pass this in as parameter
        if (isCreate) {
            return processCreate(obj, query, uri, serializer, serializer.createNewVertex(obj), enableResourceVersion,
                    marshallerProperties,
                    requestContext);
        } else {
            return processUpdate(obj, query, uri, serializer, vertices.get(0), enableResourceVersion,
                    marshallerProperties, requestContext);
        }
        // vertex not in db yet, create instead of update
        // Vertex v = isCreate ? serializer.createNewVertex(obj) : vertices.get(0);
        // if (this.isPaginated()) {
        // response = Response.status(status).header("vertex-id", myvertid)
        // .header("total-results", this.getTotalVertices())
        // .header("total-pages", this.getTotalPaginationBuckets()).entity(result)
        // .type(outputMediaType).build();
        // } else {
        // response = Response.status(status).header("vertex-id",
        // myvertid).entity(result)
        // .type(outputMediaType).build();
        // }

        // mainVertexesToNotifyOn.add(v);

        // if (notificationDepth == AAIProperties.MINIMUM_DEPTH) {
        // Map<String, Pair<Introspector, LinkedHashMap<String, Introspector>>>
        // allImpliedDeleteObjs =
        // serializer.getImpliedDeleteUriObjectPair();

        // for (Map.Entry<String, Pair<Introspector, LinkedHashMap<String,
        // Introspector>>> entry : allImpliedDeleteObjs
        // .entrySet()) {
        // // The format is purposefully %s/%s%s due to the fact
        // // that every aai-uri will have a slash at the beginning
        // // If that assumption isn't true, then its best to change this code
        // String curUri = String.format("%s/%s%s", basePath, version, entry.getKey());
        // Introspector curObj = entry.getValue().getValue0();
        // HashMap<String, Introspector> curObjRelated = entry.getValue().getValue1();
        // notification.createNotificationEvent(transactionId, sourceOfTruth,
        // Status.NO_CONTENT, URI.create(curUri), curObj, curObjRelated, basePath);
        // }
        // }
    }

    private Response processUpdate(Introspector obj, QueryParser query, URI uri, DBSerializer serializer, Vertex v,
            boolean enableResourceVersion, MarshallerProperties marshallerProperties, String requestContext) {
        try {
            if (enableResourceVersion) {
                String resourceVersion = obj.getValue(AAIProperties.RESOURCE_VERSION);
                serializer.verifyResourceVersion("update", query.getResultType(),
                        v.<String>property(AAIProperties.RESOURCE_VERSION).orElse(null),
                        resourceVersion, obj.getURI());
            }
            serializer.serializeToDb(obj, v, query, uri.getRawPath(), requestContext);
        } catch (UnsupportedEncodingException | AAIException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return Response
                .ok(uri)
                .header("vertex-id", v.id().toString())
                .type(MediaType.valueOf(marshallerProperties.getMediaType().toString()))
                .build();
    }

    private Response processCreate(Introspector obj, QueryParser query, URI uri, DBSerializer serializer, Vertex v,
            boolean enableResourceVersion, MarshallerProperties marshallerProperties, String requestContext) {
        String resourceVersion = obj.getValue(AAIProperties.RESOURCE_VERSION);
        try {
            if (enableResourceVersion) {
                serializer.verifyResourceVersion("create", query.getResultType(), "", resourceVersion,
                        obj.getURI());
            }
            serializer.serializeToDb(obj, v, query, uri.getRawPath(), requestContext);
        } catch (UnsupportedEncodingException | AAIException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return Response
                .created(uri)
                .header("vertex-id", v.id().toString())
                .type(MediaType.valueOf(marshallerProperties.getMediaType().toString()))
                .build();
    }

    private Response processGetRequest(DBRequest request, Introspector obj, QueryParser query, URI uri,
            DBSerializer serializer,
            List<Vertex> vertices, MultivaluedMap<String, String> params, MarshallerProperties marshallerProperties)
            throws AAIException {

        int depth = getDepth(obj, params.getFirst("depth"));
        boolean nodeOnly = getNodeOnlyParam(params);
        String cleanUp = getCleanUpParam(params);
        boolean skipRelatedTo = getSkipRelatedToParam(params);

        String result = null;

        if (!params.containsKey("format")) {
            try {
                // obj = this.getObjectFromDb(vertices, serializer, query, obj, uri, depth,
                // nodeOnly, cleanUp, skipRelatedTo);
                if (vertices.isEmpty()) {
                    String msg = createNotFoundMessage(query.getResultType(), uri);
                    // throw new AAIException("AAI_6114", msg);
                    return getErrorResponse(request, new AAIException("AAI_6114", msg), marshallerProperties.getMediaType().toString());
                }

                obj = serializer.dbToObject(vertices, obj, depth, nodeOnly, cleanUp, skipRelatedTo);
            } catch (IllegalArgumentException | SecurityException
                    | UnsupportedEncodingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            if (obj != null) {
                result = obj.marshal(marshallerProperties);
            }
        } else {
            FormatFactory formatFactory = new FormatFactory(loader, serializer, schemaVersions, basePath + "/",
                    serverBase);
            Format queryFormat = Format.getFormat(params.getFirst("format"));
            Formatter formatter = formatFactory.get(queryFormat, params);
            result = formatter.output(
                    vertices.stream().map(Object.class::cast).collect(Collectors.toList()))
                    .toString();

            marshallerProperties.getMediaType();
            // TODO: Do this mediatype comparison here
            // if
            // (MediaType.APPLICATION_XML_TYPE.isCompatible(marshallerProperties.getMediaType()))
            // {
            // result = xmlFormatTransformer.transform(result);
            // }
            // status = Status.OK;
        }

        // TODO: Handle pagination

        String vertexId = vertices.get(0).id().toString();
        return Response.ok().header("vertex-id", vertexId).entity(result)
                .type(MediaType.valueOf(marshallerProperties.getMediaType().toString())).build();
    }

    private boolean getNodeOnlyParam(MultivaluedMap<String, String> params) {
        return params.getFirst("nodes-only") != null;
    }

    private boolean getSkipRelatedToParam(MultivaluedMap<String, String> params) {
        return !params.containsKey("skip-related-to") || !params.getFirst("skip-related-to").equals("false");
    }

    public String getCleanUpParam(MultivaluedMap<String, String> params) {
        String cleanup = params.getFirst("cleanup");
        if (cleanup == null) {
            return "false";
        }
        switch (cleanup.toLowerCase()) {
            case "true":
                return "true";
            case "false":
                return "false";
            default:
                throw new IllegalArgumentException("Only true or false is allowed");
        }
    }

    private Response createErrorResponse(DBRequest request, Exception ex) {
        return null;
    }

    private Response getErrorResponse(DBRequest request, AAIException aaiException, String mediaType) {
        ArrayList<String> templateVars = new ArrayList<>();
        templateVars.add(request.getMethod().toString()); // GET, PUT, etc
        templateVars.add(request.getUri().getPath());
        ErrorLogHelper.logException(aaiException);
        Response response = Response.status(aaiException.getErrorObject().getHTTPResponseCode())
                .entity(ErrorLogHelper.getRESTAPIErrorResponse(
                        request.getHeaders().getAcceptableMediaTypes(), aaiException, templateVars))
                .type(mediaType).build();
        return response;
        // return Pair.with(request.getUri(), response);
    }

    private Pair<Boolean, List<Pair<URI, Response>>> processOld(List<DBRequest> requests, String sourceOfTruth,
            Set<String> groups, boolean enableResourceVersion) throws AAIException {

        DBSerializer serializer = null;

        if (serverBase != null) {
            serializer = new DBSerializer(version, dbEngine, introspectorFactoryType, sourceOfTruth, groups,
                    notificationDepth, serverBase);
        } else {
            serializer = new DBSerializer(version, dbEngine, introspectorFactoryType, sourceOfTruth, groups,
                    notificationDepth);
        }

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

        if (requests != null && !requests.isEmpty()) {
            HttpHeaders headers = requests.get(0).getHeaders();
            outputMediaType = getMediaType(headers.getAcceptableMediaTypes());
        }

        for (DBRequest request : requests) {

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

                    List<Vertex> vertices = getVerticesFromQuery(groups, serializer, query);

                    HttpHeaders headers = request.getHeaders();
                    outputMediaType = getMediaType(headers.getAcceptableMediaTypes());

                    params = request.getInfo().getQueryParameters(false);
                    depth = getDepth(obj, params.getFirst("depth"));
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
                    boolean isNewVertex;
                    if (method.equals(HttpMethod.PUT)) {
                        String resourceVersion = obj.getValue(AAIProperties.RESOURCE_VERSION);
                        if (vertices.isEmpty()) {
                            if (enableResourceVersion) {
                                serializer.verifyResourceVersion("create", query.getResultType(), "", resourceVersion,
                                        obj.getURI());
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
                     * This skip-related-to query parameter is used to determine if the
                     * relationships object will omit
                     * the related-to-property
                     * If a GET is sent to resources without a format, if format=resource, or if
                     * format=resource_and_url
                     * with this param set to false
                     * then behavior will be keep the related-to properties. By default, set to
                     * true.
                     * Otherwise, for any other case, when the skip-related-to parameter exists, has
                     * value=true, or some
                     * unfamiliar input (e.g. skip-related-to=bogusvalue), the value is true.
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
                    String result = null;
                    switch (method) {
                        case GET:
                            if (format == null) {
                                obj = this.getObjectFromDb(vertices, serializer, query, obj, request.getUri(), depth,
                                        isNodeOnly, cleanUp, isSkipRelatedTo);

                                if (obj != null) {
                                    status = Status.OK;
                                    MarshallerProperties properties;
                                    Optional<MarshallerProperties> marshallerPropOpt = request
                                            .getMarshallerProperties();
                                    if (marshallerPropOpt.isPresent()) {
                                        properties = marshallerPropOpt.get();
                                    } else {
                                        properties = new MarshallerProperties.Builder(
                                                org.onap.aai.restcore.MediaType.getEnum(outputMediaType)).build();
                                    }
                                    result = obj.marshal(properties);
                                }
                            } else {
                                FormatFactory ff = new FormatFactory(loader, serializer, schemaVersions, basePath + "/",
                                        serverBase);
                                Formatter formatter = ff.get(format, params);
                                result = formatter.output(
                                        vertices.stream().map(vertex -> (Object) vertex).collect(Collectors.toList()))
                                        .toString();

                                if (outputMediaType == null) {
                                    outputMediaType = MediaType.APPLICATION_JSON;
                                }

                                if (MediaType.APPLICATION_XML_TYPE.isCompatible(MediaType.valueOf(outputMediaType))) {
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
                                FormatFactory ff = new FormatFactory(loader, serializer, schemaVersions, basePath + "/",
                                        serverBase);
                                Formatter formatter = ff.get(format, params);
                                result = formatter.output(
                                        vertices.stream().map(vertex -> (Object) vertex).collect(Collectors.toList()))
                                        .toString();

                                if (outputMediaType == null) {
                                    outputMediaType = MediaType.APPLICATION_JSON;
                                }

                                if (MediaType.APPLICATION_XML_TYPE.isCompatible(MediaType.valueOf(outputMediaType))) {
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
                                Map<String, Pair<Introspector, LinkedHashMap<String, Introspector>>> allImpliedDeleteObjs = serializer
                                        .getImpliedDeleteUriObjectPair();

                                for (Map.Entry<String, Pair<Introspector, LinkedHashMap<String, Introspector>>> entry : allImpliedDeleteObjs
                                        .entrySet()) {
                                    // The format is purposefully %s/%s%s due to the fact
                                    // that every aai-uri will have a slash at the beginning
                                    // If that assumption isn't true, then its best to change this code
                                    String curUri = String.format("%s/%s%s", basePath, version, entry.getKey());
                                    Introspector curObj = entry.getValue().getValue0();
                                    HashMap<String, Introspector> curObjRelated = entry.getValue().getValue1();
                                    notification.createNotificationEvent(transactionId, sourceOfTruth,
                                            Status.NO_CONTENT, URI.create(curUri), curObj, curObjRelated, basePath);
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
                                relatedObjects = serializer.getRelatedObjects(queryEngine, v, obj, this.loader);
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
                                deleteRelatedObjects = this.buildRelatedObjects(serializer, queryEngine, deleteObjects);
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
                    Response response = null;
                    if (response == null && v != null && (method.equals(HttpMethod.PUT) || method.equals(HttpMethod.GET)
                            || method.equals(HttpMethod.MERGE_PATCH) || method.equals(HttpMethod.GET_RELATIONSHIP))) {
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
                Response response = Response.status(e.getErrorObject().getHTTPResponseCode())
                        .entity(ErrorLogHelper.getRESTAPIErrorResponse(
                                request.getHeaders().getAcceptableMediaTypes(), e, templateVars))
                        .type(outputMediaType).build();
                Pair<URI, Response> pairedResp = Pair.with(request.getUri(), response);
                responses.add(pairedResp);
            } catch (Exception e) {
                success = false;
                AAIException ex = new AAIException("AAI_4000", e);
                ArrayList<String> templateVars = new ArrayList<>();
                templateVars.add(request.getMethod().toString()); // GET, PUT, etc
                templateVars.add(request.getUri().getPath());
                ErrorLogHelper.logException(ex);
                Response response = Response.status(ex.getErrorObject().getHTTPResponseCode())
                        .entity(ErrorLogHelper.getRESTAPIErrorResponse(
                                request.getHeaders().getAcceptableMediaTypes(), ex, templateVars))
                        .type(outputMediaType).build();
                Pair<URI, Response> pairedResp = Pair.with(request.getUri(), response);
                responses.add(pairedResp);
            } finally {
                // if (response != null) {
                // metricLog.post(request, response);
                // }
            }
        }

        if (success) {
            generateEvents(sourceOfTruth, serializer, transactionId, queryEngine, mainVertexesToNotifyOn);
        } else {
            notification.clearEvents();
        }

        return Pair.with(success, responses);
    }

    private List<Vertex> getVerticesFromQuery(Set<String> groups, DBSerializer serializer, QueryParser query) {
        boolean groupsAvailable = serializer.getGroups() != null && !serializer.getGroups().isEmpty();
        List<Vertex> queryResult = query.getQueryBuilder().toList();
        List<Vertex> vertices;
        if (this.isPaginated()) {
            List<Vertex> vertTemp = groupsAvailable ? queryResult.stream().filter((vx) -> {
                return OwnerCheck.isAuthorized(groups, vx);
            }).collect(Collectors.toList()) : queryResult;
            this.setTotalsForPaging(vertTemp.size(), this.paginationBucket);
            vertices = vertTemp.subList(((this.paginationIndex - 1) * this.paginationBucket),
                    Math.min((this.paginationBucket * this.paginationIndex), vertTemp.size()));
        } else {
            vertices = groupsAvailable && queryResult.size() > 1 ? queryResult.stream().filter((vx) -> {
                return OwnerCheck.isAuthorized(groups, vx);
            }).collect(Collectors.toList()) : queryResult;

        }
        return vertices;
    }

    private DBSerializer createDBSerializer(boolean enableResourceVersion, String sourceOfTruth, Set<String> groups)
            throws AAIException {
        if (serverBase != null) {
            return new DBSerializer(version, dbEngine, introspectorFactoryType, sourceOfTruth, groups,
                    notificationDepth, serverBase);
        } else {
            return new DBSerializer(version, dbEngine, introspectorFactoryType, sourceOfTruth, groups,
                    notificationDepth);
        }
    }

    private URI buildProcessedUri(URI originalUri) {
        String uriTemp = originalUri.getRawPath().replaceFirst("^v\\d+/", "");
        return UriBuilder.fromPath(uriTemp).build();
    }

    private Response createInvalidMethodResponse(HttpMethod method) {
        // Create a response indicating invalid HTTP method
        return null;
    }

    /**
     * Generate notification events for the resulting db requests.
     */
    private void generateEvents(String sourceOfTruth, DBSerializer serializer, String transactionId,
            QueryEngine queryEngine, Set<Vertex> mainVertexesToNotifyOn) throws AAIException {
        if (notificationDepth == AAIProperties.MINIMUM_DEPTH) {
            serializer.getUpdatedVertexes().entrySet().stream().filter(Map.Entry::getValue).map(Map.Entry::getKey)
                    .forEach(mainVertexesToNotifyOn::add);
        }
        Set<Vertex> edgeVertexes = serializer.touchStandardVertexPropertiesForEdges().stream()
                .filter(v -> !mainVertexesToNotifyOn.contains(v)).collect(Collectors.toSet());
        try {
            createNotificationEvents(mainVertexesToNotifyOn, sourceOfTruth, serializer, transactionId, queryEngine,
                    notificationDepth);
            if ("true".equals(AAIConfig.get("aai.notification.both.sides.enabled", "true"))) {
                createNotificationEvents(edgeVertexes, sourceOfTruth, serializer, transactionId, queryEngine,
                        AAIProperties.MINIMUM_DEPTH);
            }
        } catch (UnsupportedEncodingException e) {
            LOGGER.warn("Encountered exception generating events", e);
        }

        // Since @Autowired required is set to false, we need to do a null check
        // for the existence of the validationService since its only enabled if profile
        // is enabled
        if (validationService != null) {
            validationService.validate(notification.getEvents());
        }
        notification.triggerEvents();
        if (isDeltaEventsEnabled) {
            try {
                DeltaEvents deltaEvents = new DeltaEvents(transactionId, sourceOfTruth, version.toString(),
                        serializer.getObjectDeltas());
                deltaEvents.triggerEvents();
            } catch (Exception e) {
                LOGGER.error("Error sending Delta Events", e);
            }
        }
    }

    /**
     * Generate notification events for provided set of vertexes at the specified
     * depth
     */
    private void createNotificationEvents(Set<Vertex> vertexesToNotifyOn, String sourceOfTruth, DBSerializer serializer,
            String transactionId, QueryEngine queryEngine, int eventDepth)
            throws AAIException, UnsupportedEncodingException {
        for (Vertex vertex : vertexesToNotifyOn) {
            if (canGenerateEvent(vertex)) {
                boolean isCurVertexNew = vertex.value(AAIProperties.CREATED_TS)
                        .equals(vertex.value(AAIProperties.LAST_MOD_TS));
                Status curObjStatus = (isCurVertexNew) ? Status.CREATED : Status.OK;

                Introspector curObj = serializer.getLatestVersionView(vertex, eventDepth);
                String aaiUri = vertex.<String>property(AAIProperties.AAI_URI).value();
                String uri = String.format("%s/%s%s", basePath, version, aaiUri);
                HashMap<String, Introspector> curRelatedObjs = new HashMap<>();
                if (!curObj.isTopLevel()) {
                    curRelatedObjs = serializer.getRelatedObjects(queryEngine, vertex, curObj, this.loader);
                }
                notification.createNotificationEvent(transactionId, sourceOfTruth, curObjStatus, URI.create(uri),
                        curObj, curRelatedObjs, basePath);
            }
        }
    }

    /**
     * Verifies that vertex has needed properties to generate on
     *
     * @param vertex Vertex to be verified
     * @return <code>true</code> if vertex has necessary properties and exists
     */
    private boolean canGenerateEvent(Vertex vertex) {
        boolean canGenerate = true;
        try {
            if (!vertex.property(AAIProperties.AAI_URI).isPresent()) {
                LOGGER.debug("Encountered an vertex {} with missing aai-uri", vertex.id());
                canGenerate = false;
            } else if (!vertex.property(AAIProperties.CREATED_TS).isPresent()
                    || !vertex.property(AAIProperties.LAST_MOD_TS).isPresent()) {
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
        boolean isXmlCompatible = mediaTypeList.stream()
                .anyMatch(MediaType.APPLICATION_XML_TYPE::isCompatible);
        return isXmlCompatible ? MediaType.APPLICATION_XML : MediaType.APPLICATION_JSON;
    }

    /**
     * Gets the object from db.
     *
     * @param serializer the serializer
     * @param query      the query
     * @param obj        the obj
     * @param uri        the uri
     * @param depth      the depth
     * @param cleanUp    the clean up
     * @return the object from db
     * @throws AAIException                 the AAI exception
     * @throws IllegalAccessException       the illegal access exception
     * @throws IllegalArgumentException     the illegal argument exception
     * @throws InvocationTargetException    the invocation target exception
     * @throws SecurityException            the security exception
     * @throws InstantiationException       the instantiation exception
     * @throws NoSuchMethodException        the no such method exception
     * @throws UnsupportedEncodingException the unsupported encoding exception
     * @throws MalformedURLException        the malformed URL exception
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
     * @param serializer      the serializer
     * @param query           the query
     * @param obj             the obj
     * @param uri             the uri
     * @param depth           the depth
     * @param cleanUp         the clean up
     * @param isSkipRelatedTo include related to flag
     * @return the object from db
     * @throws AAIException                 the AAI exception
     * @throws IllegalAccessException       the illegal access exception
     * @throws IllegalArgumentException     the illegal argument exception
     * @throws InvocationTargetException    the invocation target exception
     * @throws SecurityException            the security exception
     * @throws InstantiationException       the instantiation exception
     * @throws NoSuchMethodException        the no such method exception
     * @throws UnsupportedEncodingException the unsupported encoding exception
     * @throws MalformedURLException        the malformed URL exception
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
     * @param query      the query
     * @param uri        the uri
     * @return the object from db
     * @throws AAIException                 the AAI exception
     * @throws IllegalAccessException       the illegal access exception
     * @throws IllegalArgumentException     the illegal argument exception
     * @throws InvocationTargetException    the invocation target exception
     * @throws SecurityException            the security exception
     * @throws InstantiationException       the instantiation exception
     * @throws NoSuchMethodException        the no such method exception
     * @throws UnsupportedEncodingException the unsupported encoding exception
     * @throws MalformedURLException        the malformed URL exception
     * @throws AAIUnknownObjectException
     * @throws URISyntaxException
     */
    private Introspector getRelationshipObjectFromDb(List<Vertex> results, DBSerializer serializer, QueryParser query,
            URI uri, boolean isSkipRelatedTo) throws AAIException, IllegalArgumentException, SecurityException,
            UnsupportedEncodingException, AAIUnknownObjectException {

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
     * @param uri        the uri
     * @return the string
     */
    private String createNotFoundMessage(String resultType, URI uri) {
        return "No Node of type " + resultType + " found at: " + uri.getPath();
    }

    /**
     * Creates the not found message.
     *
     * @param resultType the result type
     * @param uri        the uri
     * @return the string
     */
    private String createRelationshipNotFoundMessage(String resultType, URI uri) {
        return "No relationship found of type " + resultType + " at the given URI: " + uri.getPath()
                + "/relationship-list";
    }

    /**
     * Sets the depth.
     *
     * @param depthParam the depth param
     * @return the int
     * @throws AAIException the AAI exception
     */
    protected int getDepth(Introspector obj, String depthParam) throws AAIException {
        int minimumDepth = getMinimumDepth(depthParam);
        int maximumDepth = getMaximumDepth(obj);
        if (minimumDepth > maximumDepth) {
            throw new AAIException("AAI_3303");
        }
        return minimumDepth;
    }

    public int getMinimumDepth(String depthParam) throws AAIException {
        String configDepthParam = AAIConfig.get("aai.rest.getall.depthparam", "");
        if (configDepthParam != null && !configDepthParam.isEmpty() && configDepthParam.equals(depthParam)) {
            return AAIProperties.MAXIMUM_DEPTH;
        }
        if (depthParam == null) {
            boolean isSchemaGreaterDepthVersion = this.version.compareTo(schemaVersions.getDepthVersion()) >= 0;
            if (isSchemaGreaterDepthVersion) {
                return 0;
            }
            return AAIProperties.MAXIMUM_DEPTH;
        } else if (!depthParam.isEmpty() && !"all".equals(depthParam)) {
            try {
                return Integer.parseInt(depthParam);
            } catch (Exception e) {
                throw new AAIException("AAI_4016");
            }
        } else {
            return AAIProperties.MAXIMUM_DEPTH;
        }
    }

    public int getMaximumDepth(Introspector obj) throws AAIException {
        String maxDepth = obj.getMetadata(ObjectMetadata.MAXIMUM_DEPTH);
        if (maxDepth != null) {
            try {
                return Integer.parseInt(maxDepth);
            } catch (Exception ex) {
                throw new AAIException("AAI_4018");
            }
        } else {
            return AAIProperties.MAXIMUM_DEPTH;
        }
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
                HashMap<String, Introspector> relatedObjects = serializer.getRelatedObjects(queryEngine, entry.getKey(),
                        entry.getValue(), this.loader);
                if (null != entry.getValue()) {
                    relatedObjectsMap.put(entry.getValue().getObjectId(), relatedObjects);
                }
            } catch (IllegalArgumentException | SecurityException | UnsupportedEncodingException | AAIException e) {
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
                    int endIndex = Math.min((this.getPaginationBucket() * this.getPaginationIndex()),
                            vertexList.size());
                    if (startIndex > endIndex) {
                        throw new AAIException("AAI_6150",
                                " ResultIndex is not appropriate for the result set, Needs to be <= " + endIndex);
                    }
                    finalList.add(new ArrayList<Object>());
                    for (int i = startIndex; i < endIndex; i++) {
                        ((ArrayList<Object>) finalList.get(0))
                                .add(((ArrayList<Object>) aggregateVertexList.get(0)).get(i));
                    }
                    return finalList;
                }
            }
        }
        // If the list size is greater than 1 or if pagination is not needed, return the
        // original list.
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
