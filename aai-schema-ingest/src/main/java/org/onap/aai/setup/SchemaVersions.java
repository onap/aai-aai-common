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
package org.onap.aai.setup;

import org.onap.aai.validation.AAISchemaValidationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.xml.validation.Schema;
import java.util.List;
import java.util.stream.Collectors;

@Component
@PropertySource(value = "classpath:schema-ingest.properties", ignoreResourceNotFound = true)
@PropertySource(value = "file:${schema.ingest.file}", ignoreResourceNotFound = true)
public class SchemaVersions {

    @Value("#{'${schema.version.list}'.split(',')}")
    private List<String> apiVersions;

    @Value("${schema.version.api.default}")
    private String defaultApiVersion;

    @Value("${schema.version.edge.label.start}")
    private String edgeLabelStartVersion;

    @Value("${schema.version.depth.start}")
    private String depthStartVersion;

    @Value("${schema.version.app.root.start}")
    private String appRootStartVersion;

    @Value("${schema.version.related.link.start}")
    private String relatedLinkStartVersion;

    @Value("${schema.version.namespace.change.start}")
    private String namespaceChangeStartVersion;

    private List<SchemaVersion> versions;

    private SchemaVersion edgeLabelVersion;
    private SchemaVersion defaultVersion;
    private SchemaVersion depthVersion;
    private SchemaVersion appRootVersion;
    private SchemaVersion relatedLinkVersion;
    private SchemaVersion namespaceChangeVersion;

    @PostConstruct
    public void initialize() {
        versions = apiVersions.stream().map(SchemaVersion::new).collect(Collectors.toList());

        edgeLabelVersion       = new SchemaVersion(edgeLabelStartVersion);
        defaultVersion         = new SchemaVersion(defaultApiVersion);
        depthVersion           = new SchemaVersion(depthStartVersion);
        appRootVersion         = new SchemaVersion(appRootStartVersion);
        relatedLinkVersion     = new SchemaVersion(relatedLinkStartVersion);
        namespaceChangeVersion = new SchemaVersion(namespaceChangeStartVersion);

        if (!versions.contains(edgeLabelVersion)) {
            throw new AAISchemaValidationException(
                    "Invalid, edge label version is not in the api versions list"
                    + ", please check schema.version.list and ensure that the"
                    + " schema.version.edge.label.start is in that list"
            );
        }

        if (!versions.contains(defaultVersion)) {
            throw new AAISchemaValidationException(
                    "Invalid, default version is not in the api versions list"
                            + ", please check schema.version.list and ensure that the"
                            + " schema.version.api.default is in that list"
            );
        }

        if (!versions.contains(depthVersion)) {
            throw new AAISchemaValidationException(
                    "Invalid, depth version is not in the api versions list"
                            + ", please check schema.version.list and ensure that the"
                            + " schema.version.depth.start is in that list"
            );
        }

        if(!versions.contains(appRootVersion)){
            throw new AAISchemaValidationException(
                    "Invalid, app root version is not in the api versions list"
                            + ", please check schema.version.list and ensure that the"
                            + " schema.version.app.root.start is in that list"
            );
        }

        if(!versions.contains(relatedLinkVersion)){
            throw new AAISchemaValidationException(
                    "Invalid, related link version is not in the api versions list"
                            + ", please check schema.version.list and ensure that the"
                            + " schema.version.related.link.start is in that list"
            );
        }

        if(!versions.contains(namespaceChangeVersion)){
            throw new AAISchemaValidationException(
                    "Invalid, namespace change start version is not in the api versions list"
                            + ", please check schema.version.list and ensure that the"
                            + " schema.version.related.link.start is in that list"
            );
        }
    }

    public List<SchemaVersion> getVersions() {
        return versions;
    }

    public SchemaVersion getEdgeLabelVersion() {
        return edgeLabelVersion;
    }

    public SchemaVersion getDefaultVersion() {
        return defaultVersion;
    }

    public SchemaVersion getDepthVersion() {
        return depthVersion;
    }

    public SchemaVersion getAppRootVersion(){
        return appRootVersion;
    }

    public SchemaVersion getRelatedLinkVersion(){
        return relatedLinkVersion;
    }

    public SchemaVersion getNamespaceChangeVersion() {
        return namespaceChangeVersion;
    }

    public void setNamespaceChangeVersion(SchemaVersion namespaceChangeVersion) {
        this.namespaceChangeVersion = namespaceChangeVersion;
    }

}
