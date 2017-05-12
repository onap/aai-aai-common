/*-
 * ============LICENSE_START=======================================================
 * org.openecomp.aai
 * ================================================================================
 * Copyright (C) 2017 AT&T Intellectual Property. All rights reserved.
 * ================================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ============LICENSE_END=========================================================
 */

package org.openecomp.aai.db.schema;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

import org.apache.tinkerpop.gremlin.structure.Vertex;

import com.thinkaurelius.titan.core.EdgeLabel;
import com.thinkaurelius.titan.core.PropertyKey;
import com.thinkaurelius.titan.core.TitanGraph;
import com.thinkaurelius.titan.core.schema.TitanGraphIndex;
import com.thinkaurelius.titan.core.schema.TitanManagement;

public class AuditTitan extends Auditor {

	private final TitanGraph graph;
	
	/**
	 * Instantiates a new audit titan.
	 *
	 * @param g the g
	 */
	public AuditTitan (TitanGraph g) {
		this.graph = g;
		buildSchema();
	}
	
	/**
	 * Builds the schema.
	 */
	private void buildSchema() {
		populateProperties();
		populateIndexes();
		populateEdgeLabels();
	}
	
	/**
	 * Populate properties.
	 */
	private void populateProperties() {
		TitanManagement mgmt = graph.openManagement();
		Iterable<PropertyKey> iterable = mgmt.getRelationTypes(PropertyKey.class);
		Iterator<PropertyKey> titanProperties = iterable.iterator();
		PropertyKey propKey = null;
		while (titanProperties.hasNext()) {
			propKey = titanProperties.next();
			DBProperty prop = new DBProperty();
			
			prop.setName(propKey.name());
			prop.setCardinality(propKey.cardinality());
			prop.setTypeClass(propKey.dataType());
			
			this.properties.put(prop.getName(), prop);
		}	
	}
	
	/**
	 * Populate indexes.
	 */
	private void populateIndexes() {
		TitanManagement mgmt = graph.openManagement();
		Iterable<TitanGraphIndex> iterable = mgmt.getGraphIndexes(Vertex.class);
		Iterator<TitanGraphIndex> titanIndexes = iterable.iterator();
		TitanGraphIndex titanIndex = null;
		while (titanIndexes.hasNext()) {
			titanIndex = titanIndexes.next();
			if (titanIndex.isCompositeIndex()) {
				DBIndex index = new DBIndex();
				LinkedHashSet<DBProperty> dbProperties = new LinkedHashSet<>();
				index.setName(titanIndex.name());
				index.setUnique(titanIndex.isUnique());
				PropertyKey[] keys = titanIndex.getFieldKeys();
				for (PropertyKey key : keys) {
					dbProperties.add(this.properties.get(key.name()));
				}
				index.setProperties(dbProperties);
				index.setStatus(titanIndex.getIndexStatus(keys[0]));
				this.indexes.put(index.getName(), index);
			}
		}	
	}
	
	/**
	 * Populate edge labels.
	 */
	private void populateEdgeLabels() {
		TitanManagement mgmt = graph.openManagement();
		Iterable<EdgeLabel> iterable = mgmt.getRelationTypes(EdgeLabel.class);
		Iterator<EdgeLabel> titanEdgeLabels = iterable.iterator();
		EdgeLabel edgeLabel = null;
		while (titanEdgeLabels.hasNext()) {
			edgeLabel = titanEdgeLabels.next();
			EdgeProperty edgeProperty = new EdgeProperty();
			
			edgeProperty.setName(edgeLabel.name());
			edgeProperty.setMultiplicity(edgeLabel.multiplicity());
			
			this.edgeLabels.put(edgeProperty.getName(), edgeProperty);
		}	
	}
	
}
