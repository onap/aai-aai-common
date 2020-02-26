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

package org.onap.aai.restcore.util;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;

import org.onap.aai.introspection.Introspector;
import org.onap.aai.introspection.sideeffect.exceptions.AAIMissingRequiredPropertyException;
import org.onap.aai.schema.enums.PropertyMetadata;
import org.springframework.web.util.UriUtils;

public class URITools {

    protected static final Pattern template = Pattern.compile("\\{(.*?)\\}");

    public static MultivaluedMap<String, String> getQueryMap(URI uri) {
        MultivaluedMap<String, String> result = new MultivaluedHashMap<>();
        String queryParams = uri.getRawQuery();
        if (queryParams != null) {
            String[] sections = queryParams.split("&");
            String[] query = null;
            String key, value = "";
            for (String section : sections) {
                query = section.split("=");
                key = UriUtils.decode(query[0], "UTF-8");
                if (query[1] != null) {
                    query[1] = query[1].replaceAll("\\+", "%20");
                }
                value = UriUtils.decode(query[1], "UTF-8");
                if (result.containsKey(key)) {
                    result.add(key, value);
                } else {
                    result.putSingle(key, value);
                }
            }
        }
        return result;

    }

    public static Optional<String> replaceTemplates(Introspector obj, String uriString, PropertyMetadata metadata,
            boolean replaceWithWildcard) throws AAIMissingRequiredPropertyException {
        String result = uriString;
        final Map<String, String> propMap = URITools.findProperties(obj, uriString, metadata, replaceWithWildcard);
        if (propMap.isEmpty()) {
            return Optional.empty();
        }
        for (Entry<String, String> entry : propMap.entrySet()) {
            result = result.replaceAll("\\{" + entry.getKey() + "\\}", entry.getValue());
        }
        // drop out wildcards if they exist
        result = result.replaceFirst("/[^/]+?(?:/\\*)+", "");
        return Optional.of(result);
    }

    private static Map<String, String> findProperties(Introspector obj, String uriString, PropertyMetadata metadata,
            boolean replaceWithWildcard) throws AAIMissingRequiredPropertyException {

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
                if (replaceWithWildcard) {
                    result.put(propName, "*");
                }
                missing.add(propName);
            }
        }

        if (!missing.isEmpty() && (properties != missing.size())) {
            throw new AAIMissingRequiredPropertyException(
                    "Cannot complete " + metadata.toString() + " uri. Missing properties " + missing);
        }
        return result;
    }

}
