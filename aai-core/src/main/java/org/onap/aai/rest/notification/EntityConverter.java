/**
 * ============LICENSE_START=======================================================
 * org.onap.aai
 * ================================================================================
 * Copyright © 2024 Deutsche Telekom. All rights reserved.
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

package org.onap.aai.rest.notification;

import java.util.List;

import org.onap.aai.introspection.Introspector;
import org.onap.aai.introspection.exceptions.AAIUnmarshallingException;
import org.onap.aai.parsers.uri.URIToObject;

import lombok.Value;

@Value
public class EntityConverter {

  final URIToObject parser;

  public Introspector convert(Introspector obj) throws AAIUnmarshallingException {
    List<Object> parentList = parser.getParentList();
    parentList.clear();

    if (!parser.getTopEntity().equals(parser.getEntity())) {
        Introspector child = obj;
        if (!parser.getLoader().getVersion().equals(obj.getVersion())) {
            String json = obj.marshal(false);
            child = parser.getLoader().unmarshal(parser.getEntity().getName(), json);
        }

        // wrap the child object in its parents
        parentList.add(child.getUnderlyingObject());
    }

    final Introspector eventObject;
    if (parser.getTopEntity().equals(parser.getEntity())) {
        // take the top level parent object passed in
        eventObject = obj;
    } else {
        // take the wrapped child objects (ogres are like onions)
        eventObject = parser.getTopEntity();
    }

    return eventObject;
  }

  public String getTopEntityName() {
    return parser.getTopEntityName();
  }
}
