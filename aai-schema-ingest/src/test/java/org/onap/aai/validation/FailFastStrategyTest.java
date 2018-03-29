package org.onap.aai.validation;

import static org.junit.Assert.*;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.onap.aai.validation.AAISchemaValidationException;
import org.onap.aai.validation.FailFastStrategy;

public class FailFastStrategyTest {
	@Rule
	public ExpectedException thrown = ExpectedException.none();
	
	@Test
	public void test() {
		FailFastStrategy strat = new FailFastStrategy();
		
		//simulate no issues found
		assertTrue(strat.isOK());
		assertTrue("No errors found.".equals(strat.getErrorMsg()));
		
		//simulate an issue found
		String testError = "hi i'm a problem";
		thrown.expect(AAISchemaValidationException.class);
		thrown.expectMessage(testError);
		strat.notifyOnError(testError);
	}

}
