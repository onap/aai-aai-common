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

import org.junit.Test;
import org.onap.aai.introspection.exceptions.AAIUnknownObjectException;
import org.onap.aai.restcore.MediaType;

import static org.junit.Assert.assertEquals;

public class MoxyLoaderTest extends IntrospectorTestSpec {

    /**
     * Container object.
     * @throws AAIUnknownObjectException
     */
    @Test
    public void testMethodsForExceptions() throws AAIUnknownObjectException {

        MoxyLoader loader = (MoxyLoader) LoaderFactory.createLoaderForVersion(ModelType.MOXY, Version.v9);
        String payload = "{\"att-uuid\":\"wr148d\",\"application\":\"testApp\",\"application-vendor\":\"testVendor\",\"application-version\":\"versionTest\"}";
        Boolean exceptionThrownForIntrospector = false;
        Boolean exceptionThrownForLoaderMethods = false;
        Boolean exceptionThrownForUnmarshalling = false;
        Boolean exceptionThrownForObjectFromName = false;
        try {
            loader.getAllObjects();
            loader.getJAXBContext();
        }catch(Exception e){
            exceptionThrownForLoaderMethods = true;
        }
        try{
            loader.introspectorFromName("TEST");
        }catch(Exception e){
            exceptionThrownForIntrospector = true;
        }
        try {
            loader.unmarshal("vnf-image", payload, MediaType.APPLICATION_JSON_TYPE);
            loader.unmarshal("vnf-image", null, MediaType.APPLICATION_JSON_TYPE);
            loader.unmarshal("vnf-image", "{}", MediaType.APPLICATION_JSON_TYPE);
        }catch(Exception e){
            exceptionThrownForUnmarshalling = true;
        }
        try{
            loader.objectFromName(null);
            loader.objectFromName("test");
        }catch(Exception e) {
            exceptionThrownForObjectFromName = true;
        }

        assertEquals(false, exceptionThrownForLoaderMethods);
        assertEquals(true, exceptionThrownForIntrospector);
        assertEquals(true, exceptionThrownForUnmarshalling);
        assertEquals(true, exceptionThrownForObjectFromName);

    }



}
