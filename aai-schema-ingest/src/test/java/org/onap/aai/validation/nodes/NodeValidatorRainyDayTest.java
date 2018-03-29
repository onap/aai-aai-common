package org.onap.aai.validation.nodes;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.onap.aai.nodes.NodeIngestor;
import org.onap.aai.setup.SchemaLocationsBean;
import org.onap.aai.testutils.BadNodeConfigForValidationTest;
import org.onap.aai.validation.CheckEverythingStrategy;
import org.onap.aai.validation.nodes.DefaultDuplicateNodeDefinitionValidationModule;
import org.onap.aai.validation.nodes.NodeValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {SchemaLocationsBean.class, BadNodeConfigForValidationTest.class, NodeIngestor.class,
		CheckEverythingStrategy.class, DefaultDuplicateNodeDefinitionValidationModule.class, NodeValidator.class})
@SpringBootTest
public class NodeValidatorRainyDayTest {
	@Autowired
	NodeValidator validator;

	@Test
	public void test() {
		assertNotNull(validator); //check spring wiring ok
		assertFalse(validator.validate());
		String result = validator.getErrorMsg();
		assertTrue(result.contains("LogicalLink"));
		assertTrue(result.contains("LagInterface"));
		assertFalse(result.contains("LInterface"));
	}

}
