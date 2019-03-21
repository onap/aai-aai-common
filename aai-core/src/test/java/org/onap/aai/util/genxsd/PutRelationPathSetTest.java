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
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ============LICENSE_END=========================================================
 */

package org.onap.aai.util.genxsd;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertTrue;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.onap.aai.edges.EdgeIngestor;
import org.onap.aai.setup.SchemaVersion;
import org.onap.aai.setup.SchemaVersions;
import org.onap.aai.util.GenerateXsd;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {SchemaVersions.class, EdgeIngestor.class})
@TestPropertySource(properties = {"schema.uri.base.path = /aai"})
@Ignore("This test needs to get major rework done as it is written very poorly")
public class PutRelationPathSetTest {
    private static final String EDGEFILENAME =
        "src/test/resources/dbedgerules/EdgeDescriptionRules_test.json";

    private static String json;
    private SchemaVersion v;
    private File relationsFile;
    private String target =
        "/cloud-infrastructure/cloud-regions/cloud-region/{cloud-owner}/{cloud-region-id}/availability-zones/availability-zone/{availability-zone-name}/relationship-list/relationship";
    private String opId =
        "createOrUpdateCloudInfrastructureCloudRegionsCloudRegionAvailabilityZonesAvailabilityZoneRelationshipListRelationship";
    private String path =
        "/cloud-infrastructure/cloud-regions/cloud-region/{cloud-owner}/{cloud-region-id}/availability-zones/availability-zone/{availability-zone-name}/relationship-list/relationship";
    PutRelationPathSet prp = null;
    @Autowired
    SchemaVersions schemaVersions;

    @Autowired
    EdgeIngestor edgeIngestor;

    @Before
    public void setUpBeforeClass() throws Exception {
        v = schemaVersions.getDefaultVersion();

        relationsFile = new File(GenerateXsd.getYamlDir() + "/relations/" + v.toString()
            + "/createOrUpdateCloudInfrastructureCloudRegionsCloudRegionAvailabilityZonesAvailabilityZone.json");
        json = "{" + "	\"rules\": [" + "		{" + "			\"from\": \"availability-zone\","
            + "			\"to\": \"complex\","
            + "			\"label\": \"org.onap.relationships.inventory.LocatedIn\","
            + "			\"direction\": \"OUT\"," + "			\"multiplicity\": \"MANY2ONE\","
            + "			\"contains-other-v\": \"NONE\","
            + "			\"delete-other-v\": \"NONE\"," + "			\"SVC-INFRA\": \"NONE\","
            + "			\"prevent-delete\": \"!${direction}\"," + "			\"default\": \"true\","
            + "			\"description\":\"this description\"" + "		}," + "    {"
            + "			\"from\": \"availability-zone\","
            + "			\"to\": \"service-capability\","
            + "			\"label\": \"org.onap.relationships.inventory.AppliesTo\","
            + "			\"direction\": \"OUT\"," + "			\"multiplicity\": \"MANY2MANY\","
            + "			\"contains-other-v\": \"NONE\","
            + "			\"delete-other-v\": \"NONE\"," + "			\"SVC-INFRA\": \"NONE\","
            + "			\"prevent-delete\": \"!${direction}\"," + "			\"default\": \"true\","
            + "			\"description\":\"\"" + "		}," + "		{"
            + "			\"from\": \"availability-zone\"," + "			\"to\": \"cloud-region\","
            + "			\"label\": \"org.onap.relationships.inventory.BelongsTo\","
            + "			\"direction\": \"OUT\"," + "			\"multiplicity\": \"MANY2ONE\","
            + "			\"contains-other-v\": \"!${direction}\","
            + "			\"delete-other-v\": \"!${direction}\","
            + "			\"SVC-INFRA\": \"NONE\"," + "			\"prevent-delete\": \"NONE\","
            + "			\"default\": \"true\"," + "			\"description\":\"\"" + "		},"
            + "		{" + "			\"from\": \"ctag-pool\","
            + "			\"to\": \"availability-zone\","
            + "			\"label\": \"org.onap.relationships.inventory.AppliesTo\","
            + "			\"direction\": \"OUT\"," + "			\"multiplicity\": \"MANY2MANY\","
            + "			\"contains-other-v\": \"NONE\","
            + "			\"delete-other-v\": \"NONE\"," + "			\"SVC-INFRA\": \"NONE\","
            + "			\"prevent-delete\": \"!${direction}\"," + "			\"default\": \"true\","
            + "			\"description\":\"\"" + "		}," + "		{"
            + "			\"from\": \"dvs-switch\"," + "			\"to\": \"availability-zone\","
            + "			\"label\": \"org.onap.relationships.inventory.AppliesTo\","
            + "			\"direction\": \"OUT\"," + "			\"multiplicity\": \"MANY2MANY\","
            + "			\"contains-other-v\": \"NONE\","
            + "			\"delete-other-v\": \"NONE\"," + "			\"SVC-INFRA\": \"NONE\","
            + "			\"prevent-delete\": \"!${direction}\"," + "			\"default\": \"true\","
            + "			\"description\":\"\"" + "		}," + "		{"
            + "			\"from\": \"generic-vnf\"," + "			\"to\": \"availability-zone\","
            + "			\"label\": \"org.onap.relationships.inventory.Uses\","
            + "			\"direction\": \"OUT\"," + "			\"multiplicity\": \"MANY2MANY\","
            + "			\"contains-other-v\": \"NONE\","
            + "			\"delete-other-v\": \"NONE\","
            + "			\"SVC-INFRA\": \"${direction}\","
            + "			\"prevent-delete\": \"!${direction}\"," + "			\"default\": \"true\","
            + "			\"description\":\"\"" + "		}," + "		{"
            + "			\"from\": \"vf-module\"," + "			\"to\": \"vnfc\","
            + "			\"label\": \"org.onap.relationships.inventory.Uses\","
            + "			\"direction\": \"OUT\"," + "			\"multiplicity\": \"ONE2MANY\","
            + "			\"contains-other-v\": \"NONE\","
            + "			\"delete-other-v\": \"NONE\","
            + "			\"SVC-INFRA\": \"${direction}\","
            + "			\"prevent-delete\": \"${direction}\"," + "			\"default\": \"true\","
            + "			\"description\":\"\"" + "		}," + "		{"
            + "			\"from\": \"pserver\"," + "			\"to\": \"availability-zone\","
            + "			\"label\": \"org.onap.relationships.inventory.MemberOf\","
            + "			\"direction\": \"OUT\"," + "			\"multiplicity\": \"MANY2ONE\","
            + "			\"contains-other-v\": \"NONE\","
            + "			\"delete-other-v\": \"NONE\","
            + "			\"SVC-INFRA\": \"${direction}\","
            + "			\"prevent-delete\": \"!${direction}\"," + "			\"default\": \"true\","
            + "			\"description\":\"\"" + "		}," + "		{"
            + "			\"from\": \"vce\"," + "			\"to\": \"availability-zone\","
            + "			\"label\": \"org.onap.relationships.inventory.Uses\","
            + "			\"direction\": \"OUT\"," + "			\"multiplicity\": \"MANY2MANY\","
            + "			\"contains-other-v\": \"NONE\","
            + "			\"delete-other-v\": \"NONE\"," + "			\"SVC-INFRA\": \"NONE\","
            + "			\"prevent-delete\": \"!${direction}\"," + "			\"default\": \"true\","
            + "			\"description\":\"\"" + "		}," + "	]}";

        BufferedWriter bw = new BufferedWriter(new FileWriter(EDGEFILENAME));
        bw.write(json);
        bw.close();

    }

    @Before
    public void setUp() throws Exception {

        DeleteOperation.deletePaths.put("/cloud-infrastructure/pservers/pserver/{hostname}",
            "pserver");
        DeleteOperation.deletePaths.put("/network/vces/vce/{vnf-id}", "vce");
        DeleteOperation.deletePaths
            .put("/cloud-infrastructure/complexes/complex/{physical-location-id}", "complex");
        DeleteOperation.deletePaths.put(
            "/service-design-and-creation/service-capabilities/service-capability/{service-type}/{vnf-type}",
            "service-capability");
        DeleteOperation.deletePaths.put(
            "/cloud-infrastructure/cloud-regions/cloud-region/{cloud-owner}/{cloud-region-id}",
            "cloud-region");
        DeleteOperation.deletePaths.put("/network/generic-vnfs/generic-vnf/{vnf-id}",
            "generic-vnf");
        DeleteOperation.deletePaths.put(
            "/cloud-infrastructure/cloud-regions/cloud-region/{cloud-owner}/{cloud-region-id}/dvs-switches/dvs-switch/{switch-name}",
            "dvs-switch");
        DeleteOperation.deletePaths.put(
            "/cloud-infrastructure/complexes/complex/{physical-location-id}/ctag-pools/ctag-pool/{target-pe}/{availability-zone-name}",
            "ctag-pool");

        DeleteOperation.deletePaths.put(path.replace("/relationship-list/relationship", ""),
            "availability-zone");
        PutRelationPathSet.add(opId, path);
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        File edges = new File(EDGEFILENAME);
        edges.delete();
    }

    @Test
    public void testAdd() {
        PutRelationPathSet.add(opId, path);
        assertThat(PutRelationPathSet.putRelationPaths.size(), is(1));
        assertThat(PutRelationPathSet.putRelationPaths.get(opId), is(target));
    }

    @Test
    public void testPutRelationPathSet() {

        this.prp = new PutRelationPathSet(v);
        assertThat(PutRelationPathSet.putRelationPaths.size(), is(1));
        prp.generateRelations(edgeIngestor);
        assertTrue(this.relationsFile.exists());
        this.relationsFile.delete();
    }

    @Test
    public void testPutRelationPathSetStringString() {
        this.prp = new PutRelationPathSet(opId, path, v);
        assertThat(PutRelationPathSet.putRelationPaths.size(), is(1));
    }

    @Test
    public void testGenerateRelations() {
        PutRelationPathSet prp = new PutRelationPathSet(opId, "availability-zone", v);
        prp.generateRelations(edgeIngestor);
        assertThat(PutRelationPathSet.putRelationPaths.size(), is(1));
        assertThat(PutRelationPathSet.putRelationPaths.get(opId), is(target));
        assertTrue(this.relationsFile.exists());
        // this.relationsFile.delete();
    }

}
