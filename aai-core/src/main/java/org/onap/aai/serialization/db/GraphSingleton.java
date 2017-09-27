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

import com.thinkaurelius.titan.core.TitanGraph;
import org.onap.aai.dbmap.AAIGraph;
import org.onap.aai.dbmap.DBConnectionType;

import java.util.concurrent.atomic.AtomicInteger;

/* This class simply calls AAIGraph under the covers for now */
public class GraphSingleton {

	protected AtomicInteger totalCount = new AtomicInteger();
	
	private static class Helper {
		private static final GraphSingleton INSTANCE = new GraphSingleton();
	}
	
	/**
	 * Gets the single instance of GraphSingleton.
	 *
	 * @return single instance of GraphSingleton
	 */
	public static GraphSingleton getInstance() {
		return Helper.INSTANCE;

	}
	
	/**
	 * Gets the count.
	 *
	 * @return the count
	 */
	public AtomicInteger getCount() {
		return totalCount;
	}
	
	/**
	 * Gets the tx graph.
	 *
	 * @return the tx graph
	 */
	public TitanGraph getTxGraph() {
		return AAIGraph.getInstance().getGraph();
	}
	
	public TitanGraph getTxGraph(DBConnectionType connectionType) {
		return AAIGraph.getInstance().getGraph(connectionType);
	}
}
