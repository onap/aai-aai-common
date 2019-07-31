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

package org.onap.aai.serialization.db;

import com.att.eelf.configuration.EELFLogger;
import com.att.eelf.configuration.EELFManager;
import com.google.common.base.CaseFormat;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.ws.rs.core.UriBuilder;

import org.apache.commons.collections.IteratorUtils;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__;
import org.apache.tinkerpop.gremlin.structure.Direction;
import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.apache.tinkerpop.gremlin.structure.VertexProperty;
import org.janusgraph.core.SchemaViolationException;
import org.javatuples.Triplet;
import org.onap.aai.concurrent.AaiCallable;
import org.onap.aai.config.SpringContextAware;
import org.onap.aai.db.props.AAIProperties;
import org.onap.aai.edges.EdgeIngestor;
import org.onap.aai.edges.EdgeRule;
import org.onap.aai.edges.EdgeRuleQuery;
import org.onap.aai.edges.TypeAlphabetizer;
import org.onap.aai.edges.enums.AAIDirection;
import org.onap.aai.edges.enums.EdgeField;
import org.onap.aai.edges.enums.EdgeProperty;
import org.onap.aai.edges.enums.EdgeType;
import org.onap.aai.edges.exceptions.AmbiguousRuleChoiceException;
import org.onap.aai.edges.exceptions.EdgeRuleNotFoundException;
import org.onap.aai.exceptions.AAIException;
import org.onap.aai.introspection.*;
import org.onap.aai.introspection.exceptions.AAIUnknownObjectException;
import org.onap.aai.introspection.sideeffect.*;
import org.onap.aai.logging.ErrorLogHelper;
import org.onap.aai.logging.LogFormatTools;
import org.onap.aai.logging.LoggingContext;
import org.onap.aai.logging.StopWatch;
import org.onap.aai.parsers.query.QueryParser;
import org.onap.aai.parsers.uri.URIParser;
import org.onap.aai.parsers.uri.URIToRelationshipObject;
import org.onap.aai.query.builder.QueryBuilder;
import org.onap.aai.schema.enums.ObjectMetadata;
import org.onap.aai.schema.enums.PropertyMetadata;
import org.onap.aai.serialization.db.exceptions.MultipleEdgeRuleFoundException;
import org.onap.aai.serialization.db.exceptions.NoEdgeRuleFoundException;
import org.onap.aai.serialization.engines.TransactionalGraphEngine;
import org.onap.aai.serialization.engines.query.QueryEngine;
import org.onap.aai.setup.SchemaVersion;
import org.onap.aai.setup.SchemaVersions;
import org.onap.aai.util.AAIConfig;
import org.onap.aai.util.AAIConstants;
import org.onap.aai.workarounds.NamingExceptions;
import org.springframework.context.ApplicationContext;

public class DBSerializer {

    private static final EELFLogger LOGGER = EELFManager.getInstance().getLogger(DBSerializer.class);

    private static final String IMPLICIT_DELETE = "Implicit DELETE";

    private static final String MISSING_REQUIRED_NODE_PROPERTY = "Vertex missing required aai-node-type property";

    private final TransactionalGraphEngine engine;
    private final String sourceOfTruth;
    private final ModelType introspectionType;
    private final SchemaVersion version;
    private final Loader latestLoader;
    private EdgeSerializer edgeSer;
    private EdgeIngestor edgeRules;
    private final Loader loader;
    private final String baseURL;
    private double dbTimeMsecs = 0;
    private long currentTimeMillis;

    private SchemaVersions schemaVersions;
    private Set<String> namedPropNodes;

    /**
     * Instantiates a new DB serializer.
     *
     * @param version the version
     * @param engine the engine
     * @param introspectionType the introspection type
     * @param sourceOfTruth the source of truth
     * @throws AAIException
     */
    public DBSerializer(SchemaVersion version, TransactionalGraphEngine engine, ModelType introspectionType,
            String sourceOfTruth) throws AAIException {
        this.engine = engine;
        this.sourceOfTruth = sourceOfTruth;
        this.introspectionType = introspectionType;
        this.schemaVersions = (SchemaVersions) SpringContextAware.getBean("schemaVersions");
        SchemaVersion LATEST = schemaVersions.getDefaultVersion();
        this.latestLoader =
                SpringContextAware.getBean(LoaderFactory.class).createLoaderForVersion(introspectionType, LATEST);
        this.version = version;
        this.loader =
                SpringContextAware.getBean(LoaderFactory.class).createLoaderForVersion(introspectionType, version);
        this.namedPropNodes = this.latestLoader.getNamedPropNodes();
        this.baseURL = AAIConfig.get(AAIConstants.AAI_SERVER_URL_BASE);
        this.currentTimeMillis = System.currentTimeMillis();
        initBeans();
    }

    private void initBeans() {
        // TODO proper spring wiring, but that requires a lot of refactoring so for now we have this
        ApplicationContext ctx = SpringContextAware.getApplicationContext();
        EdgeIngestor ei = ctx.getBean(EdgeIngestor.class);
        setEdgeIngestor(ei);
        EdgeSerializer es = ctx.getBean(EdgeSerializer.class);
        setEdgeSerializer(es);
    }

    private void backupESInit() {
        setEdgeSerializer(new EdgeSerializer(this.edgeRules));
    }

    public void setEdgeSerializer(EdgeSerializer edgeSer) {
        this.edgeSer = edgeSer;
    }

    public EdgeSerializer getEdgeSeriailizer() {
        return this.edgeSer;
    }

    public void setEdgeIngestor(EdgeIngestor ei) {
        this.edgeRules = ei;
    }

    public EdgeIngestor getEdgeIngestor() {
        return this.edgeRules;
    }

    /**
     * Touch standard vertex properties.
     *
     * @param v the v
     * @param isNewVertex the is new vertex
     */
    public void touchStandardVertexProperties(Vertex v, boolean isNewVertex) {
        String timeNowInSec = Long.toString(currentTimeMillis);

        if (isNewVertex) {
            v.property(AAIProperties.SOURCE_OF_TRUTH, this.sourceOfTruth);
            v.property(AAIProperties.CREATED_TS, timeNowInSec);
            v.property(AAIProperties.AAI_UUID, UUID.randomUUID().toString());
        }
        v.property(AAIProperties.RESOURCE_VERSION, timeNowInSec);
        v.property(AAIProperties.LAST_MOD_TS, timeNowInSec);
        v.property(AAIProperties.LAST_MOD_SOURCE_OF_TRUTH, this.sourceOfTruth);

    }

    private void touchStandardVertexProperties(String nodeType, Vertex v, boolean isNewVertex) {

        v.property(AAIProperties.NODE_TYPE, nodeType);
        touchStandardVertexProperties(v, isNewVertex);

    }

    /**
     * Creates the new vertex.
     *
     * @param wrappedObject the wrapped object
     * @return the vertex
     * @throws UnsupportedEncodingException the unsupported encoding exception
     * @throws AAIException the AAI exception
     */
    public Vertex createNewVertex(Introspector wrappedObject) {
        Vertex v;
        try {
            StopWatch.conditionalStart();
            v = this.engine.tx().addVertex();
            touchStandardVertexProperties(wrappedObject.getDbName(), v, true);
        } finally {
            dbTimeMsecs += StopWatch.stopIfStarted();
        }
        return v;
    }

    /**
     * Trim class name.
     *
     * @param className the class name
     * @return the string
     */
    /*
     * Removes the classpath from a class name
     */
    public String trimClassName(String className) {
        String returnValue = "";

        if (className.lastIndexOf('.') == -1) {
            return className;
        }
        returnValue = className.substring(className.lastIndexOf('.') + 1, className.length());

        return returnValue;
    }

    /**
     * Serialize to db.
     *
     * @param obj the obj
     * @param v the v
     * @param uriQuery the uri query
     * @param identifier the identifier
     * @throws SecurityException the security exception
     * @throws IllegalAccessException the illegal access exception
     * @throws IllegalArgumentException the illegal argument exception
     * @throws InvocationTargetException the invocation target exception
     * @throws InstantiationException the instantiation exception
     * @throws InterruptedException the interrupted exception
     * @throws NoSuchMethodException the no such method exception
     * @throws AAIException the AAI exception
     * @throws UnsupportedEncodingException the unsupported encoding exception
     * @throws AAIUnknownObjectException
     */
    public void serializeToDb(Introspector obj, Vertex v, QueryParser uriQuery, String identifier,
            String requestContext) throws AAIException, UnsupportedEncodingException {
        StopWatch.conditionalStart();
        try {
            if (uriQuery.isDependent()) {
                // try to find the parent
                List<Vertex> vertices = uriQuery.getQueryBuilder().getParentQuery().toList();
                if (!vertices.isEmpty()) {
                    Vertex parent = vertices.get(0);
                    this.reflectDependentVertex(parent, v, obj, requestContext);
                } else {
                    dbTimeMsecs += StopWatch.stopIfStarted();
                    throw new AAIException("AAI_6114",
                            "No parent Node of type " + uriQuery.getParentResultType() + " for " + identifier);
                }
            } else {
                serializeSingleVertex(v, obj, requestContext);
            }

        } catch (SchemaViolationException e) {
            dbTimeMsecs += StopWatch.stopIfStarted();
            throw new AAIException("AAI_6117", e);
        }
        dbTimeMsecs += StopWatch.stopIfStarted();
    }

    public void serializeSingleVertex(Vertex v, Introspector obj, String requestContext)
            throws UnsupportedEncodingException, AAIException {
        StopWatch.conditionalStart();
        try {
            boolean isTopLevel = obj.isTopLevel();
            if (isTopLevel) {
                addUriIfNeeded(v, obj.getURI());
            }

            processObject(obj, v, requestContext);
            if (!isTopLevel) {
                URI uri = this.getURIForVertex(v);
                URIParser parser = new URIParser(this.loader, uri);
                if (parser.validate()) {
                    addUriIfNeeded(v, uri.toString());
                }
            }
        } catch (SchemaViolationException e) {
            throw new AAIException("AAI_6117", e);
        } finally {
            dbTimeMsecs += StopWatch.stopIfStarted();
        }
    }

    private void addUriIfNeeded(Vertex v, String uri) {
        VertexProperty<String> uriProp = v.property(AAIProperties.AAI_URI);
        if (!uriProp.isPresent() || (uriProp.isPresent() && !uriProp.value().equals(uri))) {
            v.property(AAIProperties.AAI_URI, uri);
        }
    }

    /**
     * Process object.
     *
     * @param <T> the generic type
     * @param obj the obj
     * @param v the v
     * @return the list
     * @throws IllegalAccessException the illegal access exception
     * @throws IllegalArgumentException the illegal argument exception
     * @throws InvocationTargetException the invocation target exception
     * @throws InstantiationException the instantiation exception
     * @throws NoSuchMethodException the no such method exception
     * @throws SecurityException the security exception
     * @throws AAIException the AAI exception
     * @throws UnsupportedEncodingException the unsupported encoding exception
     * @throws AAIUnknownObjectException
     */
    /*
     * Helper method for reflectToDb
     * Handles all the property setting
     */
    private <T> List<Vertex> processObject(Introspector obj, Vertex v, String requestContext)
            throws UnsupportedEncodingException, AAIException {
        Set<String> properties = new LinkedHashSet<>(obj.getProperties());
        properties.remove(AAIProperties.RESOURCE_VERSION);
        List<Vertex> dependentVertexes = new ArrayList<>();
        List<Vertex> processedVertexes = new ArrayList<>();
        boolean isComplexType = false;
        boolean isListType = false;
        if (!obj.isContainer()) {
            this.touchStandardVertexProperties(v, false);
        }
        this.executePreSideEffects(obj, v);
        for (String property : properties) {
            Object value = null;
            final String propertyType;
            propertyType = obj.getType(property);
            isComplexType = obj.isComplexType(property);
            isListType = obj.isListType(property);
            value = obj.getValue(property);

            if (!(isComplexType || isListType)) {
                boolean canModify = this.canModify(obj, property, requestContext);

                if (canModify) {
                    final Map<PropertyMetadata, String> metadata = obj.getPropertyMetadata(property);
                    String dbProperty = property;
                    if (metadata.containsKey(PropertyMetadata.DB_ALIAS)) {
                        dbProperty = metadata.get(PropertyMetadata.DB_ALIAS);
                    }
                    if (metadata.containsKey(PropertyMetadata.DATA_LINK)) {
                        // data linked properties are ephemeral
                        // they are populated dynamically on GETs
                        continue;
                    }
                    if (value != null) {
                        if (!value.equals(v.property(dbProperty).orElse(null))) {
                            if (propertyType.toLowerCase().contains(".long")) {
                                v.property(dbProperty, new Integer(((Long) value).toString()));
                            } else {
                                v.property(dbProperty, value);
                            }
                        }
                    } else {
                        v.property(dbProperty).remove();
                    }
                }
            } else if (isListType) {
                List<Object> list = (List<Object>) value;
                if (obj.isComplexGenericType(property)) {
                    if (list != null) {
                        for (Object o : list) {
                            Introspector child = IntrospectorFactory.newInstance(this.introspectionType, o);
                            child.setURIChain(obj.getURI());
                            processedVertexes.add(reflectDependentVertex(v, child, requestContext));
                        }
                    }
                } else {
                    // simple list case
                    engine.setListProperty(v, property, list);
                }
            } else {
                // method.getReturnType() is not 'simple' then create a vertex and edge recursively returning an edge
                // back to this method
                if (value != null) { // effectively ignore complex properties not included in the object we're
                                     // processing
                    if (value.getClass().isArray()) {

                        int length = Array.getLength(value);
                        for (int i = 0; i < length; i++) {
                            Object arrayElement = Array.get(value, i);
                            Introspector child = IntrospectorFactory.newInstance(this.introspectionType, arrayElement);
                            child.setURIChain(obj.getURI());
                            processedVertexes.add(reflectDependentVertex(v, child, requestContext));

                        }
                    } else if (!property.equals("relationship-list")) {
                        // container case
                        Introspector introspector = IntrospectorFactory.newInstance(this.introspectionType, value);
                        if (introspector.isContainer()) {
                            dependentVertexes.addAll(
                                    this.engine.getQueryEngine().findChildrenOfType(v, introspector.getChildDBName()));
                            introspector.setURIChain(obj.getURI());

                            processedVertexes.addAll(processObject(introspector, v, requestContext));

                        } else {
                            dependentVertexes.addAll(
                                    this.engine.getQueryEngine().findChildrenOfType(v, introspector.getDbName()));
                            processedVertexes.add(reflectDependentVertex(v, introspector, requestContext));

                        }
                    } else if (property.equals("relationship-list")) {
                        handleRelationships(obj, v);
                    }
                }
            }
        }
        this.writeThroughDefaults(v, obj);
        /* handle those vertexes not touched */
        for (Vertex toBeRemoved : processedVertexes) {
            dependentVertexes.remove(toBeRemoved);
        }

        // If the dependent vertices are not empty, then with
        // the current behaviour, it should remove the vertices implicitly
        // We are updating the code to properly log which call
        // is doing this so the SE can work with the clients making the call to
        // tell them not to call this API and can hopefully deprecate this
        // functionality in the future releases
        if (!dependentVertexes.isEmpty()) {

            LoggingContext.responseDescription(IMPLICIT_DELETE);

            // Find all the deletable vertices from the dependent vertices that should be deleted
            // So for each of the following dependent vertices,
            // we will use the edge properties and do the cascade delete
            List<Vertex> impliedDeleteVertices = this.engine.getQueryEngine().findDeletable(dependentVertexes);
            int impliedDeleteCount = impliedDeleteVertices.size();

            LOGGER.warn(
                    "For the vertex with id {}, doing an implicit delete on update will delete total of {} vertexes",
                    v.id(), impliedDeleteCount);

            String impliedDeleteLogEnabled = AAIConfig.get(AAIConstants.AAI_IMPLIED_DELETE_LOG_ENABLED, "true");

            int impliedDeleteLogLimit = AAIConfig.getInt(AAIConstants.AAI_IMPLIED_DELETE_LOG_LIMIT, "-1");

            if (impliedDeleteLogLimit == -1) {
                impliedDeleteLogLimit = Integer.MAX_VALUE;
            }

            // If the logging is enabled for implied delete
            // then log the payload in the latest format
            if ("true".equals(impliedDeleteLogEnabled) && impliedDeleteCount <= impliedDeleteLogLimit) {
                for (Vertex vertex : impliedDeleteVertices) {
                    Introspector introspector = null;
                    try {
                        introspector = getLatestVersionView(vertex);
                        if (LOGGER.isInfoEnabled()) {
                            LOGGER.info("Implied delete object in json format {}", introspector.marshal(false));
                        }
                    } catch (Exception ex) {
                        LOGGER.warn(
                                "Encountered an exception during retrieval of vertex properties with vertex-id {} -> {}",
                                v.id(), LogFormatTools.getStackTop(ex));
                    }
                }
            }

            // After all the appropriate logging, calling the delete to delete the affected vertices
            this.delete(impliedDeleteVertices);
        }

        this.executePostSideEffects(obj, v);
        return processedVertexes;
    }

    /**
     * Handle relationships.
     *
     * @param obj the obj
     * @param vertex the vertex
     * @throws SecurityException the security exception
     * @throws IllegalAccessException the illegal access exception
     * @throws IllegalArgumentException the illegal argument exception
     * @throws InvocationTargetException the invocation target exception
     * @throws UnsupportedEncodingException the unsupported encoding exception
     * @throws AAIException the AAI exception
     */
    /*
     * Handles the explicit relationships defined for an obj
     */
    private void handleRelationships(Introspector obj, Vertex vertex)
            throws UnsupportedEncodingException, AAIException {

        Introspector wrappedRl = obj.getWrappedValue("relationship-list");
        processRelationshipList(wrappedRl, vertex);

    }

    /**
     * Process relationship list.
     *
     * @param wrapped the wrapped
     * @param v the v
     * @throws UnsupportedEncodingException the unsupported encoding exception
     * @throws AAIException the AAI exception
     */
    private void processRelationshipList(Introspector wrapped, Vertex v)
            throws UnsupportedEncodingException, AAIException {

        List<Object> relationships = (List<Object>) wrapped.getValue("relationship");

        List<Triplet<Vertex, Vertex, String>> addEdges = new ArrayList<>();
        List<Edge> existingEdges = this.engine.getQueryEngine().findEdgesForVersion(v, wrapped.getLoader());

        for (Object relationship : relationships) {
            Edge e = null;
            Vertex cousinVertex = null;
            String label = null;
            Introspector wrappedRel = IntrospectorFactory.newInstance(this.introspectionType, relationship);
            QueryParser parser = engine.getQueryBuilder().createQueryFromRelationship(wrappedRel);

            if (wrappedRel.hasProperty("relationship-label")) {
                label = wrappedRel.getValue("relationship-label");
            }

            List<Vertex> results = parser.getQueryBuilder().toList();
            if (results.isEmpty()) {
                final AAIException ex = new AAIException("AAI_6129",
                        "Node of type " + parser.getResultType() + ". Could not find object at: " + parser.getUri());
                ex.getTemplateVars().add(parser.getResultType());
                ex.getTemplateVars().add(parser.getUri().toString());
                throw ex;
            } else {
                // still an issue if there's more than one
                cousinVertex = results.get(0);
            }

            if (cousinVertex != null) {
                String vType = (String) v.property(AAIProperties.NODE_TYPE).value();
                String cousinType = (String) cousinVertex.property(AAIProperties.NODE_TYPE).value();
                EdgeRuleQuery.Builder baseQ = new EdgeRuleQuery.Builder(vType, cousinType).label(label);

                if (!edgeRules.hasRule(baseQ.build())) {
                    throw new AAIException("AAI_6120",
                            "No EdgeRule found for passed nodeTypes: "
                                    + v.property(AAIProperties.NODE_TYPE).value().toString() + ", "
                                    + cousinVertex.property(AAIProperties.NODE_TYPE).value().toString()
                                    + (label != null ? (" with label " + label) : "") + ".");
                } else if (edgeRules.hasRule(baseQ.edgeType(EdgeType.TREE).build())
                        && !edgeRules.hasRule(baseQ.edgeType(EdgeType.COUSIN).build())) {
                    throw new AAIException("AAI_6145");
                }

                e = this.getEdgeBetween(EdgeType.COUSIN, v, cousinVertex, label);

                if (e == null) {
                    addEdges.add(new Triplet<>(v, cousinVertex, label));
                } else {
                    existingEdges.remove(e);
                }
            }
        }

        for (Edge edge : existingEdges) {
            edge.remove();
        }
        for (Triplet<Vertex, Vertex, String> triplet : addEdges) {
            try {
                edgeSer.addEdge(this.engine.asAdmin().getTraversalSource(), triplet.getValue0(), triplet.getValue1(),
                        triplet.getValue2());
            } catch (NoEdgeRuleFoundException e) {
                throw new AAIException("AAI_6129", e);
            }
        }

    }

    /**
     * Write through defaults.
     *
     * @param v the v
     * @param obj the obj
     * @throws AAIUnknownObjectException
     */
    private void writeThroughDefaults(Vertex v, Introspector obj) throws AAIUnknownObjectException {
        Introspector latest = this.latestLoader.introspectorFromName(obj.getName());
        if (latest != null) {
            Set<String> required = latest.getRequiredProperties();

            for (String field : required) {
                String defaultValue = null;
                Object vertexProp = null;
                defaultValue = latest.getPropertyMetadata(field).get(PropertyMetadata.DEFAULT_VALUE);
                if (defaultValue != null) {
                    vertexProp = v.<Object>property(field).orElse(null);
                    if (vertexProp == null) {
                        v.property(field, defaultValue);
                    }
                }
            }
        }

    }

    /**
     * Reflect dependent vertex.
     *
     * @param v the v
     * @param dependentObj the dependent obj
     * @return the vertex
     * @throws IllegalAccessException the illegal access exception
     * @throws IllegalArgumentException the illegal argument exception
     * @throws InvocationTargetException the invocation target exception
     * @throws InstantiationException the instantiation exception
     * @throws NoSuchMethodException the no such method exception
     * @throws SecurityException the security exception
     * @throws AAIException the AAI exception
     * @throws UnsupportedEncodingException the unsupported encoding exception
     * @throws AAIUnknownObjectException
     */
    private Vertex reflectDependentVertex(Vertex v, Introspector dependentObj, String requestContext)
            throws AAIException, UnsupportedEncodingException {

        // QueryParser p = this.engine.getQueryBuilder().createQueryFromURI(obj.getURI());
        // List<Vertex> items = p.getQuery().toList();
        QueryBuilder<Vertex> query = this.engine.getQueryBuilder(v);
        query.createEdgeTraversal(EdgeType.TREE, v, dependentObj);
        query.createKeyQuery(dependentObj);

        List<Vertex> items = query.toList();

        Vertex dependentVertex = null;
        if (items.size() == 1) {
            dependentVertex = items.get(0);
            this.verifyResourceVersion("update", dependentObj.getDbName(),
                    dependentVertex.<String>property(AAIProperties.RESOURCE_VERSION).orElse(null),
                    (String) dependentObj.getValue(AAIProperties.RESOURCE_VERSION), (String) dependentObj.getURI());
        } else {
            this.verifyResourceVersion("create", dependentObj.getDbName(), "",
                    (String) dependentObj.getValue(AAIProperties.RESOURCE_VERSION), (String) dependentObj.getURI());
            dependentVertex = createNewVertex(dependentObj);
        }

        return reflectDependentVertex(v, dependentVertex, dependentObj, requestContext);

    }

    /**
     * Reflect dependent vertex.
     *
     * @param parent the parent
     * @param child the child
     * @param obj the obj
     * @return the vertex
     * @throws IllegalAccessException the illegal access exception
     * @throws IllegalArgumentException the illegal argument exception
     * @throws InvocationTargetException the invocation target exception
     * @throws InstantiationException the instantiation exception
     * @throws NoSuchMethodException the no such method exception
     * @throws SecurityException the security exception
     * @throws AAIException the AAI exception
     * @throws UnsupportedEncodingException the unsupported encoding exception
     * @throws AAIUnknownObjectException
     */
    private Vertex reflectDependentVertex(Vertex parent, Vertex child, Introspector obj, String requestContext)
            throws AAIException, UnsupportedEncodingException {

        String parentUri = parent.<String>property(AAIProperties.AAI_URI).orElse(null);
        if (parentUri != null) {
            String uri;
            uri = obj.getURI();
            addUriIfNeeded(child, parentUri + uri);
        }
        processObject(obj, child, requestContext);

        Edge e;
        e = this.getEdgeBetween(EdgeType.TREE, parent, child, null);

        if (e == null) {
            String canBeLinked = obj.getMetadata(ObjectMetadata.CAN_BE_LINKED);
            if (canBeLinked != null && canBeLinked.equals("true")) {
                Loader ldrForCntxt = SpringContextAware.getBean(LoaderFactory.class)
                        .createLoaderForVersion(introspectionType, getVerForContext(requestContext));
                boolean isFirst = !this.engine.getQueryBuilder(ldrForCntxt, parent)
                        .createEdgeTraversal(EdgeType.TREE, parent, obj).hasNext();
                if (isFirst) {
                    child.property(AAIProperties.LINKED, true);
                }
            }
            edgeSer.addTreeEdge(this.engine.asAdmin().getTraversalSource(), parent, child);
        }
        return child;

    }

    private SchemaVersion getVerForContext(String requestContext) {
        Pattern pattern = Pattern.compile("v[0-9]+");
        Matcher m = pattern.matcher(requestContext);
        if (!m.find()) {
            return this.version;
        } else {
            return new SchemaVersion(requestContext);
        }
    }

    /**
     * Db to object.
     *
     * @param vertices the vertices
     * @param obj the obj
     * @param depth the depth
     * @param cleanUp the clean up
     * @return the introspector
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
    public Introspector dbToObject(List<Vertex> vertices, final Introspector obj, int depth, boolean nodeOnly,
            String cleanUp) throws UnsupportedEncodingException, AAIException {
        final int internalDepth;
        if (depth == Integer.MAX_VALUE) {
            internalDepth = depth--;
        } else {
            internalDepth = depth;
        }
        StopWatch.conditionalStart();
        if (vertices.size() > 1 && !obj.isContainer()) {
            dbTimeMsecs += StopWatch.stopIfStarted();
            throw new AAIException("AAI_6136",
                    "query object mismatch: this object cannot hold multiple items." + obj.getDbName());
        } else if (obj.isContainer()) {
            final List getList;
            String listProperty = null;
            for (String property : obj.getProperties()) {
                if (obj.isListType(property) && obj.isComplexGenericType(property)) {
                    listProperty = property;
                    break;
                }
            }
            final String propertyName = listProperty;
            getList = (List) obj.getValue(listProperty);

            /*
             * This is an experimental multithreading experiment
             * on get alls.
             */
            ExecutorService pool = GetAllPool.getInstance().getPool();

            List<Future<Object>> futures = new ArrayList<>();

            QueryEngine tgEngine = this.engine.getQueryEngine();
            for (Vertex v : vertices) {

                AaiCallable<Object> task = new AaiCallable<Object>() {
                    @Override
                    public Object process() throws UnsupportedEncodingException, AAIException {
                        Set<Vertex> seen = new HashSet<>();
                        Introspector childObject;
                        try {
                            childObject = obj.newIntrospectorInstanceOfNestedProperty(propertyName);
                        } catch (AAIUnknownObjectException e) {
                            throw e;
                        }
                        try {
                            dbToObject(childObject, v, seen, internalDepth, nodeOnly, cleanUp);
                        } catch (UnsupportedEncodingException e) {
                            throw e;
                        } catch (AAIException e) {
                            throw e;
                        }
                        return childObject.getUnderlyingObject();
                        // getList.add(childObject.getUnderlyingObject());
                    }
                };
                futures.add(pool.submit(task));
            }

            for (Future<Object> future : futures) {
                try {
                    getList.add(future.get());
                } catch (ExecutionException e) {
                    dbTimeMsecs += StopWatch.stopIfStarted();
                    throw new AAIException("AAI_4000", e);
                } catch (InterruptedException e) {
                    dbTimeMsecs += StopWatch.stopIfStarted();
                    throw new AAIException("AAI_4000", e);
                }
            }
        } else if (vertices.size() == 1) {
            Set<Vertex> seen = new HashSet<>();
            dbToObject(obj, vertices.get(0), seen, depth, nodeOnly, cleanUp);
        } else {
            // obj = null;
        }

        dbTimeMsecs += StopWatch.stopIfStarted();
        return obj;
    }

    /**
     * Db to object.
     *
     * @param obj the obj
     * @param v the v
     * @param seen the seen
     * @param depth the depth
     * @param cleanUp the clean up
     * @return the introspector
     * @throws IllegalAccessException the illegal access exception
     * @throws IllegalArgumentException the illegal argument exception
     * @throws InvocationTargetException the invocation target exception
     * @throws SecurityException the security exception
     * @throws InstantiationException the instantiation exception
     * @throws NoSuchMethodException the no such method exception
     * @throws UnsupportedEncodingException the unsupported encoding exception
     * @throws AAIException the AAI exception
     * @throws MalformedURLException the malformed URL exception
     * @throws AAIUnknownObjectException
     * @throws URISyntaxException
     */
    private Introspector dbToObject(Introspector obj, Vertex v, Set<Vertex> seen, int depth, boolean nodeOnly,
            String cleanUp) throws AAIException, UnsupportedEncodingException {

        if (depth < 0) {
            return null;
        }
        depth--;
        seen.add(v);

        boolean modified = false;
        for (String property : obj.getProperties(PropertyPredicates.isVisible())) {
            List<Object> getList = null;
            Vertex[] vertices = null;

            if (!(obj.isComplexType(property) || obj.isListType(property))) {
                this.copySimpleProperty(property, obj, v);
                modified = true;
            } else {
                if (obj.isComplexType(property)) {
                    /* container case */

                    if (!property.equals("relationship-list") && depth >= 0) {
                        Introspector argumentObject = obj.newIntrospectorInstanceOfProperty(property);
                        Object result = dbToObject(argumentObject, v, seen, depth + 1, nodeOnly, cleanUp);
                        if (result != null) {
                            obj.setValue(property, argumentObject.getUnderlyingObject());
                            modified = true;
                        }
                    } else if (property.equals("relationship-list") && !nodeOnly) {
                        /* relationships need to be handled correctly */
                        Introspector relationshipList = obj.newIntrospectorInstanceOfProperty(property);
                        relationshipList = createRelationshipList(v, relationshipList, cleanUp);
                        if (relationshipList != null) {
                            modified = true;
                            obj.setValue(property, relationshipList.getUnderlyingObject());
                            modified = true;
                        }

                    }
                } else if (obj.isListType(property)) {

                    if (property.equals("any")) {
                        continue;
                    }
                    String genericType = obj.getGenericTypeClass(property).getSimpleName();
                    if (obj.isComplexGenericType(property) && depth >= 0) {
                        final String childDbName = convertFromCamelCase(genericType);
                        String vType = v.<String>property(AAIProperties.NODE_TYPE).orElse(null);
                        EdgeRule rule;

                        try {
                            rule = edgeRules.getRule(
                                    new EdgeRuleQuery.Builder(vType, childDbName).edgeType(EdgeType.TREE).build());
                        } catch (EdgeRuleNotFoundException e) {
                            throw new NoEdgeRuleFoundException(e);
                        } catch (AmbiguousRuleChoiceException e) {
                            throw new MultipleEdgeRuleFoundException(e);
                        }
                        if (!rule.getContains().equals(AAIDirection.NONE.toString())) {
                            // vertices = this.queryEngine.findRelatedVertices(v, Direction.OUT, rule.getLabel(),
                            // childDbName);
                            Direction ruleDirection = rule.getDirection();
                            Iterator<Vertex> itr = v.vertices(ruleDirection, rule.getLabel());
                            List<Vertex> verticesList = (List<Vertex>) IteratorUtils.toList(itr);
                            itr = verticesList.stream().filter(item -> {
                                return item.property(AAIProperties.NODE_TYPE).orElse("").equals(childDbName);
                            }).iterator();
                            if (itr.hasNext()) {
                                getList = (List<Object>) obj.getValue(property);
                            }
                            int processed = 0;
                            int removed = 0;
                            while (itr.hasNext()) {
                                Vertex childVertex = itr.next();
                                if (!seen.contains(childVertex)) {
                                    Introspector argumentObject = obj.newIntrospectorInstanceOfNestedProperty(property);

                                    Object result =
                                            dbToObject(argumentObject, childVertex, seen, depth, nodeOnly, cleanUp);
                                    if (result != null) {
                                        getList.add(argumentObject.getUnderlyingObject());
                                    }

                                    processed++;
                                } else {
                                    removed++;
                                    LOGGER.warn("Cycle found while serializing vertex id={}",
                                            childVertex.id().toString());
                                }
                            }
                            if (processed == 0) {
                                // vertices were all seen, reset the list
                                getList = null;
                            }
                            if (processed > 0) {
                                modified = true;
                            }
                        }
                    } else if (obj.isSimpleGenericType(property)) {
                        List<Object> temp = this.engine.getListProperty(v, property);
                        if (temp != null) {
                            getList = (List<Object>) obj.getValue(property);
                            getList.addAll(temp);
                            modified = true;
                        }

                    }

                }

            }
        }

        // no changes were made to this obj, discard the instance
        if (!modified) {
            return null;
        }
        this.enrichData(obj, v);
        return obj;

    }

    public Introspector getVertexProperties(Vertex v) throws AAIException, UnsupportedEncodingException {
        String nodeType = v.<String>property(AAIProperties.NODE_TYPE).orElse(null);
        if (nodeType == null) {
            throw new AAIException("AAI_6143");
        }

        Introspector obj = this.latestLoader.introspectorFromName(nodeType);
        Set<Vertex> seen = new HashSet<>();
        int depth = 0;
        String cleanUp = "false";
        boolean nodeOnly = true;
        StopWatch.conditionalStart();
        this.dbToObject(obj, v, seen, depth, nodeOnly, cleanUp);
        dbTimeMsecs += StopWatch.stopIfStarted();
        return obj;

    }

    public Introspector getLatestVersionView(Vertex v) throws AAIException, UnsupportedEncodingException {
        String nodeType = v.<String>property(AAIProperties.NODE_TYPE).orElse(null);
        if (nodeType == null) {
            throw new AAIException("AAI_6143");
        }
        Introspector obj = this.latestLoader.introspectorFromName(nodeType);
        Set<Vertex> seen = new HashSet<>();
        int depth = AAIProperties.MAXIMUM_DEPTH;
        String cleanUp = "false";
        boolean nodeOnly = false;
        StopWatch.conditionalStart();
        this.dbToObject(obj, v, seen, depth, nodeOnly, cleanUp);
        dbTimeMsecs += StopWatch.stopIfStarted();
        return obj;
    }

    /**
     * Copy simple property.
     *
     * @param property the property
     * @param obj the obj
     * @param v the v
     * @throws InstantiationException the instantiation exception
     * @throws IllegalAccessException the illegal access exception
     * @throws IllegalArgumentException the illegal argument exception
     * @throws InvocationTargetException the invocation target exception
     * @throws NoSuchMethodException the no such method exception
     * @throws SecurityException the security exception
     */
    private void copySimpleProperty(String property, Introspector obj, Vertex v) {
        final Object temp = getProperty(obj, property, v);
        if (temp != null) {
            obj.setValue(property, temp);
        }
    }

    /**
     * Load the introspector from the hashmap for the given property key
     *
     * @param property - vertex property
     * @param obj - introspector object representing the vertex
     * @param hashMap - Containing a list of pre-fetched properties for a given vertex
     */
    private void copySimplePropertyFromHashMap(String property, Introspector obj, Map<String, Object> hashMap) {

        final Map<PropertyMetadata, String> metadata = obj.getPropertyMetadata(property);
        String dbPropertyName = property;

        if (metadata.containsKey(PropertyMetadata.DB_ALIAS)) {
            dbPropertyName = metadata.get(PropertyMetadata.DB_ALIAS);
        }

        final Object temp = hashMap.getOrDefault(dbPropertyName, null);

        if (temp != null) {
            obj.setValue(property, temp);
        }
    }

    /**
     * Simple db to object.
     *
     * @param obj the obj
     * @param v the v
     * @throws InstantiationException the instantiation exception
     * @throws IllegalAccessException the illegal access exception
     * @throws IllegalArgumentException the illegal argument exception
     * @throws InvocationTargetException the invocation target exception
     * @throws NoSuchMethodException the no such method exception
     * @throws SecurityException the security exception
     */
    private void simpleDbToObject(Introspector obj, Vertex v) {
        for (String key : obj.getProperties()) {
            this.copySimpleProperty(key, obj, v);
        }
    }

    public Map<String, Object> convertVertexToHashMap(Introspector obj, Vertex v) {

        long startTime = System.currentTimeMillis();

        Set<String> simpleProperties = obj.getSimpleProperties(PropertyPredicates.isVisible());
        String[] simplePropsArray = new String[simpleProperties.size()];
        simplePropsArray = simpleProperties.toArray(simplePropsArray);

        Map<String, Object> simplePropsHashMap = new HashMap<>(simplePropsArray.length * 2);

        v.properties(simplePropsArray).forEachRemaining((vp) -> simplePropsHashMap.put(vp.key(), vp.value()));

        return simplePropsHashMap;
    }

    public Introspector dbToRelationshipObject(Vertex v) throws UnsupportedEncodingException, AAIException {
        Introspector relationshipList = this.latestLoader.introspectorFromName("relationship-list");
        relationshipList = createRelationshipList(v, relationshipList, "false");
        return relationshipList;
    }

    /**
     * Creates the relationship list.
     *
     * @param v the v
     * @param obj the obj
     * @param cleanUp the clean up
     * @return the object
     * @throws InstantiationException the instantiation exception
     * @throws IllegalAccessException the illegal access exception
     * @throws IllegalArgumentException the illegal argument exception
     * @throws InvocationTargetException the invocation target exception
     * @throws NoSuchMethodException the no such method exception
     * @throws SecurityException the security exception
     * @throws UnsupportedEncodingException the unsupported encoding exception
     * @throws AAIException the AAI exception
     * @throws MalformedURLException the malformed URL exception
     * @throws URISyntaxException
     */
    private Introspector createRelationshipList(Vertex v, Introspector obj, String cleanUp)
            throws UnsupportedEncodingException, AAIException {

        String[] cousinRules = new String[0];

        try {
            cousinRules = edgeRules.retrieveCachedCousinLabels(obj.getDbName());
        } catch (ExecutionException e) {
            LOGGER.warn("Encountered an execution exception while retrieving labels for the node type {} using cached",
                    obj.getDbName(), e);
        }

        List<Vertex> cousins = null;
        if (cousinRules != null && cousinRules.length != 0) {
            cousins = this.engine.getQueryEngine().findCousinVertices(v, cousinRules);
        } else {
            cousins = this.engine.getQueryEngine().findCousinVertices(v);
        }

        List<Object> relationshipObjList = obj.getValue("relationship");
        VertexProperty nodeTypeProperty = v.property(AAIProperties.NODE_TYPE);

        if (!nodeTypeProperty.isPresent()) {
            LoggingContext.responseDescription(MISSING_REQUIRED_NODE_PROPERTY);
            LOGGER.warn("Not processing the vertex {} because its missing required property aai-node-type", v.id());
            LoggingContext.remove(LoggingContext.LoggingField.RESPONSE_DESCRIPTION.toString());
            return null;
        }

        String aNodeType = nodeTypeProperty.value().toString();

        TypeAlphabetizer alphabetizer = new TypeAlphabetizer();

        EdgeIngestor edgeIngestor = SpringContextAware.getBean(EdgeIngestor.class);
        Set<String> keysWithMultipleLabels = edgeIngestor.getMultipleLabelKeys();

        // For the given vertex, find all the cousins
        // For each cousin retrieve the node type and then
        // check if the version is greater than the edge label version
        // meaning is the current version equal to greater than the version
        // where we introduced the edge labels into the relationship payload
        // If it is, then we check if the edge key there are multiple labels
        // If there are multiple labels, then we need to go to the database
        // to retrieve the labels between itself and cousin vertex
        // If there is only single label between the edge a and b, then
        // we can retrieve what that is without going to the database
        // from using the edge rules json and get the edge rule out of it
        EdgeRuleQuery.Builder queryBuilder = new EdgeRuleQuery.Builder(aNodeType);
        for (Vertex cousin : cousins) {
            VertexProperty vertexProperty = cousin.property(AAIProperties.NODE_TYPE);
            String bNodeType = null;
            if (vertexProperty.isPresent()) {
                bNodeType = cousin.property(AAIProperties.NODE_TYPE).value().toString();
            } else {
                // If the vertex is missing the aai-node-type
                // Then its either a bad vertex or its in the process
                // of getting deleted so we should ignore these vertexes
                LoggingContext.responseDescription(MISSING_REQUIRED_NODE_PROPERTY);
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("For the vertex {}, unable to retrieve the aai-node-type", v.id().toString());
                } else {
                    LOGGER.info("Unable to retrieve the aai-node-type for vertex, for more info enable debug log");
                }
                LoggingContext.remove(LoggingContext.LoggingField.RESPONSE_DESCRIPTION.toString());
                continue;
            }
            if (obj.getVersion().compareTo(schemaVersions.getEdgeLabelVersion()) >= 0) {
                String edgeKey = alphabetizer.buildAlphabetizedKey(aNodeType, bNodeType);
                if (keysWithMultipleLabels.contains(edgeKey)) {
                    List<String> edgeLabels = this.getEdgeLabelsBetween(EdgeType.COUSIN, v, cousin);
                    for (String edgeLabel : edgeLabels) {
                        Introspector relationshipObj = obj.newIntrospectorInstanceOfNestedProperty("relationship");
                        Object result = processEdgeRelationship(relationshipObj, cousin, cleanUp, edgeLabel);
                        if (result != null) {
                            relationshipObjList.add(result);
                        }
                    }
                } else {

                    EdgeRule edgeRule = null;

                    // Create a query based on the a nodetype and b nodetype
                    // which is also a cousin edge and ensure the version
                    // is used properly so for example in order to be backwards
                    // compatible if we had allowed a edge between a and b
                    // in a previous release and we decided to remove it from
                    // the edge rules in the future we can display the edge
                    // only for the older apis and the new apis if the edge rule
                    // is removed will not be seen in the newer version of the API

                    EdgeRuleQuery ruleQuery =
                            queryBuilder.to(bNodeType).edgeType(EdgeType.COUSIN).version(obj.getVersion()).build();

                    try {
                        edgeRule = edgeIngestor.getRule(ruleQuery);
                    } catch (EdgeRuleNotFoundException e) {
                        LOGGER.warn(
                                "Caught an edge rule not found exception for query {}, {},"
                                        + " it could be the edge rule is no longer valid for the existing edge in db",
                                ruleQuery, LogFormatTools.getStackTop(e));
                        continue;
                    } catch (AmbiguousRuleChoiceException e) {
                        LOGGER.error("Caught an ambiguous rule not found exception for query {}, {}", ruleQuery,
                                LogFormatTools.getStackTop(e));
                        continue;
                    }

                    Introspector relationshipObj = obj.newIntrospectorInstanceOfNestedProperty("relationship");
                    Object result = processEdgeRelationship(relationshipObj, cousin, cleanUp, edgeRule.getLabel());
                    if (result != null) {
                        relationshipObjList.add(result);
                    }
                }
            } else {
                Introspector relationshipObj = obj.newIntrospectorInstanceOfNestedProperty("relationship");
                Object result = processEdgeRelationship(relationshipObj, cousin, cleanUp, null);
                if (result != null) {
                    relationshipObjList.add(result);
                }
            }

        }

        if (relationshipObjList.isEmpty()) {
            return null;
        } else {
            return obj;
        }
    }

    /**
     * Process edge relationship.
     *
     * @param relationshipObj the relationship obj
     * @param edge the edge
     * @param cleanUp the clean up
     * @return the object
     * @throws InstantiationException the instantiation exception
     * @throws IllegalAccessException the illegal access exception
     * @throws IllegalArgumentException the illegal argument exception
     * @throws InvocationTargetException the invocation target exception
     * @throws NoSuchMethodException the no such method exception
     * @throws SecurityException the security exception
     * @throws UnsupportedEncodingException the unsupported encoding exception
     * @throws AAIException the AAI exception
     * @throws MalformedURLException the malformed URL exception
     * @throws AAIUnknownObjectException
     * @throws URISyntaxException
     */
    private Object processEdgeRelationship(Introspector relationshipObj, Vertex cousin, String cleanUp,
            String edgeLabel) throws UnsupportedEncodingException, AAIUnknownObjectException {

        VertexProperty aaiUriProperty = cousin.property("aai-uri");

        if (!aaiUriProperty.isPresent()) {
            return null;
        }

        URI uri = UriBuilder.fromUri(aaiUriProperty.value().toString()).build();

        URIToRelationshipObject uriParser = null;
        Introspector result = null;
        try {
            uriParser = new URIToRelationshipObject(relationshipObj.getLoader(), uri, this.baseURL);
            result = uriParser.getResult();
        } catch (AAIException | URISyntaxException e) {
            LOGGER.error("Error while processing edge relationship in version " + relationshipObj.getVersion()
                    + " (bad vertex ID=" + ": " + e.getMessage() + " " + LogFormatTools.getStackTop(e));
            return null;
        }

        VertexProperty cousinVertexNodeType = cousin.property(AAIProperties.NODE_TYPE);

        if (cousinVertexNodeType.isPresent()) {
            String cousinType = cousinVertexNodeType.value().toString();
            if (namedPropNodes.contains(cousinType)) {
                this.addRelatedToProperty(result, cousin, cousinType);
            }
        }

        if (edgeLabel != null && result.hasProperty("relationship-label")) {
            result.setValue("relationship-label", edgeLabel);
        }

        return result.getUnderlyingObject();
    }

    /**
     * Gets the URI for vertex.
     *
     * @param v the v
     * @return the URI for vertex
     * @throws InstantiationException the instantiation exception
     * @throws IllegalAccessException the illegal access exception
     * @throws IllegalArgumentException the illegal argument exception
     * @throws InvocationTargetException the invocation target exception
     * @throws NoSuchMethodException the no such method exception
     * @throws SecurityException the security exception
     * @throws UnsupportedEncodingException the unsupported encoding exception
     * @throws AAIUnknownObjectException
     */
    public URI getURIForVertex(Vertex v) throws UnsupportedEncodingException {

        return getURIForVertex(v, false);
    }

    public URI getURIForVertex(Vertex v, boolean overwrite) throws UnsupportedEncodingException {
        URI uri = UriBuilder.fromPath("/unknown-uri").build();

        String aaiUri = v.<String>property(AAIProperties.AAI_URI).orElse(null);

        if (aaiUri != null && !overwrite) {
            uri = UriBuilder.fromPath(aaiUri).build();
        }

        return uri;
    }

    /**
     * Gets the URI from list.
     *
     * @param list the list
     * @return the URI from list
     * @throws UnsupportedEncodingException the unsupported encoding exception
     */
    private URI getURIFromList(List<Introspector> list) throws UnsupportedEncodingException {
        String uri = "";
        StringBuilder sb = new StringBuilder();
        for (Introspector i : list) {
            sb.insert(0, i.getURI());
        }

        uri = sb.toString();
        return UriBuilder.fromPath(uri).build();
    }

    public void addRelatedToProperty(Introspector relationship, Vertex cousinVertex, String cousinType)
            throws AAIUnknownObjectException {

        Introspector obj = null;

        try {
            obj = this.loader.introspectorFromName(cousinType);
        } catch (AAIUnknownObjectException ex) {
            if (LOGGER.isTraceEnabled()) {
                LOGGER.trace("Encountered unknown object exception when trying to load nodetype of {} for vertex id {}",
                        cousinType, cousinVertex.id());
            }
            return;
        }

        String nameProps = obj.getMetadata(ObjectMetadata.NAME_PROPS);
        List<Introspector> relatedToProperties = new ArrayList<>();

        if (nameProps != null) {
            String[] props = nameProps.split(",");
            for (String prop : props) {
                final Object temp = getProperty(obj, prop, cousinVertex);
                Introspector relatedTo = relationship.newIntrospectorInstanceOfNestedProperty("related-to-property");
                relatedTo.setValue("property-key", cousinType + "." + prop);
                relatedTo.setValue("property-value", temp);
                relatedToProperties.add(relatedTo);
            }
        }

        if (!relatedToProperties.isEmpty()) {
            List relatedToList = (List) relationship.getValue("related-to-property");
            for (Introspector introspector : relatedToProperties) {
                relatedToList.add(introspector.getUnderlyingObject());
            }
        }

    }

    private Object getProperty(Introspector obj, String prop, Vertex vertex) {

        final Map<PropertyMetadata, String> metadata = obj.getPropertyMetadata(prop);
        String dbPropertyName = prop;

        if (metadata.containsKey(PropertyMetadata.DB_ALIAS)) {
            dbPropertyName = metadata.get(PropertyMetadata.DB_ALIAS);
        }

        return vertex.<Object>property(dbPropertyName).orElse(null);
    }

    /**
     * Creates the edge.
     *
     * @param relationship the relationship
     * @param inputVertex the input vertex
     * @return true, if successful
     * @throws UnsupportedEncodingException the unsupported encoding exception
     * @throws AAIException the AAI exception
     */
    public boolean createEdge(Introspector relationship, Vertex inputVertex)
            throws UnsupportedEncodingException, AAIException {

        Vertex relatedVertex = null;
        StopWatch.conditionalStart();
        QueryParser parser = engine.getQueryBuilder().createQueryFromRelationship(relationship);

        String label = null;
        if (relationship.hasProperty("relationship-label")) {
            label = relationship.getValue("relationship-label");
        }

        List<Vertex> results = parser.getQueryBuilder().toList();
        if (results.isEmpty()) {
            dbTimeMsecs += StopWatch.stopIfStarted();
            AAIException e = new AAIException("AAI_6129",
                    "Node of type " + parser.getResultType() + ". Could not find object at: " + parser.getUri());
            e.getTemplateVars().add(parser.getResultType());
            e.getTemplateVars().add(parser.getUri().toString());
            throw e;
        } else {
            // still an issue if there's more than one
            relatedVertex = results.get(0);
        }

        if (relatedVertex != null) {

            Edge e;
            try {
                e = this.getEdgeBetween(EdgeType.COUSIN, inputVertex, relatedVertex, label);
                if (e == null) {
                    edgeSer.addEdge(this.engine.asAdmin().getTraversalSource(), inputVertex, relatedVertex, label);
                } else {
                    // attempted to link two vertexes already linked
                }
            } finally {
                dbTimeMsecs += StopWatch.stopIfStarted();
            }
        }

        dbTimeMsecs += StopWatch.stopIfStarted();
        return true;
    }

    /**
     * Gets all the edges between of the type with the specified label.
     *
     * @param aVertex the out vertex
     * @param bVertex the in vertex
     * @return the edges between
     * @throws AAIException the AAI exception
     * @throws NoEdgeRuleFoundException
     */
    private Edge getEdgeBetweenWithLabel(EdgeType type, Vertex aVertex, Vertex bVertex, EdgeRule edgeRule) {

        Edge result = null;

        if (bVertex != null) {
            GraphTraversal<Vertex, Edge> findEdgesBetween = null;
            if (EdgeType.TREE.equals(type)) {
                GraphTraversal<Vertex, Vertex> findVertex = this.engine.asAdmin().getTraversalSource().V(bVertex);
                if (edgeRule.getDirection().equals(Direction.IN)) {
                    findEdgesBetween = findVertex.outE(edgeRule.getLabel())
                            .has(EdgeProperty.CONTAINS.toString(), edgeRule.getContains())
                            .not(__.has(EdgeField.PRIVATE.toString(), true));
                } else {
                    findEdgesBetween = findVertex.inE(edgeRule.getLabel())
                            .has(EdgeProperty.CONTAINS.toString(), edgeRule.getContains())
                            .not(__.has(EdgeField.PRIVATE.toString(), true));
                }
                findEdgesBetween = findEdgesBetween.filter(__.otherV().hasId(aVertex.id())).limit(1);
            } else {
                findEdgesBetween = this.engine.asAdmin().getTraversalSource().V(aVertex).bothE(edgeRule.getLabel());
                findEdgesBetween = findEdgesBetween.has(EdgeProperty.CONTAINS.toString(), "NONE")
                        .not(__.has(EdgeField.PRIVATE.toString(), true));
                findEdgesBetween = findEdgesBetween.filter(__.otherV().hasId(bVertex.id())).limit(1);
            }
            List<Edge> list = findEdgesBetween.toList();
            if (!list.isEmpty()) {
                result = list.get(0);
            }
        }

        return result;
    }

    /**
     * Gets all the edges between of the type.
     *
     * @param aVertex the out vertex
     * @param bVertex the in vertex
     * @return the edges between
     * @throws AAIException the AAI exception
     * @throws NoEdgeRuleFoundException
     */
    private List<Edge> getEdgesBetween(EdgeType type, Vertex aVertex, Vertex bVertex) {

        List<Edge> result = new ArrayList<>();

        if (bVertex != null) {
            GraphTraversal<Vertex, Edge> findEdgesBetween = null;
            findEdgesBetween = this.engine.asAdmin().getTraversalSource().V(aVertex).bothE();
            if (EdgeType.TREE.equals(type)) {
                findEdgesBetween = findEdgesBetween.not(__.or(__.has(EdgeProperty.CONTAINS.toString(), "NONE"),
                        __.has(EdgeField.PRIVATE.toString(), true)));
            } else {
                findEdgesBetween = findEdgesBetween.has(EdgeProperty.CONTAINS.toString(), "NONE")
                        .not(__.has(EdgeField.PRIVATE.toString(), true));
            }
            findEdgesBetween = findEdgesBetween.filter(__.otherV().hasId(bVertex.id()));
            result = findEdgesBetween.toList();
        }

        return result;
    }

    /**
     * Gets all the edges string between of the type.
     *
     * @param aVertex the out vertex
     * @param bVertex the in vertex
     * @return the edges between
     * @throws AAIException the AAI exception
     * @throws NoEdgeRuleFoundException
     */
    private List<String> getEdgeLabelsBetween(EdgeType type, Vertex aVertex, Vertex bVertex) {

        List<String> result = new ArrayList<>();

        if (bVertex != null) {
            GraphTraversal<Vertex, Edge> findEdgesBetween = null;
            findEdgesBetween = this.engine.asAdmin().getTraversalSource().V(aVertex).bothE();
            if (EdgeType.TREE.equals(type)) {
                findEdgesBetween = findEdgesBetween.not(__.or(__.has(EdgeProperty.CONTAINS.toString(), "NONE"),
                        __.has(EdgeField.PRIVATE.toString(), true)));
            } else {
                findEdgesBetween = findEdgesBetween.has(EdgeProperty.CONTAINS.toString(), "NONE")
                        .not(__.has(EdgeField.PRIVATE.toString(), true));
            }
            findEdgesBetween = findEdgesBetween.filter(__.otherV().hasId(bVertex.id()));
            result = findEdgesBetween.label().toList();
        }
        return result;
    }

    /**
     * Gets all the edges string between of the type.
     *
     * @param aVertex the out vertex
     * @param bVertex the in vertex
     * @return the edges between
     * @throws AAIException the AAI exception
     * @throws NoEdgeRuleFoundException
     */
    private Long getEdgeLabelsCount(Vertex aVertex, Vertex bVertex) {

        Long result = null;

        if (bVertex != null) {
            GraphTraversal<Vertex, Edge> findEdgesBetween = null;
            findEdgesBetween = this.engine.asAdmin().getTraversalSource().V(aVertex).bothE();
            findEdgesBetween = findEdgesBetween.has(EdgeProperty.CONTAINS.toString(), "NONE")
                    .not(__.has(EdgeField.PRIVATE.toString(), true));
            findEdgesBetween = findEdgesBetween.filter(__.otherV().hasId(bVertex.id()));
            result = findEdgesBetween.count().next();
        }
        return result;
    }

    /**
     * Gets all the edges between the vertexes with the label and type.
     *
     * @param aVertex the out vertex
     * @param bVertex the in vertex
     * @param label
     * @return the edges between
     * @throws AAIException the AAI exception
     */
    private Edge getEdgesBetween(EdgeType type, Vertex aVertex, Vertex bVertex, String label) throws AAIException {

        Edge edge = null;

        if (bVertex != null) {
            String aType = aVertex.<String>property(AAIProperties.NODE_TYPE).value();
            String bType = bVertex.<String>property(AAIProperties.NODE_TYPE).value();
            EdgeRuleQuery q = new EdgeRuleQuery.Builder(aType, bType).edgeType(type).label(label).build();
            EdgeRule rule;
            try {
                rule = edgeRules.getRule(q);
            } catch (EdgeRuleNotFoundException e) {
                throw new NoEdgeRuleFoundException(e);
            } catch (AmbiguousRuleChoiceException e) {
                throw new MultipleEdgeRuleFoundException(e);
            }
            edge = this.getEdgeBetweenWithLabel(type, aVertex, bVertex, rule);
        }

        return edge;
    }

    /**
     * Gets the edge between with the label and edge type.
     *
     * @param aVertex the out vertex
     * @param bVertex the in vertex
     * @param label
     * @return the edge between
     * @throws AAIException the AAI exception
     * @throws NoEdgeRuleFoundException
     */
    public Edge getEdgeBetween(EdgeType type, Vertex aVertex, Vertex bVertex, String label) throws AAIException {

        StopWatch.conditionalStart();
        if (bVertex != null) {

            Edge edge = this.getEdgesBetween(type, aVertex, bVertex, label);
            if (edge != null) {
                dbTimeMsecs += StopWatch.stopIfStarted();
                return edge;
            }

        }
        dbTimeMsecs += StopWatch.stopIfStarted();
        return null;
    }

    public Edge getEdgeBetween(EdgeType type, Vertex aVertex, Vertex bVertex) throws AAIException {
        return this.getEdgeBetween(type, aVertex, bVertex, null);
    }

    /**
     * Delete edge.
     *
     * @param relationship the relationship
     * @param inputVertex the input vertex
     * @return true, if successful
     * @throws UnsupportedEncodingException the unsupported encoding exception
     * @throws AAIException the AAI exception
     */
    public boolean deleteEdge(Introspector relationship, Vertex inputVertex)
            throws UnsupportedEncodingException, AAIException {

        Vertex relatedVertex = null;
        StopWatch.conditionalStart();
        QueryParser parser = engine.getQueryBuilder().createQueryFromRelationship(relationship);

        List<Vertex> results = parser.getQueryBuilder().toList();

        String label = null;
        if (relationship.hasProperty("relationship-label")) {
            label = relationship.getValue("relationship-label");
        }

        if (results.isEmpty()) {
            dbTimeMsecs += StopWatch.stopIfStarted();
            return false;
        }

        relatedVertex = results.get(0);
        Edge edge;
        try {
            edge = this.getEdgeBetween(EdgeType.COUSIN, inputVertex, relatedVertex, label);
        } catch (NoEdgeRuleFoundException e) {
            dbTimeMsecs += StopWatch.stopIfStarted();
            throw new AAIException("AAI_6129", e);
        }
        if (edge != null) {
            edge.remove();
            dbTimeMsecs += StopWatch.stopIfStarted();
            return true;
        } else {
            dbTimeMsecs += StopWatch.stopIfStarted();
            return false;
        }

    }

    /**
     * Delete items with traversal.
     *
     * @param vertexes the vertexes
     * @throws IllegalStateException the illegal state exception
     */
    public void deleteItemsWithTraversal(List<Vertex> vertexes) throws IllegalStateException {

        for (Vertex v : vertexes) {
            deleteWithTraversal(v);
        }

    }

    /**
     * Delete with traversal.
     *
     * @param startVertex the start vertex
     */
    public void deleteWithTraversal(Vertex startVertex) {
        StopWatch.conditionalStart();
        List<Vertex> results = this.engine.getQueryEngine().findDeletable(startVertex);

        for (Vertex v : results) {
            LOGGER.debug("Removing vertex {} with label {}", v.id(), v.label());
            v.remove();
        }
        dbTimeMsecs += StopWatch.stopIfStarted();
    }

    /**
     * Removes the list of vertexes from the graph
     * <p>
     * Current the vertex label will just be vertex but
     * in the future the aai-node-type property will be replaced
     * by using the vertex label as when retrieving an vertex
     * and retrieving an single property on an vertex will pre-fetch
     * all the properties of that vertex and this is due to the following property
     * <p>
     * <code>
     * query.fast-property=true
     * </code>
     * <p>
     * JanusGraph doesn't provide the capability to override that
     * at a transaction level and there is a plan to move to vertex label
     * so it is best to utilize this for now and when the change is applied
     *
     * @param vertices - list of vertices to delete from the graph
     */
    void delete(List<Vertex> vertices) {
        StopWatch.conditionalStart();

        for (Vertex v : vertices) {
            LOGGER.debug("Removing vertex {} with label {}", v.id(), v.label());
            v.remove();
        }

        dbTimeMsecs += StopWatch.stopIfStarted();
    }

    /**
     * Delete.
     *
     * @param v the v
     * @param resourceVersion the resource version
     * @throws IllegalArgumentException the illegal argument exception
     * @throws AAIException the AAI exception
     * @throws InterruptedException the interrupted exception
     */
    public void delete(Vertex v, List<Vertex> deletableVertices, String resourceVersion, boolean enableResourceVersion)
            throws IllegalArgumentException, AAIException {

        boolean result = verifyDeleteSemantics(v, resourceVersion, enableResourceVersion);
        /*
         * The reason why I want to call PreventDeleteSemantics second time is to catch the prevent-deletes in a chain
         * These are far-fewer than seeing a prevnt-delete on the vertex to be deleted
         * So its better to make these in 2 steps
         */
        if (result && !deletableVertices.isEmpty()) {
            result = verifyPreventDeleteSemantics(deletableVertices);
        }
        if (result) {

            try {
                deleteWithTraversal(v);
            } catch (IllegalStateException e) {
                throw new AAIException("AAI_6110", e);
            }

        }

    }

    /**
     * Delete.
     *
     * @param v the v
     * @param resourceVersion the resource version
     * @throws IllegalArgumentException the illegal argument exception
     * @throws AAIException the AAI exception
     * @throws InterruptedException the interrupted exception
     */
    public void delete(Vertex v, String resourceVersion, boolean enableResourceVersion)
            throws AAIException {

        boolean result = verifyDeleteSemantics(v, resourceVersion, enableResourceVersion);

        if (result) {

            try {
                deleteWithTraversal(v);
            } catch (IllegalStateException e) {
                throw new AAIException("AAI_6110", e);
            }

        }

    }

    /**
     * Verify delete semantics.
     *
     * @param vertex the vertex
     * @param resourceVersion the resource version
     * @return true, if successful
     * @throws AAIException the AAI exception
     */
    private boolean verifyDeleteSemantics(Vertex vertex, String resourceVersion, boolean enableResourceVersion)
            throws AAIException {
        boolean result = true;
        String nodeType = "";
        String errorDetail = " unknown delete semantic found";
        String aaiExceptionCode = "";
        nodeType = vertex.<String>property(AAIProperties.NODE_TYPE).orElse(null);
        if (enableResourceVersion && !this.verifyResourceVersion("delete", nodeType,
                vertex.<String>property(AAIProperties.RESOURCE_VERSION).orElse(null), resourceVersion, nodeType)) {
        }
        List<Vertex> vertices = new ArrayList<Vertex>();
        vertices.add(vertex);
        result = verifyPreventDeleteSemantics(vertices);

        return result;
    }

    /**
     * Verify Prevent delete semantics.
     *
     * @param vertices the list of vertices
     * @return true, if successful
     * @throws AAIException the AAI exception
     */
    private boolean verifyPreventDeleteSemantics(List<Vertex> vertices) throws AAIException {
        boolean result = true;
        String nodeType = "";
        String errorDetail = " unknown delete semantic found";
        String aaiExceptionCode = "";

        StopWatch.conditionalStart();
        /*
         * This takes in all the vertices in a cascade-delete-chain and checks if there is any edge with a
         * "prevent-delete" condition
         * If yes - that should prevent the deletion of the vertex
         * Dedup makes sure we dont capture the prevent-delete vertices twice
         * The prevent-delete vertices are stored so that the error message displays what prevents the delete
         */

        List<Object> preventDeleteVertices = this.engine.asAdmin().getReadOnlyTraversalSource().V(vertices)
                .union(__.inE().has(EdgeProperty.PREVENT_DELETE.toString(), AAIDirection.IN.toString()).outV()
                        .values(AAIProperties.NODE_TYPE),
                        __.outE().has(EdgeProperty.PREVENT_DELETE.toString(), AAIDirection.OUT.toString()).inV()
                                .values(AAIProperties.NODE_TYPE))
                .dedup().toList();

        dbTimeMsecs += StopWatch.stopIfStarted();
        if (!preventDeleteVertices.isEmpty()) {
            aaiExceptionCode = "AAI_6110";
            errorDetail = String.format(
                    "Object is being reference by additional objects preventing it from being deleted. Please clean up references from the following types %s",
                    preventDeleteVertices);
            result = false;
        }
        if (!result) {
            throw new AAIException(aaiExceptionCode, errorDetail);
        }
        return result;
    }

    /**
     * Verify resource version.
     *
     * @param action the action
     * @param nodeType the node type
     * @param currentResourceVersion the current resource version
     * @param resourceVersion the resource version
     * @param uri the uri
     * @return true, if successful
     * @throws AAIException the AAI exception
     */
    public boolean verifyResourceVersion(String action, String nodeType, String currentResourceVersion,
            String resourceVersion, String uri) throws AAIException {
        String enabled = "";
        String errorDetail = "";
        String aaiExceptionCode = "";
        boolean isDeleteResourceVersionOk = true;
        if (currentResourceVersion == null) {
            currentResourceVersion = "";
        }

        if (resourceVersion == null) {
            resourceVersion = "";
        }
        try {
            enabled = AAIConfig.get(AAIConstants.AAI_RESVERSION_ENABLEFLAG);

        } catch (AAIException e) {
            ErrorLogHelper.logException(e);
        }
        if (enabled.equals("true")) {
            if ("delete".equals(action)) {
                isDeleteResourceVersionOk = verifyResourceVersionForDelete(currentResourceVersion, resourceVersion);
            }
            if ((!isDeleteResourceVersionOk)
                    || ((!"delete".equals(action)) && (!currentResourceVersion.equals(resourceVersion)))) {
                if ("create".equals(action) && !resourceVersion.equals("")) {
                    errorDetail = "resource-version passed for " + action + " of " + uri;
                    aaiExceptionCode = "AAI_6135";
                } else if (resourceVersion.equals("")) {
                    errorDetail = "resource-version not passed for " + action + " of " + uri;
                    aaiExceptionCode = "AAI_6130";
                } else {
                    errorDetail = "resource-version MISMATCH for " + action + " of " + uri;
                    aaiExceptionCode = "AAI_6131";
                }

                throw new AAIException(aaiExceptionCode, errorDetail);

            }
        }
        return true;
    }

    /**
     * Verify resource version for delete.
     *
     * @param currentResourceVersion the current resource version
     * @param resourceVersion the resource version
     * @return true, if successful or false if there is a mismatch
     */
    private boolean verifyResourceVersionForDelete(String currentResourceVersion, String resourceVersion) {

        boolean isDeleteResourceVersionOk = true;
        String resourceVersionDisabledUuid = AAIConfig.get(AAIConstants.AAI_RESVERSION_DISABLED_UUID,
                AAIConstants.AAI_RESVERSION_DISABLED_UUID_DEFAULT);

        if ((!currentResourceVersion.equals(resourceVersion))
                && (!resourceVersion.equals(resourceVersionDisabledUuid))) {
            isDeleteResourceVersionOk = false;
        }
        return isDeleteResourceVersionOk;
    }

    /**
     * Convert from camel case.
     *
     * @param name the name
     * @return the string
     */
    private String convertFromCamelCase(String name) {
        String result = "";
        result = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_HYPHEN, name);

        NamingExceptions exceptions = NamingExceptions.getInstance();
        result = exceptions.getDBName(result);

        return result;
    }

    private boolean canModify(Introspector obj, String propName, String requestContext) {
        final String readOnly = obj.getPropertyMetadata(propName).get(PropertyMetadata.READ_ONLY);
        if (readOnly != null) {
            final String[] items = readOnly.split(",");
            for (String item : items) {
                if (requestContext.equals(item)) {
                    return false;
                }
            }
        }
        return true;
    }

    private void executePreSideEffects(Introspector obj, Vertex self) throws AAIException {

        SideEffectRunner runner = new SideEffectRunner.Builder(this.engine, this).addSideEffect(DataCopy.class)
                .addSideEffect(PrivateEdge.class).build();

        runner.execute(obj, self);
    }

    private void executePostSideEffects(Introspector obj, Vertex self) throws AAIException {

        SideEffectRunner runner =
                new SideEffectRunner.Builder(this.engine, this).addSideEffect(DataLinkWriter.class).build();

        runner.execute(obj, self);
    }

    private void enrichData(Introspector obj, Vertex self) throws AAIException {

        SideEffectRunner runner =
                new SideEffectRunner.Builder(this.engine, this).addSideEffect(DataLinkReader.class).build();

        runner.execute(obj, self);
    }

    public double getDBTimeMsecs() {
        return (dbTimeMsecs);
    }

    /**
     * Db to object With Filters
     * This is for a one-time run with Tenant Isloation to only filter relationships
     * TODO: Chnage the original dbToObject to take filter parent/cousins
     *
     * @param obj the obj
     * @param v the vertex from the graph
     * @param depth the depth
     * @param nodeOnly specify if to exclude relationships or not
     * @param filterCousinNodes
     * @return the introspector
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
    // TODO - See if you can merge the 2 dbToObjectWithFilters
    public Introspector dbToObjectWithFilters(Introspector obj, Vertex v, Set<Vertex> seen, int depth, boolean nodeOnly,
            List<String> filterCousinNodes, List<String> filterParentNodes)
            throws AAIException, UnsupportedEncodingException {
        String cleanUp = "false";
        if (depth < 0) {
            return null;
        }
        depth--;
        seen.add(v);
        boolean modified = false;
        for (String property : obj.getProperties(PropertyPredicates.isVisible())) {
            List<Object> getList = null;
            Vertex[] vertices = null;

            if (!(obj.isComplexType(property) || obj.isListType(property))) {
                this.copySimpleProperty(property, obj, v);
                modified = true;
            } else {
                if (obj.isComplexType(property)) {
                    /* container case */

                    if (!property.equals("relationship-list") && depth >= 0) {
                        Introspector argumentObject = obj.newIntrospectorInstanceOfProperty(property);
                        Object result = dbToObjectWithFilters(argumentObject, v, seen, depth + 1, nodeOnly,
                                filterCousinNodes, filterParentNodes);
                        if (result != null) {
                            obj.setValue(property, argumentObject.getUnderlyingObject());
                            modified = true;
                        }
                    } else if (property.equals("relationship-list") && !nodeOnly) {
                        /* relationships need to be handled correctly */
                        Introspector relationshipList = obj.newIntrospectorInstanceOfProperty(property);
                        relationshipList =
                                createFilteredRelationshipList(v, relationshipList, cleanUp, filterCousinNodes);
                        if (relationshipList != null) {
                            modified = true;
                            obj.setValue(property, relationshipList.getUnderlyingObject());
                            modified = true;
                        }

                    }
                } else if (obj.isListType(property)) {

                    if (property.equals("any")) {
                        continue;
                    }
                    String genericType = obj.getGenericTypeClass(property).getSimpleName();
                    if (obj.isComplexGenericType(property) && depth >= 0) {
                        final String childDbName = convertFromCamelCase(genericType);
                        String vType = v.<String>property(AAIProperties.NODE_TYPE).orElse(null);
                        EdgeRule rule;

                        boolean isthisParentRequired =
                                filterParentNodes.parallelStream().anyMatch(childDbName::contains);

                        EdgeRuleQuery q = new EdgeRuleQuery.Builder(vType, childDbName).edgeType(EdgeType.TREE).build();

                        try {
                            rule = edgeRules.getRule(q);
                        } catch (EdgeRuleNotFoundException e) {
                            throw new NoEdgeRuleFoundException(e);
                        } catch (AmbiguousRuleChoiceException e) {
                            throw new MultipleEdgeRuleFoundException(e);
                        }
                        if (!rule.getContains().equals(AAIDirection.NONE.toString()) && isthisParentRequired) {
                            // vertices = this.queryEngine.findRelatedVertices(v, Direction.OUT, rule.getLabel(),
                            // childDbName);
                            Direction ruleDirection = rule.getDirection();
                            Iterator<Vertex> itr = v.vertices(ruleDirection, rule.getLabel());
                            List<Vertex> verticesList = (List<Vertex>) IteratorUtils.toList(itr);
                            itr = verticesList.stream().filter(item -> {
                                return item.property(AAIProperties.NODE_TYPE).orElse("").equals(childDbName);
                            }).iterator();
                            if (itr.hasNext()) {
                                getList = (List<Object>) obj.getValue(property);
                            }
                            int processed = 0;
                            int removed = 0;
                            while (itr.hasNext()) {
                                Vertex childVertex = itr.next();
                                if (!seen.contains(childVertex)) {
                                    Introspector argumentObject = obj.newIntrospectorInstanceOfNestedProperty(property);

                                    Object result = dbToObjectWithFilters(argumentObject, childVertex, seen, depth,
                                            nodeOnly, filterCousinNodes, filterParentNodes);
                                    if (result != null) {
                                        getList.add(argumentObject.getUnderlyingObject());
                                    }

                                    processed++;
                                } else {
                                    removed++;
                                    LOGGER.warn("Cycle found while serializing vertex id={}",
                                            childVertex.id().toString());
                                }
                            }
                            if (processed == 0) {
                                // vertices were all seen, reset the list
                                getList = null;
                            }
                            if (processed > 0) {
                                modified = true;
                            }
                        }
                    } else if (obj.isSimpleGenericType(property)) {
                        List<Object> temp = this.engine.getListProperty(v, property);
                        if (temp != null) {
                            getList = (List<Object>) obj.getValue(property);
                            getList.addAll(temp);
                            modified = true;
                        }

                    }

                }

            }
        }

        // no changes were made to this obj, discard the instance
        if (!modified) {
            return null;
        }
        this.enrichData(obj, v);
        return obj;

    }

    /**
     * Creates the relationship list with the filtered node types.
     *
     * @param v the v
     * @param obj the obj
     * @param cleanUp the clean up
     * @return the object
     * @throws InstantiationException the instantiation exception
     * @throws IllegalAccessException the illegal access exception
     * @throws IllegalArgumentException the illegal argument exception
     * @throws InvocationTargetException the invocation target exception
     * @throws NoSuchMethodException the no such method exception
     * @throws SecurityException the security exception
     * @throws UnsupportedEncodingException the unsupported encoding exception
     * @throws AAIException the AAI exception
     * @throws MalformedURLException the malformed URL exception
     * @throws URISyntaxException
     */
    private Introspector createFilteredRelationshipList(Vertex v, Introspector obj, String cleanUp,
            List<String> filterNodes) throws UnsupportedEncodingException, AAIException {
        List<Vertex> allCousins = this.engine.getQueryEngine().findCousinVertices(v);

        Iterator<Vertex> cousinVertices = allCousins.stream().filter(item -> {
            String node = (String) item.property(AAIProperties.NODE_TYPE).orElse("");
            return filterNodes.parallelStream().anyMatch(node::contains);
        }).iterator();

        List<Vertex> cousins = (List<Vertex>) IteratorUtils.toList(cousinVertices);

        // items.parallelStream().anyMatch(inputStr::contains)
        List<Object> relationshipObjList = obj.getValue("relationship");
        for (Vertex cousin : cousins) {

            Introspector relationshipObj = obj.newIntrospectorInstanceOfNestedProperty("relationship");
            Object result = processEdgeRelationship(relationshipObj, cousin, cleanUp, null);
            if (result != null) {
                relationshipObjList.add(result);
            }

        }

        if (relationshipObjList.isEmpty()) {
            return null;
        } else {
            return obj;
        }
    }

}
