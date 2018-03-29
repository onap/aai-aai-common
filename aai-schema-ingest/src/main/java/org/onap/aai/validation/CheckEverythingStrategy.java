/**
 * 
 */
package org.onap.aai.validation;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

/**
 * When an error is found, mark that it is NOT ok to 
 * continue with installation/whatever other caller function, 
 * and keep track of the message but
 * keep validating so all issues are found in one run.
 */
public class CheckEverythingStrategy implements SchemaErrorStrategy {
	private boolean isOK = true;
	private List<String> errorMsgs = new ArrayList<>();

	/* (non-Javadoc)
	 * @see org.onap.aai.edges.validation.SchemaErrorStrategy#isOK()
	 */
	@Override
	public boolean isOK() {
		return isOK;
	}

	/* (non-Javadoc)
	 * @see org.onap.aai.edges.validation.SchemaErrorStrategy#getErrorMsg()
	 */
	@Override
	public String getErrorMsg() {
		if (errorMsgs.isEmpty()) {
			return "No errors found.";
		} else {
			return StringUtils.join(errorMsgs, "\n");
		}
	}

	/* (non-Javadoc)
	 * @see org.onap.aai.edges.validation.SchemaErrorStrategy#notifyOnError(java.lang.String)
	 */
	@Override
	public void notifyOnError(String errorMsg) {
		isOK = false;
		errorMsgs.add(errorMsg);
	}

}
