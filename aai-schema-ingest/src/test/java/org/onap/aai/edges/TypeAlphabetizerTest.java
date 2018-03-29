package org.onap.aai.edges;

import static org.junit.Assert.*;

import org.junit.Test;

public class TypeAlphabetizerTest {

	@Test
	public void test() {
		TypeAlphabetizer alpher = new TypeAlphabetizer();
		assertTrue("aaa|bbb".equals(alpher.buildAlphabetizedKey("aaa", "bbb")));
		assertTrue("l-interface|logical-link".equals(alpher.buildAlphabetizedKey("l-interface", "logical-link")));
		assertTrue("l-interface|logical-link".equals(alpher.buildAlphabetizedKey("logical-link", "l-interface")));
		assertTrue("|foo".equals(alpher.buildAlphabetizedKey(null, "foo")));
		assertTrue("|foo".equals(alpher.buildAlphabetizedKey("foo", null)));
		assertTrue("|".equals(alpher.buildAlphabetizedKey(null, null)));
	}

}
