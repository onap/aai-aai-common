/**
 * ============LICENSE_START=======================================================
 * org.onap.aai
 * ================================================================================
 * Copyright © 2017-2019 AT&T Intellectual Property. All rights reserved.
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

import com.google.common.base.CaseFormat;
import org.apache.commons.lang.StringUtils;
import org.apache.tinkerpop.gremlin.process.traversal.Path;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__;
import org.apache.tinkerpop.gremlin.structure.Direction;
import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.apache.tinkerpop.gremlin.structure.VertexProperty;
import org.janusgraph.core.SchemaViolationException;
import org.javatuples.Pair;
import org.onap.aai.concurrent.AaiCallable;
import org.onap.aai.config.SpringContextAware;
import org.onap.aai.db.props.AAIProperties;
import org.onap.aai.edges.EdgeIngestor;
import org.onap.aai.edges.EdgeRule;
import org.onap.aai.edges.EdgeRuleQuery;
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
import org.onap.aai.logging.StopWatch;
import org.onap.aai.parsers.query.QueryParser;
import org.onap.aai.parsers.relationship.RelationshipToURI;
import org.onap.aai.parsers.uri.URIParser;
import org.onap.aai.parsers.uri.URIToObject;
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
import org.onap.aai.util.delta.*;
import org.onap.aai.workarounds.NamingExceptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import javax.ws.rs.core.UriBuilder;
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

public class DBSerializer {

    private static final Logger LOGGER = LoggerFactory.getLogger(DBSerializer.class);
    private static final String RELATIONSHIP_LABEL = "relationship-label";
    private static final String RELATIONSHIP = "relationship";
    public static final String FALSE = "false";
    public static final String AAI_6145 = "AAI_6145";
    public static final String AAI_6129 = "AAI_6129";

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
    private Map<String, ObjectDelta> objectDeltas = new LinkedHashMap<>();
    private Map<Vertex, Boolean> updatedVertexes = new LinkedHashMap<>();
    private Set<Vertex> edgeVertexes = new LinkedHashSet<>();
    private Map<String, Pair<Introspector, LinkedHashMap<String, Introspector>>> impliedDeleteUriObjectPair = new LinkedHashMap<>();
    private int notificationDepth;
    private boolean isDeltaEventsEnabled;

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
        SchemaVersion latestVersion = schemaVersions.getDefaultVersion();
        this.latestLoader =
                SpringContextAware.getBean(LoaderFactory.class).createLoaderForVersion(introspectionType, latestVersion);
        this.version = version;
        this.loader =
                SpringContextAware.getBean(LoaderFactory.class).createLoaderForVersion(introspectionType, version);
        this.namedPropNodes = this.latestLoader.getNamedPropNodes();
        this.baseURL = AAIConfig.get(AAIConstants.AAI_SERVER_URL_BASE);
        this.currentTimeMillis = System.currentTimeMillis();
        // If creating the DBSerializer the old way then set the notification depth to maximum
        this.notificationDepth = AAIProperties.MAXIMUM_DEPTH;
        initBeans();
    }

    public DBSerializer(SchemaVersion version,
                        TransactionalGraphEngine engine,
                        ModelType introspectionType,
                        String sourceOfTruth,
                        int notificationDepth) throws AAIException {
        this.engine = engine;
        this.sourceOfTruth = sourceOfTruth;
        this.introspectionType = introspectionType;
        this.schemaVersions = (SchemaVersions) SpringContextAware.getBean("schemaVersions");
        SchemaVersion latestVersion = schemaVersions.getDefaultVersion();
        this.latestLoader =
            SpringContextAware.getBean(LoaderFactory.class).createLoaderForVersion(introspectionType, latestVersion);
        this.version = version;
        this.loader =
            SpringContextAware.getBean(LoaderFactory.class).createLoaderForVersion(introspectionType, version);
        this.namedPropNodes = this.latestLoader.getNamedPropNodes();
        this.baseURL = AAIConfig.get(AAIConstants.AAI_SERVER_URL_BASE);
        this.currentTimeMillis = System.currentTimeMillis();
        this.notificationDepth = notificationDepth;
        initBeans();
    }

    private void initBeans() {
        // TODO proper spring wiring, but that requires a lot of refactoring so for now we have this
        ApplicationContext ctx = SpringContextAware.getApplicationContext();
        EdgeIngestor ei = ctx.getBean(EdgeIngestor.class);
        setEdgeIngestor(ei);
        EdgeSerializer es = ctx.getBean(EdgeSerializer.class);
        setEdgeSerializer(es);
        isDeltaEventsEnabled = Boolean.parseBoolean(SpringContextAware.getApplicationContext().getEnvironment().getProperty("delta.events.enabled", FALSE));
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

    public Map<Vertex, Boolean> getUpdatedVertexes() {
        return updatedVertexes;
    }

    public Map<String, Pair<Introspector, LinkedHashMap<String, Introspector>>> getImpliedDeleteUriObjectPair(){
        return impliedDeleteUriObjectPair;
    }

    /**
     * Touch standard vertex properties.
     *  @param v the v
     * @param isNewVertex the is new vertex
     */
    public void touchStandardVertexProperties(Vertex v, boolean isNewVertex) {
        String timeNowInSec = Long.toString(currentTimeMillis);
        if (isNewVertex) {
            String uuid = UUID.randomUUID().toString();
            v.property(AAIProperties.SOURCE_OF_TRUTH, this.sourceOfTruth);
            v.property(AAIProperties.CREATED_TS, currentTimeMillis);
            v.property(AAIProperties.AAI_UUID, uuid);
            v.property(AAIProperties.RESOURCE_VERSION, timeNowInSec);
            v.property(AAIProperties.LAST_MOD_TS, currentTimeMillis);
            v.property(AAIProperties.LAST_MOD_SOURCE_OF_TRUTH, this.sourceOfTruth);
        } else {
            if(isDeltaEventsEnabled) {
                standardVertexPropsDeltas(v, timeNowInSec);
            }
            v.property(AAIProperties.RESOURCE_VERSION, timeNowInSec);
            v.property(AAIProperties.LAST_MOD_TS, currentTimeMillis);
            v.property(AAIProperties.LAST_MOD_SOURCE_OF_TRUTH, this.sourceOfTruth);
        }
    }

    private void standardVertexPropsDeltas(Vertex v, String timeNowInSec) {
        String uri = v.property(AAIProperties.AAI_URI).value().toString();
        long createdTs = (Long) v.property(AAIProperties.CREATED_TS).value();
        DeltaAction objDeltaAction = createdTs == currentTimeMillis ? DeltaAction.CREATE : DeltaAction.UPDATE;
        if (getObjectDeltas().containsKey(uri)) {
            getObjectDeltas().get(uri).setAction(objDeltaAction);
        }

        addPropDelta(uri, AAIProperties.AAI_UUID, PropertyDeltaFactory.getDelta(DeltaAction.STATIC, v.property(AAIProperties.AAI_UUID).value()), objDeltaAction);
        addPropDelta(uri, AAIProperties.NODE_TYPE, PropertyDeltaFactory.getDelta(DeltaAction.STATIC, v.property(AAIProperties.NODE_TYPE).value()), objDeltaAction);
        addPropDelta(uri, AAIProperties.SOURCE_OF_TRUTH, PropertyDeltaFactory.getDelta(DeltaAction.STATIC, v.property(AAIProperties.SOURCE_OF_TRUTH).value()), objDeltaAction);
        addPropDelta(uri, AAIProperties.CREATED_TS, PropertyDeltaFactory.getDelta(DeltaAction.STATIC, v.property(AAIProperties.CREATED_TS).value()), objDeltaAction);

        if (objDeltaAction.equals(DeltaAction.UPDATE)) {
            addPropDelta(
                uri,
                AAIProperties.RESOURCE_VERSION,
                PropertyDeltaFactory.getDelta(objDeltaAction, timeNowInSec, v.property(AAIProperties.RESOURCE_VERSION).value()),
                objDeltaAction
            );
            addPropDelta(
                uri,
                AAIProperties.LAST_MOD_TS,
                PropertyDeltaFactory.getDelta(objDeltaAction, currentTimeMillis, v.property(AAIProperties.LAST_MOD_TS).value()),
                objDeltaAction
            );
            addPropDelta(
                uri,
                AAIProperties.LAST_MOD_SOURCE_OF_TRUTH,
                PropertyDeltaFactory.getDelta(objDeltaAction, this.sourceOfTruth, v.property(AAIProperties.LAST_MOD_SOURCE_OF_TRUTH).value()),
                objDeltaAction
            );
        } else {
            addPropDelta(uri, AAIProperties.RESOURCE_VERSION, PropertyDeltaFactory.getDelta(objDeltaAction, v.property(AAIProperties.RESOURCE_VERSION).value()), objDeltaAction);
            addPropDelta(uri, AAIProperties.LAST_MOD_TS, PropertyDeltaFactory.getDelta(objDeltaAction, v.property(AAIProperties.LAST_MOD_TS).value()), objDeltaAction);
            addPropDelta(uri, AAIProperties.LAST_MOD_SOURCE_OF_TRUTH, PropertyDeltaFactory.getDelta(objDeltaAction, v.property(AAIProperties.LAST_MOD_SOURCE_OF_TRUTH).value()), objDeltaAction);
        }
    }

    public Map<String, ObjectDelta> getObjectDeltas() {return objectDeltas;}

    private void addPropDelta(String uri, String prop, PropertyDelta delta, DeltaAction objDeltaAction) {
        ObjectDelta objectDelta = this.objectDeltas.getOrDefault(uri, new ObjectDelta(uri, objDeltaAction, this.sourceOfTruth, this.currentTimeMillis));
        objectDelta.addPropertyDelta(prop, delta);
        objectDeltas.put(uri, objectDelta);
    }

    private void addRelationshipDelta(String uri, RelationshipDelta delta, DeltaAction objDeltaAction) {
        ObjectDelta objectDelta = this.objectDeltas.getOrDefault(uri, new ObjectDelta(uri, objDeltaAction, this.sourceOfTruth, this.currentTimeMillis));
        objectDelta.addRelationshipDelta(delta);
        objectDeltas.put(uri, objectDelta);
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
     */
    public Vertex createNewVertex(Introspector wrappedObject) {
        Vertex v;
        try {
            StopWatch.conditionalStart();
            v = this.engine.tx().addVertex(wrappedObject.getDbName());
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
        returnValue = className.substring(className.lastIndexOf('.') + 1);

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
     * @throws IllegalArgumentException the illegal argument exception
     * @throws AAIException the AAI exception
     * @throws UnsupportedEncodingException the unsupported encoding exception
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
            if (!isTopLevel) {
                URI uri = this.getURIForVertex(v);
                URIParser parser = new URIParser(this.loader, uri);
                if (parser.validate()) {
                    addUriIfNeeded(v, uri.toString());
                }
            }
            processObject(obj, v, requestContext);
        } catch (SchemaViolationException e) {
            throw new AAIException("AAI_6117", e);
        } finally {
            dbTimeMsecs += StopWatch.stopIfStarted();
        }
    }

    private void addUriIfNeeded(Vertex v, String uri) {
        VertexProperty<String> uriProp = v.property(AAIProperties.AAI_URI);
        if (!uriProp.isPresent() || !uriProp.value().equals(uri)) {
            v.property(AAIProperties.AAI_URI, uri);
        }
    }

    /**
     * Process object.
     *
     * @param obj the obj
     * @param v the v
     * @return the list
     * @throws IllegalArgumentException the illegal argument exception
     * @throws SecurityException the security exception
     * @throws AAIException the AAI exception
     * @throws UnsupportedEncodingException the unsupported encoding exception
     */
    /*
     * Helper method for reflectToDb
     * Handles all the property setting
     */
    private List<Vertex> processObject(Introspector obj, Vertex v, String requestContext)
            throws UnsupportedEncodingException, AAIException {
        Set<String> properties = new LinkedHashSet<>(obj.getProperties());
        properties.remove(AAIProperties.RESOURCE_VERSION);
        List<Vertex> dependentVertexes = new ArrayList<>();
        List<Vertex> processedVertexes = new ArrayList<>();

        boolean isComplexType ;
        boolean isListType;

        // If the notification depth is set to maximum
        // this is the behavior of the expected clients
        if(notificationDepth == AAIProperties.MAXIMUM_DEPTH) {
            if (!obj.isContainer()) {
                this.touchStandardVertexProperties(v, false);
            }
        }
        this.executePreSideEffects(obj, v);
        for (String property : properties) {
            final String propertyType;
            propertyType = obj.getType(property);
            isComplexType = obj.isComplexType(property);
            isListType = obj.isListType(property);
            Object value = obj.getValue(property);

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
                    Object oldValue = v.property(dbProperty).orElse(null);
                    String uri = getURIForVertex(v).toString();
                    if (value != null) {
                        if (!value.equals(oldValue)) {
                            if (propertyType.toLowerCase().contains(".long")) {
                                v.property(dbProperty, new Integer(((Long) value).toString()));
                            } else {
                                v.property(dbProperty, value);
                            }
                            if (isDeltaEventsEnabled) {
                                createDeltaProperty(uri, value, dbProperty, oldValue);
                            }
                            this.updatedVertexes.putIfAbsent(v, false);
                        }
                    } else {
                        if (oldValue != null) {
                            v.property(dbProperty).remove();
                            if (isDeltaEventsEnabled) {
                                addPropDelta(uri, dbProperty, PropertyDeltaFactory.getDelta(DeltaAction.DELETE, oldValue), DeltaAction.UPDATE);
                            }
                            this.updatedVertexes.putIfAbsent(v, false);
                        }
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
                    if (isDeltaEventsEnabled) {
                        String uri = getURIForVertex(v).toString();
                        List<Object> oldVal = engine.getListProperty(v, property);
                        engine.setListProperty(v, property, list);
                        if (list == null || list.isEmpty()) { // property delete scenario, there is no new value
                            if (oldVal != null && !oldVal.isEmpty()) { // and there is an old value
                                addPropDelta(uri, property, PropertyDeltaFactory.getDelta(DeltaAction.DELETE, oldVal), DeltaAction.UPDATE);
                            }
                        } else { // is either a create or update and is handled by the called method
                            createDeltaProperty(uri, list, property, oldVal);
                        }
                    } else {
                        engine.setListProperty(v, property, list);
                    }
                    this.updatedVertexes.putIfAbsent(v, false);
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
        for (Vertex toBeKept : processedVertexes) {
            dependentVertexes.remove(toBeKept);
        }

        ImpliedDelete impliedDelete = new ImpliedDelete(engine, this);
        List<Vertex> impliedDeleteVertices = impliedDelete.execute(v.id(), sourceOfTruth, obj.getName(), dependentVertexes);

        if(notificationDepth == AAIProperties.MINIMUM_DEPTH){
            for(Vertex curVertex : impliedDeleteVertices){
                if(!curVertex.property("aai-uri").isPresent()){
                    LOGGER.debug("Encountered an vertex {} with missing aai-uri", curVertex.id());
                    continue;
                }
                String curAaiUri = curVertex.<String>property(AAIProperties.AAI_URI).value();
                Introspector curObj = this.getLatestVersionView(curVertex, notificationDepth);

                LinkedHashMap<String, Introspector> curObjRelated = new LinkedHashMap<>();

                if(!curObj.isTopLevel()){
                    curObjRelated.putAll(this.getRelatedObjects(engine.getQueryEngine(), curVertex, curObj, this.loader));
                }

                if(!impliedDeleteUriObjectPair.containsKey(curAaiUri)){
                    impliedDeleteUriObjectPair.put(curAaiUri, new Pair<>(curObj, curObjRelated));
                }
            }
        }

        impliedDelete.delete(impliedDeleteVertices);

        // touch svp using vertex list for what changed
        // if the notification depth is zero
        if(notificationDepth == AAIProperties.MINIMUM_DEPTH){
            this.updatedVertexes.entrySet().stream()
                .filter(e -> !e.getValue())
                .filter(e -> !edgeVertexes.contains(e.getKey()))
                .forEach(e -> {
                    this.touchStandardVertexProperties(e.getKey(), false);
                    e.setValue(true);
                });
        }
        this.executePostSideEffects(obj, v);
        return processedVertexes;
    }

    private void createDeltaProperty(String uri, Object value, String dbProperty, Object oldValue) {
        if (oldValue == null) {
            addPropDelta(uri, dbProperty, PropertyDeltaFactory.getDelta(DeltaAction.CREATE, value), DeltaAction.UPDATE);
        } else {
            addPropDelta(uri, dbProperty, PropertyDeltaFactory.getDelta(DeltaAction.UPDATE, value, oldValue), DeltaAction.UPDATE);
        }
    }

    public HashMap<String, Introspector> getRelatedObjects(QueryEngine queryEngine, Vertex v,
                                                            Introspector obj, Loader loader) throws IllegalArgumentException, SecurityException, UnsupportedEncodingException, AAIException {

        HashMap<String, Introspector> relatedVertices = new HashMap<>();
        VertexProperty aaiUriProperty = v.property(AAIProperties.AAI_URI);

        if (!aaiUriProperty.isPresent()) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("For the given vertex {}, it seems aai-uri is not present so not getting related objects",
                    v.id().toString());
            } else {
                LOGGER.info(
                    "It seems aai-uri is not present in vertex, so not getting related objects, for more info enable debug log");
            }
            return relatedVertices;
        }

        String aaiUri = aaiUriProperty.value().toString();

        if (!obj.isTopLevel()) {
            String[] uriList = convertIntrospectorToUriList(aaiUri, obj, loader);
            List<Vertex> vertexChain;
            // If the uriList is null then there is something wrong with converting the uri
            // into a list of aai-uris so falling back to the old mechanism for finding parents
            if (uriList == null) {
                LOGGER.info(
                    "Falling back to the old mechanism due to unable to convert aai-uri to list of uris but this is not optimal");
                vertexChain = queryEngine.findParents(v);
            } else if (uriList.length == 1) {
                // If the uri list is size 1 the only uri in the list is the one represented by v thus no need to query
                vertexChain = Collections.singletonList(v);
            } else {
                // the uriList at element 0 is the node in question and should not be included in the vertex chain lookup.
                vertexChain = queryEngine.findParents(Arrays.copyOfRange(uriList, 1, uriList.length));
                // inject v into start of vertexChain
                vertexChain.add(0, v);
            }
            for (Vertex vertex : vertexChain) {
                try {
                    final Introspector vertexObj = this.getVertexProperties(vertex);
                    relatedVertices.put(vertexObj.getObjectId(), vertexObj);
                } catch (AAIUnknownObjectException e) {
                    LOGGER.warn("Unable to get vertex properties, partial list of related vertices returned");
                }
            }
        } else {
            try {
                final Introspector vertexObj = this.getVertexProperties(v);
                relatedVertices.put(vertexObj.getObjectId(), vertexObj);
            } catch (AAIUnknownObjectException e) {
                LOGGER.warn("Unable to get vertex properties, partial list of related vertices returned");
            }
        }

        return relatedVertices;
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
     * aai-uri ->
     * /cloud-infrastructure/cloud-regions/cloud-region/cloud-owner/cloud-region-id/tenants/tenant/tenant1/vservers/vserver/v1
     *
     * Given the uriTemplate vserver -> /vservers/vserver/{vserver-id}
     * it converts to /vservers/vserver
     *
     * lastIndexOf /vservers/vserver in
     * /cloud-infrastructure/cloud-regions/cloud-region/cloud-owner/cloud-region-id/tenants/tenant/tenant1/vservers/vserver/v1
     * ^
     * |
     * |
     * lastIndexOf
     * Use substring to get the string from 0 to that lastIndexOf
     * aai-uri -> /cloud-infrastructure/cloud-regions/cloud-region/cloud-owner/cloud-region-id/tenants/tenant/tenant1
     *
     * From this new aai-uri, generate a introspector from the URITOObject class
     * and keep doing this until you
     *
     * </blockquote>
     *
     * @param aaiUri - aai-uri of the vertex representating the unique id of a given vertex
     * @param obj - introspector object of the given starting vertex
     * @param loader - Type of loader which will always be MoxyLoader to support model driven
     * @return an array of strings which can be used to get the vertexes of parent and grand parents from a given vertex
     * @throws UnsupportedEncodingException
     * @throws AAIException
     */
    String[] convertIntrospectorToUriList(String aaiUri, Introspector obj, Loader loader)
        throws UnsupportedEncodingException, AAIException {

        List<String> uriList = new ArrayList<>();
        String template;
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

        return uriList.toArray(new String[0]);
    }

    /**
     * Handle relationships.
     *
     * @param obj the obj
     * @param vertex the vertex
     * @throws SecurityException the security exception
     * @throws IllegalArgumentException the illegal argument exception
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

        List<Object> relationships = wrapped.getValue("relationship");
        String mainUri = getURIForVertex(v).toString();
        String aNodeType = v.property(AAIProperties.NODE_TYPE).value().toString();
        EdgeRuleQuery.Builder cousinQueryBuilder = new EdgeRuleQuery.Builder(aNodeType)
            .edgeType(EdgeType.COUSIN)
            .version(wrapped.getVersion());
        EdgeRuleQuery.Builder treeQueryBuilder = new EdgeRuleQuery.Builder(aNodeType)
            .edgeType(EdgeType.TREE)
            .version(wrapped.getVersion());

        EdgeIngestor edgeIngestor = SpringContextAware.getBean(EdgeIngestor.class);

        Set<Pair<String, String>> cousinUriAndLabels = new LinkedHashSet<>();
        for (Object relationship : relationships) {

            String label;
            Introspector wrappedRel = IntrospectorFactory.newInstance(this.introspectionType, relationship);
            String relUri = urlToUri(new RelationshipToURI(loader, wrappedRel).getUri().toString());

            if (relUri.startsWith("/vnf/")) {
                QueryParser parser = engine.getQueryBuilder().createQueryFromRelationship(wrappedRel);
                List<Vertex> results = parser.getQueryBuilder().toList();
                if (results.isEmpty()) {
                    final AAIException ex = new AAIException(AAI_6129,
                        String.format("Node of type %s. Could not find object at: %s", parser.getResultType(), parser.getUri()));
                    ex.getTemplateVars().add(parser.getResultType());
                    ex.getTemplateVars().add(parser.getUri().toString());
                    throw ex;
                } else {
                    // still an issue if there's more than one
                    if (results.get(0).property(AAIProperties.AAI_URI).isPresent()) {
                        relUri = results.get(0).value(AAIProperties.AAI_URI);
                    } else {
                        LOGGER.warn("Not processing the vertex {} because its missing required property aai-uri", results.get(0).id());
                        continue;
                    }
                }
            }

            if (wrappedRel.getValue(RELATIONSHIP_LABEL) != null) {
                label = wrappedRel.getValue(RELATIONSHIP_LABEL);
            } else {
                URIToObject uriToObject = new URIToObject(loader, URI.create(relUri));
                String bNodeType = uriToObject.getEntityName();
                EdgeRuleQuery ruleQuery = cousinQueryBuilder.to(bNodeType).build();
                if (!edgeIngestor.hasRule(ruleQuery)) {
                    EdgeRuleQuery treeQuery = treeQueryBuilder.to(bNodeType).build();
                    if (edgeIngestor.hasRule(treeQuery)) {
                        throw new AAIException(AAI_6145); //attempted to create cousin edge for a parent-child edge rule
                    }
                    throw new AAIException("AAI_6120", String.format(
                        "No EdgeRule found for passed nodeTypes: %s, %s.",
                        aNodeType, bNodeType));
                } else {
                    try {
                        final List<EdgeRule> rules = new ArrayList<>(edgeIngestor.getRules(ruleQuery).values());
                        if (rules.size() == 1) {
                            label = rules.get(0).getLabel();
                        } else {
                            Optional<EdgeRule> defaultRule = rules.stream().filter(EdgeRule::isDefault).findFirst();
                            if (defaultRule.isPresent()) {
                                label = defaultRule.get().getLabel();
                            } else {
                                throw new AAIException(AAI_6145);
                            }
                        }
                    } catch (EdgeRuleNotFoundException ea) {
                        throw new AAIException(AAI_6145, ea);
                    }
                }
            }
            cousinUriAndLabels.add(Pair.with(relUri, label));
        }

        List<Path> paths = this.engine.getQueryEngine().findCousinsAsPath(v);
        Set<Path> toRemove = new HashSet<>();


        //  for each path 3 things can happen:
        //      1. The edge rule that created it is not in this version no action is to be taken on that edge
        //      2. The edge rule exits in this version it's included in the request the edge is left alone
        //      3. The edge rule exits in this version and is not included in the request it is marked for removal
        for (Path path : paths) {
            if (path.size() < 3) {
                continue;
            }

            // Path represents
            //       v   ----related-to-->    otherV
            // In the above case,
            // path objects get(0) returns vertex v
            // path objects.get(1) returns edge related-to
            // path objects.get(2) returns vertex otherV
            Vertex otherV = path.get(2);

            String bUri;
            if (otherV.property(AAIProperties.AAI_URI).isPresent()) {
                bUri = otherV.value(AAIProperties.AAI_URI);
            } else {
                continue;
            }
            String edgeLabel = path.<Edge>get(1).label();

            Pair<String, String> key = Pair.with(bUri, edgeLabel);
            if (cousinUriAndLabels.contains(key)) {
                cousinUriAndLabels.remove(key);
            } else {
                String bNodeType;
                if (otherV.property(AAIProperties.NODE_TYPE).isPresent()) {
                    bNodeType = otherV.property(AAIProperties.NODE_TYPE).value().toString();
                } else {
                    continue;
                }
                EdgeRuleQuery ruleQuery = cousinQueryBuilder.to(bNodeType).label(edgeLabel).build();
                if (edgeIngestor.hasRule(ruleQuery)) {
                    toRemove.add(path);
                }
            }

        }

        Set<Pair<Vertex, String>> toBeCreated = new HashSet<>();
        for (Pair<String, String> cousinUriAndLabel : cousinUriAndLabels) {
            Edge e;
            Vertex cousinVertex;
            String label = cousinUriAndLabel.getValue1();
            String cousinUri = cousinUriAndLabel.getValue0();
            QueryParser parser = engine.getQueryBuilder().createQueryFromURI(URI.create(cousinUri));

            List<Vertex> results = parser.getQueryBuilder().toList();
            if (results.isEmpty()) {
                final AAIException ex = new AAIException(AAI_6129,
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
                    throw new AAIException("AAI_6120", String.format(
                        "No EdgeRule found for passed nodeTypes: %s, %s%s.",
                        aNodeType, cousinType, label != null ? (" with label " + label) : ""));
                } else if (edgeRules.hasRule(baseQ.edgeType(EdgeType.TREE).build())
                    && !edgeRules.hasRule(baseQ.edgeType(EdgeType.COUSIN).build())) {
                    throw new AAIException(AAI_6145);
                }

                e = this.getEdgeBetween(EdgeType.COUSIN, v, cousinVertex, label);

                if (e == null) {
                    toBeCreated.add(Pair.with(cousinVertex, label));
                }
            }
        }

        for (Path path : toRemove) {
            if(isDeltaEventsEnabled) {
                deltaForEdge(mainUri, path.get(1), DeltaAction.DELETE_REL, DeltaAction.UPDATE);
            }
            this.updatedVertexes.putIfAbsent(v, false);
            this.edgeVertexes.add(path.get(2));
            path.<Edge>get(1).remove();
        }

        for (Pair<Vertex, String> create : toBeCreated) {
            try {
                Edge e = edgeSer.addEdge(this.engine.asAdmin().getTraversalSource(), v, create.getValue0(), create.getValue1());
                if (isDeltaEventsEnabled) {
                    deltaForEdge(mainUri, e, DeltaAction.CREATE_REL, DeltaAction.UPDATE);
                }
                this.updatedVertexes.putIfAbsent(v, false);
                this.edgeVertexes.add(create.getValue0());
            } catch (NoEdgeRuleFoundException ex) {
                throw new AAIException(AAI_6129, ex);
            }
        }
    }


    private void deltaForEdge(String mainUri, Edge edge, DeltaAction edgeAction, DeltaAction mainAction) {
        RelationshipDelta relationshipDelta = new RelationshipDelta(
            edgeAction,
            edge.inVertex().property(AAIProperties.AAI_UUID).value().toString(),
            edge.outVertex().property(AAIProperties.AAI_UUID).value().toString(),
            edge.inVertex().property(AAIProperties.AAI_URI).value().toString(),
            edge.outVertex().property(AAIProperties.AAI_URI).value().toString(),
            edge.label());
        edge.properties().forEachRemaining(p -> relationshipDelta.addProp(p.key(), p.value().toString()));
        addRelationshipDelta(mainUri, relationshipDelta, mainAction);
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
                String defaultValue = latest.getPropertyMetadata(field).get(PropertyMetadata.DEFAULT_VALUE);
                if (defaultValue != null) {
                    Object vertexProp = v.property(field).orElse(null);
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
     * @throws IllegalArgumentException the illegal argument exception
     * @throws SecurityException the security exception
     * @throws AAIException the AAI exception
     * @throws UnsupportedEncodingException the unsupported encoding exception
     * @throws AAIUnknownObjectException
     */
    private Vertex reflectDependentVertex(Vertex v, Introspector dependentObj, String requestContext)
            throws AAIException, UnsupportedEncodingException {

        QueryBuilder<Vertex> query = this.engine.getQueryBuilder(v);
        query.createEdgeTraversal(EdgeType.TREE, v, dependentObj);
        query.createKeyQuery(dependentObj);

        List<Vertex> items = query.toList();

        Vertex dependentVertex;
        if (items.size() == 1) {
            dependentVertex = items.get(0);
            this.verifyResourceVersion("update", dependentObj.getDbName(),
                    dependentVertex.<String>property(AAIProperties.RESOURCE_VERSION).orElse(null),
                    dependentObj.getValue(AAIProperties.RESOURCE_VERSION), dependentObj.getURI());
        } else {
            this.verifyResourceVersion("create", dependentObj.getDbName(), "",
                    dependentObj.getValue(AAIProperties.RESOURCE_VERSION), dependentObj.getURI());
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
     * @throws IllegalArgumentException the illegal argument exception
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
            e = edgeSer.addTreeEdge(this.engine.asAdmin().getTraversalSource(), parent, child);
            if(isDeltaEventsEnabled) {
                deltaForEdge(child.property(AAIProperties.AAI_URI).value().toString(), e, DeltaAction.CREATE_REL, DeltaAction.CREATE);
            }
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
     * @throws IllegalArgumentException the illegal argument exception
     * @throws SecurityException the security exception
     * @throws UnsupportedEncodingException the unsupported encoding exception
     * @throws AAIUnknownObjectException
     * @throws URISyntaxException
     */
    public Introspector dbToObject(List<Vertex> vertices, final Introspector obj, int depth, boolean nodeOnly,
                                   String cleanUp, boolean isSkipRelatedTo) throws UnsupportedEncodingException, AAIException {
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
            getList = obj.getValue(listProperty);

            /*
             * This is an experimental multithreading experiment
             * on get alls.
             */
            ExecutorService pool = GetAllPool.getInstance().getPool();

            List<Future<Object>> futures = new ArrayList<>();

            for (Vertex v : vertices) {
                AaiCallable<Object> task = new AaiCallable<Object>() {
                    @Override
                    public Object process() throws UnsupportedEncodingException, AAIException {
                        Set<Vertex> seen = new HashSet<>();
                        Introspector childObject;
                        childObject = obj.newIntrospectorInstanceOfNestedProperty(propertyName);
                        dbToObject(childObject, v, seen, internalDepth, nodeOnly, cleanUp, isSkipRelatedTo);
                        return childObject.getUnderlyingObject();
                    }
                };
                futures.add(pool.submit(task));
            }

            for (Future<Object> future : futures) {
                try {
                    getList.add(future.get());
                } catch (ExecutionException | InterruptedException e) {
                    dbTimeMsecs += StopWatch.stopIfStarted();
                    throw new AAIException("AAI_4000", e);
                }
            }
        } else if (vertices.size() == 1) {
            Set<Vertex> seen = new HashSet<>();
            dbToObject(obj, vertices.get(0), seen, depth, nodeOnly, cleanUp, isSkipRelatedTo);
        } else {
            // obj = null;
        }

        dbTimeMsecs += StopWatch.stopIfStarted();
        return obj;
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
     * @throws IllegalArgumentException the illegal argument exception
     * @throws SecurityException the security exception
     * @throws UnsupportedEncodingException the unsupported encoding exception
     * @throws AAIUnknownObjectException
     * @throws URISyntaxException
     */
    public Introspector dbToObject(List<Vertex> vertices, final Introspector obj, int depth, boolean nodeOnly,
            String cleanUp) throws UnsupportedEncodingException, AAIException {
        return dbToObject(vertices, obj, depth, nodeOnly, cleanUp, false);
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
     * @throws IllegalArgumentException the illegal argument exception
     * @throws SecurityException the security exception
     * @throws UnsupportedEncodingException the unsupported encoding exception
     * @throws AAIException the AAI exception
     * @throws AAIUnknownObjectException
     * @throws URISyntaxException
     */
    private Introspector dbToObject(Introspector obj, Vertex v, Set<Vertex> seen, int depth, boolean nodeOnly,
            String cleanUp) throws AAIException, UnsupportedEncodingException {
        return dbToObject(obj, v, seen, depth, nodeOnly, cleanUp, false);
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
     * @throws IllegalArgumentException the illegal argument exception
     * @throws SecurityException the security exception
     * @throws UnsupportedEncodingException the unsupported encoding exception
     * @throws AAIException the AAI exception
     * @throws AAIUnknownObjectException
     * @throws URISyntaxException
     */
    private Introspector dbToObject(Introspector obj, Vertex v, Set<Vertex> seen, int depth, boolean nodeOnly,
                                    String cleanUp, boolean isSkipRelatedTo) throws AAIException, UnsupportedEncodingException {

        if (depth < 0) {
            return null;
        }
        depth--;
        seen.add(v);

        boolean modified = false;
        for (String property : obj.getProperties(PropertyPredicates.isVisible())) {
            List<Object> getList = null;

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
                        relationshipList = createRelationshipList(v, relationshipList, cleanUp, isSkipRelatedTo);
                        if (relationshipList != null) {
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

                            Direction ruleDirection = rule.getDirection();
                            List<Vertex> verticesList = new ArrayList<>();
                            v.vertices(ruleDirection, rule.getLabel()).forEachRemaining(vertex -> {
                                if (vertex.property(AAIProperties.NODE_TYPE).orElse("").equals(childDbName)) {
                                    verticesList.add(vertex);
                                }
                            });
                            if (!verticesList.isEmpty()) {
                                getList = obj.getValue(property);
                            }
                            int processed = 0;
                            for (Vertex childVertex : verticesList) {
                                if (!seen.contains(childVertex)) {
                                    Introspector argumentObject = obj.newIntrospectorInstanceOfNestedProperty(property);

                                    Object result =
                                        dbToObject(argumentObject, childVertex, seen, depth, nodeOnly, cleanUp, isSkipRelatedTo);
                                    if (result != null) {
                                        getList.add(argumentObject.getUnderlyingObject());
                                    }

                                    processed++;
                                } else {
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
        StopWatch.conditionalStart();
        this.dbToObject(obj, v, seen, depth, false, FALSE);
        dbTimeMsecs += StopWatch.stopIfStarted();
        return obj;

    }

    public Introspector getLatestVersionView(Vertex v) throws AAIException, UnsupportedEncodingException {
        return getLatestVersionView(v, AAIProperties.MAXIMUM_DEPTH);
    }

    public Introspector getLatestVersionView(Vertex v, int depth) throws AAIException, UnsupportedEncodingException {
        String nodeType = v.<String>property(AAIProperties.NODE_TYPE).orElse(null);
        if (nodeType == null) {
            throw new AAIException("AAI_6143");
        }
        Introspector obj = this.latestLoader.introspectorFromName(nodeType);
        Set<Vertex> seen = new HashSet<>();
        StopWatch.conditionalStart();
        this.dbToObject(obj, v, seen, depth, false, FALSE);
        dbTimeMsecs += StopWatch.stopIfStarted();
        return obj;
    }

    /**
     * Copy simple property.
     *
     * @param property the property
     * @param obj the obj
     * @param v the v
     * @throws IllegalArgumentException the illegal argument exception
     * @throws SecurityException the security exception
     */
    private void copySimpleProperty(String property, Introspector obj, Vertex v) {
        final Object temp = getProperty(obj, property, v);
        if (temp != null) {
            obj.setValue(property, temp);
        }
    }

    public Map<String, Object> convertVertexToHashMap(Introspector obj, Vertex v) {

        Set<String> simpleProperties = obj.getSimpleProperties(PropertyPredicates.isVisible());
        String[] simplePropsArray = new String[simpleProperties.size()];
        simplePropsArray = simpleProperties.toArray(simplePropsArray);

        Map<String, Object> simplePropsHashMap = new HashMap<>(simplePropsArray.length * 2);

        v.properties(simplePropsArray).forEachRemaining(vp -> simplePropsHashMap.put(vp.key(), vp.value()));

        return simplePropsHashMap;
    }

    public Introspector dbToRelationshipObject(Vertex v, boolean isSkipRelatedTo) throws UnsupportedEncodingException, AAIException {
        Introspector relationshipList = this.latestLoader.introspectorFromName("relationship-list");
        relationshipList = createRelationshipList(v, relationshipList, FALSE, isSkipRelatedTo);
        return relationshipList;
    }

    /**
     * Creates the relationship list.
     *
     * @param v the v
     * @param obj the obj
     * @param cleanUp the clean up
     * @return the object
     * @throws IllegalArgumentException the illegal argument exception
     * @throws SecurityException the security exception
     * @throws UnsupportedEncodingException the unsupported encoding exception
     * @throws AAIException the AAI exception
     * @throws URISyntaxException
     */
    private Introspector createRelationshipList(Vertex v, Introspector obj, String cleanUp)
            throws UnsupportedEncodingException, AAIException {
        // default boolean value for isSkipRelatedTo is false
        return createRelationshipList(v, obj, cleanUp, false);
    }

    /**
     * Creates the relationship list.
     *
     * @param v the v
     * @param obj the obj
     * @param cleanUp the clean up
     * @param isSkipRelatedTo to determine adding related-to-property in response
     * @return the object
     * @throws IllegalArgumentException the illegal argument exception
     * @throws SecurityException the security exception
     * @throws UnsupportedEncodingException the unsupported encoding exception
     * @throws AAIException the AAI exception
     * @throws URISyntaxException
     */
    private Introspector createRelationshipList(Vertex v, Introspector obj, String cleanUp, boolean isSkipRelatedTo)
            throws UnsupportedEncodingException, AAIException {

        List<Object> relationshipObjList = obj.getValue(RELATIONSHIP);
        VertexProperty nodeTypeProperty = v.property(AAIProperties.NODE_TYPE);

        if (!nodeTypeProperty.isPresent()) {
            LOGGER.warn("Not processing the vertex {} because its missing required property aai-node-type", v.id());
            return null;
        }

        List<Path> paths = this.engine.getQueryEngine().findCousinsAsPath(v);

        String aNodeType = v.property(AAIProperties.NODE_TYPE).value().toString();

        EdgeIngestor edgeIngestor = SpringContextAware.getBean(EdgeIngestor.class);

        EdgeRuleQuery.Builder queryBuilder = new EdgeRuleQuery.Builder(aNodeType)
            .edgeType(EdgeType.COUSIN)
            .version(obj.getVersion());

        for (Path path : paths){
            if(path.size() < 3){
                continue;
            }

            // Path represents
            //       v   ----related-to-->    otherV
            // In the above case,
            // path objects get(0) returns vertex v
            // path objects.get(1) returns edge related-to
            // path objects.get(2) returns vertex otherV
            Edge edge = path.get(1);
            Vertex otherV= path.get(2);

            // TODO: Come back and revisit this code
            // Create a query based on the a nodetype and b nodetype
            // which is also a cousin edge and ensure the version
            // is used properly so for example in order to be backwards
            // compatible if we had allowed a edge between a and b
            // in a previous release and we decided to remove it from
            // the edge rules in the future we can display the edge
            // only for the older apis and the new apis if the edge rule
            // is removed will not be seen in the newer version of the API

            String bNodeType = null;
            if (otherV.property(AAIProperties.NODE_TYPE).isPresent()) {
                bNodeType = otherV.property(AAIProperties.NODE_TYPE).value().toString();
            } else {
                continue;
            }

            String edgeLabel = edge.label();
            EdgeRuleQuery ruleQuery = queryBuilder.to(bNodeType).label(edgeLabel).build();

            if(!edgeIngestor.hasRule(ruleQuery)){
                LOGGER.debug( "Caught an edge rule not found for query {}", ruleQuery);
                continue;
            }

            Introspector relationshipObj = obj.newIntrospectorInstanceOfNestedProperty(RELATIONSHIP);
            Object result = processEdgeRelationship(relationshipObj, otherV, cleanUp, edgeLabel, isSkipRelatedTo);
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

    /**
     * Process edge relationship.
     *
     * @param relationshipObj the relationship obj
     * @param edgeLabel the edge's label
     * @param cleanUp the clean up
     * @return the object
     * @throws IllegalArgumentException the illegal argument exception
     * @throws SecurityException the security exception
     * @throws UnsupportedEncodingException the unsupported encoding exception
     * @throws AAIUnknownObjectException
     * @throws URISyntaxException
     */
    private Object processEdgeRelationship(Introspector relationshipObj, Vertex cousin, String cleanUp,
            String edgeLabel, boolean isSkipRelatedTo) throws UnsupportedEncodingException, AAIUnknownObjectException {

        VertexProperty aaiUriProperty = cousin.property("aai-uri");

        if (!aaiUriProperty.isPresent()) {
            return null;
        }

        URI uri = UriBuilder.fromUri(aaiUriProperty.value().toString()).build();

        URIToRelationshipObject uriParser;
        Introspector result;
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
            if (namedPropNodes.contains(cousinType) && !isSkipRelatedTo) {
                this.addRelatedToProperty(result, cousin, cousinType);
            }
        }

        if (edgeLabel != null && result.hasProperty(RELATIONSHIP_LABEL)) {
            result.setValue(RELATIONSHIP_LABEL, edgeLabel);
        }

        return result.getUnderlyingObject();
    }

    /**
     * Gets the URI for vertex.
     *
     * @param v the v
     * @return the URI for vertex
     * @throws IllegalArgumentException the illegal argument exception
     * @throws SecurityException the security exception
     * @throws UnsupportedEncodingException the unsupported encoding exception
     * @throws AAIUnknownObjectException
     */
    public URI getURIForVertex(Vertex v) throws UnsupportedEncodingException {

        return getURIForVertex(v, false);
    }

    public URI getURIForVertex(Vertex v, boolean overwrite) {
        URI uri = UriBuilder.fromPath("/unknown-uri").build();

        String aaiUri = v.<String>property(AAIProperties.AAI_URI).orElse(null);

        if (aaiUri != null && !overwrite) {
            uri = UriBuilder.fromPath(aaiUri).build();
        }

        return uri;
    }


    public void addRelatedToProperty(Introspector relationship, Vertex cousinVertex, String cousinType)
            throws AAIUnknownObjectException {

        Introspector obj;

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
            List<Object> relatedToList = relationship.getValue("related-to-property");
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
    public Vertex createEdge(Introspector relationship, Vertex inputVertex)
            throws UnsupportedEncodingException, AAIException {

        Vertex relatedVertex;
        StopWatch.conditionalStart();
        QueryParser parser = engine.getQueryBuilder().createQueryFromRelationship(relationship);

        String label = null;
        if (relationship.hasProperty(RELATIONSHIP_LABEL)) {
            label = relationship.getValue(RELATIONSHIP_LABEL);
        }

        List<Vertex> results = parser.getQueryBuilder().toList();
        if (results.isEmpty()) {
            dbTimeMsecs += StopWatch.stopIfStarted();
            AAIException e = new AAIException(AAI_6129,
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
                    e = edgeSer.addEdge(this.engine.asAdmin().getTraversalSource(), inputVertex, relatedVertex, label);
                    if(isDeltaEventsEnabled) {
                        deltaForEdge(inputVertex.property(AAIProperties.AAI_URI).value().toString(), e, DeltaAction.CREATE_REL, DeltaAction.UPDATE);
                    }
                } else {
                    // attempted to link two vertexes already linked
                }
            } finally {
                dbTimeMsecs += StopWatch.stopIfStarted();
            }
        }

        dbTimeMsecs += StopWatch.stopIfStarted();
        return relatedVertex;
    }

    /**
     * Gets all the edges between of the type with the specified label.
     *
     * @param aVertex the out vertex
     * @param bVertex the in vertex
     * @return the edges between
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
     * Gets all the edges string between of the type.
     *
     * @param aVertex the out vertex
     * @param bVertex the in vertex
     * @return the edges between
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
    public Optional<Vertex> deleteEdge(Introspector relationship, Vertex inputVertex)
            throws UnsupportedEncodingException, AAIException {

        Vertex relatedVertex;
        StopWatch.conditionalStart();
        QueryParser parser = engine.getQueryBuilder().createQueryFromRelationship(relationship);

        List<Vertex> results = parser.getQueryBuilder().toList();

        String label = null;
        if (relationship.hasProperty(RELATIONSHIP_LABEL)) {
            label = relationship.getValue(RELATIONSHIP_LABEL);
        }

        if (results.isEmpty()) {
            dbTimeMsecs += StopWatch.stopIfStarted();
            return Optional.empty();
        }

        relatedVertex = results.get(0);
        Edge edge;
        try {
            edge = this.getEdgeBetween(EdgeType.COUSIN, inputVertex, relatedVertex, label);
        } catch (NoEdgeRuleFoundException e) {
            dbTimeMsecs += StopWatch.stopIfStarted();
            throw new AAIException(AAI_6129, e);
        }
        if (edge != null) {
            if(isDeltaEventsEnabled) {
                String mainUri = inputVertex.property(AAIProperties.AAI_URI).value().toString();
                deltaForEdge(mainUri, edge, DeltaAction.DELETE_REL, DeltaAction.UPDATE);
            }
            edge.remove();
            dbTimeMsecs += StopWatch.stopIfStarted();
            return Optional.of(relatedVertex);
        } else {
            dbTimeMsecs += StopWatch.stopIfStarted();
            return Optional.empty();
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
        this.delete(results);
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
            if(isDeltaEventsEnabled) {
                deltaForVertexDelete(v);
            }
            //add the cousin vertexes of v to have their resource-version updated and notified on.
            v.edges(Direction.BOTH)
                .forEachRemaining(e -> {
                    if (e.property(EdgeProperty.CONTAINS.toString()).isPresent()
                        && AAIDirection.NONE.toString().equals(e.<String>value(EdgeProperty.CONTAINS.toString()))) {
                        e.bothVertices().forEachRemaining(cousinV -> {
                            if (!v.equals(cousinV)) {
                                edgeVertexes.add(cousinV);
                            }
                        });
                    }
                });

            //if somewhere along the way v was added to the sets tracking the what is to be updated/notified on
            // it should be removed from them as v is to be deleted
            edgeVertexes.remove(v);
            updatedVertexes.remove(v);
            v.remove();
        }

        dbTimeMsecs += StopWatch.stopIfStarted();
    }

    private void deltaForVertexDelete(Vertex v) {
        String aaiUri = v.property(AAIProperties.AAI_URI).value().toString();
        v.keys().forEach(k -> {
            List<Object> list = new ArrayList<>();
            v.properties(k).forEachRemaining(vp -> list.add(vp.value()));
            if (list.size() == 1) {
                addPropDelta(aaiUri, k, PropertyDeltaFactory.getDelta(DeltaAction.DELETE, list.get(0)), DeltaAction.DELETE);
            } else {
                addPropDelta(aaiUri, k, PropertyDeltaFactory.getDelta(DeltaAction.DELETE, list), DeltaAction.DELETE);
            }

        });
        v.edges(Direction.BOTH).forEachRemaining(e -> deltaForEdge(aaiUri, e, DeltaAction.DELETE, DeltaAction.DELETE));
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
         * These are far-fewer than seeing a prevent-delete on the vertex to be deleted
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
            throws IllegalArgumentException, AAIException {

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
        boolean result;
        String nodeType;
        nodeType = vertex.<String>property(AAIProperties.NODE_TYPE).orElse(null);
        if (enableResourceVersion) {
            this.verifyResourceVersion("delete", nodeType,
                vertex.<String>property(AAIProperties.RESOURCE_VERSION).orElse(null), resourceVersion, nodeType);
        }
        List<Vertex> vertices = new ArrayList<>();
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
    public Introspector dbToObjectWithFilters(Introspector obj, Vertex v, Set<Vertex> seen, int depth, boolean nodeOnly,
                                              List<String> filterCousinNodes, List<String> filterParentNodes)
        throws AAIException, UnsupportedEncodingException {
        return dbToObjectWithFilters(obj, v, seen, depth, nodeOnly,
        filterCousinNodes, filterParentNodes, false);
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
     * @param isSkipRelatedTo determine to incorporated related-to-property data
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
            List<String> filterCousinNodes, List<String> filterParentNodes, boolean isSkipRelatedTo)
            throws AAIException, UnsupportedEncodingException {
        String cleanUp = FALSE;
        if (depth < 0) {
            return null;
        }
        depth--;
        seen.add(v);
        boolean modified = false;
        for (String property : obj.getProperties(PropertyPredicates.isVisible())) {
            List<Object> getList = null;

            if (!(obj.isComplexType(property) || obj.isListType(property))) {
                this.copySimpleProperty(property, obj, v);
                modified = true;
            } else {
                if (obj.isComplexType(property)) {
                    /* container case */

                    if (!property.equals("relationship-list") && depth >= 0) {
                        Introspector argumentObject = obj.newIntrospectorInstanceOfProperty(property);
                        Object result = dbToObjectWithFilters(argumentObject, v, seen, depth + 1, nodeOnly,
                                filterCousinNodes, filterParentNodes, isSkipRelatedTo);
                        if (result != null) {
                            obj.setValue(property, argumentObject.getUnderlyingObject());
                            modified = true;
                        }
                    } else if (property.equals("relationship-list") && !nodeOnly) {
                        /* relationships need to be handled correctly */
                        Introspector relationshipList = obj.newIntrospectorInstanceOfProperty(property);
                        relationshipList =
                                createFilteredRelationshipList(v, relationshipList, cleanUp, filterCousinNodes, isSkipRelatedTo);
                        if (relationshipList != null) {
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

                        boolean isThisParentRequired =
                                filterParentNodes.parallelStream().anyMatch(childDbName::contains);

                        EdgeRuleQuery q = new EdgeRuleQuery.Builder(vType, childDbName).edgeType(EdgeType.TREE).build();

                        try {
                            rule = edgeRules.getRule(q);
                        } catch (EdgeRuleNotFoundException e) {
                            throw new NoEdgeRuleFoundException(e);
                        } catch (AmbiguousRuleChoiceException e) {
                            throw new MultipleEdgeRuleFoundException(e);
                        }
                        if (!rule.getContains().equals(AAIDirection.NONE.toString()) && isThisParentRequired) {
                            Direction ruleDirection = rule.getDirection();
                            List<Vertex> verticesList = new ArrayList<>();
                            v.vertices(ruleDirection, rule.getLabel()).forEachRemaining(vertex -> {
                                if (vertex.property(AAIProperties.NODE_TYPE).orElse("").equals(childDbName)) {
                                    verticesList.add(vertex);
                                }
                            });
                            if (!verticesList.isEmpty()) {
                                getList = obj.getValue(property);
                            }
                            int processed = 0;
                            for (Vertex childVertex : verticesList) {
                                if (!seen.contains(childVertex)) {
                                    Introspector argumentObject = obj.newIntrospectorInstanceOfNestedProperty(property);

                                    Object result = dbToObjectWithFilters(argumentObject, childVertex, seen, depth,
                                            nodeOnly, filterCousinNodes, filterParentNodes, isSkipRelatedTo);
                                    if (result != null) {
                                        getList.add(argumentObject.getUnderlyingObject());
                                    }

                                    processed++;
                                } else {
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
                            getList = obj.getValue(property);
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
     * @throws IllegalArgumentException the illegal argument exception
     * @throws SecurityException the security exception
     * @throws UnsupportedEncodingException the unsupported encoding exception
     * @throws AAIException the AAI exception
     */
    private Introspector createFilteredRelationshipList(Vertex v, Introspector obj, String cleanUp,
            List<String> filterNodes, boolean isSkipRelatedTo) throws UnsupportedEncodingException, AAIException {
        List<Vertex> allCousins = this.engine.getQueryEngine().findCousinVertices(v);

        Iterator<Vertex> cousinVertices = allCousins.stream().filter(item -> {
            String node = (String) item.property(AAIProperties.NODE_TYPE).orElse("");
            return filterNodes.parallelStream().anyMatch(node::contains);
        }).iterator();

        List<Object> relationshipObjList = obj.getValue(RELATIONSHIP);

        List<Vertex> cousins = new ArrayList<>();
        cousinVertices.forEachRemaining(cousins::add);
        for (Vertex cousin : cousins) {

            Introspector relationshipObj = obj.newIntrospectorInstanceOfNestedProperty(RELATIONSHIP);
            Object result = processEdgeRelationship(relationshipObj, cousin, cleanUp, null, isSkipRelatedTo);
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

    public Set<Vertex> touchStandardVertexPropertiesForEdges() {
        this.edgeVertexes.forEach(v -> this.touchStandardVertexProperties(v, false));
        return this.edgeVertexes;
    }

    public void addVertexToEdgeVertexes(Vertex vertex){
        this.edgeVertexes.add(vertex);
    }

    private String urlToUri(String url) {
        if (url.startsWith("/")) {
            url = url.substring(1);
        }

        if (url.endsWith("/")) {
            url = url.substring(0, url.length() - 1);
        }

        // TODO - Check if this makes to do for model driven for base uri path
        url = url.replaceFirst("[a-z][a-z]*/v\\d+/", "");
        if (url.charAt(0) != '/') {
            url = '/' + url;
        }

        return url;
    }

}
