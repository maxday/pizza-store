package fr.maximedavid.serverless;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.Base64;

public class PubSubEvent extends JsonObject {

    public PubSubEvent(String uuid, String eventId, String name) {
        JsonArray messages = new JsonArray();
        this.put("messages", messages);
        JsonObject item0 = new JsonObject();
        item0.put("data", Base64.getEncoder().encodeToString(eventId.getBytes()));
        messages.add(item0);
        JsonObject attributes = new JsonObject();
        attributes.put("uuid", uuid);
        attributes.put("eventId", eventId);
        attributes.put("name", name);
        item0.put("attributes", attributes);
    }

}
