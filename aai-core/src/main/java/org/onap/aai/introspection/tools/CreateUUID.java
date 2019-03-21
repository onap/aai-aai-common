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
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ============LICENSE_END=========================================================
 */

package org.onap.aai.introspection.tools;

import java.util.Map;
import java.util.UUID;

import org.onap.aai.introspection.Introspector;
import org.onap.aai.schema.enums.PropertyMetadata;

/**
 * <b>CreateUUID</b> is an issue resolver that is responsible
 * for looking to check if the property that is missing has
 * the metadata autoGenerateUuid associated to it in the oxm
 * As if that is found, then it will automatically resolve the
 * issue by generating a uuid and setting that value to that property
 *
 * If this is needed for a specific property that you need
 * then you need to add the following xml code in the oxm
 *
 * <pre>
 *     {@code
 *      <xml-element java-attribute="myElementProp" name="my-element-prop" type="java.lang.String">
 *      <xml-properties>
 *          <xml-property name="autoGenerateUuid" value="true" />
 *      </xml-properties>
 *     }
 * </pre>
 */
public class CreateUUID implements IssueResolver {

    /**
     * Resolves the issue by checking if the issue type is missing key prop
     * and if it is it will retrieve the introspector associated with the issue
     * then gets the metadata associated to that specific property
     * and if it contains the auto generate meta property and if it does
     * then it will fix it by setting that property value to generated uuid
     *
     * @param issue the issue with the details associated to the problem
     * @return true if the issue has been successfully resolved
     *         false otherwise
     */
    @Override
    public boolean resolveIssue(Issue issue) {

        Introspector obj = issue.getIntrospector();
        if (issue.getType().equals(IssueType.MISSING_KEY_PROP)) {
            Map<PropertyMetadata, String> metadata = obj.getPropertyMetadata(issue.getPropName());
            if (metadata.containsKey(PropertyMetadata.AUTO_GENERATE_UUID)
                && metadata.get(PropertyMetadata.AUTO_GENERATE_UUID).equals("true")) {
                obj.setValue(issue.getPropName(), UUID.randomUUID().toString());
                return true;
            }
        }

        return false;
    }

}
