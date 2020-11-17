package fr.maximedavid.serverless.extension.ext.gcp.token.machine;

import io.quarkus.vertx.runtime.VertxRecorder;
import io.smallrye.mutiny.Uni;

import io.vertx.core.json.JsonObject;


import io.vertx.ext.web.client.WebClientOptions;
import io.vertx.mutiny.core.Vertx;
import io.vertx.mutiny.ext.web.client.WebClient;
import org.jboss.logging.Logger;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.function.Supplier;

@ApplicationScoped
public class PubSubService {

    private TokenMachine tokenMachine;
    private PubSubConfig pubSubConfig;
    private Vertx vertx;

    private static final Logger LOG = Logger.getLogger(PubSubService.class);

    public PubSubService() {
    }

    public void setConfig(TokenMachineConfig tokenMachineConfig, PubSubConfig pubSubConfig) {
        this.tokenMachine = new TokenMachine(tokenMachineConfig);
        this.pubSubConfig = pubSubConfig;
        this.vertx = Vertx.vertx();
    }

    public Uni<JsonObject> publishMessage(JsonObject objectToSend, String topicUrl) {
        return tokenMachine.getAccessToken(vertx).flatMap(token -> {
            if(null == token) {
                LOG.error("Token is null");
                JsonObject result = new JsonObject().put("code", 500).put("message", "error in getAccessToken");
                return Uni.createFrom().item(result);
            }
            LOG.info("Sending : " + objectToSend);
            WebClient webclient = WebClient.create(vertx,
                    new WebClientOptions()
                            .setDefaultHost(pubSubConfig.host)
                            .setDefaultPort(pubSubConfig.port).setSsl(pubSubConfig.port == 443));
            return webclient
                    .post(topicUrl)
                    .bearerTokenAuthentication(token)
                    .sendJson(objectToSend)
                    .map(e -> {
                        if(e.statusCode() == 200) {
                            return null;
                        } else {
                            return new JsonObject()
                                    .put("code", e.statusCode())
                                    .put("message", e.bodyAsString());
                        }
                    });

        });
    }

}

