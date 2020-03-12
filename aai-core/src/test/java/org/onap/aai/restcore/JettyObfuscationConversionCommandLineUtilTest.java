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

package org.onap.aai.restcore;

import org.apache.commons.io.IOUtils;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.janusgraph.core.JanusGraphFactory;
import org.junit.*;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.onap.aai.AAISetup;
import org.onap.aai.db.DbMethHelper;
import org.onap.aai.exceptions.AAIException;
import org.onap.aai.introspection.Introspector;
import org.onap.aai.introspection.Loader;
import org.onap.aai.introspection.ModelType;
import org.onap.aai.parsers.exceptions.AmbiguousMapAAIException;
import org.onap.aai.serialization.db.DBSerializer;
import org.onap.aai.serialization.db.EdgeSerializer;
import org.onap.aai.serialization.engines.JanusGraphDBEngine;
import org.onap.aai.serialization.engines.QueryStyle;
import org.onap.aai.serialization.engines.TransactionalGraphEngine;
import org.onap.aai.setup.SchemaVersion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;

import java.io.FileInputStream;
import java.io.IOException;
import java.text.ParseException;
import java.util.*;

import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

public class JettyObfuscationConversionCommandLineUtilTest extends AAISetup {
    public static JettyObfuscationConversionCommandLineUtil jettyObfuscationConversionCommandLineUtil;

    @Test
    public void testMainObfuscation() {
        String[] args = {"-e", "[thisStringToObfuscate]"};
        jettyObfuscationConversionCommandLineUtil.main(args);
        Assert.assertTrue(true);    // No exception was encountered
    }

    @Test
    public void testMainDeobfuscation() {
        String[] args = {"-d", "OBF:1pj11w261wmr1t3b1vgv1s9r1z7i1vuz1tae1qji1vg71mdb1vgn1qhs1ta01vub1z7k1sbj1vfz1t2v1wnf1w1c1pj5"};
        jettyObfuscationConversionCommandLineUtil.main(args);
        Assert.assertTrue(true);    // No exception was encountered
    }

    @Test
    public void testMain_failedParseInput() {
        String[] args = {"-e [thisStringToObfuscate]"};
        jettyObfuscationConversionCommandLineUtil.main(args);
        Assert.assertTrue(true);    // No exception was encountered
    }
}
