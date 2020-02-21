/**
 * ============LICENSE_START=======================================================
 * org.onap.aai
 * ================================================================================
 * Copyright © 2018-19 AT&T Intellectual Property. All rights reserved.
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

package org.onap.aai.prevalidation;

import com.google.gson.annotations.SerializedName;

import java.util.Objects;

public class Violation {

    @SerializedName("violationId")
    private String violationId;

    @SerializedName("modelName")
    private String modelName;

    @SerializedName("category")
    private String category;

    @SerializedName("severity")
    private String severity;

    @SerializedName("violationType")
    private String violationType;

    @SerializedName("errorMessage")
    private String errorMessage;

    public String getViolationId() {
        return violationId;
    }

    public void setViolationId(String violationId) {
        this.violationId = violationId;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }

    public String getViolationType() {
        return violationType;
    }

    public void setViolationType(String violationType) {
        this.violationType = violationType;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    @Override
    public String toString() {
        return "Violation{" +
            "violationId='" + violationId + '\'' +
            ", modelName='" + modelName + '\'' +
            ", category='" + category + '\'' +
            ", severity='" + severity + '\'' +
            ", violationType='" + violationType + '\'' +
            ", errorMessage='" + errorMessage + '\'' +
            '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Violation violation = (Violation) o;
        return Objects.equals(violationId, violation.violationId) &&
            Objects.equals(modelName, violation.modelName) &&
            Objects.equals(category, violation.category) &&
            Objects.equals(severity, violation.severity) &&
            Objects.equals(violationType, violation.violationType) &&
            Objects.equals(errorMessage, violation.errorMessage);
    }

    @Override
    public int hashCode() {
        return Objects.hash(violationId, modelName, category, severity, violationType, errorMessage);
    }
}
