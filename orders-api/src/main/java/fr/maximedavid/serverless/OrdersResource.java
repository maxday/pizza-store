package fr.maximedavid.serverless;

import io.smallrye.mutiny.Uni;
import io.vertx.core.json.JsonObject;
import org.jboss.logging.Logger;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("/orders")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class OrdersResource {

    private static final Logger LOG = Logger.getLogger(OrdersResource.class);

    @Inject
    OrdersService ordersService;

    @POST
    public Uni<JsonObject> receive(IncomingPubSubEvent pubSubEvent) {
        try {
            Uni<JsonObject> res = ordersService.receive(pubSubEvent);
            return res;
        } catch (Exception e) {
            LOG.info(e);
            return Uni.createFrom().nullItem();
        }
    }
}