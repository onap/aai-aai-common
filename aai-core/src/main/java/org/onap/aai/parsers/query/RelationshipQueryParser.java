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

package org.onap.aai.parsers.query;

import com.att.eelf.configuration.EELFLogger;
import com.att.eelf.configuration.EELFManager;

import java.io.UnsupportedEncodingException;

import org.onap.aai.config.SpringContextAware;
import org.onap.aai.edges.EdgeIngestor;
import org.onap.aai.exceptions.AAIException;
import org.onap.aai.introspection.Introspector;
import org.onap.aai.introspection.Loader;
import org.onap.aai.introspection.ModelType;
import org.onap.aai.parsers.relationship.RelationshipToURI;
import org.onap.aai.parsers.uri.URIParser;
import org.onap.aai.query.builder.QueryBuilder;
import org.springframework.context.ApplicationContext;

/**
 * The Class RelationshipQueryParser.
 */
public class RelationshipQueryParser extends LegacyQueryParser {

    private static final EELFLogger logger = EELFManager.getInstance().getLogger(RelationshipQueryParser.class);

    private Introspector relationship = null;

    private ModelType modelType = null;

    private EdgeIngestor edgeRules = null;

    /**
     * Instantiates a new relationship query parser.
     *
     * @param loader the loader
     * @param queryBuilder the query builder
     * @param obj the obj
     * @throws UnsupportedEncodingException the unsupported encoding exception
     * @throws AAIException the AAI exception
     */
    public RelationshipQueryParser(Loader loader, QueryBuilder queryBuilder, Introspector obj)
            throws UnsupportedEncodingException, AAIException {
        super(loader, queryBuilder);
        this.relationship = obj;
        this.modelType = obj.getModelType();
        initBeans();
        RelationshipToURI rToUri = new RelationshipToURI(loader, obj);
        this.uri = rToUri.getUri();
        URIParser parser = new URIParser(loader, uri);
        parser.parse(this);
    }

    private void initBeans() {
        ApplicationContext ctx = SpringContextAware.getApplicationContext();
        if (ctx == null) {
            logger.warn("Unable to retrieve the spring context");
        } else {
            EdgeIngestor ei = ctx.getBean(EdgeIngestor.class);
            this.edgeRules = ei;
        }
    }

}
