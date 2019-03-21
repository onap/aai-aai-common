/**
 * ============LICENSE_START=======================================================
 * org.onap.aai
 * ================================================================================
 * Copyright © 2017-2018 AT&T Intellectual Property. All rights reserved.
 * ================================================================================
 * Modifications Copyright © 2018 IBM.
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

package org.onap.aai.util;

public final class AAIConstants {
    private static final String AJSC_HOME = "AJSC_HOME";
    //
    //
    /** Default to unix file separator if system property file.separator is null */
    public static final String AAI_FILESEP =
        (System.getProperty("file.separator") == null) ? "/" : System.getProperty("file.separator");
    //
    /** Default to opt aai if system property aai.home is null, using file.separator */
    public static final String AAI_HOME = (System.getProperty(AJSC_HOME) == null)
        ? AAI_FILESEP + "opt" + AAI_FILESEP + "app" + AAI_FILESEP + "aai"
        : System.getProperty(AJSC_HOME);
    public static final String AAI_BUNDLECONFIG_NAME =
        (System.getProperty("BUNDLECONFIG_DIR") == null) ? "bundleconfig"
            : System.getProperty("BUNDLECONFIG_DIR");
    public static final String AAI_HOME_BUNDLECONFIG = (System.getProperty(AJSC_HOME) == null)
        ? AAI_FILESEP + "opt" + AAI_FILESEP + "app" + AAI_FILESEP + "aai" + AAI_FILESEP
            + AAI_BUNDLECONFIG_NAME
        : System.getProperty(AJSC_HOME) + AAI_FILESEP + AAI_BUNDLECONFIG_NAME;

    /** etc directory, relative to AAI_HOME */
    public static final String AAI_HOME_ETC =
        AAI_HOME_BUNDLECONFIG + AAI_FILESEP + "etc" + AAI_FILESEP;
    public static final String AAI_HOME_ETC_APP_PROPERTIES =
        AAI_HOME_ETC + "appprops" + AAI_FILESEP;
    public static final String AAI_HOME_ETC_AUTH = AAI_HOME_ETC + "auth" + AAI_FILESEP;
    public static final String AAI_CONFIG_FILENAME =
        AAI_HOME_ETC_APP_PROPERTIES + "aaiconfig.properties";
    public static final String AAI_AUTH_CONFIG_FILENAME = AAI_HOME_ETC_AUTH + "aai_policy.json";
    public static final String REALTIME_DB_CONFIG =
        AAI_HOME_ETC_APP_PROPERTIES + "janusgraph-realtime.properties";
    public static final String CACHED_DB_CONFIG =
        AAI_HOME_ETC_APP_PROPERTIES + "janusgraph-cached.properties";
    public static final String AAI_HOME_ETC_OXM = AAI_HOME_ETC + "oxm" + AAI_FILESEP;
    public static final String AAI_EVENT_DMAAP_PROPS =
        AAI_HOME_ETC_APP_PROPERTIES + "aaiEventDMaaPPublisher.properties";
    public static final String AAI_HOME_ETC_SCRIPT =
        AAI_HOME_ETC + AAI_FILESEP + "scriptdata" + AAI_FILESEP;

    public static final String AAI_LOGBACK_PROPS = "logback.xml";
    public static final String AAI_SCHEMA_MOD_LOGBACK_PROPS = "schemaMod-logback.xml";
    public static final String AAI_FORCE_DELETE_LOGBACK_PROPS = "forceDelete-logback.xml";

    public static final String AAI_TRUSTSTORE_FILENAME = "aai.truststore.filename";
    public static final String AAI_TRUSTSTORE_PASSWD = "aai.truststore.passwd";
    public static final String AAI_KEYSTORE_FILENAME = "aai.keystore.filename";
    public static final String AAI_KEYSTORE_PASSWD = "aai.keystore.passwd";

    public static final String AAI_SERVER_URL_BASE = "aai.server.url.base";
    public static final String AAI_SERVER_URL = "aai.server.url";
    public static final String AAI_OLDSERVER_URL = "aai.oldserver.url";
    public static final String AAI_LOCAL_REST = "https://localhost:%d/aai/%s/";

    public static final int AAI_RESOURCES_PORT = 8447;
    public static final int AAI_QUERY_PORT = 8446;
    public static final int AAI_LEGACY_PORT = 8443;

    public static final String AAI_DEFAULT_API_VERSION = "v10";
    public static final String AAI_DEFAULT_API_VERSION_PROP = "aai.default.api.version";
    public static final String AAI_NOTIFICATION_CURRENT_VERSION =
        "aai.notification.current.version";

    public static final String AAI_NODENAME = "aai.config.nodename";

    public static final String AAI_BULKCONSUMER_LIMIT = "aai.bulkconsumer.payloadlimit";
    public static final String AAI_BULKCONSUMER_OVERRIDE_LIMIT = "aai.bulkconsumer.payloadoverride";

    public static final String AAI_TRAVERSAL_TIMEOUT_LIMIT = "aai.traversal.timeoutlimit";
    public static final String AAI_TRAVERSAL_TIMEOUT_ENABLED = "aai.traversal.timeoutenabled";
    public static final String AAI_TRAVERSAL_TIMEOUT_APP = "aai.traversal.timeout.appspecific";

    public static final String AAI_GRAPHADMIN_TIMEOUT_LIMIT = "aai.graphadmin.timeoutlimit";
    public static final String AAI_GRAPHADMIN_TIMEOUT_ENABLED = "aai.graphadmin.timeoutenabled";
    public static final String AAI_GRAPHADMIN_TIMEOUT_APP = "aai.graphadmin.timeout.appspecific";

    public static final String AAI_CRUD_TIMEOUT_LIMIT = "aai.crud.timeoutlimit";
    public static final String AAI_CRUD_TIMEOUT_ENABLED = "aai.crud.timeoutenabled";
    public static final String AAI_CRUD_TIMEOUT_APP = "aai.crud.timeout.appspecific";

    public static final String AAI_RESVERSION_ENABLEFLAG = "aai.resourceversion.enableflag";
    public static final String AAI_RESVERSION_DISABLED_UUID = "aai.resourceversion.disabled.uuid";
    public static final String AAI_RESVERSION_DISABLED_UUID_DEFAULT =
        "38cf3090-6a0c-4e9d-8142-4332a7352846";

    public static final long HISTORY_MAX_HOURS = 192;

    public static final String LOGGING_MAX_STACK_TRACE_ENTRIES = "aai.logging.maxStackTraceEntries";

    /*** UEB ***/
    public static final String UEB_PUB_PARTITION_AAI = "AAI";

    /** Micro-service Names */
    public static final String AAI_TRAVERSAL_MS = "aai-traversal";
    public static final String AAI_RESOURCES_MS = "aai-resources";

    /**
     * Instantiates a new AAI constants.
     */
    private AAIConstants() {
        // prevent instantiation
    }

}
