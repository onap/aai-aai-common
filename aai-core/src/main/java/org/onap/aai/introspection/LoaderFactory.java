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

public class LoaderFactory {

	/**
	 * Creates a new Loader object.
	 *
	 * @param type the type
	 * @param version the version
	 * @param llBuilder the ll builder
	 * @return the loader
	 */
	public static Loader createLoaderForVersion(ModelType type, Version version) {
		
		if (type.equals(ModelType.MOXY)) {
			return new MoxyLoader(version);
		} else if (type.equals(ModelType.POJO)) {
			return new PojoLoader(version);
		}
		
		return null;
		
	}
}
