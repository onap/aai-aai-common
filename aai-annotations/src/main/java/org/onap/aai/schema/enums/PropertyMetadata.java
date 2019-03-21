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

package org.onap.aai.schema.enums;

public enum PropertyMetadata {

    /**
     * description of property
     */
    DESCRIPTION("description"),
    /**
     * default value of property
     */
    DEFAULT_VALUE("defaultValue"),
    /**
     * sets the property name used when writing to the db
     */
    DB_ALIAS("dbAlias"),
    /**
     * a URI which describes the location of a value to be
     * written on a PUT
     */
    DATA_COPY("dataCopy"),
    /**
     * a URI which describes another object which backs this value
     */
    DATA_LINK("dataLink"),
    /**
     * controls the visibility of a field based on context
     */
    VISIBILITY("visibility"),
    /**
     * specifies a field which must be populated to pass validation
     */
    REQUIRES("requires"),
    /**
     * automatically creates an id for the property if not specified
     */
    AUTO_GENERATE_UUID("autoGenerateUuid"),
    /**
     * Property is used for VNF searches in AAI UI
     */
    SUGGESTIBLE_ON_SEARCH("suggestibleOnSearch"),
    /**
     * An identifier that indicates which system is the master of this property data
     */
    SOURCE_OF_TRUTH_TYPE("sourceOfTruthType"),
    /**
     * makes property read only by version
     */
    READ_ONLY("readOnly"),
    /**
     * Add a private edge between two objects based on the uri provided by user which should provide
     * the appropriate information from the url similar to dataCopy
     */
    PRIVATE_EDGE("privateEdge");

    private final String name;

    private PropertyMetadata(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
