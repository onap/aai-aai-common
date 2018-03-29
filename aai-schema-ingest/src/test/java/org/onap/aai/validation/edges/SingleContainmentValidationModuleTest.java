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
import org.onap.aai.validation.edges.SingleContainmentValidationModule;

import com.jayway.jsonpath.DocumentContext;

public class SingleContainmentValidationModuleTest {
	private static List<DocumentContext> ctxs;
	private static SingleContainmentValidationModule validator;

	@BeforeClass
	public static void setUpBeforeClass() {
		Map<Version, List<String>> testRules = new HashMap<>();
		List<String> testFiles = new ArrayList<>();
		testFiles.add("src/test/resources/edgeRules/containsValidationTest.json");
		testRules.put(Version.getLatest(), testFiles);
		
		JsonIngestor ji = new JsonIngestor();
		ctxs = ji.ingest(testRules).get(Version.getLatest());
		validator = new SingleContainmentValidationModule();
	}

	@Test
	public void testValid() {
		assertTrue("".equals(validator.validate("human|monster", ctxs)));
	}

	@Test
	public void testValidWithNone() {
		assertTrue("".equals(validator.validate("bread|cheese", ctxs)));
	}
	
	@Test
	public void testInvalid() {
		assertTrue(validator.validate("box|cat", ctxs).contains("has multiple containment rules"));
	}
}
