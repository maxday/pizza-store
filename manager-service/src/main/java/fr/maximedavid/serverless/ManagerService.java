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
        return publishMessage(pizza.getUuid(), pizza.getEventId(), false, true);
    }

    public Uni<JsonObject> listOrders() {
        return publishMessage(null, PizzaEvent.PIZZA_ORDER_LIST_REQUEST.getEvent(), true, true);
    }

    public Uni<JsonObject> publishMessage(String uuid, String eventId, boolean isManager, boolean retry) {
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
                .flatMap(resp -> {
                    Uni<Boolean> toReturn = Uni.createFrom().item(false);
                    if (resp.statusCode() == 200) {
                        LOG.info("Successfully sent message on topic" + topicUrl);
                    } else {
                        LOG.info("Impossible to send the message" + topicUrl);
                        LOG.info("Retry = " + retry);
                        if(retry) {
                            toReturn = tokenMachine.setAccessToken(vertx);
                        }
                    }
                    return toReturn;
                })
                .flatMap(shouldRetry -> {
                    LOG.info("In shouldRetry = " + retry);
                    if(shouldRetry) {
                        LOG.info("RETRY");
                        return publishMessage(uuid, eventId, isManager, false);
                    } else {
                        LOG.info("PAS DE RETRY");
                        return Uni.createFrom().nullItem();
                    }
                });
    }

}