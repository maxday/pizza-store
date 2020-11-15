package fr.maximedavid.serverless;

import io.smallrye.mutiny.Uni;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import io.vertx.core.json.JsonObject;
import io.vertx.mutiny.core.Vertx;

import io.vertx.ext.web.client.WebClientOptions;
import io.vertx.mutiny.ext.web.client.WebClient;
import org.jboss.logging.Logger;

@ApplicationScoped
public class ClientOrdersService {

    private WebClient webclient;

    private static final Logger LOG = Logger.getLogger(ClientOrdersService.class);

    @Inject
    Vertx vertx;

    @Inject
    GCPConfiguration configuration;

    public Uni<JsonObject> createOrder(PizzaOrder pizzaOrder) {
        LOG.info("Creating pizza with uuid " + pizzaOrder.getUuid() );
        return publishMessage(pizzaOrder.getUuid(), PizzaEvent.PIZZA_ORDER_REQUEST.getEvent(), pizzaOrder.getName());
    }

    public Uni<JsonObject> get(String uuid) {
        LOG.info("Get pizza status with uuid " + uuid);
        return publishMessage(uuid, PizzaEvent.PIZZA_STATUS_REQUEST.getEvent(), null);
    }

    public Uni<JsonObject> publishMessage(String uuid, String eventName, String name) {
        String token = System.getProperty("access.token");
        PubSubEvent pubSubEvent = new PubSubEvent(uuid, eventName, name);
        this.webclient = WebClient.create(vertx,
                new WebClientOptions().setDefaultHost(configuration.getPubsubApiHost()).setDefaultPort(configuration.getPubsubApiPort()).setSsl(configuration.getPubsubApiPort() == 443));
        return this.webclient
                .post(configuration.getPubsubTopicPublishUrl())
                .bearerTokenAuthentication(token)
                .sendJsonObject(pubSubEvent)
                .onItem().transform(resp -> {
                    if (resp.statusCode() == 200) {
                        LOG.info("Successfully sent message on topic" + configuration.getPubsubApiHost());
                        return null;
                    } else {
                        LOG.error("Impossible to send message on topic" + configuration.getPubsubApiHost() + " error = " + resp.bodyAsString());
                        return new JsonObject()
                                .put("code", resp.statusCode())
                                .put("message", resp.bodyAsString());
                    }
                });
    }

}