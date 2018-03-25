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

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import static org.hamcrest.Matchers.endsWith;
import static org.junit.Assert.*;

public class VersionTest {

	@Test
	public void isLatest() throws Exception {
		assertTrue(Version.isLatest(Version.getVersion("latest")));
	}

	@Test
	public void getLatest() throws Exception {
		assertEquals(Version.getVersion("latest"), Version.getLatest());
	}

	@Test
	public void getVersionLatestString() throws Exception {
		assertEquals(Version.getLatest(), Version.getVersion("latest"));
	}

	@Test
	public void getVersion() throws Exception {
		assertEquals(Version.v13, Version.getVersion("v13"));
	}

}