import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class PubSubResource implements QuarkusTestResourceLifecycleManager {

    public static PubSubContainer pubsub = new PubSubContainer("gcr.io/google.com/cloudsdktool/cloud-sdk:316.0.0-emulators");

    @Override
    public Map<String, String> start() {
        pubsub.start();
        Map<String, String> map = new HashMap<>();
        map.put("gcp.pubsub-api-host", pubsub.getEmulatorHost());
        map.put("gcp.pubsub-api-port", pubsub.getEmulatorPort().toString());
        return map;
    }

    @Override
    public void stop() {
        pubsub.stop();
    }
}
