package fr.maximedavid.serverless;

import io.smallrye.mutiny.Uni;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.WebApplicationException;

import io.vertx.ext.web.client.WebClientOptions;
import io.vertx.mutiny.ext.web.client.WebClient;

import io.vertx.mutiny.core.Vertx;
import io.vertx.core.json.JsonObject;
import org.jboss.logging.Logger;

@ApplicationScoped
public class OrdersService {

    private WebClient client;
    private GCPConfiguration configuration;

    private static final Logger LOG = Logger.getLogger(OrdersService.class);

    public OrdersService(GCPConfiguration configuration, Vertx vertx) {
        this.client = WebClient.create(vertx,
                new WebClientOptions().setDefaultHost(configuration.getFirestoreApiHost()).setDefaultPort(443).setSsl(true)
                        .setTrustAll(true));
        this.configuration = configuration;
    }

    public Uni<JsonObject> receive(IncomingPubSubEvent pubSubEvent) {
        String eventId = pubSubEvent.getMessage().getAttributes().getEventId();
        String uuid = pubSubEvent.getMessage().getAttributes().getUuid();LOG.info("uuid = " + uuid);
        String payload = pubSubEvent.getMessage().getData();
        LOG.info("Received eventId = " + eventId + " with uuid = " + uuid);
        if(PizzaEvent.PIZZA_ORDER_REQUEST.getEvent().equals(eventId)) {
            return handlePizzaCreationRequest(uuid, payload);
        } else if (PizzaEvent.PIZZA_PREPARED_REQUEST.getEvent().equals(eventId)
                || PizzaEvent.PIZZA_BAKED_REQUEST.getEvent().equals(eventId)
                || PizzaEvent.PIZZA_LEFT_STORE_REQUEST.getEvent().equals(eventId)
                || PizzaEvent.PIZZA_DELIVERED_REQUEST.getEvent().equals(eventId)) {
            LOG.info("Change status handler");
            return handlePizzaChangeStatusRequest(uuid, eventId);
        }
        else {
            LOG.info("Unknown event, skipping");
            return Uni.createFrom().nullItem();
        }
    }

    private Uni<JsonObject> handlePizzaCreationRequest(String uuid, String name) {
        FirestoreDocument document = new FirestoreDocument(name, PizzaEvent.PIZZA_ORDERED.getEvent());
        return client.post(configuration.getApiPath() + configuration.getProjectName() + "/databases/(default)/documents/" + configuration.getDatabaseName() + "?documentId=" + uuid)
                .sendJson(document.toJson())
                .map(resp -> {
                    if (resp.statusCode() == 200) {
                        return resp.bodyAsJsonObject();
                    } else {
                        throw new WebApplicationException(resp.bodyAsString(), resp.statusCode());
                    }
                });
    }

    private Uni<JsonObject> handlePizzaChangeStatusRequest(String uuid, String eventId) {
        FirestoreDocument document = new FirestoreDocument(null, eventId);
        return client.post(configuration.getApiPath() + configuration.getProjectName() + "/databases/(default)/documents/" + configuration.getDatabaseName() + "/" + uuid)
                    .sendJson(document.toJson())
                    .map(resp -> {
                        if (resp.statusCode() == 200) {
                            return resp.bodyAsJsonObject();
                        } else {
                            throw new WebApplicationException(resp.bodyAsString(), resp.statusCode());
                        }
                    });
    }
}

