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

package org.openecomp.aai.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationModule;
import com.google.common.base.CaseFormat;
import com.google.common.collect.Multimap;
import com.thinkaurelius.titan.core.TitanVertex;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.eclipse.persistence.dynamic.DynamicEntity;
import org.eclipse.persistence.dynamic.DynamicType;
import org.eclipse.persistence.jaxb.MarshallerProperties;
import org.openecomp.aai.domain.model.AAIResource;
import org.openecomp.aai.exceptions.AAIException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.IOException;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.Map.Entry;

public class PojoUtils {
	
	/**
	 * Gets the key value list.
	 *
	 * @param <T> the generic type
	 * @param e the e
	 * @param clazz the clazz
	 * @return the key value list
	 * @throws IllegalAccessException the illegal access exception
	 * @throws IllegalArgumentException the illegal argument exception
	 * @throws InvocationTargetException the invocation target exception
	 */
	public <T> List<KeyValueList> getKeyValueList(Entity e, T clazz) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		List<KeyValueList> kvList = e.getKeyValueList();
		Object value = null;
		Method[] methods = clazz.getClass().getDeclaredMethods();
		String propertyName = "";
		
		for (Method method : methods) { 
			if (method.getName().startsWith("get")) { 
				propertyName = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_HYPHEN,method.getName().substring(3));
				if (!(method.getReturnType().getName().contains("aai")) || method.getReturnType().getName().contains("java.util.List")) {
					value = method.invoke(clazz);
					KeyValueList kv = new KeyValueList();
					kv.setKey(propertyName);
					if (value != null) { 
						kv.setValue(value.toString());
					} else { 
						kv.setValue("");
					}
					kvList.add(kv);
				}
			}
		}
		return kvList;
	}
	
	/**
	 * Gets the json from object.
	 *
	 * @param <T> the generic type
	 * @param clazz the clazz
	 * @return the json from object
	 * @throws JsonGenerationException the json generation exception
	 * @throws JsonMappingException the json mapping exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public <T> String getJsonFromObject(T clazz) throws JsonGenerationException, JsonMappingException, IOException {
		return getJsonFromObject(clazz, false, true);
	}
	
	/**
	 * Gets the json from object.
	 *
	 * @param <T> the generic type
	 * @param clazz the clazz
	 * @param wrapRoot the wrap root
	 * @param indent the indent
	 * @return the json from object
	 * @throws JsonGenerationException the json generation exception
	 * @throws JsonMappingException the json mapping exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public <T> String getJsonFromObject(T clazz, boolean wrapRoot, boolean indent) throws JsonGenerationException, JsonMappingException, IOException {
		ObjectMapper mapper = new ObjectMapper();

        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        mapper.configure(SerializationFeature.INDENT_OUTPUT, indent);
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, wrapRoot);

        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.configure(DeserializationFeature.UNWRAP_ROOT_VALUE, wrapRoot);

        mapper.registerModule(new JaxbAnnotationModule());
        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        
        mapper.writeValue(baos, clazz);
    
        return baos.toString();
	}
	
	/**
	 * Gets the json from dynamic object.
	 *
	 * @param ent the ent
	 * @param jaxbContext the jaxb context
	 * @param includeRoot the include root
	 * @return the json from dynamic object
	 * @throws JsonGenerationException the json generation exception
	 * @throws JsonMappingException the json mapping exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws JAXBException the JAXB exception
	 */
	public String getJsonFromDynamicObject(DynamicEntity ent, org.eclipse.persistence.jaxb.JAXBContext jaxbContext, boolean includeRoot) throws JsonGenerationException, JsonMappingException, IOException, JAXBException {
		org.eclipse.persistence.jaxb.JAXBMarshaller marshaller = jaxbContext.createMarshaller();
		
		marshaller.setProperty(org.eclipse.persistence.jaxb.JAXBMarshaller.JAXB_FORMATTED_OUTPUT, false);
		marshaller.setProperty(MarshallerProperties.JSON_MARSHAL_EMPTY_COLLECTIONS, Boolean.FALSE) ;
		marshaller.setProperty("eclipselink.json.include-root", includeRoot);
		marshaller.setProperty("eclipselink.media-type", "application/json");
		StringWriter writer = new StringWriter();
		marshaller.marshal(ent, writer);
        
		return writer.toString();
	}
	
	/**
	 * Gets the xml from object.
	 *
	 * @param <T> the generic type
	 * @param clazz the clazz
	 * @return the xml from object
	 * @throws JAXBException the JAXB exception
	 */
	public <T> String getXmlFromObject(T clazz) throws JAXBException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		JAXBContext jc = JAXBContext.newInstance(clazz.getClass().getPackage().getName());

		Marshaller marshaller = jc.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		marshaller.marshal(clazz, baos);
        
        return baos.toString();
	}
	
	/**
	 * Gets the lookup key.
	 *
	 * @param baseKey the base key
	 * @param lookupHash the lookup hash
	 * @param keyProps the key props
	 * @return the lookup key
	 */
	public String getLookupKey (String baseKey, HashMap<String,Object> lookupHash, Collection<String> keyProps) { 
		int baseKeyLen = baseKey.length();
		StringBuffer newKey = new StringBuffer();
		if (baseKeyLen > 0) { 
			newKey.append(baseKey);
		}
		
		Iterator <String> keyPropI = keyProps.iterator();
		while( keyPropI.hasNext() ){
			String keyProp = keyPropI.next();
			if (baseKeyLen > 0) {
				newKey.append("&");
			}
			newKey.append(keyProp + "=" + lookupHash.get(keyProp));
		}
		return newKey.toString();
	}
	
	/**
	 * Gets the lookup keys.
	 *
	 * @param lookupHashes the lookup hashes
	 * @param _dbRulesNodeKeyProps the db rules node key props
	 * @return the lookup keys
	 */
	public String getLookupKeys (LinkedHashMap<String,HashMap<String,Object>> lookupHashes, Multimap<String, String> _dbRulesNodeKeyProps) { 
		Iterator<String> it = lookupHashes.keySet().iterator();
		String lookupKeys = "";
		while (it.hasNext()) {
			String objectType = (String)it.next();
			HashMap<String,Object> lookupHash = lookupHashes.get(objectType);
						
			Collection<String> keyProps = _dbRulesNodeKeyProps.get(objectType);
			Iterator <String> keyPropI = keyProps.iterator();
			while( keyPropI.hasNext() ){
				lookupKeys += lookupHash.get(keyPropI.next());
			}
		}
		return lookupKeys;
	}

	/**
	 * Gets the example object.
	 *
	 * @param <T> the generic type
	 * @param clazz the clazz
	 * @param singleton the singleton
	 * @return the example object
	 * @throws IllegalAccessException the illegal access exception
	 * @throws IllegalArgumentException the illegal argument exception
	 * @throws InvocationTargetException the invocation target exception
	 * @throws NoSuchMethodException the no such method exception
	 * @throws SecurityException the security exception
	 * @throws AAIException the AAI exception
	 */
	public <T> void getExampleObject(T clazz, boolean singleton) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, AAIException {
		Method[] methods = clazz.getClass().getDeclaredMethods();
		String dnHypPropertyName = "";
		String upCamPropertyName = "";
		Random rand = new Random();
		int randInt = rand.nextInt(10000000);

		for (Method method : methods) { 
			boolean go = false;
			if (method.getName().startsWith("get")) { 
				dnHypPropertyName = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_HYPHEN,method.getName().substring(3));
				upCamPropertyName = CaseFormat.UPPER_CAMEL.to(CaseFormat.UPPER_CAMEL,method.getName().substring(3));
				go = true;
			} else if (method.getName().startsWith("is")) { 
				dnHypPropertyName = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_HYPHEN,method.getName().substring(2));
				upCamPropertyName = CaseFormat.UPPER_CAMEL.to(CaseFormat.UPPER_CAMEL,method.getName().substring(2));
				go = true;
			}
			// don't return resource-version on a singleton
			if (singleton && dnHypPropertyName.equals("resource-version")) {
				go = false;
			}
			if (go) { 
				String retType = method.getReturnType().getName();
				if (!retType.contains("aai") && !retType.contains("java.util.List")) {
					// get the setter
					Method meth = clazz.getClass().getMethod("set" + upCamPropertyName, method.getReturnType());
					
					if (retType.contains("String")) { 
						String val = "example-" + dnHypPropertyName + "-val-" +  randInt;
						if (val != null) { 
							meth.invoke(clazz, val);
						}
					} else if (retType.toLowerCase().contains("long")) {
						Integer foo = rand.nextInt(100000);
						meth.invoke(clazz, foo.longValue());
					} else if (retType.toLowerCase().contains("int")) { 
						meth.invoke(clazz, rand.nextInt(100000));
					} else if (retType.toLowerCase().contains("short")) { 
						Integer randShort = rand.nextInt(10000);
						meth.invoke(clazz, randShort.shortValue());
					} else if (retType.toLowerCase().contains("boolean")) { 
						meth.invoke(clazz, true);
					}
				}
			}
		}
	}

	
	/**
	 * Gets the aai object from vertex.
	 *
	 * @param <T> the generic type
	 * @param clazz the clazz
	 * @param vert the vert
	 * @param _propertyDataTypeMap the property data type map
	 * @return the aai object from vertex
	 * @throws IllegalAccessException the illegal access exception
	 * @throws IllegalArgumentException the illegal argument exception
	 * @throws InvocationTargetException the invocation target exception
	 * @throws NoSuchMethodException the no such method exception
	 * @throws SecurityException the security exception
	 * @throws AAIException the AAI exception
	 */
	public <T> void getAaiObjectFromVertex(T clazz, TitanVertex vert, Map<String, String> _propertyDataTypeMap) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, AAIException {
		Method[] methods = clazz.getClass().getDeclaredMethods();
		String dnHypPropertyName = "";
		String upCamPropertyName = "";
		for (Method method : methods) { 
			boolean go = false;
			if (method.getName().startsWith("get")) { 
				dnHypPropertyName = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_HYPHEN,method.getName().substring(3));
				upCamPropertyName = CaseFormat.UPPER_CAMEL.to(CaseFormat.UPPER_CAMEL,method.getName().substring(3));
				go = true;
			} else if (method.getName().startsWith("is")) { 
				dnHypPropertyName = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_HYPHEN,method.getName().substring(2));
				upCamPropertyName = CaseFormat.UPPER_CAMEL.to(CaseFormat.UPPER_CAMEL,method.getName().substring(2));
				go = true;
			}
			if (go) { 
				String retType = method.getReturnType().getName();
				if (!retType.contains("aai") && !retType.contains("java.util.List")) {
					// get the setter
					Method meth = clazz.getClass().getMethod("set" + upCamPropertyName, method.getReturnType());
					
					if (retType.contains("String")) { 
						String val = (String)vert.<String>property(dnHypPropertyName).orElse(null);
						if (val != null) { 
							meth.invoke(clazz, val);
						}
					} else if (retType.toLowerCase().contains("long")) {
						String titanType = _propertyDataTypeMap.get(dnHypPropertyName);
						
						Long val = null;
						// we have a case where the type in titan is "Integer" but in the POJO it's Long or long
						if (titanType.toLowerCase().contains("int")) {
							Integer intVal = (Integer)vert.<Integer>property(dnHypPropertyName).orElse(null);
							if (intVal != null) { 
								val = intVal.longValue();
							}
						} else { 
							val = (Long)vert.<Long>property(dnHypPropertyName).orElse(null);
						}
						if (val != null) { 
							meth.invoke(clazz, val);
						}
					} else if (retType.toLowerCase().contains("int")) { 
						Integer val = (Integer)vert.<Integer>property(dnHypPropertyName).orElse(null);
						if (val != null) { 
							meth.invoke(clazz, val);
						}
					} else if (retType.toLowerCase().contains("short")) { 
						Short val = (Short)vert.<Short>property(dnHypPropertyName).orElse(null);
						if (val != null) { 
							meth.invoke(clazz, val);
						}
					} else if (retType.toLowerCase().contains("boolean")) { 
						Boolean val = (Boolean)vert.<Boolean>property(dnHypPropertyName).orElse(null);
						if (val != null) { 
							meth.invoke(clazz, val);
						}
					}
				}
			}
		}
	}

	/**
	 * Gets the topology object.
	 *
	 * @param <T> the generic type
	 * @param clazz the clazz
	 * @param _dbRulesNodeNameProps the db rules node name props
	 * @param _dbRulesNodeKeyProps the db rules node key props
	 * @param vert the vert
	 * @return the topology object
	 * @throws IllegalAccessException the illegal access exception
	 * @throws IllegalArgumentException the illegal argument exception
	 * @throws InvocationTargetException the invocation target exception
	 * @throws NoSuchMethodException the no such method exception
	 * @throws SecurityException the security exception
	 * @throws AAIException the AAI exception
	 */
	public <T> void getTopologyObject(T clazz, Multimap<String, String> _dbRulesNodeNameProps, Multimap<String, String> _dbRulesNodeKeyProps, TitanVertex vert) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, AAIException {
		Method[] methods = clazz.getClass().getDeclaredMethods();
		String dnHypPropertyName = "";
//		Object value = null;
		List<String> includeProps = new ArrayList<String>();
				
		if ("false".equals(AAIConfig.get("aai.notification.topology.allAttrs", "false"))) {
			for (Method method : methods) { 
				if (method.getName().startsWith("is")) { 
					dnHypPropertyName = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_HYPHEN,method.getName().substring(2));
					String upCamPropertyName = CaseFormat.UPPER_CAMEL.to(CaseFormat.UPPER_CAMEL,method.getName().substring(2));
					String retType = method.getReturnType().getName();
					if (retType.equals("java.lang.Boolean")) {
						// get the setter
						Method setterMeth = clazz.getClass().getMethod("set" + upCamPropertyName, method.getReturnType());
						setterMeth.invoke(clazz, (Boolean)null);
					}
				}
			}
			String dnHypClassName = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_HYPHEN,clazz.getClass().getSimpleName());
			Collection<String> keepProps = _dbRulesNodeNameProps.get(dnHypClassName);
			Iterator <String> keepPropI = keepProps.iterator();
			while( keepPropI.hasNext() ){
				includeProps.add(keepPropI.next());
			}
			Collection<String> keepProps2 = _dbRulesNodeKeyProps.get(dnHypClassName);
			Iterator <String> keepPropI2 = keepProps2.iterator();
			while( keepPropI2.hasNext() ){
				includeProps.add(keepPropI2.next());
			}
		}
		
		for (Method method : methods) { 
			if (method.getName().startsWith("get")) { 
				dnHypPropertyName = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_HYPHEN,method.getName().substring(3));
				if (includeProps.size() > 0) { 
					if (!includeProps.contains(dnHypPropertyName)) {
						continue;
					}
				}
				String upCamPropertyName = CaseFormat.UPPER_CAMEL.to(CaseFormat.UPPER_CAMEL,method.getName().substring(3));
				String retType = method.getReturnType().getName();
				if (!retType.contains("aai") && !retType.contains("java.util.List")) {
					// get the setter
					Method meth = clazz.getClass().getMethod("set" + upCamPropertyName, method.getReturnType());
					
					if (retType.contains("String")) { 
						String val = (String)vert.<String>property(dnHypPropertyName).orElse(null);
						if (val != null) { 
							meth.invoke(clazz, val);
						}
					} else if (retType.toLowerCase().contains("long")) {
						Long val = (Long)vert.<Long>property(dnHypPropertyName).orElse(null);
						if (val != null) { 
							meth.invoke(clazz, val);
						}
					} else if (retType.toLowerCase().contains("int")) { 
						Integer val = (Integer)vert.<Integer>property(dnHypPropertyName).orElse(null);
						if (val != null) { 
							meth.invoke(clazz, val);
						}
					} else if (retType.toLowerCase().contains("short")) { 
						Short val = (Short)vert.<Short>property(dnHypPropertyName).orElse(null);
						if (val != null) { 
							meth.invoke(clazz, val);
						}
					}
				}
			}
		}
	}

	/**
	 * Gets the dynamic topology object.
	 *
	 * @param aaiRes the aai res
	 * @param meObjectType the me object type
	 * @param _dbRulesNodeNameProps the db rules node name props
	 * @param _dbRulesNodeKeyProps the db rules node key props
	 * @param _propertyDataTypeMap the property data type map
	 * @param vert the vert
	 * @return the dynamic topology object
	 * @throws AAIException the AAI exception
	 */
	public DynamicEntity getDynamicTopologyObject(AAIResource aaiRes, DynamicType meObjectType, Multimap<String, String> _dbRulesNodeNameProps,
                                                  Multimap<String, String> _dbRulesNodeKeyProps, Map<String, String> _propertyDataTypeMap, TitanVertex vert) throws AAIException {
		
		DynamicEntity meObject = meObjectType.newDynamicEntity();
		
		List<String> includeProps = new ArrayList<String>();
				
		if ("false".equals(AAIConfig.get("aai.notification.topology.allAttrs", "false"))) {
			String dnHypClassName = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_HYPHEN,meObjectType.getJavaClass().getSimpleName());
			Collection<String> keepProps = _dbRulesNodeNameProps.get(dnHypClassName);
			Iterator <String> keepPropI = keepProps.iterator();
			while( keepPropI.hasNext() ){
				includeProps.add(keepPropI.next());
			}
			Collection<String> keepProps2 = _dbRulesNodeKeyProps.get(dnHypClassName);
			Iterator <String> keepPropI2 = keepProps2.iterator();
			while( keepPropI2.hasNext() ) {
				includeProps.add(keepPropI2.next());
			}
		}
		
	
		
		for (String attrName : aaiRes.getStringFields()) { 
			if (includeProps.contains(attrName)) { 
				meObject.set((CaseFormat.LOWER_HYPHEN.to(CaseFormat.LOWER_CAMEL,attrName)), vert.<String>property(attrName).orElse(null));
			}
		}
		// the attrName might need to be converted to camel case!!!
		for (String attrName : aaiRes.getLongFields()) {
			if (includeProps.contains(attrName)) { 
				String titanType = _propertyDataTypeMap.get(attrName);		

				Long val = null;
				// we have a case where the type in titan is "Integer" but in the POJO it's Long or long
				if (titanType.toLowerCase().contains("int")) {
					Integer intVal = (Integer)vert.<Integer>property(attrName).orElse(null);
					if (intVal != null) { 
						val = intVal.longValue();
					}
				} else { 
					val = (Long)vert.<Long>property(attrName).orElse(null);
				}
				meObject.set((CaseFormat.LOWER_HYPHEN.to(CaseFormat.LOWER_CAMEL,attrName)), val);
			}
		}

		for (String attrName : aaiRes.getIntFields()) { 
			if (includeProps.contains(attrName)) { 
				Integer val = (Integer)vert.<Integer>property(attrName).orElse(null);
				meObject.set((CaseFormat.LOWER_HYPHEN.to(CaseFormat.LOWER_CAMEL,attrName)), val);
			}
		}

		for (String attrName : aaiRes.getShortFields()) { 
			if (includeProps.contains(attrName)) { 
				String titanType = _propertyDataTypeMap.get(attrName);		

				Short val = null;
				// we have a case where the type in titan is "Integer" but in the POJO it's Long or long
				if (titanType.toLowerCase().contains("int")) {
					Integer intVal = (Integer)vert.<Integer>property(attrName).orElse(null);
					if (intVal != null) { 
						val = intVal.shortValue();
					}
				} else { 
					val = (Short)vert.<Short>property(attrName).orElse(null);
				}
				meObject.set((CaseFormat.LOWER_HYPHEN.to(CaseFormat.LOWER_CAMEL,attrName)), val);
			}
		}

		for (String attrName : aaiRes.getBooleanFields()) {
			if (includeProps.contains(attrName)) { 
				Boolean val = (Boolean)vert.<Boolean>property(attrName).orElse(null);
				meObject.set((CaseFormat.LOWER_HYPHEN.to(CaseFormat.LOWER_CAMEL,attrName)),  val);
			}
		}
		return meObject;	
	}

	/**
	 * Gets the aai dynamic object from vertex.
	 *
	 * @param aaiRes the aai res
	 * @param meObject the me object
	 * @param vert the vert
	 * @param _propertyDataTypeMap the property data type map
	 * @return the aai dynamic object from vertex
	 */
	public void getAaiDynamicObjectFromVertex(AAIResource aaiRes, DynamicEntity meObject, TitanVertex vert,
                                              Map<String, String> _propertyDataTypeMap) {
		getAaiDynamicObjectFromVertex(aaiRes, meObject, vert, _propertyDataTypeMap, null);
	}
	
	/**
	 * Gets the aai dynamic object from vertex.
	 *
	 * @param aaiRes the aai res
	 * @param meObject the me object
	 * @param vert the vert
	 * @param _propertyDataTypeMap the property data type map
	 * @param propertyOverRideHash the property over ride hash
	 * @return the aai dynamic object from vertex
	 */
	@SuppressWarnings("unchecked")
	public void getAaiDynamicObjectFromVertex(AAIResource aaiRes, DynamicEntity meObject, TitanVertex vert,
                                              Map<String, String> _propertyDataTypeMap, HashMap<String, Object> propertyOverRideHash) {
			
		for (String attrName : aaiRes.getStringFields()) { 
			if (propertyOverRideHash == null || (propertyOverRideHash != null && propertyOverRideHash.containsKey(attrName))) {
				meObject.set((CaseFormat.LOWER_HYPHEN.to(CaseFormat.LOWER_CAMEL,attrName)), vert.<String>property(dbAliasWorkaround(attrName)).orElse(null));
			}
		}
		
		for (String attrName : aaiRes.getStringListFields()) { 
			if (propertyOverRideHash == null || (propertyOverRideHash != null && propertyOverRideHash.containsKey(attrName))) {
				meObject.set((CaseFormat.LOWER_HYPHEN.to(CaseFormat.LOWER_CAMEL,attrName)), vert.<ArrayList<String>>property(attrName).orElse(null));
			}
		}
		
		// the attrName might need to be converted to camel case!!!
		for (String attrName : aaiRes.getLongFields()) { 
			String titanType = _propertyDataTypeMap.get(attrName);		
			Long val = null;
			// we have a case where the type in titan is "Integer" but in the POJO it's Long or long
			if (titanType.toLowerCase().contains("int")) {
				Object vertexVal = vert.property(attrName).orElse(null);
				if (vertexVal != null) {
					if (vertexVal instanceof Integer) {
						val = ((Integer)vertexVal).longValue();
						
					} else {
						val = (Long)vert.<Long>property(attrName).orElse(null);	
					}
				}
			} else {
				val = (Long)vert.<Long>property(attrName).orElse(null);	
			}
			if (val != null) { 
				if (propertyOverRideHash == null || (propertyOverRideHash != null && propertyOverRideHash.containsKey(attrName))) {
					meObject.set((CaseFormat.LOWER_HYPHEN.to(CaseFormat.LOWER_CAMEL,attrName)), val);
				}
			}
		}
		
		for (String attrName : aaiRes.getIntFields()) { 
			Integer val = (Integer)vert.<Integer>property(attrName).orElse(null);
			if (val != null) { 
				if (propertyOverRideHash == null || (propertyOverRideHash != null && propertyOverRideHash.containsKey(attrName))) {
					meObject.set((CaseFormat.LOWER_HYPHEN.to(CaseFormat.LOWER_CAMEL,attrName)), val);
				}
			}
		}
		
		for (String attrName : aaiRes.getShortFields()) { 
			String titanType = _propertyDataTypeMap.get(attrName);		
			Short val = null;
			// we have a case where the type in titan is "Integer" but in the POJO it's Long or long
			if (titanType.toLowerCase().contains("int")) {
				Integer intVal = (Integer)vert.<Integer>property(attrName).orElse(null);
				if (intVal != null) { 
					val = intVal.shortValue();
				}
			} else { 
				val = (Short)vert.<Short>property(attrName).orElse(null);
			}
			if (val != null) { 
				if (propertyOverRideHash == null || (propertyOverRideHash != null && propertyOverRideHash.containsKey(attrName))) {
					meObject.set((CaseFormat.LOWER_HYPHEN.to(CaseFormat.LOWER_CAMEL,attrName)), val);
				}
			}
		}
		
		for (String attrName : aaiRes.getBooleanFields()) {
			Boolean val = (Boolean)vert.<Boolean>property(attrName).orElse(null);
			// This is not ideal, but moxy isn't marshalling these attributes.
			// TODO: Figure out how to see the default-value from the OXM at startup (or at runtime).
			String dnHypClassName = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_HYPHEN,aaiRes.getSimpleName());
			if (val == null && AAIConfig.getDefaultBools().containsKey(dnHypClassName)) {
				if (AAIConfig.getDefaultBools().get(dnHypClassName).contains(attrName)) {
					val = false;
				}
			}
			if (val != null) { 
				if (propertyOverRideHash == null || (propertyOverRideHash != null && propertyOverRideHash.containsKey(attrName))) {
					meObject.set((CaseFormat.LOWER_HYPHEN.to(CaseFormat.LOWER_CAMEL,attrName)),  val);
				}
			}
		}
			
	}
	
	private String dbAliasWorkaround(String propName) {
		final String modelInvariantIdLocal = "model-invariant-id-local";
		final String modelVersionIdLocal = "model-version-id-local";
		final String modelInvariantId = "model-invariant-id";
		final String modelVersionId = "model-version-id";
		
		if (propName.equals(modelInvariantId)) {
			return modelInvariantIdLocal;
		}
		if (propName.equals(modelVersionId)) {
			return modelVersionIdLocal;
		}
		
		return propName;
		
	}

	/**
	 * Gets the dynamic example object.
	 *
	 * @param childObject the child object
	 * @param aaiRes the aai res
	 * @param singleton the singleton
	 * @return the dynamic example object
	 */
	public void getDynamicExampleObject(DynamicEntity childObject, AAIResource aaiRes, boolean singleton) {
		// TODO Auto-generated method stub

		Random rand = new Random();
		Integer randInt = rand.nextInt(100000);
		long range = 100000000L;
		long randLong = (long)(rand.nextDouble()*range);
		Integer randShrt = rand.nextInt(20000);
		short randShort = randShrt.shortValue();

		for (String dnHypAttrName : aaiRes.getStringFields()) { 
			
			if (singleton && ("resource-version").equals(dnHypAttrName)) {
				continue;
			}
			
			String dnCamAttrName = CaseFormat.LOWER_HYPHEN.to(CaseFormat.LOWER_CAMEL,dnHypAttrName);
			childObject.set(dnCamAttrName, "example-" + dnHypAttrName + "-val-" +  randInt);

		}
		
		for (String dnHypAttrName : aaiRes.getStringListFields()) { 
			ArrayList<String> exampleList = new ArrayList<String>();
			exampleList.add("example-" + dnHypAttrName + "-val-" + randInt + "-" + 1);
			exampleList.add("example-" + dnHypAttrName + "-val-" + randInt + "-" + 2);
			String dnCamAttrName = CaseFormat.LOWER_HYPHEN.to(CaseFormat.LOWER_CAMEL,dnHypAttrName);
			childObject.set(dnCamAttrName, exampleList);
		}
		
		// the attrName might need to be converted to camel case!!!
		for (String dnHypAttrName : aaiRes.getLongFields()) { 
			String dnCamAttrName = CaseFormat.LOWER_HYPHEN.to(CaseFormat.LOWER_CAMEL,dnHypAttrName);
			childObject.set(dnCamAttrName, randLong);
		}

		for (String dnHypAttrName : aaiRes.getIntFields()) { 
			String dnCamAttrName = CaseFormat.LOWER_HYPHEN.to(CaseFormat.LOWER_CAMEL,dnHypAttrName);
			childObject.set(dnCamAttrName, randInt);
		}

		for (String dnHypAttrName : aaiRes.getShortFields()) { 
			String dnCamAttrName = CaseFormat.LOWER_HYPHEN.to(CaseFormat.LOWER_CAMEL,dnHypAttrName);
			childObject.set(dnCamAttrName, randShort);
		}

		for (String dnHypAttrName : aaiRes.getBooleanFields()) {
			String dnCamAttrName = CaseFormat.LOWER_HYPHEN.to(CaseFormat.LOWER_CAMEL,dnHypAttrName);
			childObject.set(dnCamAttrName, Boolean.TRUE);
		}
	}
}
