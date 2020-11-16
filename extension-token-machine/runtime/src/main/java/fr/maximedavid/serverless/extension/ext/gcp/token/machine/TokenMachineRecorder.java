
package fr.maximedavid.serverless.extension.ext.gcp.token.machine;

import io.quarkus.arc.Arc;
import io.quarkus.runtime.RuntimeValue;
import io.quarkus.runtime.annotations.Recorder;

@Recorder
public class TokenMachineRecorder {

    public RuntimeValue<TokenMachine> getTokenMachine() {
        return new RuntimeValue<>(new TokenMachine());
    }

    public void setConfig(TokenMachineConfig tokenMachineConfig) {
        Arc.container().instance(TokenMachine.class).get().setConfig(tokenMachineConfig);
    }





}