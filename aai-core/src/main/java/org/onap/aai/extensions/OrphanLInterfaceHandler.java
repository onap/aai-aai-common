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

package org.onap.aai.extensions;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.onap.aai.edges.enums.EdgeType;
import org.onap.aai.exceptions.AAIException;
import org.onap.aai.introspection.Introspector;
import org.onap.aai.introspection.Loader;
import org.onap.aai.introspection.exceptions.AAIUnknownObjectException;
import org.onap.aai.parsers.query.QueryParser;
import org.onap.aai.query.builder.QueryBuilder;
import org.onap.aai.rest.db.DBRequest;
import org.onap.aai.restcore.HttpMethod;

public class OrphanLInterfaceHandler {

    private QueryBuilder<Vertex> createLInterfaceQuery(AAIExtensionMap aaiReqMap, Introspector newvceObj)
            throws AAIException {
        Introspector uplinkLInterfaceTraversalIntro = aaiReqMap.getLoader().introspectorFromName("l-interface");

        Introspector customerUplinkLInterfaceTraversalIntro = aaiReqMap.getLoader().introspectorFromName("l-interface");

        Introspector logLinkIntroForTraversal = aaiReqMap.getLoader().introspectorFromName("logical-link");

        QueryBuilder<Vertex> query = aaiReqMap.getTransactionalGraphEngine().getQueryBuilder()
                .exactMatchQuery(newvceObj)
                .createEdgeTraversal(EdgeType.TREE, newvceObj, uplinkLInterfaceTraversalIntro)
                .getVerticesByProperty("interface-role", "UPLINK")
                .createEdgeTraversal(EdgeType.COUSIN, uplinkLInterfaceTraversalIntro, logLinkIntroForTraversal)
                .createEdgeTraversal(EdgeType.COUSIN, logLinkIntroForTraversal, customerUplinkLInterfaceTraversalIntro)
                .getVerticesByProperty("interface-role", "CUSTOMER-UPLINK").dedup();
        return query;
    }

    private URI buildLInterfaceURI(Vertex linterface, AAIExtensionMap aaiReqMap)
            throws UnsupportedEncodingException, AAIException, URISyntaxException {
        Loader loader = aaiReqMap.getLoader();
        Introspector lint = loader.introspectorFromName("l-interface");
        lint.setValue("interface-name", (String) linterface.property("interface-name").value());
        String lintSegment = lint.getURI();

        Introspector lagInterfaceForTrav = loader.introspectorFromName("lag-interface");
        QueryBuilder<Vertex> lagIntQuery = aaiReqMap.getTransactionalGraphEngine().getQueryBuilder()
                .exactMatchQuery(lint).createEdgeTraversal(EdgeType.TREE, linterface, lagInterfaceForTrav).dedup();
        List<Vertex> lagInterfaces = lagIntQuery.toList();
        if (lagInterfaces.isEmpty()) {
            throw new AAIException("AAI_6114");
        } else if (lagInterfaces.size() > 1) {
            throw new AAIException("AAI_6140");
        }
        Vertex lagInt = lagInterfaces.get(0);
        lagInterfaceForTrav.setValue("interface-name", (String) lagInt.property("interface-name").value());
        String lagSegment = lagInterfaceForTrav.getURI();

        Introspector gvVPEforTrav = loader.introspectorFromName("generic-vnf");
        QueryBuilder<Vertex> gvVPEquery =
                aaiReqMap.getTransactionalGraphEngine().getQueryBuilder().exactMatchQuery(lagInterfaceForTrav)
                        .createEdgeTraversal(EdgeType.TREE, lagInterfaceForTrav, gvVPEforTrav).dedup();
        List<Vertex> genvnfs = gvVPEquery.toList();
        if (genvnfs.isEmpty()) {
            throw new AAIException("AAI_6114");
        } else if (genvnfs.size() > 1) {
            throw new AAIException("AAI_6140");
        }
        Vertex genvnf = genvnfs.get(0);
        gvVPEforTrav.setValue("vnf-id", (String) genvnf.property("vnf-id").value());
        String gvSegment = gvVPEforTrav.getURI();

        return new URI(gvSegment + lagSegment + lintSegment);
    }

    public List<DBRequest> createOrphanLInterfaceDelRequests(AAIExtensionMap aaiReqMap, Introspector newvce)
            throws AAIException, UnsupportedEncodingException, URISyntaxException {
        List<DBRequest> requests = new ArrayList<>();
        QueryBuilder<Vertex> query = createLInterfaceQuery(aaiReqMap, newvce);
        List<Vertex> linterfaces = query.toList();

        for (Vertex lint : linterfaces) {
            URI lintURI = buildLInterfaceURI(lint, aaiReqMap);
            QueryParser parser = createLInterfaceQuery(aaiReqMap, newvce).createQueryFromObjectName("l-interface");
            DBRequest originalDbRequest = aaiReqMap.getDbRequest();
            DBRequest request =
                    new DBRequest.Builder(HttpMethod.DELETE, lintURI, parser, newvce, originalDbRequest.getHeaders(),
                            originalDbRequest.getInfo(), originalDbRequest.getTransactionId()).build();
            requests.add(request);
        }

        return requests;
    }
}
