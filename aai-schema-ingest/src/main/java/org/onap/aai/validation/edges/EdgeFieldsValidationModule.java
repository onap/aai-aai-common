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

package org.onap.aai.validation.edges;

import java.util.Map;

/**
 * Verifies that a given raw edge rule contains all required fields.
 *
 */
public interface EdgeFieldsValidationModule {

    /**
     * Verifies the given rule has all required fields.
     * Implement to check for what you determine to be required.
     * You may also throw an error on unexpected fields if you wish,
     * whatever makes sense for your system.
     * 
     * @param rule - Map<String, String> that will look something like this:
     *        {
     *        "from": "foo",
     *        "to": "bar",
     *        "label": "tosca.relationships.network.BindsTo",
     *        "direction": "OUT",
     *        "multiplicity": "ONE2ONE",
     *        "contains-other-v": "NONE",
     *        "delete-other-v": "NONE",
     *        "prevent-delete": "NONE",
     *        "default": "true",
     *        "description":"An edge comment"
     *        }
     * @return empty String if no errors found, or String with
     *         the appropriate error message
     */
    public String verifyFields(Map<String, String> rule);
}
