package fr.maximedavid.serverless;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.Base64;

public class OutgoingPubSubEvent extends JsonObject {

    public OutgoingPubSubEvent(String uuid, String eventId, String body) {
        JsonArray messages = new JsonArray();
        this.put("messages", messages);
        JsonObject item0 = new JsonObject();
        messages.add(item0);
        JsonObject attributes = new JsonObject();
        attributes.put("uuid", uuid);
        attributes.put("eventId", eventId);
        item0.put("attributes", attributes);
        item0.put("data", body);
    }

}
