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
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ============LICENSE_END=========================================================
 */

package org.onap.aai.serialization.queryformats.utils;

import java.io.UnsupportedEncodingException;
import java.net.URI;

import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.onap.aai.exceptions.AAIException;
import org.onap.aai.serialization.db.DBSerializer;
import org.onap.aai.serialization.queryformats.exceptions.AAIFormatVertexException;
import org.onap.aai.setup.SchemaVersion;
import org.onap.aai.setup.SchemaVersions;
import org.onap.aai.util.AAIConfig;
import org.onap.aai.util.AAIConstants;

public class UrlBuilder {

    private final DBSerializer serializer;
    private final SchemaVersion version;
    private final String serverBase;
    private final SchemaVersions schemaVersions;
    private final String basePath;

    public UrlBuilder(SchemaVersion version, DBSerializer serializer, SchemaVersions schemaVersions,
        String basePath) throws AAIException {
        this.serializer = serializer;
        this.version = version;
        this.serverBase = this.getServerBase();
        this.schemaVersions = schemaVersions;
        if (!basePath.endsWith("/")) {
            this.basePath = basePath + "/";
        } else {
            this.basePath = basePath;
        }
    }

    public UrlBuilder(SchemaVersion version, DBSerializer serializer, String serverBase,
        SchemaVersions schemaVersions, String basePath) {
        this.serializer = serializer;
        this.version = version;
        this.serverBase = serverBase;
        this.schemaVersions = schemaVersions;
        if (!basePath.endsWith("/")) {
            this.basePath = basePath + "/";
        } else {
            this.basePath = basePath;
        }
    }

    public String pathed(Vertex v) throws AAIFormatVertexException {

        try {
            final StringBuilder result = new StringBuilder();
            final URI uri = this.serializer.getURIForVertex(v);

            if (this.version.compareTo(schemaVersions.getAppRootVersion()) >= 0) {
                result.append(basePath);
            } else {
                result.append(this.serverBase);
            }
            result.append(this.version);
            result.append(uri.getRawPath());

            return result.toString();
        } catch (UnsupportedEncodingException | IllegalArgumentException | SecurityException e) {
            throw new AAIFormatVertexException(e);
        }
    }

    public String id(Vertex v) {
        final StringBuilder result = new StringBuilder();

        result.append("/resources/id/" + v.id());
        result.insert(0, this.version);
        if (this.version.compareTo(schemaVersions.getAppRootVersion()) >= 0) {
            result.insert(0, basePath);
        } else {
            result.insert(0, this.serverBase);
        }

        return result.toString();
    }

    protected String getServerBase() throws AAIException {
        return AAIConfig.get(AAIConstants.AAI_SERVER_URL_BASE);
    }
}
