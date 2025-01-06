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

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jetty.util.security.Password;
import org.onap.aai.exceptions.AAIException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AAIApplicationConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(AAIApplicationConfig.class);
    private static String GLOBAL_PROP_FILE_NAME = "application.properties";

    private static Properties serverProps;
    private static boolean propsInitialized = false;

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

        LOGGER.info("Initializing AAIApplicationConfig");
        AAIApplicationConfig.reloadConfig();
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
        } catch (Exception fnfe) {
            final InputStream is =
                    Thread.currentThread().getContextClassLoader().getResourceAsStream("application.properties");
            LOGGER.info("Unable to find the application.properties from filesystem so using file in jar");
            if (is != null) {
                try {
                    newServerProps.load(is);
                    serverProps = newServerProps;
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
        /*
         * if (!m.matches()) {
         * return originalValue;
         * }
         */
        StringBuffer sb = new StringBuffer();
        while (m.find()) {
            String text = m.group(1);
            String replacement = get(text);
            m.appendReplacement(sb, replacement);
        }
        m.appendTail(sb);
        return (sb.toString());
    }
}
