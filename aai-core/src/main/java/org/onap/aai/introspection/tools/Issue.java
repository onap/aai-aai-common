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

import org.onap.aai.introspection.Introspector;

public class Issue {

    private Severity severity;
    private IssueType error;
    private String detail;
    private Introspector obj;
    private String propName;
    private boolean resolved = false;

    /**
     * Sets the severity.
     *
     * @param severity the new severity
     */
    public void setSeverity(Severity severity) {

        this.severity = severity;
    }

    /**
     * Sets the error.
     *
     * @param error the new error
     */
    public void setType(IssueType error) {
        this.error = error;
    }

    /**
     * Sets the detail.
     *
     * @param detail the new detail
     */
    public void setDetail(String detail) {
        this.detail = detail;
    }

    /**
     * Gets the severity.
     *
     * @return the severity
     */
    public Object getSeverity() {
        return this.severity;
    }

    /**
     * Sets the introspector.
     *
     * @param obj the new introspector
     */
    public void setIntrospector(Introspector obj) {
        this.obj = obj;
    }

    /**
     * Gets the introspector.
     *
     * @return the introspector
     */
    public Introspector getIntrospector() {
        return this.obj;
    }

    /**
     * Gets the detail.
     *
     * @return the detail
     */
    public String getDetail() {
        return this.detail;
    }

    /**
     * Gets the error.
     *
     * @return the error
     */
    public IssueType getType() {
        return this.error;
    }

    /**
     * Sets the prop name.
     *
     * @param prop the new prop name
     */
    public void setPropName(String prop) {
        this.propName = prop;
    }

    /**
     * Gets the prop name.
     *
     * @return the prop name
     */
    public String getPropName() {
        return this.propName;
    }

    /**
     * Checks if is resolved.
     *
     * @return true, if is resolved
     */
    public boolean isResolved() {
        return resolved;
    }

    /**
     * Sets the resolved.
     *
     * @param resolved the new resolved
     */
    public void setResolved(boolean resolved) {
        this.resolved = resolved;
    }

}
