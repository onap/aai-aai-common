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

package org.onap.aai.serialization.db;

/**
 * Possible values for deleteScope can be:
 * USE_DEFAULT - Get the scope from ref data for this node
 * THIS_NODE_ONLY (but should fail if it there are nodes that depend on it for uniqueness)
 * CASCADE_TO_CHILDREN - will look for OUT-Edges that have parentOf/hasDelTarget = true and follow
 * those down
 * ERROR_4_IN_EDGES_OR_CASCADE - combo of error-if-any-IN-edges + CascadeToChildren
 * ERROR_IF_ANY_IN_EDGES - Fail if this node has any existing IN edges
 * ERROR_IF_ANY_EDGES - Fail if this node has any existing edges at all!
 */
public enum DeleteSemantic {
    USE_DEFAULT, THIS_NODE_ONLY, CASCADE_TO_CHILDREN, ERROR_4_IN_EDGES_OR_CASCADE, ERROR_IF_ANY_IN_EDGES, ERROR_IF_ANY_EDGES,
}
