package fr.maximedavid.serverless;

import io.vertx.core.json.JsonObject;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class FirestoreDocument {

    private Map<String, StringField> fields;

    public FirestoreDocument(String name, String status) {
        this.fields = new HashMap<>();
        if(null != name) {
            System.out.println(name);
            this.fields.put("name", new StringField(new String(Base64.getDecoder().decode(name))));
        }
        this.fields.put("status", new StringField(status));
    }

    public JsonObject toJson() {
        JsonObject result = new JsonObject();
        JsonObject fieldsObject = new JsonObject();
        result.put("fields", fieldsObject);
        for (Map.Entry<String, StringField> entry : fields.entrySet())  {
            JsonObject singleField = new JsonObject();
            singleField.put(entry.getValue().getType(), entry.getValue().getValue());
            fieldsObject.put(entry.getKey(), singleField);
        }
        return result;
    }
}
