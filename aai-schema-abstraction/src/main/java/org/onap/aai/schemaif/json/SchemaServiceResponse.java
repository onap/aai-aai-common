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

import java.util.Base64;

import org.onap.aai.schemaif.SchemaProviderException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class SchemaServiceResponse {
    public static final String SCHEMA_TYPE_OXM = "oxm";
    public static final String SCHEMA_TYPE_JSON = "json";
    
    private static final Gson gson = new GsonBuilder().create();

    private String version;
    private String type;
    private String data;
   
    public String getVersion() {
        return version;
    }

    public String getType() {
        return type;
    }

    public String getData() {
        return data;
    }
    
    public String getDataDecoded() {
        byte[] decodedBytes = null;
        if(data != null){
            decodedBytes = Base64.getDecoder().decode(data);
        }

        return new String(decodedBytes);
    }

    public String toJson() {
        return gson.toJson(this);
    }

    public static SchemaServiceResponse fromJson(String json) throws SchemaProviderException {
        try {
            if (json == null || json.isEmpty()) {
                throw new SchemaProviderException("Empty schema-service response");
            }
            
            return gson.fromJson(json, SchemaServiceResponse.class);
        } catch (Exception ex) {
            throw new SchemaProviderException("Invalid response from schema service: " + ex.getMessage());
        }
    }

}
