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

package org.onap.aai.introspection.sideeffect;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.util.*;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.onap.aai.edges.exceptions.AmbiguousRuleChoiceException;
import org.onap.aai.edges.exceptions.EdgeRuleNotFoundException;
import org.onap.aai.exceptions.AAIException;
import org.onap.aai.introspection.*;
import org.onap.aai.introspection.sideeffect.exceptions.AAIMissingRequiredPropertyException;
import org.onap.aai.schema.enums.PropertyMetadata;
import org.onap.aai.serialization.db.DBSerializer;
import org.onap.aai.serialization.engines.TransactionalGraphEngine;
import org.onap.aai.setup.SchemaVersions;

public abstract class SideEffect {

    protected static final Pattern template = Pattern.compile("\\{(.*?)\\}");
    private static final Logger logger = LoggerFactory.getLogger(SideEffect.class);

    protected final Introspector obj;
    protected final TransactionalGraphEngine dbEngine;
    protected final DBSerializer serializer;
    protected final Loader latestLoader;
    protected final Vertex self;

    protected Set<String> templateKeys = new HashSet<>();

    public SideEffect(Introspector obj, Vertex self, TransactionalGraphEngine dbEngine, DBSerializer serializer) {
        this.obj = obj;
        this.dbEngine = dbEngine;
        this.serializer = serializer;
        this.self = self;
        this.latestLoader = LoaderUtil.getLatestVersion();
    }

    protected void execute() throws UnsupportedEncodingException, URISyntaxException, AAIException {
        final Map<String, String> properties = this.findPopertiesWithMetadata(obj, this.getPropertyMetadata());
        for (Entry<String, String> entry : properties.entrySet()) {
            Optional<String> populatedUri = this.replaceTemplates(obj, entry.getValue());
            Optional<String> completeUri = this.resolveRelativePath(populatedUri);
            try {
                this.processURI(completeUri, entry);
            } catch (EdgeRuleNotFoundException | AmbiguousRuleChoiceException e) {
                logger.warn("Unable to execute the side effect {} due to ", e, this.getClass().getName());
            }
        }
    }

    protected Map<String, String> findPopertiesWithMetadata(Introspector obj, PropertyMetadata metadata) {
        final Map<String, String> result = new HashMap<>();
        for (String prop : obj.getProperties()) {
            final Map<PropertyMetadata, String> map = obj.getPropertyMetadata(prop);
            if (map.containsKey(metadata)) {
                result.put(prop, map.get(metadata));
            }
        }
        return result;
    }

    protected Map<String, String> findProperties(Introspector obj, String uriString)
            throws AAIMissingRequiredPropertyException {

        final Map<String, String> result = new HashMap<>();
        final Set<String> missing = new LinkedHashSet<>();
        Matcher m = template.matcher(uriString);
        int properties = 0;
        while (m.find()) {
            String propName = m.group(1);
            String value = obj.getValue(propName);
            properties++;
            if (value != null) {
                result.put(propName, value);
            } else {
                if (replaceWithWildcard()) {
                    result.put(propName, "*");
                }
                missing.add(propName);
            }
        }

        if (!missing.isEmpty() && (properties != missing.size())) {
            throw new AAIMissingRequiredPropertyException(
                    "Cannot complete " + this.getPropertyMetadata().toString() + " uri. Missing properties " + missing);
        }
        return result;
    }

    protected Optional<String> replaceTemplates(Introspector obj, String uriString)
            throws AAIMissingRequiredPropertyException {
        String result = uriString;
        final Map<String, String> propMap = this.findProperties(obj, uriString);
        if (propMap.isEmpty()) {
            return Optional.empty();
        }
        for (Entry<String, String> entry : propMap.entrySet()) {
            templateKeys.add(entry.getKey());
            result = result.replaceAll("\\{" + entry.getKey() + "\\}", entry.getValue());
        }
        // drop out wildcards if they exist
        result = result.replaceFirst("/[^/]+?(?:/\\*)+", "");
        return Optional.of(result);
    }

    private Optional<String> resolveRelativePath(Optional<String> populatedUri) throws UnsupportedEncodingException {
        if (!populatedUri.isPresent()) {
            return Optional.empty();
        } else {
            return Optional.of(populatedUri.get().replaceFirst("\\./", this.serializer.getURIForVertex(self) + "/"));
        }
    }

    protected abstract boolean replaceWithWildcard();

    protected abstract PropertyMetadata getPropertyMetadata();

    protected abstract void processURI(Optional<String> completeUri, Entry<String, String> entry)
            throws URISyntaxException, UnsupportedEncodingException, AAIException, EdgeRuleNotFoundException,
            AmbiguousRuleChoiceException;
}
