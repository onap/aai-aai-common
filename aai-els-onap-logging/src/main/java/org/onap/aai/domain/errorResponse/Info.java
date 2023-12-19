package org.onap.aai.domain.errorResponse;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Info {
  private List<ErrorMessage> errorMessages;
}
