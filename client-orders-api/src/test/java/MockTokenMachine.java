import fr.maximedavid.serverless.extension.ext.gcp.token.machine.TokenMachine;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.core.Vertx;

public class MockTokenMachine extends TokenMachine {
    @Override
    public Uni<String> getAccessToken(Vertx vertx) {
        return Uni.createFrom().item("myFakeToken");
    }

}
