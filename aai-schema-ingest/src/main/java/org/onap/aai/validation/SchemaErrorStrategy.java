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

package org.onap.aai.validation;

/**
 * Controls response to finding problems in the schema files.
 */
public interface SchemaErrorStrategy {
    /**
     * Gives if it is OK to proceed with whatever process
     * invoked the validation (probably the installation of
     * the A&AI instance).
     * 
     * @return boolean
     */
    public boolean isOK();

    /**
     * Gets the error message(s) gathered in the course
     * of validation.
     * 
     * @return String error message or messages concatenated together
     */
    public String getErrorMsg();

    /**
     * Invokes the ErrorStrategy to do whatever response to
     * an issue in the schema having been found.
     * 
     * Options:
     * -Throw an exception if the whole process should be
     * immediately aborted
     * -Set OK status to false, store the message and allow the
     * validation process to continue and find any other issues
     * -Completely ignore that something is wrong
     * etc.
     * 
     * @param String errorMsg - the error message from the validator module
     */
    public void notifyOnError(String errorMsg);
}
