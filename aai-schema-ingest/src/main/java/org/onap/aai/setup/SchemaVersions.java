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

import org.onap.aai.validation.AAISchemaValidationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
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

    protected String namespaceChangeStartVersion;
    protected List<SchemaVersion> versionsValue;
    protected SchemaVersion edgeLabelVersionValue;
    protected SchemaVersion defaultVersionValue;
    protected SchemaVersion depthVersionValue;
    protected SchemaVersion appRootVersionValue;
    protected SchemaVersion relatedLinkVersionValue;
    protected SchemaVersion namespaceChangeVersionValue;

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


    protected void validate() {
    	String errorMessage = "Invalid, edge label version is not in the api versions list"
                    + ", please check schema.version.list and ensure that the"
                    + " schema.version.edge.label.start is in that list";
        if (!versionsValue.contains(edgeLabelVersionValue)) {
            throw new AAISchemaValidationException(errorMessage);
        }

        if (!versionsValue.contains(defaultVersionValue)) {
            throw new AAISchemaValidationException(errorMessage);
        }

        if (!versionsValue.contains(depthVersionValue)) {
            throw new AAISchemaValidationException(errorMessage);
        }

        if (!versionsValue.contains(appRootVersionValue)) {
            throw new AAISchemaValidationException(errorMessage);
        }

        if (!versionsValue.contains(relatedLinkVersionValue)) {
            throw new AAISchemaValidationException(errorMessage);
        }

        if (!versionsValue.contains(namespaceChangeVersionValue)) {
            throw new AAISchemaValidationException(errorMessage);
        }
    }
    
    public List<SchemaVersion> getVersions() {
        return versionsValue;
    }

    public SchemaVersion getEdgeLabelVersion() {
        return edgeLabelVersionValue;
    }

    public SchemaVersion getDefaultVersion() {
        return defaultVersionValue;
    }

    public SchemaVersion getDepthVersion() {
        return depthVersionValue;
    }

    public SchemaVersion getAppRootVersion() {
        return appRootVersionValue;
    }

    public SchemaVersion getRelatedLinkVersion() {
        return relatedLinkVersionValue;
    }

    public SchemaVersion getNamespaceChangeVersion() {
        return namespaceChangeVersionValue;
    }

    public void setNamespaceChangeVersion(SchemaVersion namespaceChangeVersion) {
        this.namespaceChangeVersionValue = namespaceChangeVersion;
    }

}
