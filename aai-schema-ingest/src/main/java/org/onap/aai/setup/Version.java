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
package org.onap.aai.setup;

public enum Version {
	V8,
	V9,
	V10,
	V11,
	V12,
	V13;
	
	public static boolean isLatest(Version v) {
		return getLatest().equals(v);
	}
	
	public static Version getLatest(){
		Version[] vals = values(); //guaranteed to be in declaration order
		return vals[vals.length-1]; //requires we always have the latest version listed last
	}
}
