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
import java.util.Map;

public class Api {

    private String path;

    private List<HttpVerb> httpMethods;

    private String tag;

    public List<HttpVerb> getHttpMethods() {
        return httpMethods;
    }

    public void setHttpMethods(List<HttpVerb> httpMethods) {
        this.httpMethods = httpMethods;
    }

    public String getTag() {

        if (this.tag != null) {
            return this.tag;
        }

        if (this.httpMethods != null) {
            if (this.httpMethods.size() != 0) {
                if (this.httpMethods.get(0).getTags() != null) {
                    if (this.httpMethods.get(0).getTags().size() != 0) {
                        this.tag = this.httpMethods.get(0).getTags().get(0);
                    }
                }
            }
        }

        if (this.tag == null) {
            this.tag = "";
        }

        return this.tag;
    }

    @Override
    public String toString() {
        return "Api{" + "path='" + path + '\'' + ", httpMethods=" + httpMethods + '}';
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getPath() {
        return this.path;
    }

    public String getOperation() {

        if (this.path != null) {
            return this.path.replaceAll("[^a-zA-Z0-9\\-]", "-") + "-";
        }

        return "";
    }

    public static class HttpVerb {

        private List<String> tags;

        private String type;

        private String summary;

        private String operationId;

        private List<String> consumes;

        private boolean consumerEnabled;

        private List<String> produces;

        private List<Response> responses;

        private List<Map<String, Object>> parameters;

        private Map<String, Object> bodyParameters;

        private boolean bodyParametersEnabled;

        private boolean parametersEnabled;

        private String schemaLink;

        private String schemaType;

        private boolean hasReturnSchema;

        private String returnSchemaLink;

        private String returnSchemaObject;

        public void setConsumerEnabled(boolean consumerEnabled) {
            this.consumerEnabled = consumerEnabled;
        }

        public boolean isConsumerEnabled() {
            return consumerEnabled;
        }

        public List<String> getTags() {
            return tags;
        }

        public void setTags(List<String> tags) {
            this.tags = tags;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getSummary() {
            return summary;
        }

        public void setSummary(String summary) {
            this.summary = summary;
        }

        public String getOperationId() {
            return operationId;
        }

        public void setOperationId(String operationId) {
            this.operationId = operationId;
        }

        public List<String> getConsumes() {
            return consumes;
        }

        public void setConsumes(List<String> consumes) {
            this.consumes = consumes;
        }

        public List<String> getProduces() {
            return produces;
        }

        public void setProduces(List<String> produces) {
            this.produces = produces;
        }

        public List<Response> getResponses() {
            return responses;
        }

        public void setResponses(List<Response> responses) {
            this.responses = responses;
        }

        public List<Map<String, Object>> getParameters() {
            return parameters;
        }

        public void setParameters(List<Map<String, Object>> parameters) {
            this.parameters = parameters;
        }

        @Override
        public String toString() {
            return "HttpVerb{" + "tags=" + tags + ", type='" + type + '\'' + ", summary='" + summary
                + '\'' + ", operationId='" + operationId + '\'' + ", consumes=" + consumes
                + ", produces=" + produces + ", responses=" + responses + ", parameters="
                + parameters + '}';
        }

        public void setParametersEnabled(boolean b) {
            this.parametersEnabled = b;
        }

        public boolean isParametersEnabled() {
            return parametersEnabled;
        }

        public boolean isBodyParametersEnabled() {
            return bodyParametersEnabled;
        }

        public boolean isOpNotPatch() {
            return type.equalsIgnoreCase("patch") ? false : true;
        }

        public void setBodyParametersEnabled(boolean bodyParametersEnabled) {
            this.bodyParametersEnabled = bodyParametersEnabled;
        }

        public Map<String, Object> getBodyParameters() {
            return bodyParameters;
        }

        public void setBodyParameters(Map<String, Object> bodyParameters) {
            this.bodyParameters = bodyParameters;
        }

        public String getSchemaLink() {
            return schemaLink;
        }

        public void setSchemaLink(String schemaLink) {
            this.schemaLink = schemaLink;
        }

        public String getSchemaType() {
            return schemaType;
        }

        public void setSchemaType(String schemaType) {
            this.schemaType = schemaType;
        }

        public boolean isHasReturnSchema() {
            return hasReturnSchema;
        }

        public void setHasReturnSchema(boolean hasReturnSchema) {
            this.hasReturnSchema = hasReturnSchema;
        }

        public String getReturnSchemaLink() {
            return returnSchemaLink;
        }

        public void setReturnSchemaLink(String returnSchemaLink) {
            this.returnSchemaLink = returnSchemaLink;
        }

        public String getReturnSchemaObject() {
            return returnSchemaObject;
        }

        public void setReturnSchemaObject(String returnSchemaObject) {
            this.returnSchemaObject = returnSchemaObject;
        }

        public static class Response {

            private String responseCode;

            private String description;

            private String version;

            public String getResponseCode() {
                return responseCode;
            }

            public void setResponseCode(String responseCode) {
                this.responseCode = responseCode;
            }

            public String getDescription() {
                return description;
            }

            public void setDescription(String description) {
                this.description = description;
            }

            @Override
            public String toString() {
                return "Response{" + "responseCode='" + responseCode + '\'' + ", description='"
                    + description + '\'' + '}';
            }

            public void setVersion(String version) {
                this.version = version;
            }
        }

    }

}
