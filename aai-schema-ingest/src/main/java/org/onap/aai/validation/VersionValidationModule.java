package org.onap.aai.validation;


/**
 * Defines the behavior for what versions are required/optional.
 * 
 * Constructor must take ConfigTranslator via autowiring.
 */
public interface VersionValidationModule {
	
	/**
	 * Validates that all required versions have schema
	 * configured for them.
	 *   
	 * @return empty string if none missing or else an appropriate error
	 */
	public String validate();
}
