package org.onap.aai.setup;

import static org.junit.Assert.*;

import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.onap.aai.testutils.ConfigTranslatorForWiringTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {SchemaLocationsBean.class, ConfigTranslatorForWiringTest.class})
@TestPropertySource(properties = {"schemaIngestPropLoc = src/test/resources/forWiringTests/schemaIngestWiringTest.properties"})
@SpringBootTest
public class ConfigTranslatorWiringTest {
	@Autowired
	ConfigTranslator ct;
	
	@Test
	public void test() {
		assertNotNull(ct);
		Map<Version, List<String>> nodes = ct.getNodeFiles();
		assertTrue(nodes.containsKey(Version.V10));
		assertTrue(1 == nodes.get(Version.V10).size());
		assertTrue("src/test/resources/oxm/test_business_v10.xml".equals(nodes.get(Version.V10).get(0)));
		
		Map<Version, List<String>> edges = ct.getEdgeFiles();
		assertTrue(edges.containsKey(Version.V10));
		assertTrue(1 == edges.get(Version.V10).size());
		assertTrue("src/test/resources/edgeRules/test.json".equals(edges.get(Version.V10).get(0)));
	}

}
