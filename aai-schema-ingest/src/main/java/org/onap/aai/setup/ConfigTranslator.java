package org.onap.aai.setup;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

/**
 * Converts the contents of the schema config file
 * (which lists which schema files to be loaded) to
 * the format the Ingestors can work with.
 * 
 */
public abstract class ConfigTranslator {
	protected SchemaLocationsBean bean;
	
	@Autowired
	public ConfigTranslator(SchemaLocationsBean bean) {
		this.bean = bean;
	}
	
	/**
	 * Translates the contents of the schema config file
	 * into the input for the NodeIngestor
	 * 
	 * @return Map of Version to the list of (string) filenames to be 
	 * ingested for that version
	 */
	public abstract Map<Version, List<String>> getNodeFiles();
	
	/**
	 * Translates the contents of the schema config file
	 * into the input for the EdgeIngestor
	 * 
	 * @return Map of Version to the List of (String) filenames to be 
	 * ingested for that version
	 */
	public abstract Map<Version, List<String>> getEdgeFiles();
}
