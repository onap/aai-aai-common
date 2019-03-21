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

package org.onap.aai.serialization.db;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.tinkerpop.gremlin.structure.Direction;

public class EdgePropertyMap<K, V> extends HashMap<K, V> {

    private static final long serialVersionUID = -8298355506617458683L;

    private static final Pattern variablePattern = Pattern.compile("(!)?\\$\\{(\\w+)\\}");

    @Override
    public V get(Object arg0) {

        V value = super.get(arg0);

        Matcher m = variablePattern.matcher(value.toString());
        if (m.find()) {
            if (m.groupCount() == 2) {
                if (m.group(1) == null) {
                    value = super.get(m.group(2));
                } else {
                    value = reverse(super.get(m.group(2)));
                }
            }
        }

        return value;
    }

    protected V reverse(V value) {

        return (V) Direction.valueOf(value.toString()).opposite().toString();
    }
}
