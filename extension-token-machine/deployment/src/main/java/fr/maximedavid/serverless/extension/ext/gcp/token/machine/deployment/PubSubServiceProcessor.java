package fr.maximedavid.serverless.extension.ext.gcp.token.machine.deployment;

import fr.maximedavid.serverless.extension.ext.gcp.token.machine.*;
import io.quarkus.arc.deployment.*;
import io.quarkus.deployment.annotations.*;
import io.quarkus.deployment.annotations.Record;
import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Singleton;

import static io.quarkus.deployment.annotations.ExecutionTime.RUNTIME_INIT;
import static io.quarkus.deployment.annotations.ExecutionTime.STATIC_INIT;

public class PubSubServiceProcessor {

    @BuildStep
    @Record(STATIC_INIT)
    SyntheticBeanBuildItem syntheticBean(PubSubRecorder recorder) {
        return SyntheticBeanBuildItem.configure(PubSubService.class).scope(ApplicationScoped.class)
                .runtimeValue(recorder.getPubSubService())
                .done();
    }

    @Record(RUNTIME_INIT)
    @BuildStep
    void generateClientBeans(PubSubRecorder recorder,
                             TokenMachineConfig tokenMachineConfig,
                             PubSubConfig pubSubConfigConfig
                             ) {
        recorder.setConfig(tokenMachineConfig, pubSubConfigConfig);
    }


}