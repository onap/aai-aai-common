package org.onap.aai.validation.edges;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.BeforeClass;
import org.junit.Test;
import org.onap.aai.edges.JsonIngestor;
import org.onap.aai.setup.Version;
import org.onap.aai.validation.edges.CousinDefaultingValidationModule;

import com.jayway.jsonpath.DocumentContext;

public class CousinDefaultingValidationModuleTest {
	private static List<DocumentContext> ctxs;
	private static CousinDefaultingValidationModule validator;

	@BeforeClass
	public static void setUpBeforeClass() {
		Map<Version, List<String>> testRules = new HashMap<>();
		List<String> testFiles = new ArrayList<>();
		testFiles.add("src/test/resources/edgeRules/cousinDefaultValidationTest.json");
		testRules.put(Version.getLatest(), testFiles);
		
		JsonIngestor ji = new JsonIngestor();
		ctxs = ji.ingest(testRules).get(Version.getLatest());
		validator = new CousinDefaultingValidationModule();
	}
	
	@Test
	public void testValidCousins() {
		assertTrue("".equals(validator.validate("boop|beep", ctxs)));
	}

	@Test
	public void testValidBoth() {
		assertTrue("".equals(validator.validate("monster|human", ctxs)));
	}

	@Test
	public void testValidSingleContains() {
		assertTrue("".equals(validator.validate("family|baby", ctxs)));
	}
	
	@Test
	public void testInvalidTooManyDefaults() {
		assertTrue(validator.validate("sheep|wool", ctxs).contains("Multiple set"));
	}
	
	@Test
	public void testInvalidNoDefaults() {
		assertTrue(validator.validate("cloth|thread", ctxs).contains("None set"));
	}
}
