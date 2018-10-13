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
package org.onap.aai.dbmap;

import org.janusgraph.core.JanusGraphFactory;
import org.janusgraph.core.JanusGraph;
import org.janusgraph.core.schema.JanusGraphManagement;
import org.junit.*;
import org.onap.aai.AAISetup;
import org.onap.aai.util.AAIConstants;


import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.Matchers.matchesPattern;
import static org.junit.Assert.*;

import java.io.FileNotFoundException;

public class AAIGraphTest extends AAISetup{
	@Before
	public void setup() {
		AAIGraph.getInstance();
	}

	@Test
	public void getRealtimeInstanceConnectionName() throws Exception {

		JanusGraphManagement graphMgt = AAIGraph.getInstance().getGraph().openManagement();
		String connectionInstanceName = graphMgt.getOpenInstances().stream().filter(c -> c.contains("current")).findFirst().get();
		assertThat(connectionInstanceName, containsString(SERVICE_NAME));
		assertThat(connectionInstanceName, containsString("realtime"));
		assertThat(connectionInstanceName, matchesPattern("^\\d+_[\\w\\-\\d]+_" + SERVICE_NAME + "_realtime_\\d+\\(current\\)$"));
		graphMgt.rollback();
	}

	@Test
	public void getCachedInstanceConnectionName() throws Exception {

		JanusGraphManagement graphMgt = AAIGraph.getInstance().getGraph(DBConnectionType.CACHED).openManagement();
		String connectionInstanceName = graphMgt.getOpenInstances().stream().filter(c -> c.contains("current")).findFirst().get();
		assertThat(connectionInstanceName, containsString(SERVICE_NAME));
		assertThat(connectionInstanceName, containsString("cached"));
		assertThat(connectionInstanceName, matchesPattern("^\\d+_[\\w\\-\\d]+_" + SERVICE_NAME + "_cached_\\d+\\(current\\)$"));
		graphMgt.rollback();
	}

	@Test
	public void JanusGraphOpenNameTest() throws Exception{
		JanusGraph graph = JanusGraphFactory.open(new AAIGraphConfig.Builder(AAIConstants.REALTIME_DB_CONFIG).forService(SERVICE_NAME).withGraphType("graphType").buildConfiguration());
		JanusGraphManagement graphMgt = graph.openManagement();
		String connectionInstanceName = graphMgt.getOpenInstances().stream().filter(c -> c.contains("current")).findFirst().get();
		assertThat(connectionInstanceName,matchesPattern("^\\d+_[\\w\\-\\d]+_" + SERVICE_NAME + "_graphType_\\d+\\(current\\)$"));
		graphMgt.rollback();
		graph.close();
	}
	
	@Test (expected=FileNotFoundException.class)
	public void JanusGraphOpenNameWithInvalidFilePathTest() throws Exception{
		JanusGraph graph = JanusGraphFactory.open(new AAIGraphConfig.Builder("invalid").forService(SERVICE_NAME).withGraphType("graphType").buildConfiguration());
		JanusGraphManagement graphMgt = graph.openManagement();
		String connectionInstanceName = graphMgt.getOpenInstances().stream().filter(c -> c.contains("current")).findFirst().get();
		assertThat(connectionInstanceName,matchesPattern("^\\d+_[\\w\\-\\d]+_" + SERVICE_NAME + "_graphType_\\d+\\(current\\)$"));
		graphMgt.rollback();
		graph.close();
	}

}
