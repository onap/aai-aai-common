/*-
 * ============LICENSE_START=======================================================
 * ONAP - Logging
 * ================================================================================
 * Copyright (C) 2019 AT&T Intellectual Property. All rights reserved.
 * ================================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ============LICENSE_END=========================================================
 */

package org.onap.logging.filter.base;

import java.util.EnumSet;
import java.util.Set;

public enum ONAPComponents implements ONAPComponentsList {
    OPENSTACK_ADAPTER, BPMN, GRM, AAI, POLICY, CATALOG_DB, REQUEST_DB, SNIRO, SDC, EXTERNAL, VNF_ADAPTER, SDNC_ADAPTER, MULTICLOUD, CLAMP, PORTAL, VID, APPC, DCAE, HOLMES, SDNC, SO, VFC, ESR, DBC, DR, MR, OPTF;

    public static Set<ONAPComponents> getSOInternalComponents() {
        return EnumSet.of(OPENSTACK_ADAPTER, BPMN, CATALOG_DB, REQUEST_DB, VNF_ADAPTER, SDNC_ADAPTER);
    }


    public static Set<ONAPComponents> getAAIInternalComponents() {
        return EnumSet.of(ESR);
    }

    @Override
    public String toString() {
        if (getSOInternalComponents().contains(this))
            return SO + "." + this.name();
        else if (getAAIInternalComponents().contains(this))
            return AAI + "." + this.name();
        else
            return this.name();
    }
}
