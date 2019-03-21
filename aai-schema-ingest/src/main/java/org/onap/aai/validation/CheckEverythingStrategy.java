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

/**
 * 
 */

package org.onap.aai.validation;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

/**
 * When an error is found, mark that it is NOT ok to
 * continue with installation/whatever other caller function,
 * and keep track of the message but
 * keep validating so all issues are found in one run.
 */
public class CheckEverythingStrategy implements SchemaErrorStrategy {
    private boolean isOK = true;
    private List<String> errorMsgs = new ArrayList<>();

    /*
     * (non-Javadoc)
     * 
     * @see org.onap.aai.edges.validation.SchemaErrorStrategy#isOK()
     */
    @Override
    public boolean isOK() {
        return isOK;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.onap.aai.edges.validation.SchemaErrorStrategy#getErrorMsg()
     */
    @Override
    public String getErrorMsg() {
        if (errorMsgs.isEmpty()) {
            return "No errors found.";
        } else {
            return StringUtils.join(errorMsgs, "\n");
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.onap.aai.edges.validation.SchemaErrorStrategy#notifyOnError(java.lang.String)
     */
    @Override
    public void notifyOnError(String errorMsg) {
        isOK = false;
        errorMsgs.add(errorMsg);
    }

}
