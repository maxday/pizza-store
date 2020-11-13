package fr.maximedavid.serverless;

import io.quarkus.mongodb.reactive.ReactiveMongoClient;
import io.quarkus.mongodb.reactive.ReactiveMongoCollection;
import io.smallrye.mutiny.Uni;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import io.vertx.core.json.JsonObject;
import io.vertx.mutiny.core.Vertx;

import io.vertx.ext.web.client.WebClientOptions;
import io.vertx.mutiny.ext.web.client.WebClient;
import org.bson.Document;
import org.jboss.logging.Logger;

import static com.mongodb.client.model.Filters.eq;

@ApplicationScoped
public class ClientOrdersService {

    private WebClient webclient;

    private static final Logger LOG = Logger.getLogger(ClientOrdersService.class);

    @Inject
    ReactiveMongoClient mongoClient;

    @Inject
    Vertx vertx;

    @Inject
    GCPConfiguration configuration;

    public Uni<JsonObject> createOrder(PizzaOrder pizzaOrder) {
        return publishMessage(pizzaOrder.getUuid(), pizzaOrder.getName());
    }

    public Uni<JsonObject> get(String uuid) {
        return getCollection()
                .find(eq("uuid", uuid))
                .map(doc -> new JsonObject().put("status", doc.getString("status")))
                .collectItems()
                .first();
    }

    private ReactiveMongoCollection<Document> getCollection() {
        return mongoClient.getDatabase("pizzaStore").getCollection("orders");
    }

    public Uni<JsonObject> publishMessage(String uuid, String name) {
        String token = System.getProperty("access.token");
        PubSubEvent pubSubEvent = new PubSubEvent(uuid, "PIZZA_ORDER_REQUEST", name);
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