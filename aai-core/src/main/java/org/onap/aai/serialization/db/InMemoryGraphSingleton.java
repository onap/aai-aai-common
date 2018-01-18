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
package org.onap.aai.serialization.db;

import org.onap.aai.dbmap.DBConnectionType;

import com.thinkaurelius.titan.core.TitanGraph;

public class InMemoryGraphSingleton extends GraphSingleton {

	private static TitanGraph inMemgraph;

	private static class Helper {
		private static final InMemoryGraphSingleton INSTANCE = new InMemoryGraphSingleton();
	}

	/**
	 * Gets the single instance of TitanGraphSingleton.
	 *
	 * @return single instance of TitanGraphSingleton
	 */
	public static InMemoryGraphSingleton getInstance(TitanGraph graph) {
		inMemgraph = graph;
		return Helper.INSTANCE;
	}

	/**
	 * Gets the tx graph.
	 *
	 * @return the tx graph
	 */
	@Override
	public TitanGraph getTxGraph() {
		return inMemgraph;
	}

	@Override
	public TitanGraph getTxGraph(DBConnectionType connectionType) {
		return inMemgraph;
	}
}
