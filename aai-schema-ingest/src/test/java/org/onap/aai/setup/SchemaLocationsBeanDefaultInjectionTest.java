package org.onap.aai.setup;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {SchemaLocationsBean.class})
public class SchemaLocationsBeanDefaultInjectionTest {
	@Autowired
	SchemaLocationsBean bean;

	@Test
	public void test() {
		assertNotNull(bean);
		assertTrue("foo".equals(bean.getSchemaConfigLocation()));
		assertTrue("bar".equals(bean.getNodeDirectory()));
		assertTrue("quux".equals(bean.getEdgeDirectory()));
	}
}
