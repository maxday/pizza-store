import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PubSubEmulatorContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.utility.DockerImageName;

import static org.junit.Assert.assertEquals;

@QuarkusTest
public class SimpleTest {

    @Container
    PubSubEmulatorContainer pubsub =
            new PubSubEmulatorContainer(
                    new DockerImageName("gcr.io/google.com/cloudsdktool/cloud-sdk", "316.0.0-emulators"));

    @Test
    public void testSimple() {
        String emulatorEndpoint = pubsub.getEmulatorEndpoint();
        assertEquals(1, 1);
    }
}
