package fr.maximedavid.serverless;

import io.smallrye.mutiny.Uni;
import io.vertx.core.json.JsonObject;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("/orders")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ClientOrdersResource {

    @Inject
    ClientOrdersService clientOrdersService;

    @POST
    public Uni<JsonObject> add(PizzaOrder pizzaOrder) {
        return clientOrdersService.createOrder(pizzaOrder);
    }
}