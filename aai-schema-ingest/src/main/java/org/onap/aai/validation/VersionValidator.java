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
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ============LICENSE_END=========================================================
 */

package org.onap.aai.validation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Verifies that the schema config covers
 * all required versions
 */
@Component
public class VersionValidator {
    private SchemaErrorStrategy strat;
    private VersionValidationModule verMod;

    @Autowired
    public VersionValidator(SchemaErrorStrategy strategy, VersionValidationModule verMod) {
        this.strat = strategy;
        this.verMod = verMod;
    }

    public boolean validate() {
        String result = verMod.validate();
        if (!"".equals(result)) {
            strat.notifyOnError(result);
        }

        return strat.isOK();
    }

    public String getErrorMsg() {
        return strat.getErrorMsg();
    }
}
