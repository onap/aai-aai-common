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

package org.onap.aai.ingestModel;

import org.onap.aai.introspection.Introspector;
import org.onap.aai.introspection.Loader;
import org.onap.aai.introspection.LoaderFactory;
import org.onap.aai.introspection.ModelType;
import org.onap.aai.setup.SchemaVersion;
import org.onap.aai.util.AAIConfig;
import org.onap.aai.util.AAIConstants;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import javax.xml.transform.stream.StreamSource;
import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;

/**
 * The Class CreateWidgetModels.
 */
public class CreateWidgetModels {
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

        AnnotationConfigApplicationContext ctx =
                new AnnotationConfigApplicationContext("org.onap.aai.config", "org.onap.aai.setup");

        LoaderFactory loaderFactory = ctx.getBean(LoaderFactory.class);
        Loader loader = loaderFactory.createLoaderForVersion(ModelType.MOXY, new SchemaVersion(_apiVersion));

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

                Set<String> introspectorProperties = aaiRes.getProperties();

                if (!(introspectorProperties.contains("model-version-id")
                        && introspectorProperties.contains("model-invariant-id"))) {
                    System.out.println(aaiRes.getDbName() + " does not contain model properties so skipping");
                }
                processedWidgets.add(resource);

                String widgetName = resource;
                String filePathString = widgetJsonDir + "/" + widgetName + "-" + modelVersion + ".json";
                File f = new File(filePathString);

                String filePathString2 =
                        widgetJsonDir + "/../widget-model-json-old/" + widgetName + "-" + modelVersion + ".json";
                File f2 = new File(filePathString2);

                if (!f.exists() && !f.isDirectory()) {

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
                        meObject.setValue("model-vers", mevsObject.getUnderlyingObject());
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
