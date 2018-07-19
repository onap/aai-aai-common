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

/**
 * Requires the order to be in ascending order
 */
public enum Version {
	v8,
	v9,
	v10,
	v11,
	v12,
	v13,
	v14;

	public static final String VERSION_EXPRESSION = "v8|v9|v10|v11|v12|v13|v14|latest";
	public static final String VERSION_EXPRESSION_V8_PLUS = "v8|v9|v10|v11|v12|v13|v14|latest";
	public static final String VERSION_EXPRESSION_V9_PLUS = "v9|v10|v11|v12|v13|v14|latest";

	/**
	 * Checks if v is the latest version
	 * @param v
	 * @return
	 */
	public static boolean isLatest(Version v) {
		return Version.getLatest().equals(v);
	}

	/**
	 * Gets the latest version
	 * @return
	 */
	public static Version getLatest(){
		return Version.values()[Version.values().length-1];
	}

	/**
	 * To be used inplace of <b>Version.getVersion(String)</b> to correctly get version of "latest"
	 * @param v
	 * @return
	 */
	public static Version getVersion(String v) {
		if ("latest".equals(v)) {
			return Version.getLatest();
		}
		return Version.valueOf(v);
	}

}
