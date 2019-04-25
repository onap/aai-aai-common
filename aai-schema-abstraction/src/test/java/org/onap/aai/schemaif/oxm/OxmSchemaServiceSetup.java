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

package org.onap.aai.schemaif.oxm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.Before;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.onap.aai.edges.EdgeIngestor;
import org.onap.aai.nodes.NodeIngestor;
import org.onap.aai.setup.AAIConfigTranslator;
import org.onap.aai.setup.SchemaLocationsBean;
import org.onap.aai.setup.SchemaVersion;
import org.onap.aai.setup.SchemaVersions;
import org.onap.aai.setup.Translator;

public class OxmSchemaServiceSetup {

    private OxmEdgeRulesLoader edgeLoader;
    private OxmSchemaLoader vertexLoader;
    
    @Mock
    private SchemaLocationsBean schemaLocationsBean;

    @Mock
    private SchemaVersions schemaVersions;

    private EdgePropsConfiguration edgePropsConfiguration = new EdgePropsConfiguration();

    private List<SchemaVersion> schemaVersionList = new ArrayList<>();

    @Before
    public void schemaBeanMockSetup() throws Exception {
        schemaVersionList.add(new SchemaVersion("v13"));

        Mockito.when(schemaVersions.getVersions()).thenReturn(schemaVersionList);
        Mockito.when(schemaLocationsBean.getNodesInclusionPattern()).thenReturn(Arrays.asList(".*oxm(.*).xml"));
        Mockito.when(schemaLocationsBean.getEdgesInclusionPattern()).thenReturn(Arrays.asList("DbEdgeRules_.*.json"));
        Mockito.when(schemaLocationsBean.getNodeDirectory()).thenReturn("src/test/resources/oxm/oxm");
        Mockito.when(schemaLocationsBean.getEdgeDirectory()).thenReturn("src/test/resources/oxm/edge-rules");

        AAIConfigTranslator aaiConfigTranslator = new AAIConfigTranslator(schemaLocationsBean, schemaVersions);
        Set<Translator> translators = new HashSet<>();
        translators.add(aaiConfigTranslator);
        NodeIngestor nodeIngestor = new NodeIngestor(translators);
        nodeIngestor.initialize();
        EdgeIngestor edgeIngestor = new EdgeIngestor(translators);
        edgeIngestor.initialize();
        
        edgePropsConfiguration.setEdgePropsDir("src/test/resources/oxm/edge-props");
        edgeLoader = new OxmEdgeRulesLoader(aaiConfigTranslator, edgeIngestor, edgePropsConfiguration);
        vertexLoader = new OxmSchemaLoader(aaiConfigTranslator, nodeIngestor);
    }

}
