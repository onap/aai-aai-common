package org.onap.aai.rest.notification;

import java.util.Set;

import org.onap.aai.util.AAIUtils;
import org.onap.aai.util.delta.DeltaEventsConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class DeltaEventsService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DeltaEventsService.class);

    private final boolean isDeltaEventsEnabled;
    private final Set<String> deltaEventActions;
    private final boolean isRelationshipDeltaEnabled;
    private final Set<String> nodeTypes;

    public DeltaEventsService(
            @Value("${delta.events.enabled:false}") boolean isDeltaEventsEnabled,
            @Value("${delta.events.actions:}") String deltaEventActions,
            @Value("${delta.relationship.events.enabled:false}") boolean isRelationshipDeltaEnabled,
            @Value("${delta.events.node.types:}") String nodeTypes) {

        this.isDeltaEventsEnabled = isDeltaEventsEnabled;
        this.deltaEventActions = AAIUtils.toSetFromDelimitedString(deltaEventActions);
        this.isRelationshipDeltaEnabled = isRelationshipDeltaEnabled;
        this.nodeTypes = AAIUtils.toSetFromDelimitedString(nodeTypes);

        LOGGER.info("DeltaEventsService initialized with: enabled={}, actions={}, relationshipEnabled={}, nodeTypes={}",
                isDeltaEventsEnabled, deltaEventActions, isRelationshipDeltaEnabled, nodeTypes);
    }

    /**
     * Builds and returns a DeltaEventsConfig object containing all delta configuration parameters.
     *
     * @return DeltaEventsConfig
     */
    public DeltaEventsConfig getDeltaEventsConfig() {
        DeltaEventsConfig config = new DeltaEventsConfig();
        config.setDeltaEventsEnabled(isDeltaEventsEnabled);
        config.setDeltaEventActions(deltaEventActions);
        config.setRelationshipDeltaEnabled(isRelationshipDeltaEnabled);
        config.setNodeTypes(nodeTypes);

        LOGGER.debug("Constructed DeltaEventsConfig: {}", config);
        return config;
    }
}
