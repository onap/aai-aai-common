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

import java.util.Collections;
import java.util.Set;

import org.onap.aai.db.props.AAIProperties;
import org.onap.aai.query.builder.QueryOptions;
import org.onap.aai.rest.notification.UEBNotification;
import org.onap.aai.util.AAIConfig;

/**
 * Per-call options for {@link DbRequestProcessor#process}. Replaces the former
 * overload sprawl on {@code HttpEntry.process(...)} and the mutable notification
 * fields on {@code HttpEntry}.
 *
 * <p>{@code notification} may be null, in which case the processor supplies a
 * fresh {@link UEBNotification}. {@code queryOptions} may be null (no pagination
 * or sort).
 */
public record ProcessOptions(
        String sourceOfTruth,
        Set<String> groups,
        boolean enableResourceVersion,
        QueryOptions queryOptions,
        UEBNotification notification,
        int notificationDepth) {

    /** Notification depth default, mirroring the legacy HttpEntry.setHttpEntryProperties logic. */
    public static int defaultNotificationDepth() {
        String allEnabled;
        try {
            allEnabled = AAIConfig.get("aai.notification.depth.all.enabled", "true");
        } catch (RuntimeException e) {
            // AAIConfig not initialized (e.g. a plain unit test with no Spring context).
            // Fall back to the documented default that AAIConfig.get(key, default) itself
            // promises to return. In production AAIConfig is always initialized, so this
            // branch never runs and behavior matches the legacy HttpEntry code exactly.
            allEnabled = "true";
        }
        return "true".equals(allEnabled)
                ? AAIProperties.MAXIMUM_DEPTH
                : AAIProperties.MINIMUM_DEPTH;
    }

    public static Builder forSourceOfTruth(String sourceOfTruth) {
        return new Builder(sourceOfTruth);
    }

    public static final class Builder {
        private final String sourceOfTruth;
        private Set<String> groups = Collections.emptySet();
        private boolean enableResourceVersion = true;
        private QueryOptions queryOptions = null;
        private UEBNotification notification = null;
        private int notificationDepth = defaultNotificationDepth();

        private Builder(String sourceOfTruth) {
            this.sourceOfTruth = sourceOfTruth;
        }

        public Builder groups(Set<String> groups) {
            this.groups = groups;
            return this;
        }

        public Builder enableResourceVersion(boolean enableResourceVersion) {
            this.enableResourceVersion = enableResourceVersion;
            return this;
        }

        public Builder queryOptions(QueryOptions queryOptions) {
            this.queryOptions = queryOptions;
            return this;
        }

        public Builder notification(UEBNotification notification) {
            this.notification = notification;
            return this;
        }

        public Builder notificationDepth(int notificationDepth) {
            this.notificationDepth = notificationDepth;
            return this;
        }

        public ProcessOptions build() {
            return new ProcessOptions(sourceOfTruth, groups, enableResourceVersion,
                    queryOptions, notification, notificationDepth);
        }
    }
}
