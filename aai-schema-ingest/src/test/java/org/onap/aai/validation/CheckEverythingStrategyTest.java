package org.onap.aai.validation;

import static org.junit.Assert.*;

import org.junit.Test;
import org.onap.aai.validation.CheckEverythingStrategy;

public class CheckEverythingStrategyTest {

	@Test
	public void test() {
		CheckEverythingStrategy strat = new CheckEverythingStrategy();
		//no issues so nothing notified, should be fine
		assertTrue(strat.isOK());
		assertTrue("No errors found.".equals(strat.getErrorMsg()));
		
		//simulate post one error
		String testError1 = "oh noes a problem with the gooble-gobble edge rule!";
		strat.notifyOnError(testError1);
		assertFalse(strat.isOK());
		assertTrue(testError1.equals(strat.getErrorMsg()));
		
		//simulate multiple found
		String testError2 = "error 2";
		String testError3 = "duplicate labels not everything is a fork";
		strat.notifyOnError(testError2);
		strat.notifyOnError(testError3);
		assertFalse(strat.isOK());
		System.out.println(strat.getErrorMsg());
		assertTrue(strat.getErrorMsg().contains(testError1));
		assertTrue(strat.getErrorMsg().contains(testError2));
		assertTrue(strat.getErrorMsg().contains(testError3));
	}

}
