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

package org.onap.aai.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

public class AAIUtilsTest {

    @Test
    public void testNullCheckWithNull() {
        List<String> newList = null;
        Iterable<String> res = AAIUtils.nullCheck(newList);
        assertNotNull("nullCheck() should return empty list", res);
        assertEquals(Collections.<String>emptyList(), res);
    }

    @Test
    public void testNullCheckWithList() {
        List<String> newList = new ArrayList<String>();
        newList.add("testString");

        Iterable<String> res = AAIUtils.nullCheck(newList);

        assertNotNull("nullCheck() should return back list", res);
        assertEquals(newList, res);
    }

    @Test
    public void testGenDate() {

        Date d1 = new Date(0);

        DateFormat formatter = new SimpleDateFormat("YYMMdd-HH:mm:ss:SSS");
        formatter.setTimeZone(TimeZone.getTimeZone("GMT"));
        formatter.setLenient(false);

        Date d2 = null;

        try {
            d2 = formatter.parse(AAIUtils.genDate());
        } catch (ParseException e) {
            fail("Date parsing exception");
            e.printStackTrace();
        }

        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e1) {
        }

        Date d3 = new Date();

        assertTrue("Generated date is not after a past date", d2.after(d1));
        assertTrue("Generated date is not before a future date", d2.before(d3));
    }

}
