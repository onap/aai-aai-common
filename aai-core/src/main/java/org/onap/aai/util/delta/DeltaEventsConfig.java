package org.onap.aai.util.delta;

import java.util.Set;

import lombok.Data;

@Data
public class DeltaEventsConfig {

    private boolean deltaEventsEnabled;
    private Set<String> deltaEventActions;
    private boolean relationshipDeltaEnabled;
    private Set<String> nodeTypes;

}
