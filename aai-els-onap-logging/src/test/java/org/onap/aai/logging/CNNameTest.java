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

import ch.qos.logback.access.spi.IAccessEvent;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.mock.web.MockHttpServletRequest;

import javax.security.auth.x500.X500Principal;
import java.security.cert.X509Certificate;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CNNameTest {
    @Mock
    X509Certificate cert;

    @Mock
    IAccessEvent accessEvent;

    @Spy
    @InjectMocks
    private CNName cnName;

    @Before
    public void setup() {
        when(cnName.isStarted()).thenReturn(true);
    }
    @Test
    public void basicAuthTest(){

        MockHttpServletRequest https = new MockHttpServletRequest();
        https.addHeader("Authorization", "Basic dXNlcjpwYXNzd29yZA==");
        https.setAttribute("javax.servlet.request.cipher_suite", "");
        https.setAttribute("javax.servlet.request.X509Certificate", null);

        when(accessEvent.getRequest()).thenReturn(https);
        assertEquals("user", cnName.convert(accessEvent));

    }
    @Test
    public void incorrectHeaderBasicAuthTest(){

        MockHttpServletRequest https = new MockHttpServletRequest();

        https.addHeader("Authorization", "dXNlcjpwYXNzd29yZA==");
        https.setAttribute("javax.servlet.request.cipher_suite", "");
        https.setAttribute("javax.servlet.request.X509Certificate", null);

        when(accessEvent.getRequest()).thenReturn(https);
        assertEquals("-", cnName.convert(accessEvent));

    }
    @Test
    public void noCipherSuiteTest(){

        MockHttpServletRequest https = new MockHttpServletRequest();

        https.addHeader("Authorization", "Basic dXNlcjpwYXNzd29yZA==");
        https.setAttribute("javax.servlet.request.cipher_suite", null);
        https.setAttribute("javax.servlet.request.X509Certificate", null);

        when(accessEvent.getRequest()).thenReturn(https);
        assertEquals("-", cnName.convert(accessEvent));

    }
    @Test
    public void certificateTest(){
        String testSubject = "CN=TestName, OU=TestOU, O=TestOrg, C=Country";
        X509Certificate[] certChain = { cert };
        MockHttpServletRequest https = new MockHttpServletRequest();

        https.setAttribute("javax.servlet.request.cipher_suite", "");
        https.setAttribute("javax.servlet.request.X509Certificate", certChain );

        when(accessEvent.getRequest()).thenReturn(https);
        when(cert.getSubjectX500Principal()).thenReturn(new X500Principal(testSubject) );

        assertEquals(testSubject, cnName.convert(accessEvent));
    }

}
