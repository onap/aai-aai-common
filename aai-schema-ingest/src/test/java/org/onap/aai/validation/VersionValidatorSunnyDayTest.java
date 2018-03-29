package org.onap.aai.validation;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.onap.aai.nodes.NodeIngestor;
import org.onap.aai.setup.SchemaLocationsBean;
import org.onap.aai.testutils.GoodConfigForValidationTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {SchemaLocationsBean.class, GoodConfigForValidationTest.class, NodeIngestor.class,
		CheckEverythingStrategy.class, DefaultVersionValidationModule.class, VersionValidator.class})
@SpringBootTest
public class VersionValidatorSunnyDayTest {
	@Autowired
	VersionValidator validator;

	@Test
	public void test() {
		assertTrue(validator.validate());
	}

}
