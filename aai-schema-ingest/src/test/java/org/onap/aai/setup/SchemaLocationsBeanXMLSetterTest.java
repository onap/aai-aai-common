package org.onap.aai.setup;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:forWiringTests/testContext.xml"})
public class SchemaLocationsBeanXMLSetterTest {
	@Autowired
	SchemaLocationsBean bean;

	@Test
	public void test() {
		assertNotNull(bean);
		assertTrue("fromXML".equals(bean.getSchemaConfigLocation()));
		assertTrue("whatAnXML".equals(bean.getNodeDirectory()));
		assertTrue("XMLwiringYAY".equals(bean.getEdgeDirectory()));
	}
}
