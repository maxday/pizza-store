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

    private Vertx vertx;
    private GCPConfiguration configuration;
    private TokenMachine tokenMachine;

    private WebClient webclient;
    private static final Logger LOG = Logger.getLogger(ManagerService.class);

    public ManagerService(GCPConfiguration configuration, TokenMachine tokenMachine, Vertx vertx) {
        this.configuration = configuration;
        this.tokenMachine = tokenMachine;
        this.vertx = vertx;
        this.webclient = WebClient.create(vertx,
                new WebClientOptions().setDefaultHost(configuration.getPubsubApiHost()).setDefaultPort(configuration.getPubsubApiPort()).setSsl(configuration.getPubsubApiPort() == 443));
    }

    public Uni<JsonObject> setStatus(Pizza pizza) {
        return publishMessage(pizza.getUuid(), pizza.getEventId(), false);
    }

    public Uni<JsonObject> listOrders() {
        return publishMessage(null, PizzaEvent.PIZZA_ORDER_LIST_REQUEST.getEvent(), true);
    }

    public Uni<JsonObject> publishMessage(String uuid, String eventId, boolean isManager) {
        return tokenMachine.getAccessToken(vertx).flatMap(token -> {
            LOG.info("HERE IS MY TOKEN = " + token);
            if(null == token) {
                LOG.error("Token is null");
                JsonObject result = new JsonObject().put("code", 500).put("message", "error in getAccessToken");
                return Uni.createFrom().item(result);
            }
            PubSubEvent pubSubEvent = new PubSubEvent(uuid, eventId);
            String topicUrl = isManager ? configuration.getPubsubManagerTopicPublishUrl() : configuration.getPubsubTopicPublishUrl();
            LOG.info("Sending : " + pubSubEvent);
            return this.webclient
                        .post(topicUrl)
                        .bearerTokenAuthentication(token)
                        .sendJsonObject(pubSubEvent)
                        .map(e -> new JsonObject());
        });
    }
}