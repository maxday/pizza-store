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

import java.util.function.Consumer;
import java.util.function.Function;

@ApplicationScoped
public class OrdersService {

    private WebClient webclient;

    @Inject
    Vertx vertx;

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
        System.out.println("ENTERING IN PUBLISH MESSAGE");
        String prefix = "pubsub.googleapis.com";
        String topicId = "projects/myeventpic/topics/pizza-orders";
        String accessToken = "ya29.c.Kp0B4wf5KpHiDL7s9JM_9MOcrhZUv-fqRRDOT5cgSst7m3SlOJwcByWg43NGFyhaa_ttk2zMEXzNL3EJJ0UFVxfNNnladSh4vqY3hd_7Ak6fmHQ66hlJ6Ieq-uNwRowAuXhhp6BVII8B9XxWE1BQziaMGetVZZZ2OqVSl_V6Sur7VQPgXp5ysoR58kddmqOC5bXISePZy1zWXv8wfqz08g";

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
                new WebClientOptions().setDefaultHost(prefix).setDefaultPort(443).setSsl(true));
        return this.webclient
                .post("/v1/" + topicId + ":publish")
                .bearerTokenAuthentication(accessToken)
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