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

import org.onap.aai.validation.AAISchemaValidationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@PropertySource(value = "classpath:schema-ingest.properties", ignoreResourceNotFound = true)
@PropertySource(value = "file:${schema.ingest.file}", ignoreResourceNotFound = true)
public class SchemaVersions {

    protected List<SchemaVersion> versionsValue;
    protected SchemaVersion edgeLabelVersionValue;
    protected SchemaVersion defaultVersionValue;
    protected SchemaVersion depthVersionValue;
    protected SchemaVersion appRootVersionValue;
    protected SchemaVersion relatedLinkVersionValue;
    protected SchemaVersion namespaceChangeVersionValue;

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
