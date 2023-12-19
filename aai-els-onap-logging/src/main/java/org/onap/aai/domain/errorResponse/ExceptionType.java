package org.onap.aai.domain.errorResponse;

import com.fasterxml.jackson.annotation.JsonValue;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum ExceptionType {
  SERVICE("serviceException"),
  POLICY("policyException");
  private final String type;

  @JsonValue
  public String getType() {
      return type;
  }
}
