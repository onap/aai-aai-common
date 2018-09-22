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
package org.onap.aai.serialization.queryformats.utils;

import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.onap.aai.exceptions.AAIException;
import org.onap.aai.introspection.Loader;
import org.onap.aai.serialization.db.DBSerializer;
import org.onap.aai.serialization.queryformats.Resource;
import org.onap.aai.serialization.queryformats.Resource.Builder;

import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;

import static org.junit.Assert.assertEquals;

public class QueryParamInjectorTest {

    
    @Mock private Loader loader;
    @Mock private DBSerializer serializer;
    @Mock private UrlBuilder urlBuilder;
    
    @Test
    public void test() throws AAIException {
        MockitoAnnotations.initMocks(this);
        QueryParamInjector injector = QueryParamInjector.getInstance();
        
        Builder b = new Builder(loader, serializer, urlBuilder);
        MultivaluedMap<String, String> params = new MultivaluedHashMap<>(); 
        params.putSingle("nodesOnly", "true");
        params.putSingle("depth", "10");
        params.putSingle("invalid", "1000");
        injector.injectParams(b, params);
        
        assertEquals("is nodes only", true, b.isNodesOnly());
        assertEquals("is depth 10", 10, b.getDepth());
    }
}
