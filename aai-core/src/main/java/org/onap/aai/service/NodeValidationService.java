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

package org.onap.aai.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;

import org.onap.aai.validation.nodes.NodeValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(name = "schema.translator.list", havingValue = "config", matchIfMissing = true)
@PropertySource(value = "classpath:schema-ingest.properties", ignoreResourceNotFound = true)
@PropertySource(value = "file:${schema.ingest.file}", ignoreResourceNotFound = true)
public class NodeValidationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(NodeValidationService.class);

    @Autowired(required = false)
    private NodeValidator nodeValidator;

    public NodeValidationService(NodeValidator nodeValidator) {
        this.nodeValidator = nodeValidator;
    }

    @PostConstruct
    public void initialize() {
        if (!nodeValidator.validate()) {
            LOGGER.warn(nodeValidator.getErrorMsg());
        } else {
            LOGGER.info("Node validation check passed");
        }
    }
}
