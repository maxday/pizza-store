import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.LogMessageWaitStrategy;

public class PubSubContainer extends GenericContainer<org.testcontainers.containers.PubSubEmulatorContainer> {
    private static final String CMD = "gcloud beta emulators pubsub start --host-port 0.0.0.0:8085";
    private static final int PORT = 8085;

    public PubSubContainer(String dockerImageName) {
        super(dockerImageName);
        this.withExposedPorts(new Integer[]{8085});
        this.setWaitStrategy((new LogMessageWaitStrategy()).withRegEx("(?s).*started.*$"));
        this.withFileSystemBind("//var/run/docker.sock", "/var/run/docker.sock");
        this.withCommand(new String[]{"/bin/sh", "-c", "gcloud beta emulators pubsub start --host-port 0.0.0.0:8085"});
    }
    public String getEmulatorHost() {
        return this.getContainerIpAddress();
    }
    public Integer getEmulatorPort() {
        return this.getMappedPort(8085);
    }

}
