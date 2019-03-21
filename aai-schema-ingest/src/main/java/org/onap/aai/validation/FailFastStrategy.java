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

/**
 * 
 */

package org.onap.aai.validation;

/**
 * Fails out the validation process as soon as
 * an error is found. Tells the validation's calling
 * process to abort.
 */
public class FailFastStrategy implements SchemaErrorStrategy {
    private boolean isOK = true;
    private String errorMsg = "No errors found.";

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
        return errorMsg;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.onap.aai.edges.validation.SchemaErrorStrategy#notifyOnError(java.lang.String)
     */
    @Override
    public void notifyOnError(String errorMsg) {
        isOK = false;
        this.errorMsg = errorMsg;
        throw new AAISchemaValidationException(errorMsg);
    }

}
