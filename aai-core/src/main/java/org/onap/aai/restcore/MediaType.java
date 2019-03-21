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

package org.onap.aai.restcore;

/**
 * The Enum MediaType.
 */
public enum MediaType {
    APPLICATION_JSON_TYPE("application/json"), APPLICATION_XML_TYPE("application/xml");

    private final String text;

    /**
     * Instantiates a new media type.
     *
     * @param text the text
     */
    private MediaType(final String text) {
        this.text = text;
    }

    /**
     * Gets the enum.
     *
     * @param value the value
     * @return the enum
     */
    public static MediaType getEnum(String value) {

        for (MediaType v : values()) {
            if (v.toString().equalsIgnoreCase(value)) {
                return v;
            }
        }

        throw new IllegalArgumentException("bad value: " + value);

    }

    /**
     * @{inheritDoc}
     */
    @Override
    public String toString() {
        return text;
    }
}
