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

    private Vertx vertx;
    private GCPConfiguration configuration;
    private TokenMachine tokenMachine;

    private WebClient webclient;

    private static final Logger LOG = Logger.getLogger(ClientOrdersService.class);

    public ClientOrdersService(GCPConfiguration configuration, TokenMachine tokenMachine, Vertx vertx) {
        this.configuration = configuration;
        this.tokenMachine = tokenMachine;
        this.vertx = vertx;
        this.webclient = WebClient.create(vertx,
                new WebClientOptions().setDefaultHost(configuration.getPubsubApiHost()).setDefaultPort(configuration.getPubsubApiPort()).setSsl(configuration.getPubsubApiPort() == 443));
    }

    public Uni<JsonObject> createOrder(PizzaOrder pizzaOrder) {
        LOG.info("Creating pizza with uuid " + pizzaOrder.getUuid() );
        return publishMessage(pizzaOrder.getUuid(), PizzaEvent.PIZZA_ORDER_REQUEST.getEvent(), pizzaOrder.getName());
    }

    public Uni<JsonObject> get(String uuid) {
        LOG.info("Get pizza status with uuid " + uuid);
        return publishMessage(uuid, PizzaEvent.PIZZA_STATUS_REQUEST.getEvent(), null);
    }

    public Uni<JsonObject> publishMessage(String uuid, String eventName, String name) {
        return tokenMachine.getAccessToken(vertx).flatMap(token -> {
            if(null == token) {
                LOG.error("Token is null");
                JsonObject result = new JsonObject().put("code", 500).put("message", "error in getAccessToken");
                return Uni.createFrom().item(result);
            }
            PubSubEvent pubSubEvent = new PubSubEvent(uuid, eventName, name);
            LOG.info("Sending : " + pubSubEvent);
            return this.webclient
                    .post(configuration.getPubsubTopicPublishUrl())
                    .bearerTokenAuthentication(token)
                    .sendJsonObject(pubSubEvent)
                    .map(e -> new JsonObject());
        });
    }

}