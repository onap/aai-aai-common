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

package org.openecomp.aai.ingestModel;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.dynamic.DynamicType;
import org.eclipse.persistence.internal.dynamic.DynamicTypeImpl;
import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.jaxb.JAXBContextProperties;
import org.eclipse.persistence.jaxb.dynamic.DynamicJAXBContext;
import org.eclipse.persistence.jaxb.dynamic.DynamicJAXBContextFactory;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.oxm.XMLField;
import org.eclipse.persistence.oxm.mappings.XMLCompositeCollectionMapping;
import org.eclipse.persistence.oxm.mappings.XMLCompositeDirectCollectionMapping;

import org.openecomp.aai.domain.model.AAIResource;
import org.openecomp.aai.domain.model.AAIResourceKey;
import org.openecomp.aai.domain.model.AAIResourceKeys;
import org.openecomp.aai.domain.model.AAIResources;
import org.openecomp.aai.exceptions.AAIException;
import org.openecomp.aai.logging.ErrorLogHelper;
import org.openecomp.aai.util.AAIConfig;
import org.openecomp.aai.util.AAIConstants;
import org.openecomp.aai.util.FileWatcher;
import com.att.eelf.configuration.EELFLogger;
import com.att.eelf.configuration.EELFManager;
import com.google.common.base.CaseFormat;
import com.google.common.base.Splitter;
import com.google.common.collect.Multimap;

/**
 * The Class IngestModelMoxyOxm.
 */
public class IngestModelMoxyOxm
{

	private static final EELFLogger LOGGER = EELFManager.getInstance().getLogger(IngestModelMoxyOxm.class);

	public static HashMap<String, AAIResources> aaiResourceContainer;
	public static HashMap<String, DbMaps> dbMapsContainer;

	private static HashMap<String, Timer> timers = new HashMap<String,Timer>();

	/**
	 * The main method.
	 *
	 * @param args the arguments
	 * @throws Exception the exception
	 */
	public static void main(String[] args) throws Exception {

		String _apiVersion = AAIConfig.get(AAIConstants.AAI_DEFAULT_API_VERSION_PROP);

		if (args.length > 0) { 
			if (args[0] != null) {
				_apiVersion = args[0];
			}
		}
		ArrayList<String> apiVersions = new ArrayList<String>();
		apiVersions.add(_apiVersion);
		final IngestModelMoxyOxm m = new IngestModelMoxyOxm();
		m.init(apiVersions, false);

		ArrayList<String> endpoints = new ArrayList<String>();

		for (Map.Entry<String, AAIResources> ent: aaiResourceContainer.entrySet()) {

			AAIResources aaiResources = ent.getValue();
			DynamicJAXBContext jaxbContext = aaiResources.getJaxbContext();
			for (Map.Entry<String, AAIResource> aaiResEnt : aaiResources.getAaiResources().entrySet()) { 
				AAIResource aaiRes = aaiResEnt.getValue();

				String uri = aaiRes.getUri();
				if (uri != null) { 
					endpoints.add(uri);
					DynamicType dt = jaxbContext.getDynamicType(aaiRes.getResourceClassName());
					if (dt.containsProperty("relationshipList")) {
						endpoints.add(uri + "/relationship-list/relationship");
					}
				}
			}
			Collections.sort(endpoints);
			for (String endpoint : endpoints) {
				if (!endpoint.contains("/aai-internal/")) { 
					System.out.println(endpoint);
				}
			}
		}

		System.exit(0);
	}

	/**
	 * Inits the.
	 *
	 * @param apiVersions the api versions
	 * @throws Exception the exception
	 */
	public synchronized void init(ArrayList<String> apiVersions) throws AAIException {
		final IngestModelMoxyOxm m = new IngestModelMoxyOxm();
		m.init(apiVersions, true);
	}

	/**
	 * Inits the.
	 *
	 * @param apiVersions the api versions
	 * @param setTimer the set timer
	 * @throws AAIException If AAIConfig is missing necessary properties
	 * @throws Exception the exception
	 */
	public synchronized void init(ArrayList<String> apiVersions, Boolean setTimer) throws AAIException {

		aaiResourceContainer = new HashMap<String, AAIResources>();
		dbMapsContainer = new HashMap<String, DbMaps>();

		final IngestModelMoxyOxm m = new IngestModelMoxyOxm();

		for (String apiVersion : apiVersions) { 

			String relationshipUtils = "org.openecomp.aai.dbmap.RelationshipUtils";
			final String thisRelationshipUtils = relationshipUtils;

			final String thisApiVersion = apiVersion;
			final String schemaFile = AAIConstants.AAI_HOME_ETC_OXM + "aai_oxm_" + apiVersion + ".xml";

			m.loadSchema(apiVersion, schemaFile, relationshipUtils);

			if (!setTimer) continue;

			TimerTask task = null;
			task = new FileWatcher ( new File(schemaFile)) {
				protected void onChange( File file ) {
					m.loadSchema(thisApiVersion, schemaFile, thisRelationshipUtils);
				}
			};

			if (!timers.containsKey(apiVersion)) {
				Timer timer = new Timer();
				timer.schedule( task , new Date(), 10000 );
				timers.put(apiVersion,  timer);

			}
		}
		if (apiVersions.contains(AAIConfig.get(AAIConstants.AAI_DEFAULT_API_VERSION_PROP))) { 
			m.createPropertyAndNodeVersionInfoMapFromDbMaps();

			for (Map.Entry<String, AAIResources> ent: aaiResourceContainer.entrySet()) {
				String apiVersion = ent.getKey();
				AAIResources aaiResources = ent.getValue();

				DbMaps dbMap = dbMapsContainer.get(apiVersion);

				for (Map.Entry<String, AAIResource> aaiResEnt : aaiResources.getAaiResources().entrySet()) { 
					AAIResource aaiRes = aaiResEnt.getValue();
					aaiRes.setPropertyDataTypeMap(dbMap.PropertyDataTypeMap); 
					aaiRes.setNodeKeyProps(dbMap.NodeKeyProps);
					aaiRes.setNodeNameProps(dbMap.NodeNameProps);
					aaiRes.setNodeMapIndexedProps(dbMap.NodeMapIndexedProps);
				}
			}
		}
	}

	/**
	 * Load schema.
	 *
	 * @param apiVersion the api version
	 * @param schemaFile the schema file
	 * @param relationshipUtils the relationship utils
	 * @return the dynamic JAXB context
	 */
	private DynamicJAXBContext loadSchema(String apiVersion,
			String schemaFile, 
			String relationshipUtils)  {

		AAIResources aaiResources = new AAIResources();
		DbMaps dbMaps = new DbMaps();
		DynamicJAXBContext jaxbContext = null;

		try {

			InputStream iStream = new FileInputStream(new File(schemaFile));

			Map<String, Object> properties = new HashMap<String, Object>(); 
			properties.put(JAXBContextProperties.OXM_METADATA_SOURCE, iStream); 

			jaxbContext = 
					DynamicJAXBContextFactory.createContextFromOXM(IngestModelMoxyOxm.class.getClassLoader(), properties);

			aaiResources.setJaxbContext(jaxbContext);
			String rootClassName = "inventory.aai.openecomp.org." + apiVersion + ".Inventory";

			if ("v2".equals(apiVersion)) { 
				rootClassName = "inventory.aai.openecomp.org.Inventory";
			} 

			DynamicTypeImpl t = (DynamicTypeImpl)jaxbContext.getDynamicType(rootClassName);

			lookAtDynamicResource("Inventory",
					"inventory.aai.openecomp.org." + apiVersion, 
					jaxbContext, 
					t,
					1, "", "", apiVersion, "/" + apiVersion, false, aaiResources, dbMaps, relationshipUtils);

		} catch (Exception e) {
			ErrorLogHelper.logException(new AAIException("AAI_3000", e));
		}

		LOGGER.info("---> Loading " + apiVersion + " in aaiResourceContainer");
		aaiResourceContainer.put(apiVersion,  aaiResources);

		createDbMapsfromAAIResources(aaiResources, dbMaps);

		LOGGER.info("---> Loading " + apiVersion + " in dbMapsContainer");
		dbMapsContainer.put(apiVersion,  dbMaps);

		return jaxbContext;
	}

	/**
	 * Cleanup.
	 */
	public void cleanup() {
		aaiResourceContainer.clear();
		dbMapsContainer.clear();
	}

	/**
	 * Look at dynamic resource.
	 *
	 * @param resource the resource
	 * @param pojoBase the pojo base
	 * @param jaxbContext the jaxb context
	 * @param t the t
	 * @param depth the depth
	 * @param parent the parent
	 * @param namespace the namespace
	 * @param apiVersion the api version
	 * @param url the url
	 * @param container the container
	 * @param aaiResources the aai resources
	 * @param dbMaps the db maps
	 * @param relationshipUtils the relationship utils
	 * @throws ClassNotFoundException the class not found exception
	 * @throws NoSuchFieldException the no such field exception
	 * @throws SecurityException the security exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private void lookAtDynamicResource(String resource, 
			String pojoBase, 
			DynamicJAXBContext jaxbContext, 
			DynamicTypeImpl t,
			int depth,
			String parent,
			String namespace,
			String apiVersion,
			String url, 
			boolean container, 
			AAIResources aaiResources,
			DbMaps dbMaps,
			String relationshipUtils
			) 
					throws ClassNotFoundException, NoSuchFieldException, SecurityException,  IOException {

		String className = pojoBase + "." + CaseFormat.UPPER_CAMEL.to(CaseFormat.UPPER_CAMEL, resource);

		AAIResource parentResource = aaiResources.getAaiResources().get(parent);

		AAIResources siblings = null;

		if (parentResource == null) { 
			String fullName = "/" +  resource;
			AAIResource aaiRes = new AAIResource();
			aaiRes.setFullName(fullName);
			aaiRes.setSimpleName(resource);
			aaiRes.setResourceType("container");
			aaiRes.setResourceClassName(className);
			aaiRes.setApiVersion(apiVersion);

			aaiResources.getAaiResources().put(fullName, aaiRes);
			parentResource = aaiRes;
		}

		if (depth >= 50) return;

		siblings = parentResource.getChildren();

		if (depth == 2) { 
			namespace = resource;
		}
		if (depth >= 50) {
			return;
		}

		/*		if ("Actions".equals(namespace) || "Search".equals(namespace)) {
			return;
		}*/

		ClassDescriptor cd = t.getDescriptor();

		createDbMapsfromOXM(cd.getProperties(), resource, dbMaps);

		

		Vector<DatabaseMapping> dm = cd.getMappings();		

		for (DatabaseMapping dmInst : dm) {
			String dmName = dmInst.getAttributeName();

			ClassDescriptor cd2 = dmInst.getReferenceDescriptor();
			if (cd2 != null) { 

				String newClassName = cd2.getJavaClassName();
				//				
				if (newClassName.contains("RelationshipList")) {
					continue;
				}

				DynamicTypeImpl newDt = (DynamicTypeImpl)jaxbContext.getDynamicType(newClassName);

				if (dmInst instanceof XMLCompositeCollectionMapping) { 
					String simpleName = CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_CAMEL, dmName);
					//					System.out.println(spaces + "+ List of A&AI Object named " + simpleName);

					String hypName = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_HYPHEN, simpleName);

					String fullName = parent + "/" + simpleName;

					//Class<?> newClazz = Class.forName(newClassName);
					AAIResource aaiRes = new AAIResource();

					if ("cvlan-tag-entry".equals(hypName)) {
					}

					ClassDescriptor cd3 = newDt.getDescriptor();

					boolean allowDirectWrite = true;
					if (cd3.getProperties().containsKey("allowDirectWrite")) {
						if (cd3.getProperties().get("allowDirectWrite").equals("false")) {
							allowDirectWrite = false;
						}
					}
					

					boolean allowDirectRead = true;
					if (cd3.getProperties().containsKey("allowDirectRead")) {
						if (cd3.getProperties().get("allowDirectRead").equals("false")) {
							allowDirectRead = false;
						}
					}
					
					List<DatabaseField> dbfList = cd3.getPrimaryKeyFields();
					ArrayList<String> keyFields = new ArrayList<String>();

					if (dbfList != null) { 
						for (DatabaseField dbf : dbfList) {
							String name = dbf.getName();
							name = name.substring(0, name.indexOf('/'));
							keyFields.add(name);
						}
					}
					Vector<DatabaseMapping> dm2 = cd3.getMappings();
					for (DatabaseMapping dmInst2 : dm2) {
						String dmName2= CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_HYPHEN,dmInst2.getAttributeName());
						DatabaseField xf2 = dmInst2.getField();
						if (dmInst2.getProperties().containsKey("autoGenerateUuid")) { 
							if (dmInst2.getProperties().get("autoGenerateUuid").equals("true")) { 
								aaiRes.getAutoGenUuidFields().add(dmName2);
							}
						}
						if (xf2 instanceof XMLField) { 
							XMLField x = (XMLField)xf2;
							if (x != null) { 
								if (x.isRequired()) {
									aaiRes.getRequiredFields().add(dmName2);
								}
							}
							
						}
						try {
							Class<?> xf2Clazz = xf2.getType();
							if (xf2Clazz.getSimpleName().equals("String")) {
								if (dmInst2 instanceof XMLCompositeDirectCollectionMapping) { 
									aaiRes.getStringListFields().add(CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_HYPHEN,dmName2));
								} else { 
									aaiRes.getStringFields().add(CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_HYPHEN,dmName2));
								}
							} else if (xf2Clazz.getSimpleName().toLowerCase().contains("long")) {
								aaiRes.getLongFields().add(CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_HYPHEN,dmName2));
							} else if (xf2Clazz.getSimpleName().toLowerCase().contains("int")) {
								aaiRes.getIntFields().add(CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_HYPHEN,dmName2));
							} else if (xf2Clazz.getSimpleName().toLowerCase().contains("short")) {
								aaiRes.getShortFields().add(CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_HYPHEN,dmName2));
							} else if (xf2Clazz.getSimpleName().toLowerCase().contains("boolean")) {
								aaiRes.getBooleanFields().add(CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_HYPHEN,dmName2));
							}
						} catch (Exception e) {  // this xf2.getType() throws null pointer when I try to get the type and it doesn't have one
							;
						}
					}

					// get the key(s) from DbRules
					String uriKey = "";
					LinkedHashMap<String, ArrayList<String>> itemKeyList = new LinkedHashMap<String, ArrayList<String>>();

					aaiRes.setApiVersion(apiVersion);
					itemKeyList.put(hypName, new ArrayList<String>());
					for (String thisKey : keyFields) { 
						String pathParamName = hypName + "-" + thisKey;

						AAIResourceKey aaiResKey = new AAIResourceKey();
						aaiResKey.setKeyName(thisKey);
						aaiResKey.setDnCamKeyName(CaseFormat.LOWER_HYPHEN.to(CaseFormat.LOWER_CAMEL, pathParamName));
						aaiResKey.setPathParamName(pathParamName);

						for (DatabaseMapping dmInst2 : dm2) {
							String dmName2= CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_HYPHEN,dmInst2.getAttributeName());
							if (dmName2.equals(thisKey)){ 
								DatabaseField xf2 = dmInst2.getField();
								aaiResKey.setKeyType(xf2.getType().getSimpleName());
								break;
							}
						}

						aaiRes.getAaiResourceKeys().getAaiResourceKey().add(aaiResKey);

						if (siblings != null) {
							siblings.getAaiResources().put(fullName, aaiRes);
						}

						uriKey += "/{" + pathParamName + "}";
					}

					String newUri = url + "/" + hypName + uriKey;


					if ("v2".equals(apiVersion)) { 
						aaiRes.setResourceClassName("inventory.aai.openecomp.org." + simpleName);
					} else { 
						aaiRes.setResourceClassName("inventory.aai.openecomp.org." + apiVersion + "." + simpleName);
					}
					
					aaiRes.setAllowDirectWrite(allowDirectWrite);
					aaiRes.setAllowDirectRead(allowDirectRead);
					aaiRes.setNamespace(namespace);
					aaiRes.setSimpleName(simpleName);
					
					if (!aaiResources.getResourceLookup().containsKey(simpleName)) {
						aaiResources.getResourceLookup().put(simpleName, aaiRes);
					}
					
					aaiRes.setFullName(fullName);
					aaiRes.setUri(newUri);
					aaiRes.setResourceType("node");
					if ("v2".equals(apiVersion)) { 
						aaiRes.setRelationshipListClass("inventory.aai.openecomp.org.RelationshipList");
					} else { 
						aaiRes.setRelationshipListClass("inventory.aai.openecomp.org." + apiVersion + ".RelationshipList");
					}
					aaiRes.setRelationshipUtils(relationshipUtils);

					if (parentResource != null) { 
						aaiRes.setParent(parentResource);
					} else { 
						aaiRes.setParent(aaiRes);
					}

					aaiResources.getAaiResources().put(fullName, aaiRes);

					if (siblings != null) {
						siblings.getAaiResources().put(fullName, aaiRes);
					}
//					AAIResource ancestor = parentResource;
//
//					boolean recursiveEntity = false;
//					while (ancestor != null) { 
//
//						if (ancestor.getSimpleName().equals(aaiRes.getSimpleName())) {
//							recursiveEntity = true;
//							// attach it to the container that contains the resource above this one with the same name
//							if (ancestor.getParent() != null && ancestor.getParent().getResourceType().equals("container")) {
//								AAIResource recurseHere = ancestor.getParent();
//								aaiRes.setRecurseToResource(recurseHere);
//							}
//							break;
//						}
//						ancestor = ancestor.getParent();
//
//					}
//					if (recursiveEntity == false) { 
						lookAtDynamicResource(cd2.getJavaClass().getSimpleName(),
								pojoBase, 
								jaxbContext, 
								newDt,
								(depth + 1), fullName, namespace, apiVersion, newUri, false, aaiResources, dbMaps,
								relationshipUtils);
//					}
				} else { 
					String simpleName = CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_CAMEL, dmName);
					String fullName = parent + "/" +  simpleName;
					//					System.out.println(spaces + "+ Container of A&AI Object named " + simpleName);

					AAIResource aaiRes = new AAIResource();
					if (parentResource != null) { 
						aaiRes.setParent(parentResource);
					} else { 
						aaiRes.setParent(aaiRes);
					}
					aaiRes.setAllowDirectWrite(true);
					aaiRes.setAllowDirectRead(true);
					aaiRes.setFullName(fullName);
					aaiRes.setSimpleName(simpleName);
					if (!aaiResources.getResourceLookup().containsKey(simpleName)) {
						aaiResources.getResourceLookup().put(simpleName, aaiRes);
					}
					aaiRes.setResourceType("container");

					if ("v2".equals(apiVersion)) { 
						aaiRes.setResourceClassName("inventory.aai.openecomp.org." + simpleName);
						aaiRes.setRelationshipListClass("inventory.aai.openecomp.org.RelationshipList");
					} else {
						aaiRes.setResourceClassName("inventory.aai.openecomp.org." + apiVersion + "." + simpleName);
						aaiRes.setRelationshipListClass("inventory.aai.openecomp.org." + apiVersion  + ".RelationshipList");
					}
					aaiRes.setApiVersion(apiVersion);

					aaiResources.getAaiResources().put(fullName, aaiRes);
					aaiRes.setRelationshipUtils(relationshipUtils);

					String hypName = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_HYPHEN, simpleName);

					if (siblings != null) {
						siblings.getAaiResources().put(fullName, aaiRes);
					}

					lookAtDynamicResource(cd2.getJavaClass().getSimpleName(),
							pojoBase, 
							jaxbContext, 
							(DynamicTypeImpl)jaxbContext.getDynamicType(newClassName),
							(depth + 1), fullName, namespace, apiVersion, url + "/" + hypName, false, aaiResources, dbMaps,
							relationshipUtils);


				}
			}
		}
	}

	/**
	 * Creates the db mapsfrom OXM.
	 *
	 * @param propMap the prop map
	 * @param resource the resource
	 * @param dbMaps the db maps
	 */
	private void createDbMapsfromOXM(Map<?, ?> propMap, String resource, DbMaps dbMaps) {
		String nodeType = CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_HYPHEN, resource);
		if ("cvlan-tag-entry".equals(nodeType)) {
			nodeType = "cvlan-tag";
		}

		// if we have nodes dependent on multiple nodes we might revisit the node again - skip then
		if (propMap.size() > 1 && !dbMaps.NodeMapIndexedProps.containsKey(nodeType)) {

			if (propMap.containsKey("nameProps")) 
				dbMaps.NodeNameProps.putAll(nodeType, 
						(Iterable<String>) fromCommaSeparatedString(propMap.get("nameProps").toString()));

			if (propMap.containsKey("indexedProps")) 
				dbMaps.NodeMapIndexedProps.putAll(nodeType, 
						(Iterable<String>) fromCommaSeparatedString(propMap.get("indexedProps").toString()));

			if (propMap.containsKey("dependentOn")) 
				dbMaps.NodeDependencies.putAll(nodeType, 
						(Iterable<String>) fromCommaSeparatedString(propMap.get("dependentOn").toString()));

			if (propMap.containsKey("alternateKeys1")) 
				dbMaps.NodeAltKey1Props.putAll(nodeType, 
						(Iterable<String>) fromCommaSeparatedString(propMap.get("alternateKeys1").toString()));

			if (propMap.containsKey("uniqueProps")) 
				dbMaps.NodeMapUniqueProps.putAll(nodeType, 
						(Iterable<String>) fromCommaSeparatedString(propMap.get("uniqueProps").toString()));

			// build EdgeInfoMap
			if (propMap.containsKey("edgeInfo")) {
				int i = 0;
				Iterable<String> edgeInfoIterable = (Iterable<String>) fromCommaSeparatedString(propMap.get("edgeInfo").toString());
				Iterator<String> edgeInfoIterator = edgeInfoIterable.iterator();
				while(edgeInfoIterator.hasNext()) {
					String propName = edgeInfoIterator.next();
					dbMaps.EdgeInfoMap.put(i++, propName);
				}
			}
		}
	}

	/**
	 * Creates the db mapsfrom AAI resources.
	 *
	 * @param aaiResources the aai resources
	 * @param dbMaps the db maps
	 */
	private void createDbMapsfromAAIResources(AAIResources aaiResources, DbMaps dbMaps) {

		for (String  resource: aaiResources.getAaiResources().keySet()) {

			AAIResource aaiResource = aaiResources.getAaiResources().get(resource);
			String nodeType = CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_HYPHEN,aaiResource.getSimpleName());

			if (nodeType.equals("cvlan-tag-entry"))
				nodeType = "cvlan-tag";	

			// Build NodeNamespace
			if (aaiResource.getNamespace() != null && !aaiResource.getNamespace().equalsIgnoreCase("search")) 
				// oamNetworks is also defined under the search namespace - do not want that namespace 
				dbMaps.NodeNamespace.put(nodeType, CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_HYPHEN, aaiResource.getNamespace()));

			// only process one nodetype once 
			if (dbMaps.NodeProps.containsKey(nodeType)) 
				continue;

			// Build NodePlural
			if (aaiResource.getPluralName() != null && !aaiResource.getPluralName().equals(aaiResource.getNamespace())) 
				// dont want resources which are namespaces themselves in map
				dbMaps.NodePlural.put(nodeType, CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_HYPHEN, aaiResource.getPluralName()));

			// Build NodeProps
			dbMaps.NodeProps.putAll(nodeType, aaiResource.getAllFields());

			// build ReservedNames
			if (nodeType.equalsIgnoreCase("reserved-prop-names")) {
				for (String propName: aaiResource.getAllFields()) {
					dbMaps.ReservedPropNames.put(propName, "");
				}
			}

			// Build NodekeyProps
			AAIResourceKeys aaiResKeys = aaiResource.getAaiResourceKeys();
			List<String> keyList = new ArrayList<String>();
			for (AAIResourceKey rk : aaiResKeys.getAaiResourceKey()) { 
				String keyProp = CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_HYPHEN,rk.getKeyName());
				if (!keyList.contains(keyProp))
					keyList.add(keyProp);
			}				
			dbMaps.NodeKeyProps.putAll(nodeType, (Iterable<String>)keyList);

			// Build PropertyDataTypeMap
			for (String propName: aaiResource.getBooleanFields()) {
				if (nodeType.equalsIgnoreCase("edge-prop-names")) // these properties are in mixed format in DB
					propName = CaseFormat.LOWER_HYPHEN.to(CaseFormat.LOWER_CAMEL, propName);
				if (propName.equals("sVCINFRA")) propName = "SVC-INFRA";
				if (propName.equals("sVCINFRAREV")) propName = "SVC-INFRA-REV";
				if (!dbMaps.PropertyDataTypeMap.containsKey(propName))
					dbMaps.PropertyDataTypeMap.put(propName, "Boolean");
				else if (!dbMaps.PropertyDataTypeMap.get(propName).equals("Boolean"))
					System.out.println(propName + "defined with mis-matched types in oxm file");
			}
			for (String propName: aaiResource.getShortFields()) {
				if (nodeType.equalsIgnoreCase("edge-prop-names")) // these properties are in mixed format in DB
					propName = CaseFormat.LOWER_HYPHEN.to(CaseFormat.LOWER_CAMEL, propName);
				if (propName.equals("sVCINFRA")) propName = "SVC-INFRA";
				if (propName.equals("sVCINFRAREV")) propName = "SVC-INFRA-REV";
				if (!dbMaps.PropertyDataTypeMap.containsKey(propName))
					dbMaps.PropertyDataTypeMap.put(propName, "Integer");
				else if (!dbMaps.PropertyDataTypeMap.get(propName).equals("Integer"))
					System.out.println(propName + "defined with mis-matched types in oxm file");
			}
			for (String propName: aaiResource.getLongFields()) {
				if (nodeType.equalsIgnoreCase("edge-prop-names")) // these properties are in mixed format in DB
					propName = CaseFormat.LOWER_HYPHEN.to(CaseFormat.LOWER_CAMEL, propName);
				if (propName.equals("sVCINFRA")) propName = "SVC-INFRA";
				if (propName.equals("sVCINFRAREV")) propName = "SVC-INFRA-REV";
				if (!dbMaps.PropertyDataTypeMap.containsKey(propName)) {
					if (propName.contains("-ts"))
						dbMaps.PropertyDataTypeMap.put(propName, "Long");
					else
						dbMaps.PropertyDataTypeMap.put(propName, "Integer");
				} else if (!dbMaps.PropertyDataTypeMap.get(propName).equals("Integer"))
					System.out.println(propName + "defined with mis-matched types in oxm file");
			}
			for (String propName: aaiResource.getIntFields()) {
				if (nodeType.equalsIgnoreCase("edge-prop-names")) // these properties are in mixed format in DB
					propName = CaseFormat.LOWER_HYPHEN.to(CaseFormat.LOWER_CAMEL, propName);
				if (propName.equals("sVCINFRA")) propName = "SVC-INFRA";
				if (propName.equals("sVCINFRAREV")) propName = "SVC-INFRA-REV";
				if (!dbMaps.PropertyDataTypeMap.containsKey(propName))
					dbMaps.PropertyDataTypeMap.put(propName, "Integer");
				else if (!dbMaps.PropertyDataTypeMap.get(propName).equals("Integer"))
					System.out.println(propName + "defined with mis-matched types in oxm file");
			}
			for (String propName: aaiResource.getStringFields()) {
				if (nodeType.equalsIgnoreCase("edge-prop-names")) // these properties are in mixed format in DB
					propName = CaseFormat.LOWER_HYPHEN.to(CaseFormat.LOWER_CAMEL, propName);
				if (propName.equals("sVCINFRA")) propName = "SVC-INFRA";
				if (propName.equals("sVCINFRAREV")) propName = "SVC-INFRA-REV";
				if (!dbMaps.PropertyDataTypeMap.containsKey(propName))
					dbMaps.PropertyDataTypeMap.put(propName, "String");
				else if (!dbMaps.PropertyDataTypeMap.get(propName).equals("String"))
					System.out.println(propName + "defined with mis-matched types in oxm file");
			}
			for (String propName: aaiResource.getStringListFields()) {
				if (nodeType.equalsIgnoreCase("edge-prop-names")) // these properties are in mixed format in DB
					propName = CaseFormat.LOWER_HYPHEN.to(CaseFormat.LOWER_CAMEL, propName);
				if (propName.equals("sVCINFRA")) propName = "SVC-INFRA";
				if (propName.equals("sVCINFRAREV")) propName = "SVC-INFRA-REV";
				if (!dbMaps.PropertyDataTypeMap.containsKey(propName))
					dbMaps.PropertyDataTypeMap.put(propName, "Set<String>");
				else if (!dbMaps.PropertyDataTypeMap.get(propName).equals("Set<String>"))
					System.out.println(propName + "defined with mis-matched types in oxm file");
			}
		}

	}


	/**
	 * Creates the property and node version info map from db maps.
	 */
	private void createPropertyAndNodeVersionInfoMapFromDbMaps() {
		DbMaps dbMaps = null;
		String previousApiVersion = null;
		List<Integer> apiIntegerKeySet = new ArrayList<Integer>();
		for ( String vers : dbMapsContainer.keySet()) {
			apiIntegerKeySet.add(Integer.valueOf(vers.substring(1)));
		}
		ArrayList<Integer> apiIntegerVersionsList = (ArrayList<Integer>) asSortedList(apiIntegerKeySet);
		String apiVersion;
		for ( Integer apiIntegerVersion : apiIntegerVersionsList) {
			apiVersion = "v" + apiIntegerVersion;
			System.out.println("apiVersion=" + apiVersion);
			dbMaps = dbMapsContainer.get(apiVersion);

			if (previousApiVersion != null) { // when running more than one version
				dbMaps.PropertyVersionInfoMap.putAll(dbMapsContainer.get(previousApiVersion).PropertyVersionInfoMap);
				dbMaps.NodeVersionInfoMap.putAll(dbMapsContainer.get(previousApiVersion).NodeVersionInfoMap);
			}
			
			Iterator<String> nodeTypeIterator = dbMaps.NodeProps.keySet().iterator();
			while( nodeTypeIterator.hasNext() ){
				String nType = nodeTypeIterator.next();
				if (!dbMaps.NodeVersionInfoMap.containsKey(nType)) {
					dbMaps.NodeVersionInfoMap.put(nType, apiVersion);
				}
				Collection <String> nodePropsForType =  dbMaps.NodeProps.get(nType);
				Iterator <String> propIter = nodePropsForType.iterator();
				while( propIter.hasNext() ){
					String propName = propIter.next();
					String infoKey = nType + "|" + propName;
					if( ! dbMaps.PropertyVersionInfoMap.containsKey(infoKey) ){
						// We haven't seen this one yet -- add it in.
						dbMaps.PropertyVersionInfoMap.put(infoKey, apiVersion);
					}
				}
			}
			dbMapsContainer.put(apiVersion, dbMaps);
			previousApiVersion = apiVersion;
		}
	}

	/**
	 * As sorted list.
	 *
	 * @param <T> the generic type
	 * @param c the c
	 * @return the list
	 */
	private <T extends Comparable<? super T>> List<T> asSortedList(Collection<T> c) {
		List<T> list = new ArrayList<T>(c);
		java.util.Collections.sort(list);
		return list;
	}

	/**
	 * From comma separated string.
	 *
	 * @param string the string
	 * @return the iterable
	 */
	public Iterable<String> fromCommaSeparatedString( String string ) {
		Iterable<String> split = Splitter.on( "," ).omitEmptyStrings().trimResults().split( string );
		return split;
	}

	/**
	 * Pretty print map.
	 *
	 * @param map the map
	 * @return the string
	 */
	public String prettyPrintMap(Multimap<String, String> map) {
		StringBuilder sb = new StringBuilder();
		sb.append('\n');
		for (String key:map.keySet()) {
			sb.append('\t');
			sb.append(key);
			sb.append('=').append('"');
			sb.append(map.get(key));
			sb.append('"');
			sb.append('\n');
		}
		return sb.toString();
	}

	/**
	 * Pretty print map.
	 *
	 * @param map the map
	 * @return the string
	 */
	public String prettyPrintMap(Map<?, String> map) {
		StringBuilder sb = new StringBuilder();
		sb.append('\n');
		for (Object key:map.keySet()) {
			sb.append('\t');
			sb.append(key);
			sb.append('=').append('"');
			sb.append(map.get(key));
			sb.append('"');
			sb.append('\n');
		}
		return sb.toString();
	}

}
