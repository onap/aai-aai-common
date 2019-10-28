/**
 * ﻿============LICENSE_START=======================================================
 * org.onap.aai
 * ================================================================================
 * Copyright © 2019 AT&T Intellectual Property. All rights reserved.
 * Copyright © 2019 Amdocs
 * ================================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ============LICENSE_END=========================================================
 */
package org.onap.aai.schemaif.json;


public class JsonSchemaProviderConfig {
    
    private String schemaServiceBaseUrl;
    private String schemaServiceCertFile;
    private String schemaServiceCertPwd;
    private String serviceName;
    
    public String getSchemaServiceBaseUrl() {
        return schemaServiceBaseUrl;
    }
    public void setSchemaServiceBaseUrl(String schemaServiceBaseUrl) {
        this.schemaServiceBaseUrl = schemaServiceBaseUrl;
    }
    
    public String getSchemaServiceCertFile() {
        return schemaServiceCertFile;
    }
    public void setSchemaServiceCertFile(String schemaServiceCertFile) {
        this.schemaServiceCertFile = schemaServiceCertFile;
    }
    
    public String getSchemaServiceCertPwd() {
        return schemaServiceCertPwd;
    }
    public void setSchemaServiceCertPwd(String schemaServiceCertPwd) {
        this.schemaServiceCertPwd = schemaServiceCertPwd;
    }
    
    public String getServiceName() {
        return serviceName;
    }
    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }
}
