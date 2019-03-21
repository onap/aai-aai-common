/**
 * ============LICENSE_START=======================================================
 * org.onap.aai
 * ================================================================================
 * Copyright © 2017-18 AT&T Intellectual Property. All rights reserved.
 * ================================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ============LICENSE_END=========================================================
 */

package org.onap.aai.validation.nodes;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.List;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.onap.aai.setup.SchemaVersion;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Default duplicate rules for A&AI -
 * node types may never have a duplicate definition
 * within the same Version's file set.
 * 
 * Finds all duplicates and what files they're in.
 *
 */
public class DefaultDuplicateNodeDefinitionValidationModule
    implements DuplicateNodeDefinitionValidationModule {

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.onap.aai.nodes.validation.DuplicateNodeDefinitionValidationModule#findDuplicates(java.
     * util.List)
     */
    @Override
    public String findDuplicates(List<String> files, SchemaVersion v) {
        try {
            final DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            docFactory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
            final DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

            Multimap<String, String> types = ArrayListMultimap.create();
            boolean foundDups = false;
            for (String file : files) {
                InputStream inputStream = new FileInputStream(file);
                final Document doc = docBuilder.parse(inputStream);
                final NodeList list = doc.getElementsByTagName("java-type");

                for (int i = 0; i < list.getLength(); i++) {
                    String type = list.item(i).getAttributes().getNamedItem("name").getNodeValue();
                    if (types.containsKey(type)) {
                        foundDups = true;
                    }
                    types.put(type, file);
                }
            }

            if (foundDups) {
                return buildErrorMsg(types, v);
            } else {
                return "";
            }
        } catch (ParserConfigurationException | SAXException | IOException e) {
            // TODO something useful with this information
            return e.getMessage();
        }
    }

    private String buildErrorMsg(Multimap<String, String> types, SchemaVersion v) {
        StringBuilder errorMsg = new StringBuilder().append("Duplicates found in version ")
            .append(v.toString()).append(". ");
        for (String nodeType : types.keySet()) {
            Collection<String> files = types.get(nodeType);
            if (files.size() == 1) {
                continue; // only record the duplicated ones
            }
            errorMsg.append(nodeType).append(" has definitions in ");
            for (String file : files) {
                errorMsg.append(file).append(" ");
            }
        }
        return errorMsg.toString();
    }

}
