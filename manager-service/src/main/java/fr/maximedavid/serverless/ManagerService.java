package fr.maximedavid.serverless;

import io.smallrye.mutiny.Uni;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import io.quarkus.mongodb.reactive.ReactiveMongoClient;
import io.quarkus.mongodb.reactive.ReactiveMongoCollection;

import io.vertx.core.json.JsonObject;

import io.vertx.mutiny.core.Vertx;

import io.vertx.ext.web.client.WebClientOptions;
import io.vertx.mutiny.ext.web.client.WebClient;
import org.bson.Document;

import java.util.List;

@ApplicationScoped
public class ManagerService {

    private WebClient webclient;

    @Inject
    Vertx vertx;

    @Inject
    ReactiveMongoClient mongoClient;

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
                new WebClientOptions().setDefaultHost(configuration.getPubsubApiHost()).setDefaultPort(443).setSsl(true));
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

    public Uni<List<PizzaEvent>> listOrders() {
        return getCollection().find()
                .map(doc -> {
                    PizzaEvent pizzaEvent = new PizzaEvent();
                    pizzaEvent.setUuid(doc.getString("uuid"));
                    pizzaEvent.setName(doc.getString("name"));
                    pizzaEvent.setEventId(doc.getString("status"));
                    return pizzaEvent;
                }).collectItems().asList();
    }

    private ReactiveMongoCollection<Document> getCollection() {
        return mongoClient.getDatabase("pizzaStore").getCollection("orders");
    }


}