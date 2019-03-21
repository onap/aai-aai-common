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

package org.onap.aai.introspection;

import org.onap.aai.restcore.MediaType;

public class MarshallerProperties {

    private final MediaType type;
    private final boolean includeRoot;
    private final boolean wrapperAsArrayName;
    private final boolean formatted;

    /**
     * Instantiates a new marshaller properties.
     *
     * @param builder the builder
     */
    private MarshallerProperties(Builder builder) {

        this.type = builder.type;
        this.includeRoot = builder.includeRoot;
        this.wrapperAsArrayName = builder.wrapperAsArrayName;
        this.formatted = builder.formatted;
    }

    /**
     * Gets the media type.
     *
     * @return the media type
     */
    public MediaType getMediaType() {
        return this.type;
    }

    /**
     * Gets the include root.
     *
     * @return the include root
     */
    public boolean getIncludeRoot() {
        return this.includeRoot;
    }

    /**
     * Gets the wrapper as array name.
     *
     * @return the wrapper as array name
     */
    public boolean getWrapperAsArrayName() {
        return this.wrapperAsArrayName;
    }

    /**
     * Gets the formatted.
     *
     * @return the formatted
     */
    public boolean getFormatted() {
        return this.formatted;
    }

    public static class Builder {

        private final MediaType type;
        private boolean includeRoot = false;
        private boolean wrapperAsArrayName = true;
        private boolean formatted = false;

        /**
         * Instantiates a new builder.
         *
         * @param type the type
         */
        public Builder(MediaType type) {
            this.type = type;
        }

        /**
         * Include root.
         *
         * @param includeRoot the include root
         * @return the builder
         */
        public Builder includeRoot(boolean includeRoot) {
            this.includeRoot = includeRoot;
            return this;
        }

        /**
         * Wrapper as array name.
         *
         * @param wrapperAsArrayName the wrapper as array name
         * @return the builder
         */
        public Builder wrapperAsArrayName(boolean wrapperAsArrayName) {
            this.wrapperAsArrayName = wrapperAsArrayName;
            return this;
        }

        /**
         * Formatted.
         *
         * @param formatted the formatted
         * @return the builder
         */
        public Builder formatted(boolean formatted) {
            this.formatted = formatted;
            return this;
        }

        /**
         * Builds the properties.
         *
         * @return the marshaller properties
         */
        public MarshallerProperties build() {
            return new MarshallerProperties(this);
        }
    }
}
