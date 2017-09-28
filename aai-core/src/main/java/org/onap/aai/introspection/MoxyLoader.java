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
package org.onap.aai.introspection;

import com.att.eelf.configuration.EELFLogger;
import com.att.eelf.configuration.EELFManager;
import com.google.common.base.CaseFormat;
import com.google.common.collect.ImmutableMap;
import org.eclipse.persistence.dynamic.DynamicEntity;
import org.eclipse.persistence.jaxb.UnmarshallerProperties;
import org.eclipse.persistence.jaxb.dynamic.DynamicJAXBContext;
import org.onap.aai.exceptions.AAIException;
import org.onap.aai.introspection.exceptions.AAIUnknownObjectException;
import org.onap.aai.introspection.exceptions.AAIUnmarshallingException;
import org.onap.aai.logging.ErrorLogHelper;
import org.onap.aai.restcore.MediaType;
import org.onap.aai.workarounds.NamingExceptions;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.stream.StreamSource;
import java.io.IOException;
import java.io.StringReader;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class MoxyLoader extends Loader {

	private DynamicJAXBContext jaxbContext = null;
	private EELFLogger LOGGER = EELFManager.getInstance().getLogger(MoxyLoader.class);
	private Map<String, Introspector> allObjs = null;

	/**
	 * Instantiates a new moxy loader.
	 *
	 * @param version the version
	 * @param llBuilder the ll builder
	 */
	protected MoxyLoader(Version version) {
		super(version, ModelType.MOXY);
		process(version);
	}

	/**
	 * {@inheritDoc}
	 * @throws AAIUnknownObjectException 
	 */
	@Override
	public Introspector introspectorFromName(String name) throws AAIUnknownObjectException {

		return IntrospectorFactory.newInstance(ModelType.MOXY, objectFromName(name));
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object objectFromName(String name) throws AAIUnknownObjectException {

		if (name == null) {
			throw new AAIUnknownObjectException("null name passed in");
		}
		final String sanitizedName = NamingExceptions.getInstance().getObjectName(name);
		final String upperCamel;

		//Contains any uppercase, then assume it's upper camel
		if (name.matches(".*[A-Z].*")) {
			upperCamel = sanitizedName;
		} else {
			upperCamel = CaseFormat.LOWER_HYPHEN.to(CaseFormat.UPPER_CAMEL, sanitizedName);
		}
		
		try {
			final DynamicEntity result = jaxbContext.newDynamicEntity(upperCamel);

			if (result == null) throw new AAIUnknownObjectException("Unrecognized AAI object " + name);

			return result;
		} catch (IllegalArgumentException e) {
			//entity does not exist
			throw new AAIUnknownObjectException("Unrecognized AAI object " + name, e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void process(Version version) {
		ModelInjestor injestor = ModelInjestor.getInstance();
		jaxbContext = injestor.getContextForVersion(version);
		
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Introspector unmarshal(String type, String json, MediaType mediaType) throws AAIUnmarshallingException {		
		try {
			final Object clazz = objectFromName(type);
			final Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();

			if (mediaType.equals(MediaType.APPLICATION_JSON_TYPE)) {
				unmarshaller.setProperty(UnmarshallerProperties.MEDIA_TYPE, "application/json");
				unmarshaller.setProperty(UnmarshallerProperties.JSON_INCLUDE_ROOT, false);
				unmarshaller.setProperty(UnmarshallerProperties.JSON_WRAPPER_AS_ARRAY_NAME, true);
			}

			final DynamicEntity entity = (DynamicEntity) unmarshaller.unmarshal(new StreamSource(new StringReader(json)), clazz.getClass()).getValue();
			return IntrospectorFactory.newInstance(ModelType.MOXY, entity);
		} catch (JAXBException e) {
			AAIException ex = new AAIException("AAI_4007", e);
			ErrorLogHelper.logException(ex);
			throw new AAIUnmarshallingException("Could not unmarshall: " + e.getMessage(), ex);
		} catch (AAIUnknownObjectException e) {
			throw new AAIUnmarshallingException("Could not unmarshall: " + e.getMessage(), e);
		}
	}
	
	@Override
	public Map<String, Introspector> getAllObjects() {
		if (this.allObjs != null) {
			return allObjs;
		} else {
			ImmutableMap.Builder<String, Introspector> map = new ImmutableMap.Builder<String, Introspector>();
			Set<String> objs = objectsInVersion();
			for (String objName : objs) {
				try {
					Introspector introspector = this.introspectorFromName(objName);
					map.put(introspector.getDbName(), introspector);
				} catch (AAIUnknownObjectException e) {
					LOGGER.warn("Unexpected AAIUnknownObjectException while running getAllObjects()", e);
				}
			}
			allObjs = map.build();
			return allObjs;
		}
	}
	
	private Set<String> objectsInVersion() {
		final Set<String> result = new HashSet<>();

		try {
			final DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			final String fileName = ModelInjestor.getInstance().getOXMFileName(getVersion());

			docFactory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);

			final DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
			final Document doc = docBuilder.parse(fileName);
			final NodeList list = doc.getElementsByTagName("java-type");

			for (int i = 0; i < list.getLength(); i++) {
				result.add(list.item(i).getAttributes().getNamedItem("name").getNodeValue());
			}
		} catch (ParserConfigurationException | SAXException | IOException e) {
			LOGGER.warn("Exception while enumerating objects for API version " + getVersion() + " (returning partial results)", e);
		}

		//result.remove("EdgePropNames");
		return result;
	}
	
	public DynamicJAXBContext getJAXBContext() {
		return this.jaxbContext;
	}

}
