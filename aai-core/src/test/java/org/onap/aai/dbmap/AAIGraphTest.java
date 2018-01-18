/**
 * ============LICENSE_START=======================================================
 * org.onap.aai
 * ================================================================================
 * Copyright © 2017 AT&T Intellectual Property. All rights reserved.
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
 *
 * ECOMP is a trademark and service mark of AT&T Intellectual Property.
 */

package org.onap.aai.dbmap;

import com.thinkaurelius.titan.core.TitanFactory;
import com.thinkaurelius.titan.core.TitanGraph;
import com.thinkaurelius.titan.core.schema.TitanManagement;
import org.hamcrest.CoreMatchers;
import org.junit.*;
import org.onap.aai.AAISetup;
import org.onap.aai.util.AAIConstants;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.Matchers.matchesPattern;
import static org.junit.Assert.*;

public class AAIGraphTest extends AAISetup{

	private static final String SERVICE_NAME = "JUNIT";

	@Before
	public void setup() {
		System.setProperty("aai.service.name", SERVICE_NAME);
		AAIGraph.getInstance();
	}

	@Test
	public void getRealtimeInstanceConnectionName() throws Exception {

		TitanManagement graphMgt = AAIGraph.getInstance().getGraph().openManagement();
		String connectionInstanceName = graphMgt.getOpenInstances().stream().filter(c -> c.contains("current")).findFirst().get();
		assertThat(connectionInstanceName, containsString(SERVICE_NAME));
		assertThat(connectionInstanceName, containsString("realtime"));
		assertThat(connectionInstanceName, matchesPattern("^\\d+_[\\w\\d]+_" + SERVICE_NAME + "_realtime_\\d+\\(current\\)$"));
		graphMgt.rollback();
	}

	@Test
	public void getCachedInstanceConnectionName() throws Exception {

		TitanManagement graphMgt = AAIGraph.getInstance().getGraph(DBConnectionType.CACHED).openManagement();
		String connectionInstanceName = graphMgt.getOpenInstances().stream().filter(c -> c.contains("current")).findFirst().get();
		assertThat(connectionInstanceName, containsString(SERVICE_NAME));
		assertThat(connectionInstanceName, containsString("cached"));
		assertThat(connectionInstanceName, matchesPattern("^\\d+_[\\w\\d]+_" + SERVICE_NAME + "_cached_\\d+\\(current\\)$"));
		graphMgt.rollback();
	}

	@Test
	public void titanGraphOpenNameTest() throws Exception{
		TitanGraph graph = TitanFactory.open(new AAIGraphConfig.Builder(AAIConstants.REALTIME_DB_CONFIG).forService(SERVICE_NAME).withGraphType("graphType").buildConfiguration());
		TitanManagement graphMgt = graph.openManagement();
		String connectionInstanceName = graphMgt.getOpenInstances().stream().filter(c -> c.contains("current")).findFirst().get();
		assertThat(connectionInstanceName,matchesPattern("^\\d+_[\\w\\d]+_" + SERVICE_NAME + "_graphType_\\d+\\(current\\)$"));
		graphMgt.rollback();
		graph.close();
	}

}