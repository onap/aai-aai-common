package org.onap.aai.validation.edges;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.onap.aai.nodes.NodeIngestor;
import org.onap.aai.setup.SchemaLocationsBean;
import org.onap.aai.testutils.GoodConfigForValidationTest;
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
@ContextConfiguration(classes = {SchemaLocationsBean.class, GoodConfigForValidationTest.class, NodeIngestor.class,
		CheckEverythingStrategy.class, DefaultEdgeFieldsValidationModule.class, UniqueLabelValidationModule.class,
		SingleContainmentValidationModule.class, CousinDefaultingValidationModule.class, NodeTypesValidationModule.class,
		EdgeRuleValidator.class})
@SpringBootTest
public class EdgeRuleValidatorSunnyDayTest {
	@Autowired
	EdgeRuleValidator validator;

	@Test
	public void test() {
		assertNotNull(validator); //verify spring wiring OK
		assertTrue(validator.validate());
		assertTrue("No errors found.".equals(validator.getErrorMsg()));
	}
}
