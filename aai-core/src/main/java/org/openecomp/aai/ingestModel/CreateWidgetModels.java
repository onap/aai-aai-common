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
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;

import javax.xml.transform.stream.StreamSource;

import org.eclipse.persistence.dynamic.DynamicEntity;
import org.eclipse.persistence.jaxb.JAXBMarshaller;
import org.eclipse.persistence.jaxb.JAXBUnmarshaller;
import org.eclipse.persistence.jaxb.MarshallerProperties;
import org.eclipse.persistence.jaxb.dynamic.DynamicJAXBContext;

import org.openecomp.aai.domain.model.AAIResource;
import org.openecomp.aai.domain.model.AAIResources;
import org.openecomp.aai.util.AAIConfig;
import org.openecomp.aai.util.AAIConstants;
import com.google.common.base.CaseFormat;

/**
 * The Class CreateWidgetModels.
 */
public class CreateWidgetModels
{
	/**
	 * The main method.
	 *
	 * @param args the arguments
	 * @throws Exception the exception
	 */
	public static void main(String[] args) throws Exception {

		String _apiVersion = AAIConfig.get(AAIConstants.AAI_DEFAULT_API_VERSION_PROP);
		String widgetJsonDir = null;
		String modelVersion = null;
		if (args.length > 0) { 
			if (args[0] != null) {
				_apiVersion = args[0];
			}
			if (args[1] != null) { 
				widgetJsonDir = args[1];
			}
			if (args[2] != null) { 
				modelVersion = args[2];
			}
		}

		if (widgetJsonDir == null) { 
			System.err.println("You must specify a directory for widgetModelJson");
			System.exit(0);
		}
		if (modelVersion == null) { 
			System.err.println("You must specify a modelVersion");
			System.exit(0);
		}

		ArrayList<String> apiVersions = new ArrayList<String>();
		apiVersions.add(_apiVersion);
		final IngestModelMoxyOxm m = new IngestModelMoxyOxm();
		m.init(apiVersions, false);

		AAIResources aaiResources = IngestModelMoxyOxm.aaiResourceContainer.get(_apiVersion);

		DynamicJAXBContext jaxbContext = aaiResources.getJaxbContext();

		// iterate the collection of resources

		ArrayList<String> processedWidgets = new ArrayList<String>();
		for (Map.Entry<String, AAIResource> aaiResEnt : aaiResources.getAaiResources().entrySet()) { 
			DynamicEntity meObject = jaxbContext.newDynamicEntity("inventory.aai.openecomp.org." + _apiVersion + ".Model");
			// no need for a ModelVers DynamicEntity

			AAIResource aaiRes = aaiResEnt.getValue();

			if (aaiRes.getResourceType().equals("node")) {
				String resource = aaiRes.getSimpleName();

				if (processedWidgets.contains(resource)) {
					continue;
				}
				processedWidgets.add(resource);

				String widgetName = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_HYPHEN, resource);
				String filePathString = widgetJsonDir + "/" + widgetName + "-" + modelVersion + ".json";
				File f = new File(filePathString);

				String filePathString2 = widgetJsonDir + "/../widget-model-json-old/" + widgetName + "-" + modelVersion + ".json";
				File f2 = new File(filePathString2);

				if(!f.exists() && !f.isDirectory()) { 

					if (f2.exists()) { 
						System.out.println("Using old file for " + resource + ".");

						JAXBUnmarshaller unmarshaller = jaxbContext.createUnmarshaller();
						unmarshaller.setProperty("eclipselink.media-type", "application/json");
						unmarshaller.setProperty("eclipselink.json.include-root", false);
						Class<? extends DynamicEntity> resultClass = meObject.getClass();
						meObject = (DynamicEntity) unmarshaller.unmarshal(new StreamSource(f2), resultClass).getValue();
						// override, some of them are wrong
						meObject.set("modelVersion", modelVersion);
					} else { 

						System.out.println("Making new file for " + resource + ".");
						meObject.set("modelInvariantId", UUID.randomUUID().toString());
						meObject.set("modelType", "widget");
						DynamicEntity mevObject = jaxbContext.newDynamicEntity("inventory.aai.openecomp.org." + _apiVersion + ".ModelVer");
						DynamicEntity mevsObject = jaxbContext.newDynamicEntity("inventory.aai.openecomp.org." + _apiVersion + ".ModelVers");
						mevObject.set("modelVersionId", UUID.randomUUID().toString());
						mevObject.set("modelVersion", modelVersion);
						mevObject.set("modelName", widgetName);
						// make a list of dynamic Entities
						ArrayList<DynamicEntity> mevsList = new ArrayList<DynamicEntity>();
						// add this one, it will be the only one in the list in this case
						mevsList.add(mevObject);
						mevsObject.set("modelVer", mevsList);
						// Have to figure out how to add my mev object to the mevsObject, 
						// the modelVers is a list of dynamic entities so we can just attach the array here
						meObject.set("modelVers",mevsObject);
					}

					// put it out as JSON

					JAXBMarshaller marshaller = jaxbContext.createMarshaller();
					marshaller.setProperty(JAXBMarshaller.JAXB_FORMATTED_OUTPUT, true);

					marshaller.setProperty("eclipselink.media-type", "application/json");
					marshaller.setProperty("eclipselink.json.include-root", false);
					marshaller.setProperty(MarshallerProperties.JSON_MARSHAL_EMPTY_COLLECTIONS, Boolean.FALSE) ;

					StringWriter writer = new StringWriter();
					marshaller.marshal(meObject, writer);
					PrintWriter out = new PrintWriter(f);
					out.println(writer.toString());
					out.close();

				} else { 
					System.out.println("File already exists for " + resource + ".  Skipping.");
				}
			}
		}
		System.exit(0);
	}
}
