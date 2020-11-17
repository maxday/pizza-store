package fr.maximedavid.serverless.extension.ext.gcp.token.machine.deployment;

import fr.maximedavid.serverless.extension.ext.gcp.token.machine.TokenMachine;
import fr.maximedavid.serverless.extension.ext.gcp.token.machine.TokenMachineConfig;
import fr.maximedavid.serverless.extension.ext.gcp.token.machine.TokenMachineRecorder;
import io.quarkus.arc.deployment.*;
import io.quarkus.deployment.annotations.*;
import io.quarkus.deployment.annotations.Record;
import org.jboss.logging.Logger;

import javax.inject.Singleton;

import static io.quarkus.deployment.annotations.ExecutionTime.RUNTIME_INIT;
import static io.quarkus.deployment.annotations.ExecutionTime.STATIC_INIT;

public class TokenMachineProcessor {

    private static final Logger LOG = Logger.getLogger(TokenMachineProcessor.class);

    @BuildStep
    @Record(STATIC_INIT)
    SyntheticBeanBuildItem syntheticBean(TokenMachineRecorder recorder) {
        return SyntheticBeanBuildItem.configure(TokenMachine.class).scope(Singleton.class)
                .runtimeValue(recorder.getTokenMachine())
                .done();
    }

    @Record(RUNTIME_INIT)
    @BuildStep
    void generateClientBeans(TokenMachineRecorder recorder,
                             TokenMachineConfig tokenMachineConfig
                             ) {
        recorder.setConfig(tokenMachineConfig);
    }

}