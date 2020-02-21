/**
 * ============LICENSE_START=======================================================
 * org.onap.aai
 * ================================================================================
 * Copyright © 2017-2018 AT&T Intellectual Property. All rights reserved.
 * ================================================================================
 *  Modifications Copyright © 2018 IBM.
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
package org.onap.aai.prevalidation;

import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.Objects;

public class Validation {

    @SerializedName("validationId")
    private String validationId;

    @SerializedName("action")
    private String action;

    @SerializedName("violations")
    private List<Violation> violations;

    public String getValidationId() {
        return validationId;
    }

    public void setValidationId(String validationId) {
        this.validationId = validationId;
    }

    public List<Violation> getViolations() {
        return violations;
    }

    public void setViolations(List<Violation> violations) {
        this.violations = violations;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Validation that = (Validation) o;
        return Objects.equals(validationId, that.validationId) &&
            Objects.equals(action, that.action) &&
            Objects.equals(violations, that.violations);
    }

    @Override
    public int hashCode() {
        return Objects.hash(validationId, action, violations);
    }

    @Override
    public String toString() {
        return "Validation{" +
            "validationId='" + validationId + '\'' +
            ", action='" + action + '\'' +
            ", violations=" + violations +
            '}';
    }

}
