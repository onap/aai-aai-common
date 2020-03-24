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
package org.onap.aai.serialization.db;

import org.apache.commons.lang.StringUtils;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.onap.aai.exceptions.AAIException;
import org.onap.aai.introspection.Introspector;
import org.onap.aai.logging.LogFormatTools;
import org.onap.aai.serialization.engines.TransactionalGraphEngine;
import org.onap.aai.serialization.engines.query.QueryEngine;
import org.onap.aai.util.AAIConfigProxy;
import org.onap.aai.util.AAIConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Objects;

/**
 * <b>ImpliedDelete</b> class is responsible for deleting children
 * of a parent vertex if the client explicitly asks to remove them
 * It will check if they are allowed and based on that information
 * it will decide whether to proceed with the deletion or
 * throw an exception back to the requester if they are not allowed
 *
 * It implements the AAIProxy Interface and any calls to the
 * AAIConfig should be using the proxy methods and any new
 * methods that needs to be invoked should be added there first
 *
 * @see org.onap.aai.util.AAIConfigProxy
 */
public class ImpliedDelete implements AAIConfigProxy {

    private static final Logger LOGGER = LoggerFactory.getLogger(ImpliedDelete.class);

    private static final String IMPLICIT_DELETE = "Implicit DELETE";
    private static final String STAR = "*";

    private TransactionalGraphEngine engine;
    private DBSerializer serializer;

    public ImpliedDelete(TransactionalGraphEngine engine, DBSerializer serializer){
        this.engine = engine;
        this.serializer = serializer;
    }

    /**
     * Checks if the user is allowed to execute implied delete
     * If they are allowed to do the delete, then for all the dependent vertices
     * it will identify all the deletable vertices to them
     * and log them based on the following aaiconfig properties:
     *
     * aai.implied.delete.log.enabled=true
     * aai.implied.delete.log.limit=-1
     *
     * Above properties are the default assumption of the code if they are not overwritten
     * So the code will log for every vertex it is about to delete
     * If there are thousands of vertexes that get implicitly deleted,
     * its preferable that the operation timeout rather than
     * someone accidentally deleting thousands of children
     *
     * @param id - Identifier of the vertex whose children could be potentially deleted
     * @param sot - source of truth of who the requester who is making the request
     * @param objectType - type of the parent object whose children are being deleted
     * @param dependentVertexes - list of children vertexes
     * @throws AAIException if the user is not allowed to implicitly delete children
     */
    public List<Vertex> execute(Object id, String sot, String objectType, List<Vertex> dependentVertexes) throws AAIException {
        if(dependentVertexes != null && !dependentVertexes.isEmpty()){
            // Find all the deletable vertices from the dependent vertices that should be deleted
            // So for each of the following dependent vertices,
            // we will use the edge properties and do the cascade delete
            QueryEngine queryEngine = this.engine.getQueryEngine();
            List<Vertex> impliedDeleteVertices = queryEngine.findDeletable(dependentVertexes);
            if(this.allow(sot, objectType)){

                int impliedDeleteCount = impliedDeleteVertices.size();

                LOGGER.warn(
                    "For the vertex with id {}, doing an implicit delete on update will delete total of {} vertexes",
                    id,
                    impliedDeleteCount
                );

                String impliedDeleteLogEnabled = get(AAIConstants.AAI_IMPLIED_DELETE_LOG_ENABLED, "true");

                int impliedDeleteLogLimit = getInt(AAIConstants.AAI_IMPLIED_DELETE_LOG_LIMIT, "-1");

                if (impliedDeleteLogLimit == -1) {
                    impliedDeleteLogLimit = Integer.MAX_VALUE;
                }

                // If the logging is enabled for implied delete
                // then log the payload in the latest format
                if ("true".equals(impliedDeleteLogEnabled) && impliedDeleteCount <= impliedDeleteLogLimit) {
                    for (Vertex vertex : impliedDeleteVertices) {
                        Introspector introspector = null;
                        try {
                            introspector = serializer.getLatestVersionView(vertex);
                            if (LOGGER.isInfoEnabled()) {
                                LOGGER.info("Implied delete object in json format {}", introspector.marshal(false));
                            }
                        } catch (Exception ex) {
                            LOGGER.warn(
                                "Encountered an exception during retrieval of vertex properties with vertex-id {} -> {}",
                                id, LogFormatTools.getStackTop(ex));
                        }
                    }
                }
            } else {
                LOGGER.error("User {} is not allowed to implicit delete on parent object {}", sot, objectType);
                throw new AAIException("AAI_9109");
            }
            return impliedDeleteVertices;
        } else {
            // Return null or an empty list back to the user based on input
            return dependentVertexes;
        }
    }

    public void delete(List<Vertex> vertices){
       // After all the appropriate logging, calling the serializer delete to delete the affected vertices
       if(vertices != null && !vertices.isEmpty()){
           serializer.delete(vertices);
       }
    }

    /**
     * Checks the property in the aaiconfig properties
     * to see if the user is allowed to do implicit delete
     *
     * Expecting the aaiconfig.properties to have following type of properties
     *
     * <code>
     * aai.implied.delete.whitelist.sdnc=*
     * aai.implied.delete.whitelist.sdc='pserver','vserver'
     * </code>
     *
     * So in the above code, the expectation is for any of the following user:
     *
     * <ul>
     *     <li>SDC</li>
     *     <li>SDc</li>
     *     <li>Sdc</li>
     *     <li>sDc</li>
     *     <li>SdC</li>
     *     <li>sdC</li>
     *     <li>sdc</li>
     * </ul>
     *
     * They are allowed to delete the children of pserver and vserver by implicit delete
     *
     * Note: The reason the property values are placed inside the single quotes is
     * so if there is an object called volume and there is another object called volume-group
     * when doing an contains it can falsely allow volume-group children to be implicitly deleted
     * and the cost of turning the string into an array and then searching if its inside it
     * or loading into an set which is unnecessary and it could potentially be done for every request
     *
     * @param sourceOfTruth - the original requester that the request is coming from,
     *                        derived from HTTP Header X-FromAppId
     * @param parentObjectType - parent object in which they are trying to do the implicit delete against
     *
     * @return true  - if the requester is allowed to implicit delete against the object type
     *         false - if they are not allowed
     */
    private boolean allow(String sourceOfTruth, String parentObjectType){
        Objects.requireNonNull(sourceOfTruth);
        Objects.requireNonNull(parentObjectType);

        String propertyName = AAIConstants.AAI_IMPLIED_DELETE_WHITELIST + sourceOfTruth.toLowerCase();
        String whitelist = get(propertyName, StringUtils.EMPTY);

        if(whitelist.isEmpty()){
            return false;
        }

        if(STAR.equals(whitelist)){
            return true;
        }

        if(whitelist.contains("'" + parentObjectType + "'")){
            return true;
        }

        return false;
    }
}
