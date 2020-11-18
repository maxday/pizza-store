import fr.maximedavid.serverless.PizzaOrder;
import fr.maximedavid.serverless.extension.ext.gcp.token.machine.PubSubService;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import io.restassured.http.ContentType;
import io.smallrye.mutiny.Uni;
import io.vertx.core.json.JsonObject;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.mockito.Mockito.when;

@QuarkusTest
public class ClientOrdersServiceTest {

    @InjectMock
    PubSubService pubSubService;

    @Test
    public void testCreateOrder() {
        when(pubSubService.publishMessage(null, null)).thenReturn(Uni.createFrom().item(new JsonObject()));
        given().when()
                .contentType(ContentType.JSON)
                .body(new PizzaOrder("myUuid", "name"))
                .post("/orders")
                .then().statusCode(204);
    }

    @Test
    public void testGetOrderStatus() {
        when(pubSubService.publishMessage(null, null)).thenReturn(Uni.createFrom().item(new JsonObject()));
        given().when()
                .contentType(ContentType.JSON)
                .get("/orders/1234")
                .then().statusCode(204);
    }

}
