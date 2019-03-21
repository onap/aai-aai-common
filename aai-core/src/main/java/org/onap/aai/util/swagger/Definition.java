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

package org.onap.aai.util.swagger;

import java.util.List;

public class Definition {

    private String definitionName;

    private String definitionDescription;

    private List<Property> propertyList;

    private List<Property> schemaPropertyList;

    private List<Property> regularPropertyList;

    private boolean hasDescription;

    public String getDefinitionName() {
        return definitionName;
    }

    public void setDefinitionName(String definitionName) {
        this.definitionName = definitionName;
    }

    public List<Property> getPropertyList() {
        return propertyList;
    }

    public void setPropertyList(List<Property> propertyList) {
        this.propertyList = propertyList;
    }

    public String getDefinitionDescription() {
        return definitionDescription;
    }

    public void setDefinitionDescription(String definitionDescription) {
        this.definitionDescription = definitionDescription;
    }

    @Override
    public String toString() {
        return "Definition{" + "definitionName='" + definitionName + '\''
            + ", definitionDescription='" + definitionDescription + '\'' + ", propertyList="
            + propertyList + '}';
    }

    public boolean isHasDescription() {
        return hasDescription;
    }

    public void setHasDescription(boolean hasDescription) {
        this.hasDescription = hasDescription;
    }

    public List<Property> getSchemaPropertyList() {
        return schemaPropertyList;
    }

    public void setSchemaPropertyList(List<Property> schemaPropertyList) {
        this.schemaPropertyList = schemaPropertyList;
    }

    public List<Property> getRegularPropertyList() {
        return regularPropertyList;
    }

    public void setRegularPropertyList(List<Property> regularPropertyList) {
        this.regularPropertyList = regularPropertyList;
    }

    public static class Property {

        private String propertyName;

        private String propertyDescription;

        private boolean hasPropertyDescription;

        private String propertyType;

        private boolean hasType;

        private String propertyReference;

        private String propertyReferenceObjectName;

        private boolean isRequired;

        private boolean hasPropertyReference;

        public Property() {
        }

        public String getPropertyName() {
            return propertyName;
        }

        public void setPropertyName(String propertyName) {
            this.propertyName = propertyName;
        }

        public String getPropertyType() {
            return propertyType;
        }

        public void setPropertyType(String propertyType) {
            this.propertyType = propertyType;
        }

        public String getPropertyReference() {
            return propertyReference;
        }

        public void setPropertyReference(String propertyReference) {
            this.propertyReference = propertyReference;
        }

        @Override
        public String toString() {
            return "Property{" + "propertyName='" + propertyName + '\'' + ", propertyType='"
                + propertyType + '\'' + ", propertyReference='" + propertyReference + '\'' + '}';
        }

        public boolean isHasType() {
            return hasType;
        }

        public void setHasType(boolean hasType) {
            this.hasType = hasType;
        }

        public boolean isRequired() {
            return isRequired;
        }

        public void setRequired(boolean required) {
            isRequired = required;
        }

        public boolean isHasPropertyReference() {
            return hasPropertyReference;
        }

        public void setHasPropertyReference(boolean hasPropertyReference) {
            this.hasPropertyReference = hasPropertyReference;
        }

        public String getPropertyReferenceObjectName() {
            return propertyReferenceObjectName;
        }

        public void setPropertyReferenceObjectName(String propertyReferenceObjectName) {
            this.propertyReferenceObjectName = propertyReferenceObjectName;
        }

        public String getPropertyDescription() {
            return propertyDescription;
        }

        public void setPropertyDescription(String propertyDescription) {
            this.propertyDescription = propertyDescription;
        }

        public boolean isHasPropertyDescription() {
            return hasPropertyDescription;
        }

        public void setHasPropertyDescription(boolean hasPropertyDescription) {
            this.hasPropertyDescription = hasPropertyDescription;
        }
    }
}
