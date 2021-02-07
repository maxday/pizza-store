package fr.maximedavid.serverless;

import fr.maximedavid.serverless.extension.ext.gcp.token.machine.PubSubService;
import io.smallrye.mutiny.Uni;

import javax.enterprise.context.ApplicationScoped;
import io.vertx.core.json.JsonObject;
import org.jboss.logging.Logger;

@ApplicationScoped
public class ManagerService {

    private GCPConfiguration configuration;
    private PubSubService pubSubService;

    private static final Logger LOG = Logger.getLogger(ManagerService.class);

    public ManagerService(GCPConfiguration configuration, PubSubService pubSubService) {
        this.configuration = configuration;
        this.pubSubService = pubSubService;
    }

    public Uni<JsonObject> setStatus(Pizza pizza) {
        return publishMessage(pizza.getUuid(), pizza.getEventId());
    }

    public Uni<JsonObject> publishMessage(String uuid, String eventId) {
        PubSubEvent pubSubEvent = new PubSubEvent(uuid, eventId);
        LOG.info("Publishing : " + pubSubEvent.toString());
        return this.pubSubService.publishMessage(pubSubEvent, configuration.getPubsubTopicPublishUrl());
    }
}