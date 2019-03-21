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

package org.onap.aai.edges;

import com.jayway.jsonpath.Filter;

import java.util.Objects;

import org.onap.aai.setup.SchemaVersion;

public class SchemaFilter {

    private String filter;

    private SchemaVersion schemaVersion;

    public SchemaFilter(Filter filter, SchemaVersion schemaVersion) {
        if (filter != null) {
            this.filter = filter.toString();
        }
        this.schemaVersion = schemaVersion;
    }

    public SchemaVersion getSchemaVersion() {
        return schemaVersion;
    }

    public String getFilter() {
        return filter;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        SchemaFilter that = (SchemaFilter) o;
        return Objects.equals(filter, that.filter) && Objects.equals(schemaVersion, that.schemaVersion);
    }

    @Override
    public int hashCode() {
        return Objects.hash(filter, schemaVersion);
    }

    @Override
    public String toString() {
        return "SchemaFilter{" + "filter='" + filter + '\'' + ", schemaVersion=" + schemaVersion + '}';
    }
}
