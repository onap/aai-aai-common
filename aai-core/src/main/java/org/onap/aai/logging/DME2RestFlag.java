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

import ch.qos.logback.access.pattern.AccessConverter;
import ch.qos.logback.access.spi.IAccessEvent;

public class DME2RestFlag extends AccessConverter {

    /**
     * @{inheritDoc}
     */
    @Override
    public String convert(IAccessEvent accessEvent) {
        if (!isStarted()) {
            return "INACTIVE_HEADER_CONV";
        }

        String flag = "-";

        if (accessEvent.getRequestParameter("envContext").length > 0
                && !accessEvent.getRequestParameter("envContext")[0].isEmpty()
                && !accessEvent.getRequestParameter("envContext")[0].equals("-")
                && accessEvent.getRequestParameter("routeOffer").length > 0
                && !accessEvent.getRequestParameter("routeOffer")[0].isEmpty()
                && !accessEvent.getRequestParameter("routeOffer")[0].equals("-")
                && accessEvent.getRequestParameter("version").length > 0
                && !accessEvent.getRequestParameter("version")[0].isEmpty()
                && !accessEvent.getRequestParameter("version")[0].equals("-")) {
            flag = "DME2";
        } else {
            flag = "REST";
        }

        return flag;
    }
}
