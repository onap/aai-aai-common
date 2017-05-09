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

package org.openecomp.aai.parsers.exceptions;

import org.openecomp.aai.exceptions.AAIException;

public class AmbiguousMapAAIException extends AAIException {
	
	private static final long serialVersionUID = -878581771971431246L;
	
	public AmbiguousMapAAIException(String message) {
		super("AAI_6146", message);
	}

	public AmbiguousMapAAIException(Throwable cause) {
		super("AAI_6146",cause);
	}

	public AmbiguousMapAAIException(String message, Throwable cause) {
		super("AAI_6146", cause, message);
	}

}
