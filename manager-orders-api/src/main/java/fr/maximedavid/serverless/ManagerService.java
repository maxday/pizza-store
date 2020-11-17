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
        return publishMessage(pizza.getUuid(), pizza.getEventId(), false);
    }

    public Uni<JsonObject> listOrders() {
        return publishMessage(null, PizzaEvent.PIZZA_ORDER_LIST_REQUEST.getEvent(), true);
    }

    public Uni<JsonObject> publishMessage(String uuid, String eventId, boolean isManager) {
        PubSubEvent pubSubEvent = new PubSubEvent(uuid, eventId);
        LOG.info("Publishing : " + pubSubEvent.toString());
        String topicUrl = isManager ? configuration.getPubsubManagerTopicPublishUrl() : configuration.getPubsubTopicPublishUrl();
        return this.pubSubService.publishMessage(pubSubEvent, topicUrl);
    }
}