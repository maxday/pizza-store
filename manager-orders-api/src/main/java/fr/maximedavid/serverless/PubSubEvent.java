package fr.maximedavid.serverless;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

public class PubSubEvent extends JsonObject {

    public PubSubEvent(String uuid, String eventId) {
        JsonArray messages = new JsonArray();
        this.put("messages", messages);
        JsonObject item = new JsonObject();
        JsonObject attributes = new JsonObject();
        attributes.put("uuid", uuid);
        attributes.put("eventId", eventId);
        item.put("attributes", attributes);
    }

}
