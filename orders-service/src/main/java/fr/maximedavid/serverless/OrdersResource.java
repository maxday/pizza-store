package fr.maximedavid.serverless;

import io.smallrye.mutiny.Uni;
import io.vertx.core.json.JsonObject;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("/orders")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class OrdersResource {

    @Inject
    OrdersService ordersService;

    @POST
    public Uni<JsonObject> receive(IncomingPubSubEvent pubSubEvent) {
        return ordersService.receive(pubSubEvent);
    }
}