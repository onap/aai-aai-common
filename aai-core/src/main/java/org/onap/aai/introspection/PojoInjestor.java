/**
 * ============LICENSE_START=======================================================
 * org.onap.aai
 * ================================================================================
 * Copyright © 2017-2018 AT&T Intellectual Property. All rights reserved.
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
 */
package org.onap.aai.introspection;

import com.att.eelf.configuration.EELFLogger;
import com.att.eelf.configuration.EELFManager;
import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.onap.aai.db.props.AAIProperties;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PojoInjestor {
	private static final EELFLogger LOGGER = EELFManager.getInstance().getLogger(PojoInjestor.class);
	
	private String POJO_CLASSPATH = "org.onap.aai.domain.yang";
	private final Pattern classNamePattern = Pattern.compile("\\.(v\\d+)\\.");

	public PojoInjestor() {
	}
	
	public JAXBContext getContextForVersion(Version v) {
		JAXBContext context = null;
		try {
			if (!v.equals(AAIProperties.LATEST)) {
				POJO_CLASSPATH += "." + v; 
			}
			context = JAXBContextFactory.createContext(POJO_CLASSPATH, this.getClass().getClassLoader());
		} catch (JAXBException e) {
			LOGGER.error(e.getMessage(),e);
		}
		
		return context;
	}
	public Version getVersion (String classname) {
		Matcher m = classNamePattern.matcher(classname);
		String version;
		if (m.find()) {
			version = m.group(1);
		} else {
			//only POJOs of old versions have the version number in their classnames
			//so if we can't find a version, default to the latest
			version = AAIProperties.LATEST.toString();
		}
		
		return Version.valueOf(version);
	}
	
}
