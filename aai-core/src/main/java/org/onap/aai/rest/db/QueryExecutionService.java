/**
 * ============LICENSE_START=======================================================
 * org.onap.aai
 * ================================================================================
 * Copyright © 2024 Deutsche Telekom. All rights reserved.
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

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.onap.aai.introspection.sideeffect.OwnerCheck;
import org.onap.aai.parsers.query.QueryParser;
import org.onap.aai.query.builder.QueryOptions;
import org.onap.aai.query.entities.PaginationResult;
import org.onap.aai.serialization.db.DBSerializer;
import org.springframework.stereotype.Service;

@Service
public class QueryExecutionService {

  public List<Vertex> processGetRequest(DBRequest dbRequest, QueryOptions queryOptions, DBSerializer serializer,
      Set<String> groups, boolean enableResourceVersion) {
    QueryParser query = dbRequest.getParser();
    List<Vertex> queryResult = executeQuery(query, queryOptions);

    boolean groupsAvailable = serializer.getGroups() != null && !serializer.getGroups().isEmpty();
    List<Vertex> vertices = groupsAvailable
        ? queryResult.stream()
            .filter(vertex -> OwnerCheck.isAuthorized(groups, vertex))
            .collect(Collectors.toList())
        : queryResult;

    return vertices;
  }

  public PaginationResult<Vertex> processPaginatedGetRequest(DBRequest dbRequest, QueryOptions queryOptions, DBSerializer serializer,
  Set<String> groups, boolean enableResourceVersion) {
    PaginationResult<Vertex> paginationResult = executePaginatedQuery(dbRequest.getParser(), queryOptions);
    List<Vertex> queryResult = paginationResult.getResults();

    return paginationResult;
  }

  private List<Vertex> executeQuery(QueryParser query, QueryOptions queryOptions) {
    return (queryOptions != null && queryOptions.getSort() != null)
        ? query.getQueryBuilder().sort(queryOptions.getSort()).toList()
        : query.getQueryBuilder().toList();
  }

  private PaginationResult<Vertex> executePaginatedQuery(QueryParser query, QueryOptions queryOptions) {
    return queryOptions.getSort() != null
        ? query.getQueryBuilder().sort(queryOptions.getSort()).toPaginationResult(queryOptions.getPageable())
        : query.getQueryBuilder().toPaginationResult(queryOptions.getPageable());
  }
}
