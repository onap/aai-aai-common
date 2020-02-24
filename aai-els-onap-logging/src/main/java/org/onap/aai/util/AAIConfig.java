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

import org.eclipse.jetty.util.security.Password;
import org.onap.aai.exceptions.AAIException;
import org.onap.aai.logging.ErrorLogHelper;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AAIConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(AAIConfig.class);
    private static final String GLOBAL_PROP_FILE_NAME = AAIConstants.AAI_CONFIG_FILENAME;
    private static Properties serverProps;
    private static boolean propsInitialized = false;

    /**
     * Instantiates a new AAI config.
     */
    // Don't instantiate
    private AAIConfig() {
    }

    /**
     * Inits the.
     *
     * @throws AAIException the AAI exception
     */
    public synchronized static void init() throws AAIException {

        LOGGER.info("Initializing AAIConfig");

        AAIConfig.getConfigFile();
        AAIConfig.reloadConfig();

        if (AAIConstants.AAI_NODENAME == null || AAIConstants.AAI_NODENAME == "") {
            ErrorLogHelper.logError("AAI_4005", " AAI_NODENAME is not defined");
        } else {
            LOGGER.info("A&AI Server Node Name = " + AAIConstants.AAI_NODENAME);
        }
    }

    /**
     * Gets the config file.
     *
     * @return the config file
     */
    public static String getConfigFile() {
        return GLOBAL_PROP_FILE_NAME;
    }

    /**
     * Reload config.
     */
    public synchronized static void reloadConfig() {

        String propFileName = GLOBAL_PROP_FILE_NAME;
        Properties newServerProps = new Properties();

        LOGGER.debug("Reloading config from " + propFileName);

        try (InputStream is = new FileInputStream(propFileName)) {
            LOGGER.info("Found the aaiconfig.properties in the following location: {}", GLOBAL_PROP_FILE_NAME);
            newServerProps.load(is);
            propsInitialized = true;
            serverProps = newServerProps;
        } catch (Exception fnfe) {
            final InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("aaiconfig.properties");
            LOGGER.info("Unable to find the aaiconfig.properties from filesystem so using file in jar");
            if (is != null) {
                try {
                    newServerProps.load(is);
                    serverProps = newServerProps;
                } catch (IOException e) {
                    LOGGER.warn("Encountered IO Exception during loading of aaiconfig props from inputstream", e);
                }
            } else {
                LOGGER.error("Expected to find the error.properties in the jar but unable to find it");
            }
        }
    }

    /**
     * Gets the.
     *
     * @param key the key
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
     * Gets the.
     *
     * @param key the key
     * @return the string
     * @throws AAIException the AAI exception
     */
    public static String get(String key) throws AAIException {
        String response = null;

        if (key.equals(AAIConstants.AAI_NODENAME)) {
            // Get this from InetAddress rather than the properties file
            String nodeName = getNodeName();
            if (nodeName != null) {
                return nodeName;
            }
            // else get from property file
        }

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
        }
        return response;
    }

    /**
     * Gets the int.
     *
     * @param key the key
     * @return the int
     * @throws AAIException the AAI exception
     */
    public static int getInt(String key) throws AAIException {
        return Integer.parseInt(AAIConfig.get(key));
    }

    /**
     * Gets the int.
     *
     * @param key the key
     * @return the int
     */
    public static int getInt(String key, String value) {
        return Integer.parseInt(AAIConfig.get(key, value));
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
     * Gets the node name.
     *
     * @return the node name
     */
    public static String getNodeName() {
        try {
            InetAddress ip = InetAddress.getLocalHost();
            if (ip != null) {
                String hostname = ip.getHostName();
                if (hostname != null) {
                    return hostname;
                }
            }
        } catch (Exception e) {
            return null;
        }
        return null;
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
}
