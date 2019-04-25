/**
 * ﻿============LICENSE_START=======================================================
 * org.onap.aai
 * ================================================================================
 * Copyright © 2019 AT&T Intellectual Property. All rights reserved.
 * Copyright © 2019 Amdocs
 * ================================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ============LICENSE_END=========================================================
 */
package org.onap.aai.schemaif.oxm;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.persistence.dynamic.DynamicType;
import org.eclipse.persistence.internal.oxm.mappings.Descriptor;
import org.eclipse.persistence.jaxb.dynamic.DynamicJAXBContext;
import org.onap.aai.cl.eelf.LoggerFactory;
import org.onap.aai.nodes.NodeIngestor;
import org.onap.aai.schemaif.SchemaProviderException;
import org.onap.aai.schemaif.SchemaProviderMsgs;
import org.onap.aai.setup.SchemaVersion;
import org.onap.aai.setup.Translator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * This class contains all of the logic for importing OXM model schemas from the available OXM
 * schema files.
 */
@Component
public class OxmSchemaLoader {

    private static Translator translator;
    private static NodeIngestor nodeIngestor;

    private static Map<String, DynamicJAXBContext> versionContextMap = new ConcurrentHashMap<>();
    private static Map<String, HashMap<String, DynamicType>> xmlElementLookup = new ConcurrentHashMap<>();

    final static Pattern versionPattern = Pattern.compile("(?i)v(\\d*)");

    private static org.onap.aai.cl.api.Logger logger =
            LoggerFactory.getInstance().getLogger(OxmSchemaLoader.class.getName());

    private OxmSchemaLoader() {}

    /**
     * This constructor presents an awkward marrying of Spring bean creation and static method use. This
     * is technical debt that will need fixing.
     *
     * @param translator contains schema versions configuration
     * @param nodeIngestor provides DynamicJAXBContext for the OXM version
     */
    @Autowired
    public OxmSchemaLoader(Translator translator, NodeIngestor nodeIngestor) {
        OxmSchemaLoader.translator = translator;
        OxmSchemaLoader.nodeIngestor = nodeIngestor;
    }

    /**
     * Finds all OXM model files
     *
     * @throws SchemaProviderException
     * @throws IOException
     *
     */
    public synchronized static void loadModels() throws SchemaProviderException {
        if (logger.isDebugEnabled()) {
            logger.debug("Loading OXM Models");
        }

        for (SchemaVersion oxmVersion : translator.getSchemaVersions().getVersions()) {
            DynamicJAXBContext jaxbContext = nodeIngestor.getContextForVersion(oxmVersion);
            if (jaxbContext != null) {
                loadModel(oxmVersion.toString(), jaxbContext);
            }
        }
    }

    private synchronized static void loadModel(String oxmVersion, DynamicJAXBContext jaxbContext) {
        versionContextMap.put(oxmVersion, jaxbContext);
        loadXmlLookupMap(oxmVersion, jaxbContext);
        logger.info(SchemaProviderMsgs.LOADED_SCHEMA_FILE, oxmVersion);
    }

    /**
     * Retrieves the JAXB context for the specified OXM model version.
     *
     * @param version - The OXM version that we want the JAXB context for.
     *
     * @return - A JAXB context derived from the OXM model schema.
     *
     * @throws SchemaProviderException
     */
    public static DynamicJAXBContext getContextForVersion(String version) throws SchemaProviderException {

        // If we haven't already loaded in the available OXM models, then do so now.
        if (versionContextMap == null || versionContextMap.isEmpty()) {
            loadModels();
        } else if (!versionContextMap.containsKey(version)) {
            throw new SchemaProviderException("Error loading oxm model: " + version);
        }

        return versionContextMap.get(version);
    }

    public static String getLatestVersion() throws SchemaProviderException {

        // If we haven't already loaded in the available OXM models, then do so now.
        if (versionContextMap == null || versionContextMap.isEmpty()) {
            loadModels();
        }

        // If there are still no models available, then there's not much we can do...
        if (versionContextMap.isEmpty()) {
            throw new SchemaProviderException("No available OXM schemas to get latest version for.");
        }

        // Iterate over the available model versions to determine which is the most
        // recent.
        Integer latestVersion = null;
        String latestVersionStr = null;
        for (String versionKey : versionContextMap.keySet()) {

            Matcher matcher = versionPattern.matcher(versionKey);
            if (matcher.find()) {

                int currentVersion = Integer.valueOf(matcher.group(1));

                if ((latestVersion == null) || (currentVersion > latestVersion)) {
                    latestVersion = currentVersion;
                    latestVersionStr = versionKey;
                }
            }
        }

        return latestVersionStr;
    }

    private static void loadXmlLookupMap(String version, DynamicJAXBContext jaxbContext) {

        @SuppressWarnings("rawtypes")
        List<Descriptor> descriptorsList = jaxbContext.getXMLContext().getDescriptors();
        HashMap<String, DynamicType> types = new HashMap<String, DynamicType>();

        for (@SuppressWarnings("rawtypes")
        Descriptor desc : descriptorsList) {

            DynamicType entity = jaxbContext.getDynamicType(desc.getAlias());
            String entityName = desc.getDefaultRootElement();
            types.put(entityName, entity);
        }
        xmlElementLookup.put(version, types);
    }
    
    /**
     * Retrieves the list of all Loaded OXM versions.
     *
     * @return - A List of Strings of all loaded OXM versions.
     *
     * @throws SpikeException
     */
    public static List<String> getLoadedOXMVersions() throws SchemaProviderException {
        // If we haven't already loaded in the available OXM models, then do so now.
        if (versionContextMap == null || versionContextMap.isEmpty()) {
            loadModels();
        }
        // If there are still no models available, then there's not much we can do...
        if (versionContextMap.isEmpty()) {
            logger.error(SchemaProviderMsgs.SCHEMA_LOAD_ERROR, "No available OXM schemas to get versions for.");
            throw new SchemaProviderException("No available OXM schemas to get latest version for.");
        }
        List<String> versions = new ArrayList<String>();
        for (String versionKey : versionContextMap.keySet()) {
            Matcher matcher = versionPattern.matcher(versionKey);
            if (matcher.find()) {
                versions.add("V" + matcher.group(1));
            }
        }
        return versions;
    }
    
    public static HashMap<String, DynamicType> getXmlLookupMap(String version) {
        return xmlElementLookup.get(version);
    }

}
