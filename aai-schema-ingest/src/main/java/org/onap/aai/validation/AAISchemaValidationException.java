package org.onap.aai.validation;

/**
 * Indicates that a fatal error in the A&AI schema has been found.
 */
public class AAISchemaValidationException extends IllegalStateException {
	public AAISchemaValidationException(String msg) {
		super(msg);
	}
}
