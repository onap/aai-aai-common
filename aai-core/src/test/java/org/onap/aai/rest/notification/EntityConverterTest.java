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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.onap.aai.introspection.Introspector;
import org.onap.aai.introspection.Loader;
import org.onap.aai.introspection.exceptions.AAIUnmarshallingException;
import org.onap.aai.parsers.uri.URIToObject;
import org.onap.aai.setup.SchemaVersion;

public class EntityConverterTest {

    @Mock URIToObject parser;
    @Mock Introspector introspector;
    @Mock Loader loader;
    @Mock List<Object> parentList;

    EntityConverter entityConverter;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        entityConverter = new EntityConverter(parser);
    }

    @Test
    public void testConvert_topEntitySameAsEntity() throws AAIUnmarshallingException {
        when(parser.getParentList()).thenReturn(parentList);
        when(parser.getTopEntity()).thenReturn(introspector);
        when(parser.getEntity()).thenReturn(introspector);

        Introspector result = entityConverter.convert(introspector);

        assertEquals(introspector, result);
        verify(parser.getParentList()).clear();
    }

    @Test
    public void testConvert_topEntityDifferentFromEntity_withVersionMismatch() throws AAIUnmarshallingException {
        Introspector topEntity = mock(Introspector.class);
        Introspector childEntity = mock(Introspector.class);
        String json = "{}";

        when(parser.getParentList()).thenReturn(parentList);
        when(parser.getTopEntity()).thenReturn(topEntity);
        when(parser.getEntity()).thenReturn(childEntity);
        when(childEntity.getName()).thenReturn("smth");
        when(parser.getLoader()).thenReturn(loader);
        when(introspector.getVersion()).thenReturn(new SchemaVersion("v1"));
        when(loader.getVersion()).thenReturn(new SchemaVersion("v2"));
        when(introspector.marshal(false)).thenReturn(json);
        when(loader.unmarshal(anyString(), eq(json))).thenReturn(childEntity);

        Introspector result = entityConverter.convert(introspector);

        assertEquals(topEntity, result);
    }

    @Test
    public void testConvert_topEntityDifferentFromEntity_withoutVersionMismatch() throws AAIUnmarshallingException {
        Introspector topEntity = mock(Introspector.class);
        Introspector childEntity = mock(Introspector.class);

        when(parser.getParentList()).thenReturn(parentList);
        when(parser.getTopEntity()).thenReturn(topEntity);
        when(parser.getEntity()).thenReturn(childEntity);
        when(parser.getLoader()).thenReturn(loader);
        when(introspector.getVersion()).thenReturn(new SchemaVersion("v1"));
        when(loader.getVersion()).thenReturn(new SchemaVersion("v1"));

        Introspector result = entityConverter.convert(introspector);

        assertEquals(topEntity, result);
        verify(parentList).add(any());
    }

    @Test
    public void testGetTopEntityName() {
        String topEntityName = "TopEntity";
        when(parser.getTopEntityName()).thenReturn(topEntityName);

        String result = entityConverter.getTopEntityName();

        assertEquals(topEntityName, result);
    }
}
