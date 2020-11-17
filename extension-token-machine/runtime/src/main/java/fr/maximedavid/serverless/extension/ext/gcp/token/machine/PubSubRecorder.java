
package fr.maximedavid.serverless.extension.ext.gcp.token.machine;

import io.quarkus.arc.Arc;
import io.quarkus.runtime.RuntimeValue;
import io.quarkus.runtime.annotations.Recorder;
import io.quarkus.vertx.runtime.VertxRecorder;
import io.vertx.mutiny.core.Vertx;


import javax.inject.Inject;
import java.util.function.Supplier;

@Recorder
public class PubSubRecorder {

    public RuntimeValue<PubSubService> getPubSubService() {
        return new RuntimeValue<>(new PubSubService());
    }

    public void setConfig(TokenMachineConfig tokenMachineConfig, PubSubConfig pubSubConfig) {
        Arc.container().instance(PubSubService.class).get().setConfig(tokenMachineConfig, pubSubConfig);
    }

}