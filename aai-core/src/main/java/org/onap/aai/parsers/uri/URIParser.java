/**
 * ============LICENSE_START=======================================================
 * org.onap.aai
 * ================================================================================
 * Copyright © 2017-2018 AT&T Intellectual Property. All rights reserved.
 * Modification Copyright © 2019 IBM
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

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Set;

import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriBuilder;

import org.onap.aai.edges.enums.EdgeType;
import org.onap.aai.exceptions.AAIException;
import org.onap.aai.introspection.Introspector;
import org.onap.aai.introspection.Loader;
import org.onap.aai.introspection.exceptions.AAIUnknownObjectException;
import org.onap.aai.parsers.exceptions.DoesNotStartWithValidNamespaceException;
import org.onap.aai.rest.RestTokens;
import org.onap.aai.schema.enums.ObjectMetadata;
import org.springframework.web.util.UriUtils;

/**
 * The Class URIParser.
 */
public class URIParser {

    private URI uri = null;
    protected Loader loader = null;
    protected Loader originalLoader = null;
    private URI originalURI = null;
    private MultivaluedMap<String, String> queryParams = null;
    private static String aaiExceptionCode = "AAI_3001";

    /**
     * Instantiates a new URI parser.
     *
     * @param loader the loader
     * @param uri the uri
     */
    public URIParser(Loader loader, URI uri) {
        this.uri = uri;
        this.originalLoader = loader;
        // Load the latest version because we need it for cloud region
        this.loader = loader;
    }

    /**
     * Instantiates a new URI parser.
     *
     * @param loader the loader
     * @param uri the uri
     * @param queryParams the query params
     */
    public URIParser(Loader loader, URI uri, MultivaluedMap<String, String> queryParams) {
        this(loader, uri);
        this.queryParams = queryParams;
    }

    public Loader getLoader() {
        return this.loader;
    }

    /**
     * Gets the original URI.
     *
     * @return the original URI
     */
    public URI getOriginalURI() {
        return this.originalURI;
    }

    /**
     * Parses the.
     *
     * @param p the p
     * @throws UnsupportedEncodingException the unsupported encoding exception
     * @throws AAIException the AAI exception
     */
    public void parse(Parsable p) throws UnsupportedEncodingException, AAIException {
        try {
            boolean isRelative = uri.getRawPath().startsWith("./");
            uri = formatUri();
            if (p.useOriginalLoader()) {
                this.loader = this.originalLoader;
            }
            String[] parts = uri.getRawPath().split("/");
            Introspector previousObj = null;
            EdgeType type = EdgeType.TREE;
            for (int i = 0; i < parts.length;) {
                String part = parts[i];
                if (part.equals(RestTokens.COUSIN.toString())) {
                    boolean isPathInvalid = i == parts.length - 1;
                    if (isPathInvalid) {
                        throw new AAIException("AAI_3000",
                                uri + " not a valid path. Cannot end in " + RestTokens.COUSIN);
                    }
                    boolean isFinalContainer = i == parts.length - 2;
                    previousObj = parseCousin(p, parts[i + 1], previousObj, isFinalContainer);
                    type = EdgeType.ALL;
                    i += 2;
                    continue;
                }
                Introspector introspector = loader.introspectorFromName(part);
                validatePath(isRelative, previousObj, part, introspector);
                Set<String> keys = introspector.getKeys();
                if (keys.size() > 0) {
                    MultivaluedMap<String, String> uriKeys = new MultivaluedHashMap<>();
                    boolean isLastPart = i+1 == parts.length;
                    if (isLastPart && queryParams != null) {
                        uriKeys = queryParams;
                    } else {
                        for (String key : keys) {
                            String value = UriUtils.decode(parts[i+1], "UTF-8");
                            introspector.setValue(key, value);
                            // skip this for further processing
                            i++;
                        }
                    }

                    p.processObject(introspector, type, uriKeys);
                    type = EdgeType.TREE;
                } else if (introspector.isContainer()) {
                    boolean isFinalContainer = i == parts.length - 1;
                    MultivaluedMap<String, String> uriKeys = isFinalContainer && queryParams != null
                            ? queryParams
                            : new MultivaluedHashMap<>();
                    p.processContainer(introspector, type, uriKeys, isFinalContainer);
                } else {
                    p.processNamespace(introspector);
                }
                i++;
                previousObj = introspector;
            }
        } catch (AAIException e) {
            throw e;
        } catch (Exception e) {
            throw new AAIException(aaiExceptionCode, e);
        }
    }

    private void validatePath(boolean isRelative, Introspector previousObj,
            String part, Introspector introspector) throws AAIException, DoesNotStartWithValidNamespaceException {
        if (introspector == null) {
            throw new AAIException(aaiExceptionCode, "invalid item found in path: " + part);
        }
        // previous has current as property
        boolean isPathInvalid = previousObj != null && !previousObj.hasChild(introspector)
                && !previousObj.getDbName().equals("nodes");
        if (isPathInvalid) {
            throw new AAIException(aaiExceptionCode, uri + " not a valid path. " + part + " not valid");
        }
        if (previousObj == null) {
            // first time through, make sure it starts from a valid namespace
            // ignore abstract types
            String abstractType = introspector.getMetadata(ObjectMetadata.ABSTRACT);
            Introspector validNamespaces = loader.introspectorFromName("inventory");
            if (!isRelative && !"true".equals(abstractType) && !validNamespaces.hasChild(introspector)) {
                throw new DoesNotStartWithValidNamespaceException(
                        uri + " not a valid path. It does not start from a valid namespace");
            }
        }
    }

    private Introspector parseCousin(Parsable p, String name, Introspector previousObj, boolean isFinalContainer)
            throws AAIException, AAIUnknownObjectException {
        Introspector introspector;
        if (null == previousObj) {
            throw new AAIException(aaiExceptionCode);
        }
        introspector = loader.introspectorFromName(name);
        if (previousObj.isContainer() && introspector.isContainer()) {
            throw new AAIException("AAI_3000", uri + " not a valid path. Cannot chain plurals together");
        }

        if (introspector.isContainer()) {
            MultivaluedMap<String, String> uriKeys = isFinalContainer && queryParams != null
                    ? queryParams
                    : new MultivaluedHashMap<>();
            /*
             * Related-to could be COUSIN OR TREE and in some cases BOTH. So Let
             * EdgeRuleBuilder use all the
             * edgeTypes
             */
            p.processContainer(introspector, EdgeType.ALL, uriKeys, isFinalContainer);
        }
        return introspector;
    }

    public boolean validate() throws UnsupportedEncodingException, AAIException {
        this.parse(new URIValidate());
        return true;
    }

    /**
     * Trim URI.
     *
     * @param uri the uri
     * @return the uri
     */
    protected URI trimURI(URI uri) {
        String result = uri.getRawPath();
        if (result.startsWith("/")) {
            result = result.substring(1, result.length());
        }

        if (result.endsWith("/")) {
            result = result.substring(0, result.length() - 1);
        }

        // TODO - Check if this makes to do for model driven for base uri path
        result = result.replaceFirst("[a-z][a-z]*/v\\d+/", "");

        return UriBuilder.fromPath(result).build();
    }

    private URI formatUri() throws URISyntaxException {
        uri = this.trimURI(uri);
        this.originalURI = UriBuilder.fromPath(uri.getRawPath()).build();
        boolean isRelative = uri.getRawPath().startsWith("./");
        if (isRelative) {
            uri = new URI(uri.getRawPath().replaceFirst("\\./", ""));
        }
        return uri;
    }

}
