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
package org.onap.aai.auth.aaf;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Properties;
import org.junit.Test;
import static org.junit.Assert.assertTrue;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import javax.servlet.http.HttpServletRequest;

import org.onap.aai.AAISetup;


/**
 * The Class CertUtilTest
 */
public class CertUtilTest extends AAISetup {

	@Test
	public void testCadiCertIssuers() throws IOException {
		String propFile = System.getProperty("BUNDLECONFIG_DIR") + "/aaf/cadi.properties";
		Properties cadiProperties = new Properties();
        cadiProperties.load(new FileInputStream(new File(propFile)));
		
		List<String> issuersList = CertUtil.getCadiCertIssuers(cadiProperties);
		assertTrue("issuersList isn't populated", !issuersList.isEmpty());
		
		int x = issuersList.get(0).indexOf(" ");
		assertTrue("issuer contains spaces", x < 0);
	}
	@Test
    public void testAaiSslClientOuHeader () {

        HttpServletRequest mockRequest = createMock(HttpServletRequest.class);
        expect(mockRequest.getHeader(CertUtil.AAI_SSL_CLIENT_OU_HDR)).andReturn("m55555@org.onap.com:TEST").times(1, 4);
        expect(mockRequest.getHeader(CertUtil.AAI_SSL_CLIENT_CN_HDR)).andReturn("CN").times(1, 2);
        expect(mockRequest.getHeader(CertUtil.AAI_SSL_CLIENT_O_HDR)).andReturn("O").times(1, 2);
        expect(mockRequest.getHeader(CertUtil.AAI_SSL_CLIENT_L_HDR)).andReturn("L").times(1, 2);
        expect(mockRequest.getHeader(CertUtil.AAI_SSL_CLIENT_ST_HDR)).andReturn("ST").times(1, 2);
        expect(mockRequest.getHeader(CertUtil.AAI_SSL_CLIENT_C_HDR)).andReturn("C").times(1, 2);

        replay(mockRequest);
        String ou = CertUtil.getAaiSslClientOuHeader (mockRequest);
        assertTrue("OU Header value is not as expected", ou.equals("m55555@org.onap.com:TEST"));

        assertTrue("Unexpected isHaProxy() return value", CertUtil.isHaProxy(mockRequest));

        String mechId = CertUtil.getMechId(mockRequest);
        assertTrue("mechid value is not as expected", mechId.equals("m55555@org.onap.com"));

    }
    @Test
    public void testBuildUserChain () {

        // aaf.userchain.pattern=<AAF-ID>:${aaf.userchain.service.reference}:${aaf.userchain.auth.type}:AS
        String aafUserChainPattern = "<AAF-ID>:org.onap.haproxy:X509:AS";
        String mechid = "m11111@onap.org";
        String result = CertUtil.buildUserChainHeader(mechid, aafUserChainPattern);

        assertTrue("user chain value is not as expected",
            "m11111@onap.org:org.onap.haproxy:X509:AS".equals(result));

    }
}
