package org.onap.aai.rest;

import lombok.Builder;

@Builder
public class QueryOptions {
  private final boolean skipRelatedTo;
}
