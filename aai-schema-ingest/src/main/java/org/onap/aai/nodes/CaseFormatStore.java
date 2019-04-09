/**
 * ============LICENSE_START=======================================================
 * org.onap.aai
 * ================================================================================
 * Copyright © 2019 AT&T Intellectual Property. All rights reserved.
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
package org.onap.aai.nodes;

import com.google.common.base.CaseFormat;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * CaseFormatStore stores the converted strings from
 * lower hyphen (example-object) to lower camel case (exampleObject)
 * so it avoids the creation of the object for every single request
 * and cause an issue with taking too much memory just for the conversion
 */
public class CaseFormatStore {

    private final Map<String, String> lowerHyphenToLowerCamel = new HashMap<>();
    private final Map<String, String> lowerHyphenToUpperCamel = new HashMap<>();
    private final Map<String, String> lowerCamelToLowerHyphen = new HashMap<>();
    private final Map<String, String> upperCamelToLowerHyphen = new HashMap<>();

    CaseFormatStore(){}

    /**
     * Parses the document and creates a lower camel case string
     * upper camel string, lower hyphen and lower camel case
     *
     * @param doc   Takes an xml document and adds it to the hash maps as appropriate
     */
    void parse(Document doc){

        // Get the xml-root-element and add those nodes
        // with the attribute name and it to the hashmaps
        // For the attribute with name, it is going to be lower-hyphen
        // If the attribute is javaAttribute then it will be lower camel case
        NodeList list = doc.getElementsByTagName("xml-root-element");
        addCaseFormatForNodesAndProperties(list, "name");

        list = doc.getElementsByTagName("xml-element");
        addCaseFormatForNodesAndProperties(list, "java-attribute");

        list = doc.getElementsByTagName("xml-any-element");
        addCaseFormatForNodesAndProperties(list, "java-attribute");
    }

    private void addCaseFormatForNodesAndProperties(NodeList list, String attributeName) {
        for (int i = 0; i < list.getLength(); i++) {

            String lowerCamel = null;
            String lowerHyphen = null;

            if ("java-attribute".equals(attributeName)) {
                lowerCamel = list.item(i).getAttributes().getNamedItem(attributeName).getNodeValue();
                lowerHyphen = CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_HYPHEN, lowerCamel);
            } else {
                lowerHyphen = list.item(i).getAttributes().getNamedItem(attributeName).getNodeValue();
                lowerCamel = CaseFormat.LOWER_HYPHEN.to(CaseFormat.LOWER_CAMEL, lowerHyphen);
            }

            String upperCamel = CaseFormat.LOWER_HYPHEN.to(CaseFormat.UPPER_CAMEL, lowerHyphen);
            lowerHyphenToLowerCamel.put(lowerHyphen, lowerCamel);
            lowerHyphenToUpperCamel.put(lowerHyphen, upperCamel);
            upperCamelToLowerHyphen.put(upperCamel, lowerHyphen);
            lowerCamelToLowerHyphen.put(lowerCamel, lowerHyphen);
        }
    }

    public Optional<String> fromLowerHyphenToLowerCamel(String value){
        return Optional.ofNullable(lowerHyphenToLowerCamel.get(value));
    }

    public Optional<String> fromLowerHyphenToUpperCamel(String value){
        return Optional.ofNullable(lowerHyphenToUpperCamel.get(value));
    }

    public Optional<String> fromUpperCamelToLowerHyphen(String value){
        return Optional.ofNullable(upperCamelToLowerHyphen.get(value));
    }

    public Optional<String> fromLowerCamelToLowerHyphen(String value){
        return Optional.ofNullable(lowerCamelToLowerHyphen.get(value));
    }

}
