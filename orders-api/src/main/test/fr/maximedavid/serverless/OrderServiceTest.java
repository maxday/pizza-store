package fr.maximedavid.serverless;

import fr.maximedavid.serverless.extension.ext.gcp.token.machine.PubSubService;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import io.restassured.path.json.JsonPath;
import io.smallrye.mutiny.Uni;
import io.vertx.core.json.JsonObject;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;

import java.util.Base64;

import static io.restassured.RestAssured.given;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;


@QuarkusTestResource(DataResource.class)
@QuarkusTest
public class OrderServiceTest {

    @InjectMock
    PubSubService pubSubService;

    @Test
    public void testReceiveCreationOrder() {
        IncomingPubSubEvent event = new IncomingPubSubEvent();
        Message message = new Message();
        Attributes attributes = new Attributes();
        attributes.setEventId(PizzaEvent.PIZZA_ORDER_REQUEST.getEvent());
        attributes.setName("myPizza");
        attributes.setUuid("myUUID");
        message.setAttributes(attributes);
        event.setMessage(message);

        when(pubSubService.publishMessage(any(), any())).thenAnswer(
                invocation -> {
                    Object[] args = invocation.getArguments();
                    return Uni.createFrom().item(args[0]);
                });

        JsonPath res = given()
                .body(event)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .when()
                .post("/orders")
                .then()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .response()
                .jsonPath();

        assertEquals(PizzaEvent.PIZZA_ORDER_LIST_REQUEST_COMPLETED.getEvent(), res.getString("messages[0].attributes.eventId"));
        JsonPath retrievedJson = new JsonPath(new String(Base64.getDecoder().decode(res.getString("messages[0].data"))));
        assertEquals("myUUID", retrievedJson.getString("[0].uuid"));
        assertEquals("myPizza", retrievedJson.getString("[0].name"));
        assertEquals(PizzaEvent.PIZZA_ORDERED.getEvent(), retrievedJson.getString("[0].status"));
    }
    
}
