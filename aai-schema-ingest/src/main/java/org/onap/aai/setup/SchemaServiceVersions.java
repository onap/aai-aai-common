/**
 * ============LICENSE_START=======================================================
 * org.onap.aai
 * ================================================================================
 * Copyright © 2017-18 AT&T Intellectual Property. All rights reserved.
 * ================================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ============LICENSE_END=========================================================
 */

package org.onap.aai.setup;

import java.util.List;
import java.util.stream.Collectors;

import jakarta.annotation.PostConstruct;

import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component("schemaServiceVersions")
@ConditionalOnExpression("'${schema.translator.list}'.contains('schema-service')")
@PropertySource(value = "classpath:schema-ingest.properties", ignoreResourceNotFound = true)
@PropertySource(value = "file:${schema.ingest.file}", ignoreResourceNotFound = true)
public class SchemaServiceVersions extends SchemaVersions {
    private List<String> versions;
    private String edgeVersion;
    private String defaultVersion;
    private String depthVersion;
    private String appRootVersion;
    private String relatedLinkVersion;
    private String namespaceChangeVersion;

    public List<String> getVersionsAll() {
        return versions;
    }

    public void setVersions(List<String> versions) {
        this.versions = versions;
    }

    @PostConstruct
    public void initializeFromSchemaService() throws ExceptionInInitializerError {

        versionsValue = versions.stream().map(SchemaVersion::new).collect(Collectors.toList());
        edgeLabelVersionValue = new SchemaVersion(edgeVersion);
        defaultVersionValue = new SchemaVersion(defaultVersion);
        depthVersionValue = new SchemaVersion(depthVersion);
        appRootVersionValue = new SchemaVersion(appRootVersion);
        relatedLinkVersionValue = new SchemaVersion(relatedLinkVersion);
        namespaceChangeVersionValue = new SchemaVersion(namespaceChangeVersion);

        this.validate();
    }

    /*
     * TODO Change Method names
     */
    public void initializeFromSchemaConfig(SchemaConfigVersions schemaConfigVersion)
            throws ExceptionInInitializerError {

        versions = schemaConfigVersion.getApiVersions();
        appRootVersion = schemaConfigVersion.getAppRootStartVersion();
        defaultVersion = schemaConfigVersion.getDefaultApiVersion();
        depthVersion = schemaConfigVersion.getDepthStartVersion();
        edgeVersion = schemaConfigVersion.getEdgeLabelStartVersion();
        namespaceChangeVersion = schemaConfigVersion.getNamespaceChangeStartVersion();
        relatedLinkVersion = schemaConfigVersion.getRelatedLinkStartVersion();
        this.initializeFromSchemaService();
    }

}
