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

package org.onap.aai.parsers.uri;

import com.google.common.base.Joiner;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.MultivaluedMap;

import org.onap.aai.edges.enums.EdgeType;
import org.onap.aai.exceptions.AAIException;
import org.onap.aai.introspection.Introspector;
import org.onap.aai.introspection.Loader;

/**
 * Creates a Unique database key from a URI
 * 
 * The key is of the form node-type/key(s).
 */
public class URIToDBKey implements Parsable {

    private List<String> dbKeys = new ArrayList<>();

    /**
     * Instantiates a new URI to DB key.
     *
     * @param loader the loader
     * @param uri the uri
     * @throws IllegalArgumentException the illegal argument exception
     * @throws AAIException the AAI exception
     * @throws UnsupportedEncodingException the unsupported encoding exception
     */
    public URIToDBKey(Loader loader, URI uri)
            throws IllegalArgumentException, AAIException, UnsupportedEncodingException {
        URIParser parser = new URIParser(loader, uri);
        parser.parse(this);
    }

    /**
     * @{inheritDoc}
     */
    @Override
    public void processNamespace(Introspector obj) {

    }

    /**
     * @{inheritDoc}
     */
    @Override
    public String getCloudRegionTransform() {
        return "add";
    }

    /**
     * Gets the result.
     *
     * @return the result
     */
    public Object getResult() {
        return Joiner.on("/").join(this.dbKeys);
    }

    /**
     * @{inheritDoc}
     */
    @Override
    public boolean useOriginalLoader() {
        return false;
    }

    @Override
    public void processObject(Introspector obj, EdgeType type, MultivaluedMap<String, String> uriKeys)
            throws AAIException {

        dbKeys.add(obj.getDbName());

        for (String key : uriKeys.keySet()) {
            dbKeys.add(uriKeys.getFirst(key).toString());
        }
    }

    @Override
    public void processContainer(Introspector obj, EdgeType type, MultivaluedMap<String, String> uriKeys,
            boolean isFinalContainer) {
    }
}
