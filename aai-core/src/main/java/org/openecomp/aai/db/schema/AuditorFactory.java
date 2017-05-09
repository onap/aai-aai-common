/*-
 * ============LICENSE_START=======================================================
 * org.openecomp.aai
 * ================================================================================
 * Copyright (C) 2017 AT&T Intellectual Property. All rights reserved.
 * ================================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 * ============LICENSE_END=========================================================
 */

package org.openecomp.aai.db.schema;

import org.openecomp.aai.introspection.Version;
import com.thinkaurelius.titan.core.TitanGraph;

public class AuditorFactory {

	/**
	 * Gets the OXM auditor.
	 *
	 * @param v the v
	 * @return the OXM auditor
	 */
	public static Auditor getOXMAuditor (Version v) {
		return new AuditOXM(v);
	}
	
	/**
	 * Gets the graph auditor.
	 *
	 * @param g the g
	 * @return the graph auditor
	 */
	public static Auditor getGraphAuditor (TitanGraph g) {
		return new AuditTitan(g);
	}
}
