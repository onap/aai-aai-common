package org.onap.aai.validation.edges;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.onap.aai.edges.enums.EdgeField;
import org.onap.aai.validation.edges.DefaultEdgeFieldsValidationModule;
import org.onap.aai.validation.edges.EdgeFieldsValidationModule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {DefaultEdgeFieldsValidationModule.class})
@SpringBootTest
public class DefaultEdgeFieldsValidationModuleTest {
	@Autowired
	EdgeFieldsValidationModule validator;
	
	@Test
	public void test() {
		Map<String, String> test = new HashMap<>();
		for (EdgeField f : EdgeField.values()) {
			test.put(f.toString(), "test");
		}
		assertTrue("".equals(validator.verifyFields(test)));
		
		test.remove(EdgeField.DESCRIPTION.toString());
		assertTrue("".equals(validator.verifyFields(test))); //bc description is optional
		
		test.remove(EdgeField.CONTAINS.toString());
		assertTrue(validator.verifyFields(test).contains("missing required fields: contains-other-v"));
		
		test.remove(EdgeField.FROM.toString());
		assertTrue(validator.verifyFields(test).contains("missing required fields: from contains-other-v"));
	}

}
