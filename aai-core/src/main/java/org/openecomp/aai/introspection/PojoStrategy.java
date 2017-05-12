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

package org.openecomp.aai.introspection;

import java.io.StringReader;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.transform.stream.StreamSource;

import org.eclipse.persistence.jaxb.UnmarshallerProperties;

import org.openecomp.aai.annotations.Metadata;
import org.openecomp.aai.logging.ErrorLogHelper;
import org.openecomp.aai.restcore.MediaType;
import org.openecomp.aai.schema.enums.ObjectMetadata;
import org.openecomp.aai.schema.enums.PropertyMetadata;
import com.google.common.base.CaseFormat;
import com.google.common.base.Joiner;
import com.google.common.collect.Multimap;

public class PojoStrategy extends Introspector {

	private Object internalObject = null;
	private PojoInjestor injestor = null;
	private Multimap<String, String> keyProps = null;
	private Metadata classLevelMetadata = null;
	private Version version;
	private JAXBContext jaxbContext;
	private Marshaller marshaller;
	private Unmarshaller unmarshaller;
	private Set<String> properties = null;
	private Set<String> keys = null;
	private Set<String> requiredProperties = null;

	private boolean isInitialized = false;
	
	protected PojoStrategy(Object obj) {
		super(obj);
		className = PojoStrategy.class.getSimpleName();
		this.internalObject = obj;
		injestor = new PojoInjestor();
		classLevelMetadata = obj.getClass().getAnnotation(Metadata.class);

		version = injestor.getVersion(obj.getClass().getName());
		jaxbContext = injestor.getContextForVersion(version);
		super.loader = LoaderFactory.createLoaderForVersion(getModelType(), version);
		try {
			marshaller = jaxbContext.createMarshaller();
			unmarshaller = jaxbContext.createUnmarshaller();
		} catch (JAXBException e) {

		}
		
	}

	private void init() {

		isInitialized = true;

		Set<String> properties = new LinkedHashSet<>();
		Set<String> keys = new LinkedHashSet<>();
		Set<String> required = new LinkedHashSet<>();

		Field[] fields = this.internalObject.getClass().getDeclaredFields();
		
		for (Field field : fields) {
			if (!field.getName().equals("any")) {
				properties.add(covertFieldToOutputFormat(field.getName()));
				Metadata annotation = field.getAnnotation(Metadata.class);
				XmlElement xmlAnnotation = field.getAnnotation(XmlElement.class);
				if (annotation != null) {
					if (annotation.isKey()) {
						keys.add(covertFieldToOutputFormat(field.getName()));
					}
				}
				if (xmlAnnotation != null) {
					if (xmlAnnotation.required()) {
						required.add(covertFieldToOutputFormat(field.getName()));
					}
				}
			}
		}
		properties = Collections.unmodifiableSet(properties);
		this.properties = properties;
		
		keys = Collections.unmodifiableSet(keys);
		this.keys = keys;
		
		required = Collections.unmodifiableSet(required);
		this.requiredProperties = required;
		
	}
	private String covertFieldToOutputFormat(String propName) {
		return CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_HYPHEN, propName);
	}
	
	@Override
	public boolean hasProperty(String name) {
		//TODO 
		return true;
	}
	
	@Override
	/**
	 * Gets the value of the property via reflection
	 */
	public Object get(String name) {
		String getMethodName = "get" + CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_CAMEL, name);
		try {
			return this.internalObject.getClass().getDeclaredMethod(getMethodName).invoke(this.internalObject);			
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
			return null;
		}
	}

	@Override
	public void set(String name, Object value) {
		String setMethodName = "set" + CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_CAMEL, name);
		try {
			this.internalObject.getClass().getDeclaredMethod(setMethodName, value.getClass()).invoke(this.internalObject, value);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
			ErrorLogHelper.logError("AAI_4017", "Error setting name/value pair on POJO: " + e.getMessage());
		}
	}

	@Override
	public Set<String> getProperties() {

		if(!isInitialized){
			this.init();
		}
		return this.properties;
	}

	
	@Override
	public Set<String> getRequiredProperties() {

		if(!isInitialized) {
			this.init();
		}
		return this.requiredProperties;
	}

	@Override
	public Set<String> getKeys() {

		if(!isInitialized){
			this.init();
		}
		return this.keys;
	}

	public Class<?> getClass(String name) {

		Field field = null;
		try {
			field = this.internalObject.getClass().getDeclaredField(CaseFormat.LOWER_HYPHEN.to(CaseFormat.LOWER_CAMEL, name));
		} catch (NoSuchFieldException | SecurityException e) {
			
			return null;
		}
		
		return field.getType();
	}
	
	public Class<?> getGenericTypeClass(String name) {
		
		try {
			String getMethodName = "get" + CaseFormat.LOWER_HYPHEN.to(CaseFormat.UPPER_CAMEL, name);
			Method method = internalObject.getClass().getDeclaredMethod(getMethodName);
			Type t = method.getGenericReturnType();
			if(t instanceof ParameterizedType) {
				ParameterizedType pt = (ParameterizedType)t;
				return ((Class<?>)pt.getActualTypeArguments()[0]);
			} else {
				return null;
			}
			
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public String getJavaClassName() {
		return internalObject.getClass().getName();
	}
	
	@Override
	public Object getUnderlyingObject() {
		return this.internalObject;
	}
	
	@Override
	public String getName() {
		String className = internalObject.getClass().getSimpleName();
		
		return CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_HYPHEN, className);
	}
	
	@Override
	protected String findKey() {
		Set<String> keys = null;
		keys = this.getKeys();
		List<String> results = new ArrayList<>();
		for (String key : keys) {
			if (this.getType(key).toLowerCase().contains("long")) {
				key = ((Long)this.getValue(key)).toString();
			} else {
				key = (String)this.getValue(key);
			}
			results.add(key);
		}
		
		return Joiner.on("/").join(results);
	}
	
	@Override
	public String marshal(MarshallerProperties properties) {
		StringWriter result = new StringWriter();
        try {
        	if (properties.getMediaType().equals(MediaType.APPLICATION_JSON_TYPE)) {
				marshaller.setProperty(org.eclipse.persistence.jaxb.MarshallerProperties.MEDIA_TYPE, "application/json");
		        marshaller.setProperty(org.eclipse.persistence.jaxb.MarshallerProperties.JSON_INCLUDE_ROOT, properties.getIncludeRoot());
		        marshaller.setProperty(org.eclipse.persistence.jaxb.MarshallerProperties.JSON_WRAPPER_AS_ARRAY_NAME, properties.getWrapperAsArrayName());
        	}
	        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, properties.getFormatted());
	        marshaller.marshal(this.internalObject, result);
		} catch (JAXBException e) {
			//e.printStackTrace();
		}

        return result.toString();
	}
	
	@Override
	public Object clone() {
		Object result = null;
		 try {
				unmarshaller = jaxbContext.createUnmarshaller();

		        unmarshaller.setProperty(UnmarshallerProperties.MEDIA_TYPE, "application/json");
		        unmarshaller.setProperty(UnmarshallerProperties.JSON_INCLUDE_ROOT, false);
				unmarshaller.setProperty(UnmarshallerProperties.JSON_WRAPPER_AS_ARRAY_NAME, true);
				
				result = unmarshaller.unmarshal(new StreamSource(new StringReader(this.marshal(true))), this.internalObject.getClass()).getValue();
			 } catch (JAXBException e) {
					// TODO Auto-generated catch block
					//e.printStackTrace();
			}
		 result = IntrospectorFactory.newInstance(getModelType(), result);
		 return result;
	}
	
	@Override
	public String preProcessKey (String key) {
		String result = "";
		//String trimmedRestURI = restURI.replaceAll("/[\\w\\-]+?/[\\w\\-]+?$", "");
		String[] split = key.split("/");
		int i = 0;
		for (i = split.length-1; i >= 0; i--) {
			
			if (keyProps.containsKey(split[i])) {
				break;
				
			}
			
		}
		result = Joiner.on("/").join(Arrays.copyOfRange(split, 0, i));
		
		return result;
		
	}
	
	@Override
	public ModelType getModelType() {
		return ModelType.POJO;
	}

	@Override
	public String getChildName() {
		String className = internalObject.getClass().getSimpleName();
		String lowerHyphen = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_HYPHEN, className);
		
		if (this.isContainer()) {
			lowerHyphen = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_HYPHEN,this.getGenericTypeClass(this.getProperties().iterator().next()).getSimpleName());
		}
		
		return lowerHyphen;
	}

	@Override
	public Map<PropertyMetadata, String> getPropertyMetadata(String prop) {
		Field f;
		Map<PropertyMetadata, String> result = new HashMap<>();
		try {
			f = internalObject.getClass().getField(prop);
			Metadata m = f.getAnnotation(Metadata.class);
			if (m != null) {
				Field[] fields = m.getClass().getFields();
				String fieldName;
				for (Field field : fields) {
					fieldName = field.getName();
					if (fieldName.equals("isAbstract")) {
						fieldName = "abstract";
					} else if (fieldName.equals("extendsFrom")) {
						fieldName = "extends";
					}
					fieldName = CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_UNDERSCORE, fieldName);
					result.put(PropertyMetadata.valueOf(fieldName), (String)field.get(m));
				}
			}
		} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
			// TODO Auto-generated catch block
		}
		
		return result;
	}

	@Override
	public String getObjectId() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public String getMetadata(ObjectMetadata metadataName) {
		String value = null;
		String methodName;
		if (ObjectMetadata.ABSTRACT.equals(metadataName)) {
			methodName = "isAbstract";
		} else if (ObjectMetadata.EXTENDS.equals(metadataName)) {
			methodName = "extendsFrom";
		} else {
			methodName = metadataName.toString();
		}
		
		try {
			value = (String)this.classLevelMetadata.getClass().getMethod(methodName).invoke(classLevelMetadata);
		} catch (IllegalArgumentException | IllegalAccessException | SecurityException | InvocationTargetException | NoSuchMethodException e) {
			//TODO
		}
		
		return value;
	}

	@Override
	public Version getVersion() {
		// TODO Auto-generated method stub
		return null;
	}
}
