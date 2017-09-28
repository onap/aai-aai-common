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
import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.eclipse.persistence.jaxb.UnmarshallerProperties;
import org.onap.aai.db.props.AAIProperties;
import org.onap.aai.introspection.exceptions.AAIUnknownObjectException;
import org.onap.aai.introspection.exceptions.AAIUnmarshallingException;
import org.onap.aai.logging.ErrorLogHelper;
import org.onap.aai.restcore.MediaType;
import org.onap.aai.workarounds.NamingExceptions;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;
import java.io.StringReader;
import java.util.Map;

public class PojoLoader extends Loader {

	private static final EELFLogger LOGGER = EELFManager.getInstance().getLogger(PojoLoader.class);
	private static final String POJO_BASE_PACKAGE_NAME = "org.onap.aai.domain.yang";

	protected JAXBContext context;
	private final String pojoPackageName;

	protected PojoLoader(Version version) {
		super(version, ModelType.POJO);

		if (!version.equals(AAIProperties.LATEST)) {
			pojoPackageName = POJO_BASE_PACKAGE_NAME + "." + version;
		} else {
			pojoPackageName = POJO_BASE_PACKAGE_NAME;
		}

		try {
			context = JAXBContextFactory.createContext(pojoPackageName, this.getClass().getClassLoader());
		} catch (JAXBException e) {
			LOGGER.error("JAXBException while instantiation contect for PojoLoader", e);
		}
	}

	@Override
	public Introspector introspectorFromName(String name) throws AAIUnknownObjectException {
		return IntrospectorFactory.newInstance(ModelType.POJO, objectFromName(name));
	}
	
	@Override
	public Object objectFromName(String name) throws AAIUnknownObjectException {

		if (name == null) {
			throw new AAIUnknownObjectException("null name passed in");
		}
		final String sanitizedName = NamingExceptions.getInstance().getObjectName(name);
		final String upperCamel;

		//Contains any uppercase, then assume it's upper camel
		if (sanitizedName.matches(".*[A-Z].*")) {
			upperCamel = sanitizedName;
		} else {
			upperCamel = CaseFormat.LOWER_HYPHEN.to(CaseFormat.UPPER_CAMEL, sanitizedName);
		}

		final String objectClassName;
		
		if (!upperCamel.contains(pojoPackageName)) {
			objectClassName = pojoPackageName + "." + upperCamel;
		} else {
			objectClassName = upperCamel;
		}

		try {
			return Class.forName(objectClassName).newInstance();
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
			throw new AAIUnknownObjectException("Unrecognized AAI object " + name);
		}
	}

	@Override
	protected void process(Version version) {
		LOGGER.warn("PojoLoader.process(Version) has not been implemented");
	}

	@Override
	public Introspector unmarshal(String type, String json, MediaType mediaType) throws AAIUnmarshallingException {

		 try {
			final Unmarshaller unmarshaller = context.createUnmarshaller();

			if (mediaType.equals(MediaType.APPLICATION_JSON_TYPE)) {
		        unmarshaller.setProperty(UnmarshallerProperties.MEDIA_TYPE, "application/json");
		        unmarshaller.setProperty(UnmarshallerProperties.JSON_INCLUDE_ROOT, false);
				unmarshaller.setProperty(UnmarshallerProperties.JSON_WRAPPER_AS_ARRAY_NAME, true);
			}

			final Object clazz = objectFromName(type);
			final Object obj = unmarshaller.unmarshal(new StreamSource(new StringReader(json)), clazz.getClass()).getValue();

			return IntrospectorFactory.newInstance(ModelType.POJO, obj);
		 } catch (JAXBException e) {
			ErrorLogHelper.logError("AAI_4007", "Could not unmarshall: " + e.getMessage());
			throw new AAIUnmarshallingException("Could not unmarshall: " + e.getMessage());
		} catch (AAIUnknownObjectException e) {
			throw new AAIUnmarshallingException("Could not unmarshall: " + e.getMessage(), e);
		}
	}

	@Override
	public Map<String, Introspector> getAllObjects() {
		//TODO
		return null;
	}

}
