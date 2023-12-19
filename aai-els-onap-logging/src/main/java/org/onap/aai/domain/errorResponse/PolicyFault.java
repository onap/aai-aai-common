package org.onap.aai.domain.errorResponse;

import lombok.Data;

@Data
public class PolicyFault {

  private RequestError requestError;

  @Data
  public class RequestError {
    private PolicyException policyException;
  }
}
