package fr.maximedavid.serverless;

import io.smallrye.mutiny.Uni;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import io.vertx.core.json.JsonObject;

import io.vertx.mutiny.core.Vertx;

import io.vertx.ext.web.client.WebClientOptions;
import io.vertx.mutiny.ext.web.client.WebClient;

@ApplicationScoped
public class ManagerService {

    private WebClient webclient;

    @Inject
    Vertx vertx;

    @Inject
    GCPConfiguration configuration;

    public Uni<JsonObject> setStatus(PizzaEvent pizzaEvent) {
        return publishMessage(pizzaEvent.getUuid(), pizzaEvent.getEventId());
    }

    public Uni<JsonObject> publishMessage(String uuid, String eventId) {
        PubSubEvent pubSubEvent = new PubSubEvent(uuid, eventId);
        System.out.println(pubSubEvent);
        String token = System.getProperty("access.token");
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

    public Uni<JsonObject> listOrders() {
        return publishMessage(null, "PIZZA_ORDER_LIST_REQUEST");
    }


}