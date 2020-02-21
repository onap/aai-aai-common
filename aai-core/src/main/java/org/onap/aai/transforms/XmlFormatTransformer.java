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
package org.onap.aai.transforms;

import com.bazaarvoice.jolt.Chainr;
import com.bazaarvoice.jolt.JsonUtils;
import org.json.JSONObject;
import org.json.XML;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class XmlFormatTransformer {

    private static final Logger LOGGER = LoggerFactory.getLogger(XmlFormatTransformer.class);

    private static final String RESULT_WITH_QUOTES = "\"result\"";
    private static final String RESULTS_STRING = "results";

    private Chainr chainr;

    public XmlFormatTransformer() {
        List<Object> spec = JsonUtils.classpathToList("/specs/transform-related-to-node.json");
        this.chainr       = Chainr.fromSpec(spec);
    }

    public String transform(String input) {

        Object transformedOutput;

        if(!input.contains(RESULT_WITH_QUOTES)){
            Object inputMap = JsonUtils.jsonToMap(input);
            transformedOutput = chainr.transform(inputMap);

            JSONObject jsonObject;
            if(transformedOutput == null){
                LOGGER.debug("For the input {}, unable to transform it so returning null", input);
                jsonObject = new JSONObject();
            } else {
                jsonObject = new JSONObject(JsonUtils.toJsonString(transformedOutput));
            }

            return XML.toString(jsonObject, RESULTS_STRING);
        } else {
            // If the json is already conforming to the following format
            // {"results":[{"results":"v[2]"}]}
            // Then no transformation is required
            return XML.toString(new JSONObject(input));
        }

    }
}
