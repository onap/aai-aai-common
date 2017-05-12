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
import java.io.StringWriter;
import java.util.ArrayList;

import javax.xml.transform.stream.StreamSource;

import org.eclipse.persistence.dynamic.DynamicEntity;
import org.eclipse.persistence.jaxb.JAXBMarshaller;
import org.eclipse.persistence.jaxb.JAXBUnmarshaller;
import org.eclipse.persistence.jaxb.MarshallerProperties;
import org.eclipse.persistence.jaxb.dynamic.DynamicJAXBContext;

import org.openecomp.aai.util.AAIConfig;
import org.openecomp.aai.util.AAIConstants;

/**
 * The Class ConvertXmlToJsonMoxyOxm.
 */
public class ConvertXmlToJsonMoxyOxm
{
	/**
	 * The main method.
	 *
	 * @param args the arguments
	 * @throws Exception the exception
	 */
	public static void main(String[] args) throws Exception {

		String _apiVersion = AAIConfig.get(AAIConstants.AAI_DEFAULT_API_VERSION_PROP);
		String fileName = null;
		String dynamicType = null;
		if (args.length > 0) { 
			if (args[0] != null) {
				_apiVersion = args[0];
			}
			if (args[1] != null) { 
				fileName = args[1];
			}
			if (args[2] != null) { 
				dynamicType = args[2];
			}
		}
		
		if (fileName == null) { 
			System.err.println("You must specify a fileName");
			System.exit(0);
		}
		if (dynamicType == null) { 
			System.err.println("You must specify a dynamic Type");
			System.exit(0);
		}
		
		ArrayList<String> apiVersions = new ArrayList<String>();
		apiVersions.add(_apiVersion);
		final IngestModelMoxyOxm m = new IngestModelMoxyOxm();
		m.init(apiVersions, false);

		DynamicJAXBContext jaxbContext = IngestModelMoxyOxm.aaiResourceContainer.get(_apiVersion).getJaxbContext();

		JAXBUnmarshaller unmarshaller = jaxbContext.createUnmarshaller();

		Class<? extends DynamicEntity> resultClass = jaxbContext.newDynamicEntity(dynamicType).getClass();

		DynamicEntity meObject = (DynamicEntity) unmarshaller.unmarshal(new StreamSource(new File(fileName)), resultClass).getValue();

		// put it out as JSON
		
		JAXBMarshaller marshaller = jaxbContext.createMarshaller();
		marshaller.setProperty(JAXBMarshaller.JAXB_FORMATTED_OUTPUT, true);

		marshaller.setProperty("eclipselink.media-type", "application/json");
		marshaller.setProperty("eclipselink.json.include-root", false);
		marshaller.setProperty(MarshallerProperties.JSON_MARSHAL_EMPTY_COLLECTIONS, Boolean.FALSE) ;

		StringWriter writer = new StringWriter();
		marshaller.marshal(meObject, writer);
		
		System.out.println(writer.toString());
		
		System.exit(0);
	}

}
