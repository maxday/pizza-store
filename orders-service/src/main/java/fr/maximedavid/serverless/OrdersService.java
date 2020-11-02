package fr.maximedavid.serverless;

import com.mongodb.client.result.InsertOneResult;
import io.quarkus.mongodb.reactive.ReactiveMongoClient;
import io.quarkus.mongodb.reactive.ReactiveMongoCollection;
import io.smallrye.mutiny.Uni;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import org.bson.Document;

import io.vertx.mutiny.core.Vertx;

import io.vertx.ext.web.client.WebClientOptions;
import io.vertx.mutiny.ext.web.client.WebClient;

@ApplicationScoped
public class OrdersService {

    private WebClient webclient;

    @Inject
    Vertx vertx;

    @Inject
    GCPConfiguration configuration;

    @Inject
    ReactiveMongoClient mongoClient;

    public Uni<JsonObject> add(PubSubEvent event) {
        Document document = new Document()
                .append("orderId", event.getMessage().getData());
        return getCollection().insertOne(document)
                .flatMap(res -> publishMessage(event));
    }

    private ReactiveMongoCollection<Document> getCollection() {
        return mongoClient.getDatabase("pizzaStore").getCollection("orders");
    }

    public Uni<JsonObject> publishMessage(PubSubEvent event) {
        PubSubEvent pubSubEvent = new PubSubEvent();
        PubSubMessage pubSubMessage = new PubSubMessage("ORDER_CREATED", event.getMessage().getData());
        pubSubEvent.setMessage(pubSubMessage);

        JsonObject obj = new JsonObject();
        JsonArray messages = new JsonArray();
        obj.put("messages", messages);
        JsonObject item0 = new JsonObject();
        item0.put("data", event.getMessage().getData());
        messages.add(item0);

        System.out.println(obj);

        this.webclient = WebClient.create(vertx,
                new WebClientOptions().setDefaultHost(configuration.getApiHost()).setDefaultPort(443).setSsl(true));
        return this.webclient
                .post(configuration.getPubsubTopicPublishUrl())
                .bearerTokenAuthentication(configuration.getApiToken())
                .sendJsonObject(obj)
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