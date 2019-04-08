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

import com.att.eelf.configuration.EELFLogger;
import com.att.eelf.configuration.EELFManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.cert.X509Certificate;
import java.util.*;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;

/**
 * The Class CertUtil provides cert related utility methods.
 */
public class CertUtil {
	public static final String DEFAULT_CADI_ISSUERS = "CN=ATT AAF CADI Test Issuing CA 01, OU=CSO, O=ATT, C=US:CN=ATT AAF CADI Test Issuing CA 02, OU=CSO, O=ATT, C=US";
	public static final String CADI_PROP_FILES = "cadi_prop_files";
	public static final String CADI_ISSUERS_PROP_NAME = "cadi_x509_issuers";
	public static final String CADI_ISSUERS_SEPARATOR = ":";
    public static final String AAI_SSL_CLIENT_OU_HDR = "X-AAI-SSL-Client-OU";
    public static final String AAI_SSL_ISSUER_HDR = "X-AAI-SSL-Issuer";
    public static final String AAI_SSL_CLIENT_CN_HDR = "X-AAI-SSL-Client-CN";
    public static final String AAI_SSL_CLIENT_O_HDR = "X-AAI-SSL-Client-O";
    public static final String AAI_SSL_CLIENT_L_HDR = "X-AAI-SSL-Client-L";
    public static final String AAI_SSL_CLIENT_ST_HDR = "X-AAI-SSL-Client-ST";
    public static final String AAI_SSL_CLIENT_C_HDR = "X-AAI-SSL-Client-C";
    public static final String AAF_USER_CHAIN_HDR = "USER_CHAIN";
    public static final String AAF_ID = "<AAF-ID>";
    private static final EELFLogger LOGGER = EELFManager.getInstance().getLogger(CertUtil.class);

    public static String getAaiSslClientOuHeader (HttpServletRequest hsr) {
        return(hsr.getHeader(AAI_SSL_CLIENT_OU_HDR));
    }
    public static boolean isHaProxy (HttpServletRequest hsr) {

        String haProxyUser = "";
        if (Objects.isNull(hsr.getHeader(AAI_SSL_CLIENT_CN_HDR))
            || Objects.isNull(hsr.getHeader(AAI_SSL_CLIENT_OU_HDR))
            || Objects.isNull(hsr.getHeader(AAI_SSL_CLIENT_O_HDR))
            || Objects.isNull(hsr.getHeader(AAI_SSL_CLIENT_L_HDR))
            || Objects.isNull(hsr.getHeader(AAI_SSL_CLIENT_ST_HDR))
            || Objects.isNull(hsr.getHeader(AAI_SSL_CLIENT_C_HDR))) {
            haProxyUser = "";
        } else {
            haProxyUser = String.format("CN=%s, OU=%s, O=\"%s\", L=%s, ST=%s, C=%s",
                Objects.toString(hsr.getHeader(AAI_SSL_CLIENT_CN_HDR), ""),
                Objects.toString(hsr.getHeader(AAI_SSL_CLIENT_OU_HDR), ""),
                Objects.toString(hsr.getHeader(AAI_SSL_CLIENT_O_HDR), ""),
                Objects.toString(hsr.getHeader(AAI_SSL_CLIENT_L_HDR), ""),
                Objects.toString(hsr.getHeader(AAI_SSL_CLIENT_ST_HDR), ""),
                Objects.toString(hsr.getHeader(AAI_SSL_CLIENT_C_HDR), "")).toLowerCase();
        }
        if ( !haProxyUser.isEmpty() ) {
            LOGGER.debug("isHaProxy haProxyUser=" + haProxyUser);
            return true;
        }
        LOGGER.debug("isHaProxy haProxyUser not found");
        return false;
    }

    public static String getMechId(HttpServletRequest hsr) {
        String mechId = null;
        String ou = getAaiSslClientOuHeader(hsr);
        if ((ou != null) && (!ou.isEmpty())){
            String[] parts = ou.split(CADI_ISSUERS_SEPARATOR);
            if (parts != null && parts.length >= 1) {
                mechId = parts[0];
            }
        }
        LOGGER.debug("getMechId mechId=" + mechId);
        return(mechId);
    }
    public static String getCertIssuer(HttpServletRequest hsr) {
		String issuer =  hsr.getHeader(AAI_SSL_ISSUER_HDR);
		if (issuer != null && !issuer.isEmpty()) {
            LOGGER.debug("getCertIssuer issuer from header " + AAI_SSL_ISSUER_HDR + " " + issuer );
			// the haproxy header replaces the ', ' with '/' and reverses on the '/' need to undo that.
			List<String> broken = Arrays.asList(issuer.split("/"));
			broken = broken.stream().filter(s -> !s.isEmpty()).collect(Collectors.toList());
			Collections.reverse(broken);
			issuer = String.join(", ", broken);
		} else {
            if (hsr.getAttribute("javax.servlet.request.cipher_suite") != null) {
                X509Certificate[] certChain = (X509Certificate[]) hsr.getAttribute("javax.servlet.request.X509Certificate");
                if (certChain != null && certChain.length > 0) {
                    X509Certificate clientCert = certChain[0];
                    issuer = clientCert.getIssuerX500Principal().getName();
                    LOGGER.debug("getCertIssuer issuer from client cert " + issuer );
                }
            }
		}
		return issuer;
	}
	
	public static List<String> getCadiCertIssuers(Properties cadiProperties) {
	
		List<String> defaultList = new ArrayList<String>();
		List<String> resultList = new ArrayList<String>();
		
		String[] cIssuers = DEFAULT_CADI_ISSUERS.split(CADI_ISSUERS_SEPARATOR);
        for (String issuer : cIssuers) {
			defaultList.add(issuer.replaceAll("\\s+","").toUpperCase());
		}
		try {
			String certPropFileName = cadiProperties.getProperty(CADI_PROP_FILES);
			String configuredIssuers = DEFAULT_CADI_ISSUERS;
	    	Properties certProperties = new Properties();
	        if ( certPropFileName != null ) {
		    	certProperties.load(new FileInputStream(new File(certPropFileName)));
		    	configuredIssuers = certProperties.getProperty(CADI_ISSUERS_PROP_NAME);
	        }
	    	if ((configuredIssuers != null) && (!configuredIssuers.isEmpty())) {
	    		cIssuers = configuredIssuers.split(CADI_ISSUERS_SEPARATOR);
	    		for (String issuer : cIssuers) {
	    			resultList.add(issuer.replaceAll("\\s+","").toUpperCase());
	    		}
	    	}
		}
		catch (IOException ioe) {
			return (defaultList);
		}
		if (resultList.isEmpty()) {
			return defaultList;
		}
        LOGGER.debug("getCadiCertIssuers " + resultList.toString() );
		return resultList;
	}
    public static String buildUserChainHeader(String user, String userChainPattern) {
        // aaf.userchain.pattern=<AAF-ID>:${aaf.userchain.service.reference}:${aaf.userchain.auth.type}:AS
        return (userChainPattern.replaceAll( AAF_ID, user));
    }
}
