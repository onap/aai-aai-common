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
package org.onap.aai.query.builder.optimization.tests;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.onap.aai.query.builder.optimization.AbstractGraphTraversalBuilderTestQueryiesToRun;

@Ignore
public class AOneTenantOneVserversPerTenantTest extends AbstractGraphTraversalBuilderTestQueryiesToRun {

	private static int tenantNum = 1;
	private static int vserverPerTenantNum = 1;
	private static String prefix = AOneTenantOneVserversPerTenantTest.class.getSimpleName() + "-";

	@BeforeClass
	public void setup() throws Exception {
		setupData(tenantNum,vserverPerTenantNum, prefix);
	}

	@Override
	protected int getTenantNum() {
		return tenantNum;
	}

	@Override
	protected int getVserverNumPerTenant() {
		return vserverPerTenantNum;
	}

	@Override
	protected String getPrefix() {
		return prefix;
	}

}
