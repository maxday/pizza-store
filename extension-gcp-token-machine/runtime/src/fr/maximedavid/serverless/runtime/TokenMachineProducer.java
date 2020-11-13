package fr.maximedavid.serverless.runtime;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

@ApplicationScoped
public class TokenMachineProducer {

    private TokenMachineConfig tokenMachineConfig;

    @Produces
    public TokenMachine produceTokenMachine()  {
        return new TokenMachine(
                tokenMachineConfig.audience,
                tokenMachineConfig.expiryLength,
                tokenMachineConfig.apiHost,
                tokenMachineConfig.apiPath,
                tokenMachineConfig.scope,
                tokenMachineConfig.serviceAccount
        );
    }

    public void setTokenMachineConfig(TokenMachineConfig tokenMachineConfig) {
        this.tokenMachineConfig = tokenMachineConfig;
    }
}