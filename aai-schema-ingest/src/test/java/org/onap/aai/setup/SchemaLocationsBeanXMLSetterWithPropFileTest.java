package org.onap.aai.setup;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:forWiringTests/testUsingPropFileContext.xml"})
public class SchemaLocationsBeanXMLSetterWithPropFileTest {
	@Autowired
	SchemaLocationsBean bean;

	@Test
	public void test() {
		assertNotNull(bean);
		assertTrue("imatest".equals(bean.getSchemaConfigLocation()));
		assertTrue("andIMalittleteapot".equals(bean.getNodeDirectory()));
		assertTrue("meh".equals(bean.getEdgeDirectory()));
	}
}
