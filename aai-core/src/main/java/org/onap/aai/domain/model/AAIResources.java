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

package org.onap.aai.domain.model;

import java.util.HashMap;

import org.eclipse.persistence.jaxb.dynamic.DynamicJAXBContext;

public class AAIResources {

    private DynamicJAXBContext jaxbContext;

    private HashMap<String, AAIResource> aaiResources;
    private HashMap<String, AAIResource> resourceLookup;

    /**
     * Gets the aai resources.
     *
     * @return the aai resources
     */
    public HashMap<String, AAIResource> getAaiResources() {
        if (aaiResources == null) {
            aaiResources = new HashMap<String, AAIResource>();
        }
        return aaiResources;
    }

    /**
     * Gets the jaxb context.
     *
     * @return the jaxb context
     */
    public DynamicJAXBContext getJaxbContext() {
        return jaxbContext;
    }

    /**
     * Sets the jaxb context.
     *
     * @param jaxbContext the new jaxb context
     */
    public void setJaxbContext(DynamicJAXBContext jaxbContext) {
        this.jaxbContext = jaxbContext;
    }

    /**
     * Gets the resource lookup.
     *
     * @return the resource lookup
     */
    public HashMap<String, AAIResource> getResourceLookup() {
        if (resourceLookup == null) {
            resourceLookup = new HashMap<String, AAIResource>();
        }
        return resourceLookup;
    }

    /**
     * Sets the resource lookup.
     *
     * @param resourceLookup the resource lookup
     */
    public void setResourceLookup(HashMap<String, AAIResource> resourceLookup) {
        this.resourceLookup = resourceLookup;
    }

}
