package org.onap.aai.edges;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.onap.aai.edges.exceptions.EdgeRuleNotFoundException;
import org.onap.aai.setup.SchemaLocationsBean;
import org.onap.aai.setup.Version;
import org.onap.aai.testutils.ConfigTranslatorForWiringTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.google.common.collect.Multimap;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {SchemaLocationsBean.class, ConfigTranslatorForWiringTest.class, EdgeIngestor.class})
@TestPropertySource(properties = {"schemaIngestPropLoc = src/test/resources/forWiringTests/schemaIngestWiringTest.properties"})
@SpringBootTest
public class EdgeIngestorWiringTest {
	@Autowired
	EdgeIngestor ei;
	
	@Test
	public void test() throws EdgeRuleNotFoundException {
		assertNotNull(ei);
		EdgeRuleQuery q = new EdgeRuleQuery.Builder("quux", "foo").label("dancesWith").version(Version.V10).build();
		Multimap<String, EdgeRule> results = ei.getRules(q);
		assertTrue(results.size() == 1);
		assertTrue(results.containsKey("foo|quux"));
	}

}
