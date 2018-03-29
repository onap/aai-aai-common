package org.onap.aai.setup;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {SchemaLocationsBean.class})
@TestPropertySource(properties = {"schemaIngestPropLoc = src/test/resources/forWiringTests/schemaIngest2.properties"})
public class SchemaLocationsBeanEnvVarInjectionTest {
	@Autowired
	SchemaLocationsBean bean;
	
	@Test
	public void test() {
		assertNotNull(bean);
		assertTrue("testConfig.json".equals(bean.getSchemaConfigLocation()));
		assertTrue("bloop/blap".equals(bean.getNodeDirectory()));
		assertTrue("different".equals(bean.getEdgeDirectory()));
	}
}
