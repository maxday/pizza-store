package fr.maximedavid.serverless;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import io.smallrye.mutiny.Uni;
import io.vertx.core.json.JsonArray;
import io.vertx.mutiny.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClientOptions;
import io.vertx.mutiny.ext.web.client.WebClient;

@Path("/events")
public class EventsResource {

    @Inject
    Vertx vertx;

    private WebClient webclient;

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Uni<JsonObject> createEvent(Event event) {
        return publishMessage(event);
    }

    private JsonObject buildPubSubEvent(Event event) {
        JsonObject object = new JsonObject();
        JsonArray messages = new JsonArray();
        JsonObject data = new JsonObject().put("data", event.getData());
        object.put("messages", messages);
        messages.add(data);
        return object;
    }

    public Uni<JsonObject> publishMessage(Event event) {
        String prefix = "pubsub.googleapis.com";
        String topicId = "projects/myeventpic/topics/pizza-orders";
        String accessToken = "ya29.c.Kp0B4we1vuTCzByM3GAdvCDTVye4RqfhuqYIqurZUnlICpEbLkqCtQV-R3zEwRSRMMP_0FaE6Yg4_3qOjQwDaxtINVmgFHMh7ua0hCOeN9osVgxVSnuKEqIIqWXGzKZJadvMP22faAkvwjC5DjvzDEX3kTQDi3jPZn9ZPXN_vSHxUeFi3YY-xGGZupEEf4tLwKfQfdzxsdYZ_6ghYacJbA";

        JsonObject built = buildPubSubEvent(event);
        System.out.println(built);

        this.webclient = WebClient.create(vertx,
                new WebClientOptions().setDefaultHost(prefix).setDefaultPort(443).setSsl(true));
        return this.webclient
                .post("/v1/" + topicId + ":publish")
                .bearerTokenAuthentication(accessToken)
                .sendJsonObject(built)
                .onItem().transform(resp -> {
                    if (resp.statusCode() == 200) {
                        return null;
                    } else {
                        return new JsonObject()
                                .put("code", resp.statusCode())
                                .put("message", resp.bodyAsString());
                    }
                });
    }

}