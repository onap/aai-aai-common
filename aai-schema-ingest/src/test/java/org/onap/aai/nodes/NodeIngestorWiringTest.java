package org.onap.aai.nodes;

import static org.junit.Assert.*;

import org.eclipse.persistence.dynamic.DynamicEntity;
import org.eclipse.persistence.jaxb.dynamic.DynamicJAXBContext;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.onap.aai.setup.SchemaLocationsBean;
import org.onap.aai.setup.Version;
import org.onap.aai.testutils.ConfigTranslatorForWiringTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {SchemaLocationsBean.class, ConfigTranslatorForWiringTest.class, NodeIngestor.class})
@TestPropertySource(properties = {"schemaIngestPropLoc = src/test/resources/forWiringTests/schemaIngestWiringTest.properties"})
@SpringBootTest
public class NodeIngestorWiringTest {
	@Autowired
	NodeIngestor ni;
	
	@Test
	public void test() {
		DynamicJAXBContext ctx10 = ni.getContextForVersion(Version.V10);
		
		//should work bc Bar is valid in test_business_v10 schema
		DynamicEntity bar10 = ctx10.newDynamicEntity("Bar");
		bar10.set("barId","bar2");
		assertTrue("bar2".equals(bar10.get("barId")));
	}
}
