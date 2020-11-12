package fr.maximedavid.serverless;

import io.smallrye.mutiny.Uni;
import io.vertx.core.json.JsonObject;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("/orders")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ManagerResource {

    @Inject
    ManagerService managerService;

    @PUT
    public Uni<JsonObject> setStatus(PizzaEvent pizzaEvent) {
        return managerService.setStatus(pizzaEvent);
    }

    @GET
    public Uni<List<PizzaEvent>> listOrders() {
        return managerService.listOrders();
    }
}