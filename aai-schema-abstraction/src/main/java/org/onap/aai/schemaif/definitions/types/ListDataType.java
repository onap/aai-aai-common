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

package org.onap.aai.schemaif.definitions.types;

public class ListDataType extends DataType {
    private DataType listType;
    
    public ListDataType(DataType listType) {
        super(Type.LIST);
        this.listType = listType;
    }

    public DataType getListType() {
        return listType;
    }
    
    @Override
    public Object validateValue(String value) {
        // TODO: Break the string into a list and validate each element against the listType
        return value;
    }

    public String toString() {
        return "LIST[" + listType.toString() + "]";
    }
}
