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
        String name = pubSubEvent.getMessage().getAttributes().getName();
        LOG.info("RECEIVED UUID = " + uuid + " AND EVENTID = " + eventId);

        if("PIZZA_ORDER_REQUEST".equals(eventId)) {
            LOG.info("in switch for PIZZA_ORDER_REQUEST");
            return handlePizzaCreationRequest(uuid, name);
        } else if ("PIZZA_PREPARED_REQUEST".equals(eventId)
                || "PIZZA_BAKED_REQUEST".equals(eventId)
                || "PIZZA_LEFT_STORE_REQUEST".equals(eventId)
                || "PIZZA_DELIVERED_REQUEST".equals(eventId)) {
            return handlePizzaChangeStatusRequest(uuid, eventId);
        } else if("PIZZA_STATUS_REQUEST".equals(eventId)) {
            return handlePizzaStatusRequest(uuid, eventId);
        }
        else {
            return Uni.createFrom().nullItem();
        }
    }

    private Uni<JsonObject> handlePizzaCreationRequest(String uuid, String name) {
        LOG.info("handlePizzaCreationRequest uuid = " + uuid);
        LOG.info("handlePizzaCreationRequest name = " + name);
        Document document = new Document()
                .append("uuid", uuid)
                .append("name", name)
                .append("status", "PIZZA_ORDERED");
        return getCollection().insertOne(document).flatMap(res -> publishMessage(uuid, "PIZZA_ORDERED", null));
    }

    private Uni<JsonObject> handlePizzaStatusRequest(String uuid, String name) {
        return getCollection()
                .find(eq("uuid", uuid))
                .map(doc -> doc.getString("status"))
                .collectItems()
                .first().flatMap(res -> publishMessage(uuid, "PIZZA_STATUS_REQUEST_COMPLTED", res));
    }

    private Uni<JsonObject> handlePizzaChangeStatusRequest(String uuid, String eventId) {
        LOG.info("handlePizzaChangeStatusRequest uuid = " + uuid);
        LOG.info("handlePizzaChangeStatusRequest eventId = " + eventId);
        String newEventId = eventId.replace("_REQUEST", "");
        return getCollection().updateOne(eq("uuid", uuid), new Document("$set", new Document("status", newEventId))).flatMap(res -> publishMessage(uuid, newEventId, null));
    }

    private ReactiveMongoCollection<Document> getCollection() {
        return mongoClient.getDatabase("pizzaStore").getCollection("orders");
    }

    public Uni<JsonObject> publishMessage(String uuid, String eventId, String extraData) {
        LOG.info("publishMessage");
        String token = System.getProperty("access.token");
        OutgoingPubSubEvent pubSubEvent = new OutgoingPubSubEvent(uuid, eventId, extraData);
        this.webclient = WebClient.create(vertx,
                new WebClientOptions().setDefaultHost(configuration.getPubsubApiHost()).setDefaultPort(configuration.getPubsubApiPort()).setSsl(configuration.getPubsubApiPort() == 443));
        return this.webclient
                .post(configuration.getPubsubTopicPublishUrl())
                .bearerTokenAuthentication(token)
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