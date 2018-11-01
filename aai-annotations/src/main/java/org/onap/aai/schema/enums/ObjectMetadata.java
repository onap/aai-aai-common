/**
 * ============LICENSE_START=======================================================
 * org.onap.aai
 * ================================================================================
 * Copyright © 2017-2018 AT&T Intellectual Property. All rights reserved.
 * ================================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ============LICENSE_END=========================================================
 */
package org.onap.aai.schema.enums;

public enum ObjectMetadata {

	/**
	 * description of object
	 */
	DESCRIPTION("description"),
	/**
	 * names of properties to appear in relationship-lists
	 * and parent objects in DMaaP messages
	 * <br><b>comma separated list</b>
	 */
	NAME_PROPS("nameProps"),
	/**
	 * names of properties to be indexed in the
	 * db schema
	 * <br><b>comma separated list</b>
	 */
	INDEXED_PROPS("indexedProps"),
	/**
	 * name of the object this one depends on
	 */
	DEPENDENT_ON("dependentOn"),
	/**
	 * name of the object which contains this object
	 */
	CONTAINER("container"),
	/**
	 * the top level namespace to which this object belongs<br>
	 * <b>only valid on top level objects</b>
	 */
	NAMESPACE("namespace"),
	/**
	 * properties which are searchable via the GUI
	 * <br><b>comma separated list</b>
	 */
	SEARCHABLE("searchable"),
	/**
	 * properties marked as unique in the db schema
	 * <br><b>comma separated list</b>
	 */
	UNIQUE_PROPS("uniqueProps"),
	/**
	 * properties marked as required
	 * <br><b>comma separated list</b>
	 */
	REQUIRED_PROPS("requiredProps"),
	/**
	 * uri template for this object
	 */
	URI_TEMPLATE("uriTemplate"),
	/**
	 * abstract type from which this object extends
	 */
	EXTENDS("extends"),
	/**
	 * comma separated list of objects who inherit this object<br>
	 * <b>only valid on abstract objects</b>
	 */
	INHERITORS("inheritors"),
	/**
	 * a value of true marks this object as abstract
	 * abstract objects cannot be read/written directly
	 * they resolve to "or queries" when used in queries
	 */
	ABSTRACT("abstract"),
	/**
	 * comma separated list of properties which are alternate ways
	 * to identify this object
	 */
	ALTERNATE_KEYS_1("alternateKeys1"),
	/**
	 * the maximum allowable retrievable depth 
	 */
	MAXIMUM_DEPTH("maximumDepth"),
	/**
	 * collection of other objects to retrieve along with this one
	 *  <br><b>comma separated list</b>
	 */
	CROSS_ENTITY_REFERENCE("crossEntityReference"),
	/**
	 * Marks that this object can be linked to via dataLink 
	 */
	CAN_BE_LINKED("canBeLinked"),
	/**
	 * The entity contains properties that are suggestible
	 */
	CONTAINS_SUGGESTIBLE_PROPS("containsSuggestibleProps"),
	/**
	 * A list of aliases for the entity name (for AAI UI searches)
	 */
	SUGGESTION_ALIASES("suggestionAliases"),
	/**
	 * a value of true allows this object to be read directly
	 */
	ALLOW_DIRECT_READ("allowDirectRead"),
	/**
	 * a value of true allows this object to be written directly
	 */
	ALLOW_DIRECT_WRITE("allowDirectWrite");
	
	 private final String name;

	  private ObjectMetadata(String name) { 
	    this.name = name;
	  }

	  @Override public String toString() {
	    return name; 
	  }
	
}
