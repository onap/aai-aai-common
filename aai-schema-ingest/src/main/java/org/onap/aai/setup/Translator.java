/** 
 * ============LICENSE_START=======================================================
 * org.onap.aai
 * ================================================================================
 * Copyright © 2017-18 AT&T Intellectual Property. All rights reserved.
 * ================================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ============LICENSE_END=========================================================
 */

package org.onap.aai.setup;

import org.springframework.beans.factory.annotation.Autowired;

import java.io.*;
import java.util.List;
/**
 * Converts the contents of the schema config file
 * (which lists which schema files to be loaded) to
 * the format the Ingestors can work with.
 * 
 */
public abstract class Translator {

	protected SchemaVersions schemaVersions;

	public Translator(SchemaVersions schemaVersions) {
		this.schemaVersions = schemaVersions;
	}
	
	/**
	 * Translates the contents of the schema config file
	 * into the input for the NodeIngestor
	 * 
	 * @return Map of Version to the list of (string) filenames to be 
	 * ingested for that version
	 */


    public abstract List<InputStream> getVersionNodeStream(SchemaVersion version) throws IOException;

    public abstract List<String>
    getJsonPayload(SchemaVersion version) throws IOException;

	/**
	 * Translates the contents of the schema config file
	 * into the input for the EdgeIngestor
	 * 
	 * @return Map of Version to the List of (String) filenames to be 
	 * ingested for that version
	 */


	public SchemaVersions getSchemaVersions(){
        return this.schemaVersions;
	}
}
