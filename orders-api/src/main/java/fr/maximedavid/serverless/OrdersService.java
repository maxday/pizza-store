package fr.maximedavid.serverless;

import fr.maximedavid.serverless.extension.ext.gcp.token.machine.PubSubService;
import io.quarkus.mongodb.reactive.ReactiveMongoClient;
import io.quarkus.mongodb.reactive.ReactiveMongoCollection;
import io.smallrye.mutiny.Uni;

import javax.enterprise.context.ApplicationScoped;

import io.vertx.core.json.JsonObject;
import org.bson.Document;
import org.jboss.logging.Logger;

import java.util.Base64;

import static com.mongodb.client.model.Filters.eq;

@ApplicationScoped
public class OrdersService {

    private GCPConfiguration configuration;
    private PubSubService pubSubService;
    private ReactiveMongoClient mongoClient;

    private static final Logger LOG = Logger.getLogger(OrdersService.class);

    public OrdersService(GCPConfiguration configuration, PubSubService pubSubService, ReactiveMongoClient mongoClient) {
        this.configuration = configuration;
        this.pubSubService = pubSubService;
        this.mongoClient = mongoClient;
    }

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
                .flatMap(res -> publishMessage(uuid, PizzaEvent.PIZZA_ORDERED.getEvent(), null, null, false))
                .flatMap(res -> handlePizzaOrderListRequest());
    }

    private Uni<JsonObject> handlePizzaStatusRequest(String uuid, String name) {
        return getCollection()
                .find(eq("uuid", uuid))
                .map(doc -> doc.getString("status"))
                .collectItems()
                .first().flatMap(res ->
                        publishMessage(uuid, PizzaEvent.PIZZA_STATUS_REQUEST_COMPLETED.getEvent(),
                                res, null, false));
    }

    private Uni<JsonObject> handlePizzaChangeStatusRequest(String uuid, String eventId) {
        String newEventId = eventId.replace("_REQUEST", "");
        return getCollection().updateOne(eq("uuid", uuid),
                new Document("$set", new Document("status", newEventId)))
                .flatMap(res -> publishMessage(uuid, newEventId, null, null, false));
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
                            null, base64EncodedString, true);
                });
    }

    public Uni<JsonObject> publishMessage(String uuid, String eventId, String extraData, String body, boolean isManager) {
        OutgoingPubSubEvent pubSubEvent = new OutgoingPubSubEvent(uuid, eventId, extraData, body);
        LOG.info("Publishing : " + pubSubEvent.toString());
        String topicUrl = isManager ? configuration.getPubsubManagerTopicPublishUrl() : configuration.getPubsubTopicPublishUrl();
        return this.pubSubService.publishMessage(pubSubEvent, topicUrl);
    }

    private ReactiveMongoCollection<Document> getCollection() {
        return mongoClient.getDatabase(configuration.getDatabaseName())
                .getCollection(configuration.getCollectionName());
    }

}