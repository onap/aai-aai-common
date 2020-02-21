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

import org.junit.Assert;
import org.junit.Test;
import org.onap.aai.introspection.exceptions.AAIUnknownObjectException;
import org.springframework.test.annotation.DirtiesContext;

@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
public class MoxyEngineTest extends IntrospectorTestSpec {

    /**
     * Container object.
     * 
     * @throws AAIUnknownObjectException
     */
    @Test
    public void containerObject() throws AAIUnknownObjectException {

        Loader loader = loaderFactory.createLoaderForVersion(ModelType.MOXY, schemaVersions.getDepthVersion());

        Introspector obj = loader.introspectorFromName("port-groups");

        this.containerTestSet(obj);

    }

    @Test
    public void testDslStartNodeProps() throws AAIUnknownObjectException {
        Loader loader = loaderFactory.createLoaderForVersion(ModelType.MOXY, schemaVersions.getDepthVersion());
        Introspector obj = loader.introspectorFromName("pserver");
        Assert.assertFalse(obj.getDslStartNodeProperties().contains("in-maint"));
        Assert.assertTrue(obj.getDslStartNodeProperties().contains("pserver-name2"));

    }

    @Test
    public void testDslStartNodePropsDefault() throws AAIUnknownObjectException {
        /*
         * Use indexedprops when there is no dslStartNodeProps
         */
        Loader loader = loaderFactory.createLoaderForVersion(ModelType.MOXY, schemaVersions.getDepthVersion());
        Introspector obj = loader.introspectorFromName("vserver");
        Assert.assertTrue(obj.getDslStartNodeProperties().contains("in-maint"));
    }

}
