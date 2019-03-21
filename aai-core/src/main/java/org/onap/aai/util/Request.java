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

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import javax.ws.rs.core.UriBuilder;

import org.onap.aai.exceptions.AAIException;

public class Request<T> {

    public static final String V14 = "v14";
    public final String fromAppId;
    public final String transactionId;
    public final String path;
    public final RestObject<T> restObj;
    public final boolean oldServer;
    public final String apiVersion;

    /**
     * Instantiates a new request.
     *
     * @param builder the builder
     */
    public Request(RequestBuilder<T> builder) {

        fromAppId = builder.fromAppId;
        transactionId = builder.transactionId;
        restObj = builder.restObj;
        oldServer = builder.oldServer;
        apiVersion = builder.apiVersion;

        if (!oldServer) {
            path = apiVersion + "/" + builder.path;
        } else {
            path = builder.path;
        }

    }

    public static class RequestBuilder<T> {
        private String fromAppId;
        private String transactionId;
        private String path;
        private RestObject<T> restObj;
        private boolean oldServer;
        private String apiVersion = Request.V14;

        /**
         * Sets the from app id.
         *
         * @param fromAppId the from app id
         * @return the request builder
         */
        public RequestBuilder<T> setFromAppId(String fromAppId) {
            this.fromAppId = fromAppId;
            return this;
        }

        /**
         * Sets the transaction id.
         *
         * @param transactionId the transaction id
         * @return the request builder
         */
        public RequestBuilder<T> setTransactionId(String transactionId) {
            this.transactionId = transactionId;
            return this;

        }

        /**
         * Sets the path.
         *
         * @param path the path
         * @return the request builder
         */
        public RequestBuilder<T> setPath(String path) {

            this.path = path;
            return this;

        }

        /**
         * Sets the restcore obj.
         *
         * @param restObj the restcore obj
         * @return the request builder
         */
        public RequestBuilder<T> setRestObj(RestObject<T> restObj) {
            this.restObj = restObj;
            return this;

        }

        /**
         * Sets the old server.
         *
         * @param oldServer the old server
         * @return the request builder
         */
        public RequestBuilder<T> setOldServer(boolean oldServer) {
            this.oldServer = oldServer;
            return this;

        }

        /**
         * Sets the api version.
         *
         * @param apiVersion the api version
         * @return the request builder
         */
        public RequestBuilder<T> setApiVersion(String apiVersion) {
            this.apiVersion = apiVersion;
            return this;
        }

        /**
         * Builds the.
         *
         * @return the request
         */
        public Request<T> build() {
            return new Request<T>(this);
        }

    }

}
