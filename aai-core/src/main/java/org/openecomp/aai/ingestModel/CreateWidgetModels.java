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
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.UUID;

import javax.xml.transform.stream.StreamSource;

import org.openecomp.aai.introspection.Introspector;
import org.openecomp.aai.introspection.Loader;
import org.openecomp.aai.introspection.LoaderFactory;
import org.openecomp.aai.introspection.ModelType;
import org.openecomp.aai.introspection.Version;
import org.openecomp.aai.util.AAIConfig;
import org.openecomp.aai.util.AAIConstants;

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


		Loader loader = LoaderFactory.createLoaderForVersion(ModelType.MOXY, Version.valueOf(_apiVersion));

		// iterate the collection of resources

		ArrayList<String> processedWidgets = new ArrayList<String>();
		for (Entry<String, Introspector> aaiResEnt : loader.getAllObjects().entrySet()) {
			Introspector meObject = loader.introspectorFromName("model");
			// no need for a ModelVers DynamicEntity

			Introspector aaiRes = aaiResEnt.getValue();

			if (!(aaiRes.isContainer() || aaiRes.getName().equals("aai-internal"))) {
				String resource = aaiRes.getName();

				if (processedWidgets.contains(resource)) {
					continue;
				}
				processedWidgets.add(resource);

				String widgetName = resource;
				String filePathString = widgetJsonDir + "/" + widgetName + "-" + modelVersion + ".json";
				File f = new File(filePathString);

				String filePathString2 = widgetJsonDir + "/../widget-model-json-old/" + widgetName + "-" + modelVersion + ".json";
				File f2 = new File(filePathString2);

				if(!f.exists() && !f.isDirectory()) { 

					if (f2.exists()) { 
						System.out.println("Using old file for " + resource + ".");

						meObject = loader.unmarshal("model", new StreamSource(f2).getReader().toString());
						// override, some of them are wrong
						meObject.setValue("model-version", modelVersion);
					} else {
						System.out.println("Making new file for " + resource + ".");
						meObject.setValue("model-invariant-id", UUID.randomUUID().toString());
						meObject.setValue("model-type", "widget");
						Introspector mevObject = loader.introspectorFromName("model-ver");
						Introspector mevsObject = loader.introspectorFromName("model-vers");
						mevObject.setValue("model-version-id", UUID.randomUUID().toString());
						mevObject.setValue("model-version", modelVersion);
						mevObject.setValue("model-Name", widgetName);
						// make a list of dynamic Entities
						List<Object> mevsList = new ArrayList<>();
						// add this one, it will be the only one in the list in this case
						mevsList.add(mevObject.getUnderlyingObject());
						mevsObject.setValue("model-ver", mevsList);
						// Have to figure out how to add my mev object to the mevsObject, 
						// the modelVers is a list of dynamic entities so we can just attach the array here
						meObject.setValue("model-vers",mevsObject.getUnderlyingObject());
					}

					// put it out as JSON

					PrintWriter out = new PrintWriter(f);
					out.println(meObject.marshal(true));
					out.close();

				} else { 
					System.out.println("File already exists for " + resource + ".  Skipping.");
				}
			}
		}
		System.exit(0);
	}
}
