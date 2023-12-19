package org.onap.aai.domain.errorResponse;

import lombok.Data;

@Data
public class ServiceFault {

  private RequestError requestError;

  @Data
  public class RequestError {
    private ServiceException serviceException;
  }
}
