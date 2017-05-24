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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.bind.JAXBException;

import org.eclipse.persistence.dynamic.DynamicType;
import org.eclipse.persistence.jaxb.JAXBContextProperties;
import org.eclipse.persistence.jaxb.dynamic.DynamicJAXBContext;
import org.eclipse.persistence.jaxb.dynamic.DynamicJAXBContextFactory;

import org.openecomp.aai.util.AAIConstants;

public class ModelInjestor {
	
	private Map<Version, DynamicJAXBContext> versionContextMap = new HashMap<>();
	private static final Pattern classNamePattern = Pattern.compile("\\.(v\\d+)\\.");
	private static final Pattern uriPattern = 	Pattern.compile("(v\\d+)\\/");

	
	/**
	 * Instantiates a new model injestor.
	 */
	private ModelInjestor() {
		try {
			injestModels();
		} catch (FileNotFoundException | JAXBException e) {
			throw new RuntimeException(e);
		}
	}
	
	private static class Helper {
		private static final ModelInjestor INSTANCE = new ModelInjestor();
	}
	
	/**
	 * Gets the single instance of ModelInjestor.
	 *
	 * @return single instance of ModelInjestor
	 */
	public synchronized static ModelInjestor getInstance() {
		return Helper.INSTANCE;
	}
	
	/**
	 * Injest models.
	 *
	 * @throws FileNotFoundException the file not found exception
	 * @throws JAXBException the JAXB exception
	 */
	private void injestModels() throws FileNotFoundException, JAXBException {
		
		for (Version version : Version.values()) {
			this.injestModel(version);
		}
	}
	
	/**
	 * Injest model.
	 *
	 * @param version the version
	 * @throws JAXBException the JAXB exception
	 * @throws FileNotFoundException the file not found exception
	 */
	private void injestModel (Version version) throws JAXBException, FileNotFoundException {
		String fileName = this.getOXMFileName(version);
		InputStream iStream = new FileInputStream(new File(fileName));
		Map<String, Object> properties = new HashMap<String, Object>(); 
		properties.put(JAXBContextProperties.OXM_METADATA_SOURCE, iStream);
		final DynamicJAXBContext jaxbContext = DynamicJAXBContextFactory.createContextFromOXM(this.getClass().getClassLoader(), properties);
		versionContextMap.put(version, jaxbContext);
	}
	
	/**
	 * Gets the version from class name.
	 *
	 * @param classname the classname
	 * @return the version from class name
	 */
	public Version getVersionFromClassName (String classname) {
		Matcher m = classNamePattern.matcher(classname);
		String version = "v2"; //for the OXM, only the v2 ones don't include a model name, hence this default
		if (m.find()) {
			version = m.group(1);
		}
		
		return Version.valueOf(version);
	}
	
	/**
	 * Gets the context for URI.
	 *
	 * @param uri the uri
	 * @return the context for URI
	 */
	public DynamicJAXBContext getContextForURI(String uri) {
		DynamicJAXBContext result = null;
		Matcher m = uriPattern.matcher(uri);
		Version version = null;
		if (m.find()) {
			version = Version.valueOf(m.group(1));
			result = versionContextMap.get(version);
		}
		
		return result;
	}
	
	/**
	 * Gets the context for version.
	 *
	 * @param version the version
	 * @return the context for version
	 */
	public DynamicJAXBContext getContextForVersion(Version version) {
		DynamicJAXBContext result = null;
		
		result = versionContextMap.get(version);
		
		
		return result;
	}
	
	/**
	 * Gets the dynamic type for class name.
	 *
	 * @param classname the classname
	 * @return the dynamic type for class name
	 */
	public DynamicType getDynamicTypeForClassName(String classname) {
		DynamicType result = null;
		DynamicJAXBContext context = null;

		Version version = this.getVersionFromClassName(classname);

		context = versionContextMap.get(version);
		
		if (context != null) {
			result = context.getDynamicType(classname);
		}
		
		return result;
	}
	
	public String getOXMFileName(Version v) {
		return 	AAIConstants.AAI_HOME_ETC_OXM + "aai_oxm_" + v.toString() + ".xml";
	}
	
}
