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
package org.onap.aai.util;

import java.io.UnsupportedEncodingException;
import org.springframework.web.util.UriUtils;


public class RestURLEncoder {

	
	/**
	 * Encode URL.
	 *
	 * @param nodeKey the node key
	 * @return the string
	 * @throws UnsupportedEncodingException the unsupported encoding exception
	 */
	public static final String encodeURL (String nodeKey) throws UnsupportedEncodingException {
		return UriUtils.encode(nodeKey, "UTF-8").replaceAll("\\+", "%20");
	}
}

