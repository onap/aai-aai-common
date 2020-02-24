/*-
 * ============LICENSE_START=======================================================
 * ONAP - Logging
 * ================================================================================
 * Copyright (C) 2019 AT&T Intellectual Property. All rights reserved.
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

package org.onap.aai.logging;

public class AaiElsErrorCode {
    public static final String SUCCESS = "0";
    public static final String PERMISSION_ERROR = "100";
    public static final String AVAILABILITY_TIMEOUT_ERROR = "200";
    public static final String DATA_ERROR = "300";
    public static final String SCHEMA_ERROR = "400";
    public static final String BUSINESS_PROCESS_ERROR = "500";
    public static final String UNKNOWN_ERROR = "900";
}
