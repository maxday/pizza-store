package fr.maximedavid.serverless;

import io.quarkus.mongodb.reactive.ReactiveMongoClient;
import io.quarkus.mongodb.reactive.ReactiveMongoCollection;
import io.smallrye.mutiny.Uni;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import io.vertx.core.json.JsonObject;
import org.bson.Document;

import io.vertx.mutiny.core.Vertx;

import io.vertx.ext.web.client.WebClientOptions;
import io.vertx.mutiny.ext.web.client.WebClient;
import org.jboss.logging.Logger;

import static com.mongodb.client.model.Filters.eq;

@ApplicationScoped
public class OrdersService {

    private WebClient webclient;

    @Inject
    Vertx vertx;

    @Inject
    GCPConfiguration configuration;

    @Inject
    ReactiveMongoClient mongoClient;

    private static final Logger LOG = Logger.getLogger(OrdersService.class);

    public Uni<JsonObject> receive(IncomingPubSubEvent pubSubEvent) {
        String eventId = pubSubEvent.getMessage().getAttributes().getEventId();
        String uuid = pubSubEvent.getMessage().getAttributes().getUuid();
        String data = pubSubEvent.getMessage().getData();
        LOG.info("RECEIVED UUID = " + uuid + " AND EVENTID = " + eventId);
        if("PIZZA_ORDER_REQUEST".equals(eventId)) {
            LOG.info("in switch for PIZZA_ORDER_REQUEST");
            return handlePizzaCreationRequest(uuid, data);
        } else if("PIZZA_BAKING_REQUEST".equals(eventId)) {
            return handlePizzaBakingRequest(uuid);
        } else {
            return Uni.createFrom().nullItem();
        }
    }

    private Uni<JsonObject> handlePizzaCreationRequest(String uuid, String data) {
        LOG.info("handlePizzaCreationRequest uuid = " + uuid);
        LOG.info("handlePizzaCreationRequest data = " + data);
        Document document = new Document()
                .append("uuid", uuid)
                .append("toppings", data)
                .append("status", "PIZZA_ORDERED");
        return getCollection().insertOne(document).flatMap(res -> publishMessage(uuid, "PIZZA_ORDERED"));
    }

    private Uni<JsonObject> handlePizzaBakingRequest(String uuid) {
        LOG.info("handlePizzaBakingRequest uuid = " + uuid);
        return getCollection().updateOne(eq("uuid", uuid), new Document("$set", new Document("status", "PIZZA_BAKED"))).flatMap(res -> publishMessage(uuid, "PIZZA_BAKED"));
    }


    private ReactiveMongoCollection<Document> getCollection() {
        return mongoClient.getDatabase("pizzaStore").getCollection("orders");
    }

    public Uni<JsonObject> publishMessage(String uuid, String eventId) {
        LOG.info("publishMessage");
        OutgoingPubSubEvent pubSubEvent = new OutgoingPubSubEvent(uuid, eventId);
        this.webclient = WebClient.create(vertx,
                new WebClientOptions().setDefaultHost(configuration.getApiHost()).setDefaultPort(443).setSsl(true));
        return this.webclient
                .post(configuration.getPubsubTopicPublishUrl())
                .bearerTokenAuthentication(configuration.getApiToken())
                .sendJsonObject(pubSubEvent)
                .onItem().transform(resp -> {
                    if (resp.statusCode() == 200) {
                        LOG.info("200");
                        return null;
                    } else {
                        LOG.info("NOT OK 200");
                        LOG.info(resp.bodyAsString());
                        return new JsonObject()
                                .put("code", resp.statusCode())
                                .put("message", resp.bodyAsString());
                    }
                });
    }

}