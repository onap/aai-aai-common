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

package org.onap.aai.introspection;

import java.util.Map;

import org.onap.aai.setup.SchemaVersion;
import org.springframework.stereotype.Component;

/**
 * Factory method that grants access to the globally loaded schema versions.
 * There is one {@link MoxyLoader} instance for each api version ({@link SchemaVersion}) that the AAI supports.
 */
@Component
public class LoaderFactory {

    private final Map<SchemaVersion, MoxyLoader> moxyLoaderInstance;

    public LoaderFactory(Map<SchemaVersion, MoxyLoader> moxyLoaderInstance) {
        this.moxyLoaderInstance = moxyLoaderInstance;
    }

    /**
     * Contrary to the naming, this method does not create a new loader,
     * but rather returns an existing loader instance
     */
    public Loader createLoaderForVersion(ModelType type, SchemaVersion version) {

        if (type.equals(ModelType.MOXY)) {
            return getMoxyLoaderInstance().get(version);
        }

        return null;
    }

    public Loader getLoaderStrategy(ModelType type, SchemaVersion version) {

        if (type.equals(ModelType.MOXY)) {
            return getMoxyLoaderInstance().get(version);
        }
        return null;
    }

    public Map<SchemaVersion, MoxyLoader> getMoxyLoaderInstance() {
        return moxyLoaderInstance;
    }
}
