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
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ============LICENSE_END=========================================================
 */

package org.onap.aai.auth;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.onap.aai.AAISetup;
import org.onap.aai.auth.exceptions.AAIUnrecognizedFunctionException;

public class AAIAuthCoreTest extends AAISetup {

    private AAIAuthCore authCore;

    @Before
    public void setup() {
        authCore = new AAIAuthCore("/aai");
    }

    @Test
    public void getAuthPolicyFunctionNameTest() {

        String uri = "/aai/v3/search/edge-tag-query";
        assertEquals("Get aai function name from " + uri, "search",
            authCore.getAuthPolicyFunctName(uri));

        uri = "/aai/v10/search/edge-tag-query";
        assertEquals("Get aai function name from " + uri, "search",
            authCore.getAuthPolicyFunctName(uri));

        uri = "/aai/search/model";
        assertEquals("Get aai function name from " + uri, "search",
            authCore.getAuthPolicyFunctName(uri));

        uri =
            "/aai/v9/cloud-infrastructure/cloud-regions/cloud-region/somecloudregion/some-cloud-owner";
        assertEquals("Get aai function name from " + uri, "cloud-infrastructure",
            authCore.getAuthPolicyFunctName(uri));

        uri = "/aai/v8/network/pnfs/pnf/ff4ca01orc/p-interfaces";
        assertEquals("Get aai function name from " + uri, "network",
            authCore.getAuthPolicyFunctName(uri));

        uri = "/aai/util/echo";
        assertEquals("Get aai function name from " + uri, "util",
            authCore.getAuthPolicyFunctName(uri));

        uri = "/aai/tools";
        assertEquals("Get aai function name from " + uri, "tools",
            authCore.getAuthPolicyFunctName(uri));

        uri = "/aai/v12/bulk/single-transaction";
        assertEquals("Get aai function name from " + uri, "bulk",
            authCore.getAuthPolicyFunctName(uri));

    }

    @Test
    public void validUsernameAuthTest() throws AAIUnrecognizedFunctionException {
        assertTrue(authCore.authorize("testUser".toLowerCase(), "/aai/v0/testFunction/someUri",
            "PUT", ""));
    }

    @Test
    public void validUsernameInvalidHttpMethodAuthTest() throws AAIUnrecognizedFunctionException {
        assertFalse(authCore.authorize("testUser".toLowerCase(), "/aai/v0/testFunction/someUri",
            "POST", ""));
    }

    @Test(expected = AAIUnrecognizedFunctionException.class)
    public void validUsernameInvalidFunctionInURIAuthTest()
        throws AAIUnrecognizedFunctionException {
        authCore.authorize("testUser".toLowerCase(), "/aai/v0/badFunction/someUri", "PUT", "");
    }

    @Test
    public void invalidUsernameAuthTest() throws AAIUnrecognizedFunctionException {
        assertFalse(authCore.authorize("invlaidTestUser".toLowerCase(),
            "/aai/v0/testFunction/someUri", "PUT", ""));
    }

    @Test
    public void validUsernameIsTheExactWildcardIdAuthTest()
        throws AAIUnrecognizedFunctionException {
        assertTrue(authCore.authorize("testWildcardId".toLowerCase(),
            "/aai/v0/testFunction/someUri", "PUT", ""));
    }

    @Test
    public void validUsernameContainsTheWildcardIdAuthTest()
        throws AAIUnrecognizedFunctionException {
        assertTrue(authCore.authorize("cn=blah, testWildcardId, O=".toLowerCase(),
            "/aai/v0/testFunction/someUri", "PUT", "", "aafWildCardIssuer"));
    }

    @Test
    public void validUsernameContainsTheWildcardIdInvalidIssuerAuthTest()
        throws AAIUnrecognizedFunctionException {
        assertFalse(authCore.authorize("cn=blah, testWildcardId, O=".toLowerCase(),
            "/aai/v0/testFunction/someUri", "PUT", "", "invalidIssuer"));
    }

    @Test
    public void invalidUsernameContainsRegularUsernameAuthTest()
        throws AAIUnrecognizedFunctionException {
        assertFalse(authCore.authorize("cn=blah, testUser, O=".toLowerCase(),
            "/aai/v0/testFunction/someUri", "PUT", ""));
    }

    @Test
    public void haProxyUsernameAuthTest() throws AAIUnrecognizedFunctionException {
        assertTrue(authCore.authorize("ha-proxy-user".toLowerCase(), "/aai/util/echo", "GET", ""));
    }

    @Test
    public void haProxyUsernameInvalidFunctionAuthTest() throws AAIUnrecognizedFunctionException {
        assertFalse(authCore.authorize("ha-proxy-user".toLowerCase(),
            "/aai/v0/testFunction/someUri", "PUT", ""));
    }

    @Test
    public void validUsernameViaHaProxyAuthTest() throws AAIUnrecognizedFunctionException {
        assertTrue(authCore.authorize("ha-proxy-user".toLowerCase(), "/aai/v0/testFunction/someUri",
            "PUT", "testUser".toLowerCase()));
    }

    @Test
    public void validUsernameInvalidHttpMethodViaHaProxyAuthTest()
        throws AAIUnrecognizedFunctionException {
        assertFalse(authCore.authorize("ha-proxy-user".toLowerCase(),
            "/aai/v0/testFunction/someUri", "POST", "testUser".toLowerCase()));
    }

    @Test(expected = AAIUnrecognizedFunctionException.class)
    public void validUsernameInvalidFunctionInURIViaHaProxyAuthTest()
        throws AAIUnrecognizedFunctionException {
        authCore.authorize("ha-proxy-user".toLowerCase(), "/aai/v0/badFunction/someUri", "PUT",
            "testUser".toLowerCase());
    }

    @Test
    public void invalidUsernameViaHaProxyAuthTest() throws AAIUnrecognizedFunctionException {
        assertFalse(authCore.authorize("ha-proxy-user".toLowerCase(),
            "/aai/v0/testFunction/someUri", "PUT", "invlaidTestUser".toLowerCase()));
    }

    @Test
    public void validUsernameIsTheExactWildcardIdViaHaProxyAuthTest()
        throws AAIUnrecognizedFunctionException {
        assertTrue(authCore.authorize("ha-proxy-user".toLowerCase(), "/aai/v0/testFunction/someUri",
            "PUT", "testWildcardId".toLowerCase()));
    }

    @Test
    public void validUsernameContainsTheWildcardIdViaHaProxyAuthTest()
        throws AAIUnrecognizedFunctionException {
        assertTrue(authCore.authorize("ha-proxy-user".toLowerCase(), "/aai/v0/testFunction/someUri",
            "PUT", "cn=blah, testWildcardId, O=".toLowerCase(), "aafWildCardIssuer"));
    }

    @Test
    public void invalidUsernameContainsRegularUsernameViaHaProxyAuthTest()
        throws AAIUnrecognizedFunctionException {
        assertFalse(authCore.authorize("ha-proxy-user".toLowerCase(),
            "/aai/v0/testFunction/someUri", "PUT", "cn=blah, testUser, O=".toLowerCase()));
    }

    @Test
    public void haProxyUsernameTwiceAuthTest() throws AAIUnrecognizedFunctionException {
        assertFalse(authCore.authorize("ha-proxy-user".toLowerCase(),
            "/aai/v0/testFunction/someUri", "PUT", "ha-proxy-user".toLowerCase()));
    }

    @Test
    public void haProxyWildcardIdAuthTest() throws AAIUnrecognizedFunctionException {
        assertTrue(authCore.authorize("cn=blah, ha-proxy-wildcard-id, O=".toLowerCase(),
            "/aai/util/echo", "GET", "", "aafWildCardIssuer"));
    }

    @Test
    public void haProxyWildcardIdInvalidFunctionAuthTest() throws AAIUnrecognizedFunctionException {
        assertFalse(authCore.authorize("cn=blah, ha-proxy-wildcard-id, O=".toLowerCase(),
            "/aai/v0/testFunction/someUri", "PUT", ""));
    }

    @Test
    public void validUsernameViaHaProxyWildcardIdAuthTest()
        throws AAIUnrecognizedFunctionException {
        assertTrue(authCore.authorize("cn=blah, ha-proxy-wildcard-id, O=".toLowerCase(),
            "/aai/v0/testFunction/someUri", "PUT", "testUser".toLowerCase(), "aafWildCardIssuer"));
    }

    @Test
    public void validUsernameInvalidHttpMethodViaHaProxyWildcardIdAuthTest()
        throws AAIUnrecognizedFunctionException {
        assertFalse(authCore.authorize("cn=blah, ha-proxy-wildcard-id, O=".toLowerCase(),
            "/aai/v0/testFunction/someUri", "POST", "testUser".toLowerCase()));
    }

    @Test(expected = AAIUnrecognizedFunctionException.class)
    public void validUsernameInvalidFunctionInURIViaHaProxyWildcardIdAuthTest()
        throws AAIUnrecognizedFunctionException {
        authCore.authorize("cn=blah, ha-proxy-wildcard-id, O=".toLowerCase(),
            "/aai/v0/badFunction/someUri", "PUT", "testUser".toLowerCase());
    }

    @Test
    public void invalidUsernameViaHaProxyWildcardIdAuthTest()
        throws AAIUnrecognizedFunctionException {
        assertFalse(authCore.authorize("cn=blah, ha-proxy-wildcard-id, O=".toLowerCase(),
            "/aai/v0/testFunction/someUri", "PUT", "invlaidTestUser".toLowerCase()));
    }

    @Test
    public void validUsernameIsTheExactWildcardIdViaHaProxyWildcardIdAuthTest()
        throws AAIUnrecognizedFunctionException {
        assertTrue(authCore.authorize("cn=blah, ha-proxy-wildcard-id, O=".toLowerCase(),
            "/aai/v0/testFunction/someUri", "PUT", "testWildcardId".toLowerCase(),
            "aafWildCardIssuer"));
    }

    @Test
    public void validUsernameContainsTheWildcardIdViaHaProxyWildcardIdAuthTest()
        throws AAIUnrecognizedFunctionException {
        assertTrue(authCore.authorize("cn=blah, ha-proxy-wildcard-id, O=".toLowerCase(),
            "/aai/v0/testFunction/someUri", "PUT", "cn=blah, testWildcardId, O=".toLowerCase(),
            "aafWildCardIssuer"));
    }

    @Test
    public void validUsernameContainsTheWildcardIdViaHaProxyWildcardIdInvalidIssuerAuthTest()
        throws AAIUnrecognizedFunctionException {
        assertFalse(authCore.authorize("cn=blah, ha-proxy-wildcard-id, O=".toLowerCase(),
            "/aai/v0/testFunction/someUri", "PUT", "cn=blah, testWildcardId, O=".toLowerCase(),
            "invalidIssuer"));
    }

    @Test
    public void invalidUsernameContainsRegularUsernameViaHaProxyWildcardIdAuthTest()
        throws AAIUnrecognizedFunctionException {
        assertFalse(authCore.authorize("cn=blah, ha-proxy-wildcard-id, O=".toLowerCase(),
            "/aai/v0/testFunction/someUri", "PUT", "cn=blah, testUser, O=".toLowerCase()));
    }

}
