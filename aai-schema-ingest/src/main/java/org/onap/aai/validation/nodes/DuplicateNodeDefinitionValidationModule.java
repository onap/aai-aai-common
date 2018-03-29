package org.onap.aai.validation.nodes;

import java.util.List;

import org.onap.aai.setup.Version;

/**
 * Defines rules for duplicate node definitions in a set of files
 * (where the intent is the set of files is all the OXM for one version).
 * 
 * Example Options:
 * -Any duplicated definition found is an error
 * -Duplicates within a namespace are OK but not across namespaces
 * -Anything goes
 * etc.
 */
public interface DuplicateNodeDefinitionValidationModule {
	/**
	 * Finds any duplicates according to the defined rules
	 * 
	 * @param files - the OXM files to use with full directory
	 * @return empty String if none found, else a String
	 * 	with appropriate information about what node types
	 *  were found
	 */
	public String findDuplicates(List<String> files, Version v);
}
