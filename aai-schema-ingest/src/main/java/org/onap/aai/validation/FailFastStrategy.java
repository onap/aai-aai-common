/**
 * 
 */
package org.onap.aai.validation;

/**
 * Fails out the validation process as soon as
 * an error is found. Tells the validation's calling
 * process to abort.
 */
public class FailFastStrategy implements SchemaErrorStrategy {
	private boolean isOK = true;
	private String errorMsg = "No errors found.";

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
		return errorMsg;
	}

	/* (non-Javadoc)
	 * @see org.onap.aai.edges.validation.SchemaErrorStrategy#notifyOnError(java.lang.String)
	 */
	@Override
	public void notifyOnError(String errorMsg) {
		isOK = false;
		this.errorMsg = errorMsg;
		throw new AAISchemaValidationException(errorMsg);
	}

}
