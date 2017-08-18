/*-
 * ============LICENSE_START=======================================================
 * org.openecomp.aai
 * ================================================================================
 * Copyright (C) 2017 AT&T Intellectual Property. All rights reserved.
 * ================================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ============LICENSE_END=========================================================
 */

package org.openecomp.aai.introspection.tools;

import java.util.Map;

import org.openecomp.aai.introspection.Introspector;
import org.openecomp.aai.schema.enums.PropertyMetadata;

public class DefaultFields implements IssueResolver {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean resolveIssue(Issue issue) {

		Introspector obj = issue.getIntrospector();
		if (issue.getType().equals(IssueType.MISSING_REQUIRED_PROP)) {
			Map<PropertyMetadata, String> metadata = obj.getPropertyMetadata(issue.getPropName());
			if (metadata.containsKey(PropertyMetadata.DEFAULT_VALUE)) {
				obj.setValue(issue.getPropName(), metadata.get(PropertyMetadata.DEFAULT_VALUE));
				return true;
			}
		}
		
		return false;
	}

}
