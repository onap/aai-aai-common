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

package org.onap.aai.query.builder;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Object that contains the page and pageSize for pagination.
 * `includeTotalCount` can optionally be provided to include the total count of objects in the response.
 * Note that including the total count in the response will trigger a full graph scan (@see <a href="https://jayanta-mondal.medium.com/the-curious-case-of-pagination-for-gremlin-queries-d6fd9518620">The Curious Case of Pagination for Gremlin Queries</a>).
 */
@Getter
@RequiredArgsConstructor
public class Pageable {
  private final int page;
  private final int pageSize;
  private boolean includeTotalCount = false;

  public Pageable includeTotalCount() {
    this.includeTotalCount = true;
    return this;
  }
}
