/*-
 * ============LICENSE_START=======================================================
 * org.openecomp.aai
 * ================================================================================
 * Copyright (C) 2017 AT&T Intellectual Property. All rights reserved.
 * ================================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ============LICENSE_END=========================================================
 */

package org.openecomp.aai.db.props;

import org.openecomp.aai.introspection.Version;

public class AAIProperties {
	public static final String NODE_TYPE = "aai-node-type";
	public static final String LAST_MOD_SOURCE_OF_TRUTH = "last-mod-source-of-truth";
	public static final String SOURCE_OF_TRUTH = "source-of-truth";
	public static final String LAST_MOD_TS = "aai-last-mod-ts";
	public static final String UNIQUE_KEY = "aai-unique-key";
	public static final String CREATED_TS = "aai-created-ts";
	public static final String RESOURCE_VERSION = "resource-version";
	public static final String AAI_URI = "aai-uri";
	public static final Version LATEST = Version.v11;
	public static final Integer MAXIMUM_DEPTH = 10000;
	public static final String LINKED = "linked";
	public static final String DB_ALIAS_SUFFIX = "-local";

}
