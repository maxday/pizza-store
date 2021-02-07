package fr.maximedavid.serverless;

import io.smallrye.mutiny.Uni;
import io.vertx.core.json.JsonObject;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("/orders")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ManagerResource {

    @Inject
    ManagerService managerService;

    @PUT
    public Uni<JsonObject> setStatus(Pizza pizza) {
        return managerService.setStatus(pizza);
    }

}