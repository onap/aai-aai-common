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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.net.URI;
import java.util.List;

import jakarta.ws.rs.core.Response;

import org.junit.Test;

public class ProcessResultTest {

    @Test
    public void firstReturnsFirstResult() {
        Response r0 = Response.ok("a").build();
        Response r1 = Response.ok("b").build();
        RequestResult rr0 = new RequestResult(URI.create("/x"), r0);
        RequestResult rr1 = new RequestResult(URI.create("/y"), r1);
        ProcessResult result = new ProcessResult(true, List.of(rr0, rr1));

        assertTrue(result.success());
        assertEquals(2, result.results().size());
        assertSame(rr0, result.first());
        assertSame(r0, result.first().response());
        assertEquals(URI.create("/x"), result.first().uri());
    }

    @Test
    public void carriesFailureFlag() {
        ProcessResult result = new ProcessResult(false, List.of());
        assertFalse(result.success());
    }
}
