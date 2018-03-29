package org.onap.aai.validation.nodes;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.onap.aai.nodes.NodeIngestor;
import org.onap.aai.setup.SchemaLocationsBean;
import org.onap.aai.testutils.GoodConfigForValidationTest;
import org.onap.aai.validation.CheckEverythingStrategy;
import org.onap.aai.validation.nodes.DefaultDuplicateNodeDefinitionValidationModule;
import org.onap.aai.validation.nodes.NodeValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {SchemaLocationsBean.class, GoodConfigForValidationTest.class, NodeIngestor.class,
		CheckEverythingStrategy.class, DefaultDuplicateNodeDefinitionValidationModule.class, NodeValidator.class})
@SpringBootTest
public class NodeValidatorSunnyDayTest {
	@Autowired
	NodeValidator validator;

	@Test
	public void test() {
		assertNotNull(validator); //check spring wiring ok
		assertTrue(validator.validate());
		assertTrue("No errors found.".equals(validator.getErrorMsg()));
	}

}
