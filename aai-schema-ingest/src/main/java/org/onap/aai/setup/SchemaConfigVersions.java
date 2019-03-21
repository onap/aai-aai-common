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

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component("schemaConfigVersions")
@ConditionalOnExpression("'${schema.translator.list:config}'.contains('config') || '${schema.service.versions.override:false}'.equals('true')")
@PropertySource(value = "classpath:schema-ingest.properties", ignoreResourceNotFound = true)
@PropertySource(value = "file:${schema.ingest.file}", ignoreResourceNotFound = true)
public class SchemaConfigVersions extends SchemaVersions {

    @Value("#{'${schema.version.list:''}'.split(',')}")
    private List<String> apiVersions;
    @Value("${schema.version.api.default}")
    private String defaultApiVersion;
    @Value("${schema.version.edge.label.start:''}")
    private String edgeLabelStartVersion;
    @Value("${schema.version.depth.start:''}")
    private String depthStartVersion;
    @Value("${schema.version.app.root.start:''}")
    private String appRootStartVersion;
    @Value("${schema.version.related.link.start:''}")
    private String relatedLinkStartVersion;
    @Value("${schema.version.namespace.change.start:''}")
    protected String namespaceChangeStartVersion;

    public List<String> getApiVersions() {
        return apiVersions;
    }

    public String getDefaultApiVersion() {
        return defaultApiVersion;
    }

    public String getEdgeLabelStartVersion() {
        return edgeLabelStartVersion;
    }

    public String getDepthStartVersion() {
        return depthStartVersion;
    }

    public String getAppRootStartVersion() {
        return appRootStartVersion;
    }

    public String getRelatedLinkStartVersion() {
        return relatedLinkStartVersion;
    }

    public String getNamespaceChangeStartVersion() {
        return namespaceChangeStartVersion;
    }

    @PostConstruct
    public void initialize() {
        versionsValue = apiVersions.stream().map(SchemaVersion::new).collect(Collectors.toList());
        edgeLabelVersionValue = new SchemaVersion(edgeLabelStartVersion);
        defaultVersionValue = new SchemaVersion(defaultApiVersion);
        depthVersionValue = new SchemaVersion(depthStartVersion);
        appRootVersionValue = new SchemaVersion(appRootStartVersion);
        relatedLinkVersionValue = new SchemaVersion(relatedLinkStartVersion);
        namespaceChangeVersionValue = new SchemaVersion(namespaceChangeStartVersion);
        this.validate();
    }

}
