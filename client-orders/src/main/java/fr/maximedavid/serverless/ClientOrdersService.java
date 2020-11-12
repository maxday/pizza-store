package fr.maximedavid.serverless;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.algorithms.Algorithm;
import com.google.auth.oauth2.ServiceAccountCredentials;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;


import io.quarkus.mongodb.reactive.ReactiveMongoClient;
import io.quarkus.mongodb.reactive.ReactiveMongoCollection;
import io.smallrye.mutiny.Uni;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.swing.*;

import io.vertx.core.json.JsonObject;

import io.vertx.ext.web.multipart.MultipartForm;
import io.vertx.mutiny.core.Vertx;

import io.vertx.ext.web.client.WebClientOptions;
import io.vertx.mutiny.core.buffer.Buffer;
import io.vertx.mutiny.ext.web.client.HttpResponse;
import io.vertx.mutiny.ext.web.client.WebClient;
import io.vertx.reactivex.ext.auth.jwt.JWTAuth;
import org.bson.Document;

import java.io.*;
import java.security.interfaces.RSAPrivateKey;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.mongodb.client.model.Filters.eq;

@ApplicationScoped
public class ClientOrdersService {

    private WebClient webclient;

    @Inject
    ReactiveMongoClient mongoClient;

    @Inject
    Vertx vertx;

    @Inject
    GCPConfiguration configuration;

    @Inject
    TokenService tokenService;

    public Uni<JsonObject> createOrder(PizzaOrder pizzaOrder) {
        return publishMessage(pizzaOrder.getUuid(), pizzaOrder.getName());
    }

    public Uni<JsonObject> get(String uuid) {
        return getCollection()
                .find(eq("uuid", uuid))
                .map(doc -> new JsonObject().put("status", doc.getString("status")))
                .collectItems()
                .first();
    }

    private ReactiveMongoCollection<Document> getCollection() {
        return mongoClient.getDatabase("pizzaStore").getCollection("orders");
    }

    public Uni<JsonObject> publishMessage(String uuid, String name) {
        PubSubEvent pubSubEvent = new PubSubEvent(uuid, "PIZZA_ORDER_REQUEST", name);
        System.out.println(pubSubEvent);
        this.webclient = WebClient.create(vertx,
                new WebClientOptions().setDefaultHost(configuration.getApiHost()).setDefaultPort(443).setSsl(true));
        return this.webclient
                .post(configuration.getPubsubTopicPublishUrl())
                .bearerTokenAuthentication(tokenService.getAccessToken())
                .sendJsonObject(pubSubEvent)
                    .onItem().transform(resp -> {
                    System.out.println("resp");
                    if (resp.statusCode() == 200) {
                        System.out.println("200 OK");
                        return null;
                    } else {
                        System.out.println("pas 200 KO" + resp.bodyAsString());
                        return new JsonObject()
                                .put("code", resp.statusCode())
                                .put("message", resp.bodyAsString());
                    }
                });
    }

}