    package fr.maximedavid.serverless;

import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;
import org.testcontainers.containers.GenericContainer;

import java.util.Collections;
import java.util.Map;

public class DataResource implements QuarkusTestResourceLifecycleManager {

    private static final Integer MONGO_PORT = 27017;
    private static GenericContainer MONGO = null;

    @Override
    public Map<String, String> start() {
        MONGO = new GenericContainer("mongo:4.0").withExposedPorts(MONGO_PORT);
        MONGO.start();
        String hosts = (MONGO.getContainerIpAddress() + ":" + MONGO.getMappedPort(MONGO_PORT));
        return Collections.singletonMap("quarkus.mongodb.hosts", hosts);
    }

    @Override
    public void stop() {
        MONGO.stop();
    }
}
