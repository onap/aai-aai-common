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

import org.apache.commons.io.IOUtils;
import org.eclipse.jetty.util.security.Password;
import org.onap.aai.exceptions.AAIException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Properties;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AAIApplicationConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(AAIApplicationConfig.class);
    private static String GLOBAL_PROP_FILE_NAME = "application.properties";
    private static final String SERVER_CERTS_LOCATION_PROP_NAME = "server.certs.location";
    private static final String PASSPHRASSES_FILENAME = ".passphrases";
    private static final String PASSWORD_FILENAME = ".password";
    private static final String TRUSTSTORE_PASSWORD_PROP_NAME = "cadi_truststore_password";
    private static final String SERVER_SSL_KEYSTORE_PROP_NAME = "server.ssl.key-store";
    private static final String SERVER_SSL_KEYSTORE_PKCS12_PROP_NAME = "server.ssl.key-store.pkcs12";
    private static final String SERVER_SSL_TRUSTSTORE_PROP_NAME = "server.ssl.trust-store";
    private static Properties serverProps;
    private static boolean propsInitialized = false;
    private static String TRUSTSTORE_PASSWORD = null;
    private static String KEYSTORE_PASSWORD = null;
    private static final String PROPERTY_REGEX = "\\$\\{([^\\$\\{\\}]+)\\}";

    /**
     * Instantiates a new AAI config.
     */
    // Don't instantiate
    private AAIApplicationConfig() {
    }

    /**
     * Inits the.
     *
     * @throws AAIException the AAI exception
     */
    public synchronized static void init() {
        /*LoggingContext.save();
        LoggingContext.component("config");
        LoggingContext.partnerName("NA");
        LoggingContext.targetEntity("AAI");
        LoggingContext.requestId(UUID.randomUUID().toString());
        LoggingContext.serviceName("AAI");
        LoggingContext.targetServiceName("init");
        LoggingContext.statusCode(StatusCode.COMPLETE);*/

        LOGGER.info("Initializing AAIApplicationConfig");

        AAIApplicationConfig.reloadConfig();

        //LoggingContext.restore();
    }

    /**
     * Reload config.
     */
    public synchronized static void reloadConfig() {

        Properties newServerProps = new Properties();
        LOGGER.debug("Reloading config from " + GLOBAL_PROP_FILE_NAME);

        try {
            InputStream is = AAIApplicationConfig.class.getClassLoader().getResourceAsStream(GLOBAL_PROP_FILE_NAME);
            newServerProps.load(is);
            propsInitialized = true;
            serverProps = newServerProps;
            TRUSTSTORE_PASSWORD = retrieveTruststorePassword();
            KEYSTORE_PASSWORD = retrieveKeystorePassword();
        } catch (Exception fnfe) {
            final InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("application.properties");
            LOGGER.info("Unable to find the application.properties from filesystem so using file in jar");
            if (is != null) {
                try {
                    newServerProps.load(is);
                    serverProps = newServerProps;
                    TRUSTSTORE_PASSWORD = retrieveTruststorePassword();
                    KEYSTORE_PASSWORD = retrieveKeystorePassword();
                } catch (IOException e) {
                    LOGGER.warn("Encountered IO Exception during loading of props from inputstream", e);
                }
            } else {
                LOGGER.error("Expected to find the properties file in the jar but unable to find it");
            }
        }
    }

    /**
     * Gets the key value
     *
     * @param key          the key
     * @param defaultValue the default value
     * @return the string
     */
    public static String get(String key, String defaultValue) {
        String result = defaultValue;
        try {
            result = get(key);
        } catch (AAIException a) {
        }
        if (result == null || result.isEmpty()) {
            result = defaultValue;
        }
        return (result);
    }

    /**
     * Gets the key value
     *
     * @param key the key
     * @return the string
     * @throws AAIException the AAI exception
     */
    public static String get(String key) throws AAIException {
        String response = null;

        if (!propsInitialized || (serverProps == null)) {
            reloadConfig();
        }

        if ((key.endsWith("password") || key.endsWith("passwd") || key.endsWith("apisecret"))
                && serverProps.containsKey(key + ".x")) {
            String valx = serverProps.getProperty(key + ".x");
            return Password.deobfuscate(valx);
        }

        if (!serverProps.containsKey(key)) {
            throw new AAIException("AAI_4005", "Property key " + key + " cannot be found");
        } else {
            response = serverProps.getProperty(key);
            if (response == null || response.isEmpty()) {
                throw new AAIException("AAI_4005", "Property key " + key + " is null or empty");
            }
            response = replaceProperties(response);
        }
        return response;
    }

    /**
     * Gets the keystore path
     *
     * @return the string
     * @throws AAIException the AAI exception
     */
    public static String getKeystore() throws AAIException {
        return (get(SERVER_SSL_KEYSTORE_PROP_NAME));
    }
    /**
     * Gets the PKCS12 keystore path
     *
     * @return the string
     * @throws AAIException the AAI exception
     */
    public static String getKeystorePkcs12() throws AAIException {
        return (get(SERVER_SSL_KEYSTORE_PKCS12_PROP_NAME));
    }
    /**
     * Gets the keystore path
     *
     * @return the string
     * @throws AAIException the AAI exception
     */
    public static String getTruststore() throws AAIException {
        return (get(SERVER_SSL_TRUSTSTORE_PROP_NAME));
    }

    /**
     * Retrieve the keystore password
     *
     * @return the password
     */
    private static String retrieveKeystorePassword() {
        String certPath = serverProps.getProperty(SERVER_CERTS_LOCATION_PROP_NAME);
        if (certPath == null) {
            return null;
        }
        try {
            certPath = replaceProperties(certPath);
        }
        catch (AAIException e) {
            return null;
        }

        File passwordFile = null;
        InputStream passwordStream = null;
        String keystorePassword = null;

        // Override the passwords from application.properties if we find AAF certman files
        try {
            passwordFile = new File(certPath + PASSWORD_FILENAME);
            passwordStream = new FileInputStream(passwordFile);
            keystorePassword = IOUtils.toString(passwordStream, Charset.defaultCharset());
            if (keystorePassword != null) {
                keystorePassword = keystorePassword.trim();
            }

        } catch (IOException e) {
            LOGGER.warn("Not using AAF Certman password file, e=" + e.getMessage());
        } catch (NullPointerException n) {
            LOGGER.warn("Not using AAF Certman passphrases file, e=" + n.getMessage());
        } finally {
            if (passwordStream != null) {
                try {
                    passwordStream.close();
                } catch (Exception e) {
                }
            }
        }
        return keystorePassword;
    }

    /**
     * Get the keystore password
     *
     * @return the password
     */
    public static String getKeystorePassword() {
        return (KEYSTORE_PASSWORD);
    }

    /**
     * Gets the truststore password
     *
     * @return the password
     */
    private static String retrieveTruststorePassword() {
        String certPath = serverProps.getProperty(SERVER_CERTS_LOCATION_PROP_NAME);
        if (certPath == null) {
            return null;
        }
        try {
            certPath = replaceProperties(certPath);
        }
        catch (AAIException e) {
            return null;
        }
        File passphrasesFile = null;
        InputStream passphrasesStream = null;
        String truststorePassword = null;
        try {
            passphrasesFile = new File(certPath + PASSPHRASSES_FILENAME);
            passphrasesStream = new FileInputStream(passphrasesFile);


            Properties passphrasesProps = new Properties();
            passphrasesProps.load(passphrasesStream);
            truststorePassword = passphrasesProps.getProperty(TRUSTSTORE_PASSWORD_PROP_NAME);
            if (truststorePassword != null) {
                truststorePassword = truststorePassword.trim();
            }

        } catch (IOException e) {
            LOGGER.warn("Not using AAF Certman passphrases file, e=" + e.getMessage());
        } catch (NullPointerException n) {
            LOGGER.warn("Not using AAF Certman passphrases file, e=" + n.getMessage());
        } finally {
            if (passphrasesStream != null) {
                try {
                    passphrasesStream.close();
                } catch (Exception e) {
                }
            }
        }

        return truststorePassword;
    }

    /**
     * Get the trustore password
     *
     * @return the password
     */
    public static String getTruststorePassword() {
        return (TRUSTSTORE_PASSWORD);
    }

    /**
     * Gets the int value for the key.
     *
     * @param key the key
     * @return the int
     * @throws AAIException the AAI exception
     */
    public static int getInt(String key) throws AAIException {
        return Integer.parseInt(AAIApplicationConfig.get(key));
    }

    /**
     * Gets the int.
     *
     * @param key the key
     * @return the int
     */
    public static int getInt(String key, String value) {
        return Integer.parseInt(AAIApplicationConfig.get(key, value));
    }

    /**
     * Gets the server props.
     *
     * @return the server props
     */
    public static Properties getServerProps() {
        return serverProps;
    }

    /**
     * Check if a null or an Empty string is passed in.
     *
     * @param s the s
     * @return boolean
     */
    public static boolean isEmpty(String s) {
        return (s == null || s.length() == 0);
    }

    private static String replaceProperties(String originalValue) throws AAIException {
        final Pattern p = Pattern.compile(PROPERTY_REGEX);
        Matcher m = p.matcher(originalValue);
        /*if (!m.matches()) {
            return originalValue;
        }*/
        StringBuffer sb = new StringBuffer();
        while(m.find()) {
            String text = m.group(1);
            String replacement = get(text);
            m.appendReplacement(sb, replacement);
        }
        m.appendTail(sb);
        return(sb.toString());
    }
}
