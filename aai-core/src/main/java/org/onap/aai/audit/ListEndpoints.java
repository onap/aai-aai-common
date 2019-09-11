/**
 * ============LICENSE_START=======================================================
 * org.onap.aai
 * ================================================================================
 * Copyright © 2017-2018 AT&T Intellectual Property. All rights reserved.
 *
 * Modifications Copyright (C) 2019 IBM.
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

package org.onap.aai.audit;

import com.att.eelf.configuration.EELFLogger;
import com.att.eelf.configuration.EELFManager;
import com.google.common.base.CaseFormat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.onap.aai.config.SpringContextAware;
import org.onap.aai.introspection.Introspector;
import org.onap.aai.introspection.Loader;
import org.onap.aai.introspection.LoaderFactory;
import org.onap.aai.introspection.ModelType;
import org.onap.aai.introspection.exceptions.AAIUnknownObjectException;
import org.onap.aai.logging.LogFormatTools;
import org.onap.aai.setup.SchemaVersion;
import org.onap.aai.setup.SchemaVersions;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * The Class ListEndpoints.
 */
public class ListEndpoints {

    private static final EELFLogger LOGGER = EELFManager.getInstance().getLogger(ListEndpoints.class);

    private final String start = "inventory";
    private final String[] blacklist = {"search", "aai-internal"};

    private List<String> endpoints = new ArrayList<>();
    private Map<String, String> endpointToLogicalName = new HashMap<String, String>();

    /**
     * The main method.
     *
     * @param args the arguments
     */
    public static void main(String[] args) {

        
		AnnotationConfigApplicationContext context =
                new AnnotationConfigApplicationContext("org.onap.aai.config", "org.onap.aai.setup");

        String schemaUriBasePath = context.getEnvironment().getProperty("schema.uri.base.path");

        if (schemaUriBasePath == null) {
            String errorMsg = "Unable to find the property schema.uri.base.path,"
                    + " please check if specified in system property or in schema-ingest.properties";
            System.err.println(errorMsg);
            LOGGER.error(errorMsg);
        }

        SchemaVersions schemaVersions = context.getBean(SchemaVersions.class);
        ListEndpoints endPoints = new ListEndpoints(schemaUriBasePath, schemaVersions.getDefaultVersion());

        LOGGER.info(endPoints.toString("relationship-list"));
    }

    /**
     * Instantiates a new list endpoints.
     *
     * @param version the version
     */
    public ListEndpoints(String basePath, SchemaVersion version) {

        Loader loader = SpringContextAware.getBean(LoaderFactory.class).createLoaderForVersion(ModelType.MOXY, version);

        try {
            final Introspector start = loader.introspectorFromName(this.start);
            Set<String> startMap = new HashSet<>();
            beginAudit(start, basePath + "/" + version, startMap);
        } catch (AAIUnknownObjectException e) {
            throw new RuntimeException("Failed to find object " + this.start + ", cannot run ListEndpoints audit");
        }
    }

    /**
     * Begin audit.
     *
     * @param obj the obj
     * @param uri the uri
     */
    private void beginAudit(Introspector obj, String uri, Set<String> visited) {

        String currentUri = "";

        if (!obj.getDbName().equals("inventory")) {
            currentUri = uri + obj.getGenericURI();
        } else {
            currentUri = uri;
        }
        if (obj.getName().equals("relationship-data") || obj.getName().equals("related-to-property")) {
            return;
        }
        if (!obj.isContainer()) {
            endpoints.add(currentUri);
        }

        String dbName = obj.getDbName();

        populateLogicalName(obj, uri, currentUri);

        Set<String> properties = obj.getProperties();
        Set<String> props = new LinkedHashSet<>(properties);
        if (obj.isContainer()) {
            for (String key : visited) {
                if (props.remove(key)) {
                    try {
                        endpoints.add(currentUri + obj.getLoader().introspectorFromName(key).getGenericURI());
                    } catch (AAIUnknownObjectException e) {
                        LOGGER.warn(
                                "Skipping endpoint for " + key + " (Unknown object) " + LogFormatTools.getStackTop(e));
                    }
                }
            }
        }

        outer: for (String propName : props) {

            for (String item : blacklist) {
                if (propName.equals(item)) {
                    continue outer;
                }
            }
            if (obj.isListType(propName)) {
                if (obj.isComplexGenericType(propName)) {
                    try {
                        final Introspector nestedObj = obj.newIntrospectorInstanceOfNestedProperty(propName);
                        Set<String> newVisited = new HashSet<>();
                        newVisited.addAll(visited);
                        newVisited.add(nestedObj.getDbName());
                        beginAudit(nestedObj, currentUri, newVisited);
                    } catch (AAIUnknownObjectException e) {
                        LOGGER.warn("Skipping nested endpoint for " + propName + " (Unknown Object) "
                                + LogFormatTools.getStackTop(e));
                    }
                }
            } else if (obj.isComplexType(propName)) {
                try {
                    final Introspector nestedObj = obj.newIntrospectorInstanceOfProperty(propName);
                    Set<String> newVisited = new HashSet<>();
                    newVisited.addAll(visited);
                    newVisited.add(nestedObj.getDbName());
                    beginAudit(nestedObj, currentUri, visited);
                } catch (AAIUnknownObjectException e) {
                    LOGGER.warn("Skipping nested enpoint for " + propName + " (Unknown Object) "
                            + LogFormatTools.getStackTop(e));
                }
            }
        }

    }

    /**
     * Populate logical name.
     *
     * @param obj the obj
     * @param uri the uri
     * @param currentUri the current uri
     */
    private void populateLogicalName(Introspector obj, String uri, String currentUri) {

        if (obj.getDbName().equals("inventory") || currentUri.split("/").length <= 4
                || currentUri.endsWith("relationship-list")) {
            return;
        }

        if (uri.endsWith("/relationship-list")) {
            uri = uri.substring(0, uri.lastIndexOf("/"));
        }

        String logicalName = "";
        String keys = "";

        if (!obj.getAllKeys().isEmpty()) {

            Pattern p = Pattern.compile("/\\{[\\w\\d\\-]+\\}/\\{[\\w\\d\\-]+\\}+$");
            Matcher m = p.matcher(currentUri);

            if (m.find()) {
                keys = StringUtils.join(obj.getAllKeys(), "-and-");
            } else {
                keys = StringUtils.join(obj.getAllKeys(), "-or-");
            }
            keys = CaseFormat.LOWER_HYPHEN.to(CaseFormat.UPPER_CAMEL, keys);
            if (!keys.isEmpty()) {
                keys = "With" + keys;
            }
        }

        logicalName = CaseFormat.LOWER_HYPHEN.to(CaseFormat.UPPER_CAMEL, obj.getDbName()) + keys;

        if (endpointToLogicalName.containsKey(uri) && uri.endsWith("}")) {
            logicalName = logicalName + "From" + endpointToLogicalName.get(uri);
        } else if (endpointToLogicalName.containsKey(uri.substring(0, uri.lastIndexOf("/")))) {
            logicalName = logicalName + "From" + endpointToLogicalName.get(uri.substring(0, uri.lastIndexOf("/")));
        }

        endpointToLogicalName.put(currentUri, logicalName);

    }

    /**
     * Gets the logical names.
     *
     * @return the logical names
     */
    public Map<String, String> getLogicalNames() {

        return endpointToLogicalName;

    }

    /**
     * Gets the endpoints.
     *
     * @return the endpoints
     */
    public List<String> getEndpoints() {

        return this.getEndpoints("");

    }

    /**
     * Gets the endpoints.
     *
     * @param filterOut the filter out
     * @return the endpoints
     */
    public List<String> getEndpoints(String filterOut) {
        List<String> result = new ArrayList<>();
        Pattern p = null;
        Matcher m = null;
        if (!filterOut.equals("")) {
            p = Pattern.compile(filterOut);
            m = null;
        }
        for (String s : endpoints) {
            if (p != null) {
                m = p.matcher(s);
                if (m.find()) {
                    continue;
                }
            }

            result.add(s);
        }

        return result;

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (String s : endpoints) {
            sb.append(s + "\n");
        }
        return sb.toString();

    }

    /**
     * To string.
     *
     * @param filterOut the filter out
     * @return the string
     */
    public String toString(String filterOut) {
        StringBuilder sb = new StringBuilder();
        Pattern p = Pattern.compile(filterOut);
        Matcher m = null;
        for (String s : endpoints) {
            m = p.matcher(s);
            if (!m.find()) {
                sb.append(s + "\n");
            }
        }
        return sb.toString();
    }

}
