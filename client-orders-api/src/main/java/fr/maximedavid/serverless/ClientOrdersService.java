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
public class ClientOrdersService {

    private WebClient webclient;

    private static final Logger LOG = Logger.getLogger(ClientOrdersService.class);

    @Inject
    Vertx vertx;

    @Inject
    TokenMachine tokenMachine;

    @Inject
    GCPConfiguration configuration;

    public Uni<JsonObject> createOrder(PizzaOrder pizzaOrder) {
        LOG.info("Creating pizza with uuid " + pizzaOrder.getUuid() );
        return publishMessage(pizzaOrder.getUuid(), PizzaEvent.PIZZA_ORDER_REQUEST.getEvent(), pizzaOrder.getName(), true);
    }

    public Uni<JsonObject> get(String uuid) {
        LOG.info("Get pizza status with uuid " + uuid);
        return publishMessage(uuid, PizzaEvent.PIZZA_STATUS_REQUEST.getEvent(), null, true);
    }

    public Uni<JsonObject> publishMessage(String uuid, String eventName, String name, boolean retry) {
        PubSubEvent pubSubEvent = new PubSubEvent(uuid, eventName, name);
        this.webclient = WebClient.create(vertx,
                new WebClientOptions().setDefaultHost(configuration.getPubsubApiHost()).setDefaultPort(configuration.getPubsubApiPort()).setSsl(configuration.getPubsubApiPort() == 443));
        return this.webclient
                .post(configuration.getPubsubTopicPublishUrl())
                .bearerTokenAuthentication(tokenMachine.getAccessToken())
                .sendJsonObject(pubSubEvent)
                .flatMap(resp -> {
                    Uni<Boolean> toReturn = Uni.createFrom().item(false);
                    if (resp.statusCode() == 200) {
                        LOG.info("Successfully sent message on topic" + configuration.getPubsubTopicPublishUrl());
                    } else {
                        LOG.info("Impossible to send the message" + configuration.getPubsubTopicPublishUrl());
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
                        return publishMessage(uuid, eventName, name,  false);
                    } else {
                        LOG.info("PAS DE RETRY");
                        return Uni.createFrom().nullItem();
                    }
                });
    }

}