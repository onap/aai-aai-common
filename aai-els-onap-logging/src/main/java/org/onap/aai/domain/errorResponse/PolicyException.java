package org.onap.aai.domain.errorResponse;

import java.util.List;

import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

@Data
@Builder
@Jacksonized
public class PolicyException {
    private String messageId;
    private String text;
    private List<String> variables;
}
