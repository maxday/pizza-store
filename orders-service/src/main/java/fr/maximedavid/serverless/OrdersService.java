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

import java.util.Base64;

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
        } else if("PIZZA_ORDER_LIST_REQUEST".equals(eventId)) {
            return handlePizzaOrderListRequest();
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
        return getCollection().insertOne(document).flatMap(res -> publishMessage(uuid, "PIZZA_ORDERED", null, null, false));
    }

    private Uni<JsonObject> handlePizzaStatusRequest(String uuid, String name) {
        return getCollection()
                .find(eq("uuid", uuid))
                .map(doc -> doc.getString("status"))
                .collectItems()
                .first().flatMap(res -> publishMessage(uuid, "PIZZA_STATUS_REQUEST_COMPLETED", res, null, false));
    }

    private Uni<JsonObject> handlePizzaChangeStatusRequest(String uuid, String eventId) {
        LOG.info("handlePizzaChangeStatusRequest uuid = " + uuid);
        LOG.info("handlePizzaChangeStatusRequest eventId = " + eventId);
        String newEventId = eventId.replace("_REQUEST", "");
        return getCollection().updateOne(eq("uuid", uuid), new Document("$set", new Document("status", newEventId))).flatMap(res -> publishMessage(uuid, newEventId, null, null, false));
    }

    public Uni<JsonObject> handlePizzaOrderListRequest() {
        return getCollection().find()
                .map(doc -> {
                    JsonObject order = new JsonObject();
                    order.put("uuid", doc.getString("uuid"));
                    order.put("name", doc.getString("name"));
                    order.put("status", doc.getString("status"));
                    return order;
                }).collectItems().asList()
                .flatMap(res -> {
                    String base64EncodedString = Base64.getEncoder().encodeToString(res.toString().getBytes());
                    return publishMessage(null, "PIZZA_ORDER_LIST_REQUEST_COMPLETED", null, base64EncodedString, true);
                });
    }

    private ReactiveMongoCollection<Document> getCollection() {
        return mongoClient.getDatabase("pizzaStore").getCollection("orders");
    }

    public Uni<JsonObject> publishMessage(String uuid, String eventId, String extraData, String body, boolean isManagerTopic) {
        LOG.info("publishMessage");
        String token = System.getProperty("access.token");
        String topicPath = isManagerTopic ? configuration.getPubsubManagerTopicPublishUrl() : configuration.getPubsubTopicPublishUrl();
        OutgoingPubSubEvent pubSubEvent = new OutgoingPubSubEvent(uuid, eventId, extraData, body);
        this.webclient = WebClient.create(vertx,
                new WebClientOptions().setDefaultHost(configuration.getPubsubApiHost()).setDefaultPort(configuration.getPubsubApiPort()).setSsl(configuration.getPubsubApiPort() == 443));
        return this.webclient
                .post(topicPath)
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