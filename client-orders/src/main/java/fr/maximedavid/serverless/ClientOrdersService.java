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
        return publishMessage(pizzaOrder.getUuid(), "PIZZA_ORDER_REQUEST", pizzaOrder.getName());
    }

    public Uni<JsonObject> get(String uuid) {
        return publishMessage(uuid, "PIZZA_STATUS_REQUEST", null);
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
                    System.out.println("resp");
                    if (resp.statusCode() == 200) {
                        System.out.println("200 OK");
                        return null;
                    } else {
                        System.out.println("pas 200 KO" + resp.bodyAsString());
                        return new JsonObject()
                                .put("code", resp.statusCode())
                                .put("message", resp.bodyAsString());
                    }
                });
    }

}