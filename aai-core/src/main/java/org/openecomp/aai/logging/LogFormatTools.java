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

package org.openecomp.aai.logging;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class LogFormatTools {

	private static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";
	private static final DateTimeFormatter DTF = DateTimeFormatter.ofPattern(DATE_FORMAT)
																	.withZone(ZoneOffset.UTC);

	public static String getCurrentDateTime() {
		return DTF.format(ZonedDateTime.now());
	}

	public static String toDate(long timestamp) {
		return DTF.format(Instant.ofEpochMilli(timestamp));
	}

	public static long toTimestamp(String date) {
		return ZonedDateTime.parse(date, DTF).toInstant().toEpochMilli();
	}
}
