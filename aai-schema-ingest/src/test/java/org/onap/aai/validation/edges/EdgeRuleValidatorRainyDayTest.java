package org.onap.aai.validation.edges;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.onap.aai.nodes.NodeIngestor;
import org.onap.aai.setup.SchemaLocationsBean;
import org.onap.aai.testutils.BadEdgeConfigForValidationTest;
import org.onap.aai.validation.CheckEverythingStrategy;
import org.onap.aai.validation.edges.CousinDefaultingValidationModule;
import org.onap.aai.validation.edges.DefaultEdgeFieldsValidationModule;
import org.onap.aai.validation.edges.EdgeRuleValidator;
import org.onap.aai.validation.edges.NodeTypesValidationModule;
import org.onap.aai.validation.edges.SingleContainmentValidationModule;
import org.onap.aai.validation.edges.UniqueLabelValidationModule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {SchemaLocationsBean.class, BadEdgeConfigForValidationTest.class, NodeIngestor.class,
		CheckEverythingStrategy.class, DefaultEdgeFieldsValidationModule.class, UniqueLabelValidationModule.class,
		SingleContainmentValidationModule.class, CousinDefaultingValidationModule.class, NodeTypesValidationModule.class,
		EdgeRuleValidator.class})
@SpringBootTest
public class EdgeRuleValidatorRainyDayTest {
	@Autowired
	EdgeRuleValidator validator;

	@Test
	public void test() {
		assertNotNull(validator); //verify spring wiring OK
		assertFalse(validator.validate());
		String errors = validator.getErrorMsg();
		assertTrue(errors.contains("missing required fields: delete-other-v"));
		assertTrue(errors.contains("has multiple rules using the same label: org.onap.relationships.inventory.Source"));
		assertTrue(errors.contains("Invalid node type(s) found: gooble"));
	}
}
