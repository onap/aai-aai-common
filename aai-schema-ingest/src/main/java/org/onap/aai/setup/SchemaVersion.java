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
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ============LICENSE_END=========================================================
 */
package org.onap.aai.setup;

import org.onap.aai.validation.AAISchemaValidationException;

import java.util.regex.Pattern;

public class SchemaVersion implements Comparable<SchemaVersion> {

    public static final Pattern VERSION_PATTERN = Pattern.compile("v[1-9][0-9]*");

    private final String value;

    public SchemaVersion(String value){
        if(!VERSION_PATTERN.matcher(value).matches()){
            throw new AAISchemaValidationException("Invalid Schema Version " + value + ", value doesn't match the expected regex: " + VERSION_PATTERN);
        }

        this.value = value;
    }

    @Override
    public int hashCode(){
        return value.hashCode();
    }

    @Override
    public boolean equals(Object other){
        if(this == other){
            return true;
        }

        if(other == null){
            return false;
        }

        if(!(other instanceof SchemaVersion)){
            return false;
        }

        SchemaVersion obj = (SchemaVersion)other;
        return this.value.equals(obj.value);
    }

    @Override
    public String toString(){
        return value;
    }

    @Override
    public int compareTo(SchemaVersion o) {

        if(o == null){
            return -1;
        }

        // Requires to convert to integer to match the past behavior
        // Otherwise the string comparison of versions aren't working as expected

        Integer tVal = Integer.parseInt(this.value.replaceAll("v", ""));
        Integer oVal = Integer.parseInt(o.value.replaceAll("v", ""));

        return tVal.compareTo(oVal);
    }
}
