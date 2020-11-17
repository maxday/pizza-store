package fr.maximedavid.serverless;

import fr.maximedavid.serverless.extension.ext.gcp.token.machine.PubSubService;
import io.smallrye.mutiny.Uni;
import javax.enterprise.context.ApplicationScoped;
import io.vertx.core.json.JsonObject;
import org.jboss.logging.Logger;

@ApplicationScoped
public class ClientOrdersService {

    private GCPConfiguration configuration;
    private PubSubService pubSubService;

    private static final Logger LOG = Logger.getLogger(ClientOrdersService.class);

    public ClientOrdersService(GCPConfiguration configuration, PubSubService pubSubService) {
        this.configuration = configuration;
        this.pubSubService = pubSubService;
    }

    public Uni<JsonObject> createOrder(PizzaOrder pizzaOrder) {
        LOG.info("Creating pizza with uuid " + pizzaOrder.getUuid() );
        return publishMessage(pizzaOrder.getUuid(), PizzaEvent.PIZZA_ORDER_REQUEST.getEvent(), pizzaOrder.getName());
    }

    public Uni<JsonObject> get(String uuid) {
        LOG.info("Get pizza status with uuid " + uuid);
        return publishMessage(uuid, PizzaEvent.PIZZA_STATUS_REQUEST.getEvent(), null);
    }

    public Uni<JsonObject> publishMessage(String uuid, String eventName, String name) {
        PubSubEvent pubSubEvent = new PubSubEvent(uuid, eventName, name);
        LOG.info("Publishing : " + pubSubEvent.toString());
        return this.pubSubService.publishMessage(pubSubEvent, configuration.getPubsubTopicPublishUrl());
    }

}