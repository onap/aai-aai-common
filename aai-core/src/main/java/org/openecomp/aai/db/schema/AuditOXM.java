/*-
 * ============LICENSE_START=======================================================
 * org.openecomp.aai
 * ================================================================================
 * Copyright (C) 2017 AT&T Intellectual Property. All rights reserved.
 * ================================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 * ============LICENSE_END=========================================================
 */

package org.openecomp.aai.db.schema;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import org.openecomp.aai.db.props.AAIProperties;
import org.openecomp.aai.dbmodel.DbEdgeRules;
import org.openecomp.aai.introspection.Introspector;
import org.openecomp.aai.introspection.Loader;
import org.openecomp.aai.introspection.LoaderFactory;
import org.openecomp.aai.introspection.ModelType;
import org.openecomp.aai.introspection.Version;
import org.openecomp.aai.introspection.exceptions.AAIUnknownObjectException;
import org.openecomp.aai.schema.enums.ObjectMetadata;
import org.openecomp.aai.util.AAIConstants;
import com.att.eelf.configuration.EELFLogger;
import com.att.eelf.configuration.EELFManager;
import com.google.common.collect.Multimap;
import com.thinkaurelius.titan.core.Cardinality;
import com.thinkaurelius.titan.core.Multiplicity;
import com.thinkaurelius.titan.core.schema.SchemaStatus;

public class AuditOXM extends Auditor {

	private static final EELFLogger LOGGER = EELFManager.getInstance().getLogger(AuditOXM.class);

	private Set<Introspector> allObjects;
	
	/**
	 * Instantiates a new audit OXM.
	 *
	 * @param version the version
	 */
	public AuditOXM(Version version) {
		Loader loader = LoaderFactory.createLoaderForVersion(ModelType.MOXY, version);
		Set<String> objectNames = getAllObjects(version);
		allObjects = new HashSet<>();
		for (String key : objectNames) {
			try {
				final Introspector temp = loader.introspectorFromName(key);
				allObjects.add(temp);
				this.createDBProperties(temp);
			} catch (AAIUnknownObjectException e) {
				LOGGER.warn("Skipping audit for object " + key + " (Unknown Object)", e);
			}
		}
		for (Introspector temp : allObjects) {
			this.createDBIndexes(temp);
		}
		createEdgeLabels();
		
	}

	/**
	 * Gets the all objects.
	 *
	 * @param version the version
	 * @return the all objects
	 */
	private Set<String> getAllObjects(Version version) {
		String fileName = AAIConstants.AAI_HOME_ETC_OXM + "aai_oxm_" + version.toString() + ".xml";
		Set<String> result = new HashSet<>();
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		try {
			docFactory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
			Document doc = docBuilder.parse(fileName);
			NodeList list = doc.getElementsByTagName("java-type");
			for (int i = 0; i < list.getLength(); i++) {
				result.add(list.item(i).getAttributes().getNamedItem("name").getNodeValue());
			}
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		result.remove("EdgePropNames");
		return result;
		
	}
	
	/**
	 * Creates the DB properties.
	 *
	 * @param temp the temp
	 */
	private void createDBProperties(Introspector temp) {
		Set<String> objectProperties = temp.getProperties();
		
		for (String prop : objectProperties) {
			if (!properties.containsKey(prop)) {
				DBProperty dbProperty = new DBProperty();
				dbProperty.setName(prop);
				if (temp.isListType(prop)) {
					dbProperty.setCardinality(Cardinality.SET);
					if (temp.isSimpleGenericType(prop)) {
						Class<?> clazz = null;
						try {
							clazz = Class.forName(temp.getGenericType(prop));
						} catch (ClassNotFoundException e) {
							clazz = Object.class;
						}
						dbProperty.setTypeClass(clazz);
						properties.put(prop, dbProperty);
					}
				} else {
					dbProperty.setCardinality(Cardinality.SINGLE);
					if (temp.isSimpleType(prop)) {
						Class<?> clazz = null;
						try {
							clazz = Class.forName(temp.getType(prop));
						} catch (ClassNotFoundException e) {
							clazz = Object.class;
						}
						dbProperty.setTypeClass(clazz);
						properties.put(prop, dbProperty);
					}
				}
			}
		}
		
	}
	
	/**
	 * Creates the DB indexes.
	 *
	 * @param temp the temp
	 */
	private void createDBIndexes(Introspector temp) {
		String uniqueProps = temp.getMetadata(ObjectMetadata.UNIQUE_PROPS);
		String namespace = temp.getMetadata(ObjectMetadata.NAMESPACE);
		if (uniqueProps == null) {
			uniqueProps = "";
		}
		if (namespace == null) {
			namespace = "";
		}
		boolean isTopLevel = namespace != "";
		List<String> unique = Arrays.asList(uniqueProps.split(","));
		Set<String> indexed = temp.getIndexedProperties();
		Set<String> keys = temp.getKeys();
		
		for (String prop : indexed) {
			DBIndex dbIndex = new DBIndex();
			LinkedHashSet<DBProperty> properties = new LinkedHashSet<>();
			if (!this.indexes.containsKey(prop)) {
				dbIndex.setName(prop);
				dbIndex.setUnique(unique.contains(prop));
				properties.add(this.properties.get(prop));
				dbIndex.setProperties(properties);
				dbIndex.setStatus(SchemaStatus.ENABLED);
				this.indexes.put(prop, dbIndex);
			}
		}
		if (keys.size() > 1 || isTopLevel) {
			DBIndex dbIndex = new DBIndex();
			LinkedHashSet<DBProperty> properties = new LinkedHashSet<>();
			dbIndex.setName("key-for-" + temp.getDbName());
			if (!this.indexes.containsKey(dbIndex.getName())) {
				boolean isUnique = false;
				if (isTopLevel) {
					properties.add(this.properties.get(AAIProperties.NODE_TYPE));
				}
				for (String key : keys) {
					properties.add(this.properties.get(key));
	
					if (unique.contains(key) && !isUnique) {
						isUnique = true;
					}
				}
				dbIndex.setUnique(isUnique);
				dbIndex.setProperties(properties);
				dbIndex.setStatus(SchemaStatus.ENABLED);
				this.indexes.put(dbIndex.getName(), dbIndex);
			}
		}

	}
	
	/**
	 * Creates the edge labels.
	 */
	private void createEdgeLabels() {
		Multimap<String, String> edgeRules = DbEdgeRules.EdgeRules;
		for (String key : edgeRules.keySet()) {
			Collection<String> collection = edgeRules.get(key);
			EdgeProperty prop = new EdgeProperty();
			//there is only ever one, they used the wrong type for EdgeRules
			String label = "";
			for (String item : collection) {
				label = item.split(",")[0];
			}
			prop.setName(label);
			prop.setMultiplicity(Multiplicity.MULTI);
			this.edgeLabels.put(label, prop);
		}
	}
	
	/**
	 * Gets the all introspectors.
	 *
	 * @return the all introspectors
	 */
	public Set<Introspector> getAllIntrospectors() {
		return this.allObjects;
	}
	
}
