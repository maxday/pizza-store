package fr.maximedavid.serverless;

import fr.maximedavid.serverless.extension.ext.gcp.token.machine.TokenMachine;
import io.smallrye.mutiny.Uni;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import io.vertx.core.json.JsonObject;

import io.vertx.mutiny.core.Vertx;

import io.vertx.ext.web.client.WebClientOptions;
import io.vertx.mutiny.ext.web.client.WebClient;
import org.jboss.logging.Logger;

@ApplicationScoped
public class ManagerService {

    @Inject
    Vertx vertx;

    @Inject
    GCPConfiguration configuration;

    @Inject
    TokenMachine tokenMachine;

    private WebClient webclient;
    private static final Logger LOG = Logger.getLogger(ManagerService.class);

    public Uni<JsonObject> setStatus(Pizza pizza) {
        return publishMessage(pizza.getUuid(), pizza.getEventId(), false);
    }

    public Uni<JsonObject> listOrders() {
        LOG.info(tokenMachine);
        LOG.info(tokenMachine.getAccessToken());
        LOG.info(tokenMachine.getApiHost());
        tokenMachine.setAccessToken(vertx).await().indefinitely();
        return publishMessage(null, PizzaEvent.PIZZA_ORDER_LIST_REQUEST.getEvent(), true);
    }


    public Uni<JsonObject> publishMessage(String uuid, String eventId, boolean isManager) {
        LOG.info("ACCESS TOKEN = " + tokenMachine.getAccessToken());
        PubSubEvent pubSubEvent = new PubSubEvent(uuid, eventId);
        String topicUrl = isManager ? configuration.getPubsubManagerTopicPublishUrl() : configuration.getPubsubTopicPublishUrl();
        LOG.info("Sending : " + pubSubEvent);
        this.webclient = WebClient.create(vertx,
                new WebClientOptions().setDefaultHost(configuration.getPubsubApiHost()).setDefaultPort(configuration.getPubsubApiPort()).setSsl(configuration.getPubsubApiPort() == 443));
        return this.webclient
                .post(topicUrl)
                .bearerTokenAuthentication(tokenMachine.getAccessToken())
                .sendJsonObject(pubSubEvent)
                .onItem().transform(resp -> {
                    if (resp.statusCode() == 200) {
                        LOG.info("Successfully sent message on topic" + topicUrl);
                        return null;
                    } else {
                        LOG.error("Impossible to send message on topic" + topicUrl + " error = " + resp.bodyAsString());
                        return new JsonObject()
                                .put("code", resp.statusCode())
                                .put("message", resp.bodyAsString());
                    }
                });
    }

}