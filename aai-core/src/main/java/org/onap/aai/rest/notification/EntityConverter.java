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
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.HashMap;

import org.onap.aai.exceptions.AAIException;
import org.onap.aai.introspection.Introspector;
import org.onap.aai.introspection.Loader;
import org.onap.aai.parsers.uri.URIToObject;
import org.springframework.stereotype.Component;

@Component
public class EntityConverter {

  public Introspector convert(Loader currentVersionLoader, URI uri,Introspector obj, HashMap<String, Introspector> relatedObjects) throws UnsupportedEncodingException, AAIException {
    final URIToObject parser = new URIToObject(currentVersionLoader, uri, relatedObjects);
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

    // convert to most resent version
    if (!parser.getLoader().getVersion().equals(currentVersionLoader.getVersion())) {
        String json = "";
        if (parser.getTopEntity().equals(parser.getEntity())) {
            // convert the parent object passed in
            json = obj.marshal(false);
            eventObject = currentVersionLoader.unmarshal(obj.getName(), json);
        } else {
            // convert the object created in the parser
            json = parser.getTopEntity().marshal(false);
            eventObject = currentVersionLoader.unmarshal(parser.getTopEntity().getName(), json);
        }
    } else {
        if (parser.getTopEntity().equals(parser.getEntity())) {
            // take the top level parent object passed in
            eventObject = obj;
        } else {
            // take the wrapped child objects (ogres are like onions)
            eventObject = parser.getTopEntity();
        }
    }
    return eventObject;
  }
}
