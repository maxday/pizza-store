package fr.maximedavid.serverless;

import fr.maximedavid.serverless.extension.ext.gcp.token.machine.TokenMachine;
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

    @Inject
    TokenMachine tokenMachine;

    private static final Logger LOG = Logger.getLogger(OrdersService.class);

    public Uni<JsonObject> receive(IncomingPubSubEvent pubSubEvent) {
        String eventId = pubSubEvent.getMessage().getAttributes().getEventId();
        String uuid = pubSubEvent.getMessage().getAttributes().getUuid();
        String name = pubSubEvent.getMessage().getAttributes().getName();
        LOG.info("Received eventId = " + eventId + " with uuid = " + uuid);
        if(PizzaEvent.PIZZA_ORDER_REQUEST.getEvent().equals(eventId)) {
            return handlePizzaCreationRequest(uuid, name);
        } else if (PizzaEvent.PIZZA_PREPARED_REQUEST.getEvent().equals(eventId)
                || PizzaEvent.PIZZA_BAKED_REQUEST.getEvent().equals(eventId)
                || PizzaEvent.PIZZA_LEFT_STORE_REQUEST.getEvent().equals(eventId)
                || PizzaEvent.PIZZA_DELIVERED_REQUEST.getEvent().equals(eventId)) {
            LOG.info("Change status handler");
            return handlePizzaChangeStatusRequest(uuid, eventId);
        } else if(PizzaEvent.PIZZA_STATUS_REQUEST.getEvent().equals(eventId)) {
            LOG.info("Get status handler");
            return handlePizzaStatusRequest(uuid, eventId);
        } else if(PizzaEvent.PIZZA_ORDER_LIST_REQUEST.getEvent().equals(eventId)) {
            LOG.info("List order handler");
            return handlePizzaOrderListRequest();
        }
        else {
            LOG.info("Unknown event, skipping");
            return Uni.createFrom().nullItem();
        }
    }

    private Uni<JsonObject> handlePizzaCreationRequest(String uuid, String name) {
        Document document = new Document()
                .append("uuid", uuid)
                .append("name", name)
                .append("status", PizzaEvent.PIZZA_ORDERED.getEvent());
        return getCollection().insertOne(document)
                .flatMap(res -> publishMessage(uuid, PizzaEvent.PIZZA_ORDERED.getEvent(), null, null, false, true))
                .flatMap(res -> handlePizzaOrderListRequest());
    }

    private Uni<JsonObject> handlePizzaStatusRequest(String uuid, String name) {
        return getCollection()
                .find(eq("uuid", uuid))
                .map(doc -> doc.getString("status"))
                .collectItems()
                .first().flatMap(res ->
                        publishMessage(uuid, PizzaEvent.PIZZA_STATUS_REQUEST_COMPLETED.getEvent(),
                                res, null, false, true));
    }

    private Uni<JsonObject> handlePizzaChangeStatusRequest(String uuid, String eventId) {
        String newEventId = eventId.replace("_REQUEST", "");
        return getCollection().updateOne(eq("uuid", uuid),
                new Document("$set", new Document("status", newEventId)))
                .flatMap(res -> publishMessage(uuid, newEventId, null, null, false, true));
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
                    return publishMessage(null, PizzaEvent.PIZZA_ORDER_LIST_REQUEST_COMPLETED.getEvent(),
                            null, base64EncodedString, true, true);
                });
    }

    public Uni<JsonObject> publishMessage(String uuid, String eventId, String extraData, String body, boolean isManagerTopic, boolean retry) {
        LOG.info("publishMessage");
        String token = System.getProperty("access.token");
        String topicPath = isManagerTopic ? configuration.getPubsubManagerTopicPublishUrl() : configuration.getPubsubTopicPublishUrl();
        OutgoingPubSubEvent pubSubEvent = new OutgoingPubSubEvent(uuid, eventId, extraData, body);
        this.webclient = WebClient.create(vertx,
                new WebClientOptions().setDefaultHost(configuration.getPubsubApiHost()).setDefaultPort(configuration.getPubsubApiPort()).setSsl(configuration.getPubsubApiPort() == 443));
        return this.webclient
                .post(topicPath)
                .bearerTokenAuthentication(tokenMachine.getAccessToken())
                .sendJsonObject(pubSubEvent)
                .flatMap(resp -> {
                    Uni<Boolean> toReturn = Uni.createFrom().item(false);
                    if (resp.statusCode() == 200) {
                        LOG.info("Successfully sent message on topic" + topicPath);
                    } else {
                        LOG.info("Impossible to send the message" + topicPath);
                        LOG.info("Retry = " + retry);
                        if (retry) {
                            toReturn = tokenMachine.setAccessToken(vertx);
                        }
                    }
                    return toReturn;
                })
                .flatMap(shouldRetry -> {
                    LOG.info("In shouldRetry = " + retry);
                    if (shouldRetry) {
                        LOG.info("RETRY");
                        return publishMessage(uuid, eventId, extraData, body, isManagerTopic, false);
                    } else {
                        LOG.info("PAS DE RETRY");
                        return Uni.createFrom().nullItem();
                    }
                });
    }

    private ReactiveMongoCollection<Document> getCollection() {
        return mongoClient.getDatabase(configuration.getDatabaseName())
                .getCollection(configuration.getCollectionName());
    }

}