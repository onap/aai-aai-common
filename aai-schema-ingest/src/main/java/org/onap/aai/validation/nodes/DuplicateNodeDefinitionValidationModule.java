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
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ============LICENSE_END=========================================================
 */

package org.onap.aai.validation.nodes;

import java.util.List;

import org.onap.aai.setup.SchemaVersion;

/**
 * Defines rules for duplicate node definitions in a set of files
 * (where the intent is the set of files is all the OXM for one version).
 * 
 * Example Options:
 * -Any duplicated definition found is an error
 * -Duplicates within a namespace are OK but not across namespaces
 * -Anything goes
 * etc.
 */
public interface DuplicateNodeDefinitionValidationModule {
    /**
     * Finds any duplicates according to the defined rules
     * 
     * @param files - the OXM files to use with full directory
     * @return empty String if none found, else a String
     *         with appropriate information about what node types
     *         were found
     */
    String findDuplicates(List<String> files, SchemaVersion v);
}
