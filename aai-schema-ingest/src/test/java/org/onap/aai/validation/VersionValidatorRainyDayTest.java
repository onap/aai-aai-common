package org.onap.aai.validation;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.onap.aai.nodes.NodeIngestor;
import org.onap.aai.setup.SchemaLocationsBean;
import org.onap.aai.setup.Version;
import org.onap.aai.testutils.BadNodeConfigForValidationTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {SchemaLocationsBean.class, BadNodeConfigForValidationTest.class, NodeIngestor.class,
		CheckEverythingStrategy.class, DefaultVersionValidationModule.class, VersionValidator.class})
@SpringBootTest
public class VersionValidatorRainyDayTest {
	@Autowired
	VersionValidator validator;

	@Test
	public void test() {
		assertFalse(validator.validate());
		assertTrue(validator.getErrorMsg().contains(Version.V12.toString()));
		assertTrue(validator.getErrorMsg().contains(Version.V11.toString()));
	}

}
