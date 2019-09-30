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

package org.onap.aai.introspection;

import java.util.Map;
import java.util.Set;

import org.onap.aai.schema.enums.PropertyMetadata;

public final class PropertyPredicates {

    private PropertyPredicates() {

    }

    public static PropertyPredicate<Introspector, String> includeInTestGeneration() {
        return (obj, prop) -> {
            final Map<PropertyMetadata, String> map = obj.getPropertyMetadata(prop);
            if (map.containsKey(PropertyMetadata.VISIBILITY)) {
                return !(Visibility.Internal.equals(Visibility.valueOf(map.get(PropertyMetadata.VISIBILITY)))
                        || Visibility.Deployment.equals(Visibility.valueOf(map.get(PropertyMetadata.VISIBILITY))));
            }

			return map.containsKey("datalocation");
          
        };
    }

    public static PropertyPredicate<Introspector, String> isVisible() {
        return (obj, prop) -> {
            final Map<PropertyMetadata, String> map = obj.getPropertyMetadata(prop);
            if (map.containsKey(PropertyMetadata.VISIBILITY)) {
                return !Visibility.Internal.equals(Visibility.valueOf(map.get(PropertyMetadata.VISIBILITY)));
            }
            return true;
        };
    }

    public static PropertyPredicate<Introspector, String> includeInExamples() {
        return (obj, prop) -> {
            final Map<PropertyMetadata, String> map = obj.getPropertyMetadata(prop);
            if (map.containsKey(PropertyMetadata.VISIBILITY)) {
                return !Visibility.Internal.equals(Visibility.valueOf(map.get(PropertyMetadata.VISIBILITY)));
            }
            return map.containsKey("datalocation");
         
        };
    }

    public static PropertyPredicate<Introspector, String> isIndexed() {
        return (obj, prop) -> {
            Set<String> indexed = obj.getIndexedProperties();
            return indexed.contains(prop);
        };
    }
}
