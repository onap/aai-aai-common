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
package org.onap.aai.logging;

import org.onap.aai.logging.LoggingContext.LoggingField;

import ch.qos.logback.classic.pattern.ClassicConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;

public class EcompResponseCode extends ClassicConverter {

	@Override
	public String convert(ILoggingEvent event) {

		if (!event.getMDCPropertyMap().containsKey(LoggingField.RESPONSE_CODE.toString())) {
			// if response code is not set, return "unknown" (900)
			return LoggingContext.UNKNOWN_ERROR;
		}
		return event.getMDCPropertyMap().get(LoggingField.RESPONSE_CODE.toString());
	}
}
