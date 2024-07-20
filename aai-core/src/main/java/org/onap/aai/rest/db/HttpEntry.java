/**
 * ============LICENSE_START=======================================================
 * org.onap.aai
 * ================================================================================
 * Copyright © 2017-2018 AT&T Intellectual Property. All rights reserved.
 * ================================================================================
 * Modifications Copyright © 2024 Deutsche Telekom.
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
import org.onap.aai.query.builder.QueryOptions;
import org.onap.aai.query.entities.PaginationResult;
import org.onap.aai.rest.notification.NotificationService;
import org.onap.aai.rest.notification.UEBNotification;
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

    @Autowired
    private NodeIngestor nodeIngestor;

    @Autowired
    private LoaderFactory loaderFactory;

    @Autowired
    private SchemaVersions schemaVersions;

    @Autowired
    private NotificationService notificationService;

    @Value("${schema.uri.base.path}")
    private String basePath;

    private String serverBase;

    @Autowired
    private XmlFormatTransformer xmlFormatTransformer;

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
        if ("true".equals(AAIConfig.get("aai.notification.depth.all.enabled", "true"))) {
            this.notificationDepth = AAIProperties.MAXIMUM_DEPTH;
        } else {
            this.notificationDepth = AAIProperties.MINIMUM_DEPTH;
        }
        return this;
    }

    public HttpEntry setHttpEntryProperties(SchemaVersion version, String serverBase) {
        setHttpEntryProperties(version);
        this.serverBase = serverBase;
        return this;
    }

    public HttpEntry setHttpEntryProperties(SchemaVersion version, UEBNotification notification) {
        setHttpEntryProperties(version);
        this.notification = notification;
        return this;
    }

    public HttpEntry setHttpEntryProperties(SchemaVersion version, UEBNotification notification,
            int notificationDepth) {
        setHttpEntryProperties(version);
        this.notification = notification;
        this.notificationDepth = notificationDepth;
        return this;
    }

    public ModelType getIntrospectorFactoryType() {
        return introspectorFactoryType;
    }

    public QueryStyle getQueryStyle() {
        return queryStyle;
    }

    public SchemaVersion getVersion() {
        return version;
    }

    public Loader getLoader() {
        return loader;
    }

    public TransactionalGraphEngine getDbEngine() {
        return dbEngine;
    }

    public Pair<Boolean, List<Pair<URI, Response>>> process(List<DBRequest> requests, String sourceOfTruth) throws AAIException {
        return this.process(requests, sourceOfTruth, true);
    }

    public Pair<Boolean, List<Pair<URI, Response>>> process(List<DBRequest> requests, String sourceOfTruth,boolean enableResourceVersion) throws AAIException {
        return this.process(requests, sourceOfTruth, Collections.emptySet(), enableResourceVersion, null);
    }

    public Pair<Boolean, List<Pair<URI, Response>>> process(List<DBRequest> requests, String sourceOfTruth, Set<String> groups) throws AAIException {
        return this.process(requests, sourceOfTruth, groups, true, null);
    }


    public Pair<Boolean, List<Pair<URI, Response>>> process(List<DBRequest> requests, String sourceOfTruth,
            Set<String> groups, boolean enableResourceVersion, QueryOptions queryOptions) throws AAIException {

        DBSerializer serializer = null;

        if (serverBase != null) {
            serializer = new DBSerializer(version, dbEngine, introspectorFactoryType, sourceOfTruth, groups,
                    notificationDepth, serverBase);
        } else {
            serializer = new DBSerializer(version, dbEngine, introspectorFactoryType, sourceOfTruth, groups,
                    notificationDepth);
        }

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

        if (requests != null && !requests.isEmpty()) {
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

                    boolean groupsAvailable = serializer.getGroups() != null && !serializer.getGroups().isEmpty();
                    List<Vertex> queryResult;
                    PaginationResult<Vertex> paginationResult = null;
                    if(queryOptions != null && queryOptions.getPageable() != null) {
                        paginationResult = executePaginatedQuery(query, queryOptions);
                        queryResult = paginationResult.getResults();
                    } else {
                        queryResult = executeQuery(query, queryOptions);
                    }

                    List<Vertex> vertices = groupsAvailable
                        ? queryResult.stream()
                            .filter(vertex -> OwnerCheck.isAuthorized(groups, vertex))
                            .collect(Collectors.toList())
                        : queryResult;

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
                    if (vertices.size() > 1
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
                     * This skip-related-to query parameter is used to determine if the relationships object will omit
                     * the related-to-property
                     * If a GET is sent to resources without a format, if format=resource, or if format=resource_and_url
                     * with this param set to false
                     * then behavior will be keep the related-to properties. By default, set to true.
                     * Otherwise, for any other case, when the skip-related-to parameter exists, has value=true, or some
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
                    switch (method) {
                        case GET:

                            if (format == null) {
                                obj = this.getObjectFromDb(vertices, serializer, query, obj, request.getUri(), depth,
                                        isNodeOnly, cleanUp, isSkipRelatedTo);

                                if (obj != null) {
                                    status = Status.OK;
                                    MarshallerProperties properties;
                                    Optional<MarshallerProperties> marshallerPropOpt =
                                            request.getMarshallerProperties();
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
                                    String msg =
                                            createRelationshipNotFoundMessage(query.getResultType(), request.getUri());
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
                                Map<String, Pair<Introspector, LinkedHashMap<String, Introspector>>> allImpliedDeleteObjs =
                                        serializer.getImpliedDeleteUriObjectPair();

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
                                notificationService.buildNotificationEvent(sourceOfTruth, status, transactionId, notification,
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
                    if (response == null && v != null && (method.equals(HttpMethod.PUT) || method.equals(HttpMethod.GET)
                            || method.equals(HttpMethod.MERGE_PATCH) || method.equals(HttpMethod.GET_RELATIONSHIP))

                    ) {
                        String myvertid = v.id().toString();
                        if (paginationResult != null && paginationResult.getTotalCount() != null) {
                            long totalPages = getTotalPages(queryOptions, paginationResult);
                            response = Response.status(status).header("vertex-id", myvertid)
                                    .header("total-results", paginationResult.getTotalCount())
                                    .header("total-pages", totalPages)
                                    .entity(result)
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
                response =
                        Response.status(e.getErrorObject().getHTTPResponseCode())
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
                response =
                        Response.status(ex.getErrorObject().getHTTPResponseCode())
                                .entity(ErrorLogHelper.getRESTAPIErrorResponse(
                                        request.getHeaders().getAcceptableMediaTypes(), ex, templateVars))
                                .type(outputMediaType).build();
                Pair<URI, Response> pairedResp = Pair.with(request.getUri(), response);
                responses.add(pairedResp);
            } finally {
                if (response != null) {
                    metricLog.post(request, response);
                }
            }
        }

        if (success) {
            notificationService.generateEvents(notification, notificationDepth, sourceOfTruth, serializer, transactionId, queryEngine, mainVertexesToNotifyOn, version);
        } else {
            notification.clearEvents();
        }

        return Pair.with(success, responses);
    }

    private long getTotalPages(QueryOptions queryOptions, PaginationResult<Vertex> paginationResult) {
        long totalCount = paginationResult.getTotalCount();
        int pageSize = queryOptions.getPageable().getPageSize();
        long totalPages = totalCount / pageSize;
        // conditionally add a page for the remainder
        if (totalCount % pageSize > 0) {
            totalPages++;
        }
        return totalPages;
    }

    private List<Vertex> executeQuery(QueryParser query, QueryOptions queryOptions) {
        return (queryOptions != null && queryOptions.getSort() != null)
            ? query.getQueryBuilder().sort(queryOptions.getSort()).toList()
            : query.getQueryBuilder().toList();
    }

    private PaginationResult<Vertex> executePaginatedQuery(QueryParser query, QueryOptions queryOptions) {
        return queryOptions.getSort() != null
            ? query.getQueryBuilder().sort(queryOptions.getSort()).toPaginationResult(queryOptions.getPageable())
            : query.getQueryBuilder().toPaginationResult(queryOptions.getPageable());
    }

    private String getMediaType(List<MediaType> mediaTypeList) {
        String mediaType = MediaType.APPLICATION_JSON; // json is the default
        for (MediaType mt : mediaTypeList) {
            if (MediaType.APPLICATION_XML_TYPE.isCompatible(mt)) {
                mediaType = MediaType.APPLICATION_XML;
            }
        }
        return mediaType;
    }

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

    private String createNotFoundMessage(String resultType, URI uri) {
        return "No Node of type " + resultType + " found at: " + uri.getPath();
    }

    private String createRelationshipNotFoundMessage(String resultType, URI uri) {
        return "No relationship found of type " + resultType + " at the given URI: " + uri.getPath()
                + "/relationship-list";
    }

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
            } catch (IllegalArgumentException | SecurityException | UnsupportedEncodingException | AAIException e) {
                LOGGER.warn("Unable to get realted Objects, Just continue");
            }

        }

        return relatedObjectsMap;

    }
}
