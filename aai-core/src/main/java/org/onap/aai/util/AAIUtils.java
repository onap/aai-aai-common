/**
 * ============LICENSE_START=======================================================
 * org.onap.aai
 * ================================================================================
 * Copyright © 2017-2018 AT&T Intellectual Property. All rights reserved.
 * ================================================================================
 *  Modifications Copyright © 2018 IBM.
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

package org.onap.aai.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.TimeZone;

public class AAIUtils {

    /**
     * Instantiates AAIUtils.
     */
    private AAIUtils() {
        // prevent instantiation
    }

    /**
     * Null check.
     *
     * @param <T> the generic type
     * @param iterable the iterable
     * @return the iterable
     */
    public static <T> Iterable<T> nullCheck(Iterable<T> iterable) {
        return iterable == null ? Collections.<T>emptyList() : iterable;
    }

    /**
     * Gen date.
     *
     * @return the string
     */
    public static String genDate() {
        Date date = new Date();
        DateFormat formatter = new SimpleDateFormat("YYMMdd-HH:mm:ss:SSS");
        formatter.setTimeZone(TimeZone.getTimeZone("GMT"));
        return formatter.format(date);
    }
}
