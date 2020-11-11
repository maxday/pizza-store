package fr.maximedavid.serverless;

import io.smallrye.mutiny.Uni;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import io.vertx.core.json.JsonObject;

import io.vertx.mutiny.core.Vertx;

import io.vertx.ext.web.client.WebClientOptions;
import io.vertx.mutiny.ext.web.client.WebClient;

@ApplicationScoped
public class ClientOrdersService {

    private WebClient webclient;

    @Inject
    Vertx vertx;

    @Inject
    GCPConfiguration configuration;

    public Uni<JsonObject> createOrder(PizzaOrder pizzaOrder) {
        return publishMessage(pizzaOrder.getUuid());
    }

    public Uni<JsonObject> publishMessage(String uuid) {
        PubSubEvent pubSubEvent = new PubSubEvent(uuid, "PIZZA_ORDER_REQUEST ");
        this.webclient = WebClient.create(vertx,
                new WebClientOptions().setDefaultHost(configuration.getApiHost()).setDefaultPort(443).setSsl(true));
        return this.webclient
                .post(configuration.getPubsubTopicPublishUrl())
                .bearerTokenAuthentication(configuration.getApiToken())
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