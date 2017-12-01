/*-
 * ============LICENSE_START=======================================================
 * org.onap.aai
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

package org.onap.aai.serialization.db;

public enum EdgeProperty {
	CONTAINS("contains-other-v"),
	DELETE_OTHER_V("delete-other-v"),
	SVC_INFRA("SVC-INFRA"),
	PREVENT_DELETE("prevent-delete");
	private final String name;

	private EdgeProperty(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return name;
	}
}
