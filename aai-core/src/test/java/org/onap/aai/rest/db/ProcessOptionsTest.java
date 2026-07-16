/**
 * ============LICENSE_START=======================================================
 * org.onap.aai
 * ================================================================================
 * Copyright © 2026 Deutsche Telekom.
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

package org.onap.aai.rest.db;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.Collections;
import java.util.Set;

import org.junit.Test;
import org.onap.aai.db.props.AAIProperties;

public class ProcessOptionsTest {

    @Test
    public void defaultsAreSane() {
        ProcessOptions o = ProcessOptions.forSourceOfTruth("JUNIT").build();
        assertEquals("JUNIT", o.sourceOfTruth());
        assertEquals(Collections.emptySet(), o.groups());
        assertTrue(o.enableResourceVersion());
        assertNull(o.queryOptions());
        assertNull(o.notification());
        // default depth comes from config key aai.notification.depth.all.enabled (default "true" -> MAXIMUM_DEPTH)
        assertEquals(AAIProperties.MAXIMUM_DEPTH.intValue(), o.notificationDepth());
    }

    @Test
    public void builderOverridesApply() {
        Set<String> groups = Set.of("role-a");
        ProcessOptions o = ProcessOptions.forSourceOfTruth("app")
                .groups(groups)
                .enableResourceVersion(false)
                .notificationDepth(AAIProperties.MINIMUM_DEPTH)
                .build();
        assertSame(groups, o.groups());
        assertEquals(false, o.enableResourceVersion());
        assertEquals(AAIProperties.MINIMUM_DEPTH.intValue(), o.notificationDepth());
    }
}
