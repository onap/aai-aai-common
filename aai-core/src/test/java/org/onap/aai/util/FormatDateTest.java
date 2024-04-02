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

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.zone.ZoneRulesException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

@Disabled
public class FormatDateTest {

    @Test
    public void testExceptionThrownWhenInvalidPatternIsPassed() {
        assertThrows(IllegalArgumentException.class, () -> {
            FormatDate formatDate = new FormatDate("XX/TT/GGGG");
            formatDate.getDateTime();
        });
    }

    @Test
    public void correctPattern() {
        FormatDate formatDate = new FormatDate("dd/mm/yyyy");
        Assertions.assertNotNull(formatDate.getDateTime());
    }

    @Test
    public void invalidPattern() {
        assertThrows(IllegalArgumentException.class, () -> {
            FormatDate formatDate = new FormatDate("XX/TT/GGGG", "GMT");
            formatDate.getDateTime();
        });
    }

    @Test
    public void invalidZone() {
        assertThrows(ZoneRulesException.class, () -> {
            FormatDate formatDate = new FormatDate("dd/mm/yyyy", "IST");
            formatDate.getDateTime();
        });
    }

    @Test
    public void testExceptionThrownWhenInvalidPatternAndZoneIsPassed() {
        assertThrows(IllegalArgumentException.class, () -> {
            FormatDate formatDate = new FormatDate("XX/TT/GGGG", "IST");
            formatDate.getDateTime();
        });
    }

    @Test
    public void correctPatternAndZone() {
        FormatDate formatDate = new FormatDate("dd/mm/yyyy", "GMT");
        Assertions.assertNotNull(formatDate.getDateTime());
    }
}
