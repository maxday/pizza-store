import com.google.api.gax.core.CredentialsProvider;
import com.google.api.gax.core.NoCredentialsProvider;
import com.google.api.gax.grpc.GrpcTransportChannel;
import com.google.api.gax.rpc.FixedTransportChannelProvider;
import com.google.api.gax.rpc.TransportChannelProvider;
import com.google.cloud.pubsub.v1.TopicAdminClient;
import com.google.cloud.pubsub.v1.TopicAdminSettings;
import com.google.pubsub.v1.TopicName;
import fr.maximedavid.serverless.GCPConfiguration;
import fr.maximedavid.serverless.PizzaOrder;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import java.io.IOException;

import static io.restassured.RestAssured.given;

@QuarkusTest
@QuarkusTestResource(PubSubResource.class)

public class SimpleTest {

    @Inject
    GCPConfiguration configuration;

    @Test
    public void testSimple() {
        String hostport = configuration.getPubsubApiHost() + ":" + configuration.getPubsubApiPort();
        ManagedChannel channel = ManagedChannelBuilder.forTarget(hostport).usePlaintext().build();
        try {
            TransportChannelProvider channelProvider =
                    FixedTransportChannelProvider.create(GrpcTransportChannel.create(channel));
            CredentialsProvider credentialsProvider = NoCredentialsProvider.create();

            TopicAdminClient topicClient =
                    TopicAdminClient.create(
                            TopicAdminSettings.newBuilder()
                                    .setTransportChannelProvider(channelProvider)
                                    .setCredentialsProvider(credentialsProvider)
                                    .build());

            TopicName topicName = TopicName.of("my-project-id", "my-topic-id");

            topicClient.createTopic(topicName);

            given().when()
                    .contentType(ContentType.JSON)
                    .body(new PizzaOrder("myUuid", "name"))
                    .post("/orders")
                    .then().statusCode(204);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            channel.shutdown();
        }

    }

}
