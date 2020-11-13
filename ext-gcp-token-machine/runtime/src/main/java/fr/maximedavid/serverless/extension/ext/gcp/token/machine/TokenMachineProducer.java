package fr.maximedavid.serverless.extension.ext.gcp.token.machine;

import io.quarkus.runtime.annotations.ConfigItem;

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

    public void setTokenMachineConfig(String serviceAccount) {
        this.tokenMachineConfig = new TokenMachineConfig();
        this.tokenMachineConfig.serviceAccount = serviceAccount;
        this.tokenMachineConfig.expiryLength = 3600;
        this.tokenMachineConfig.scope = "https://www.googleapis.com/auth/pubsub";
        this.tokenMachineConfig.audience = "https://www.googleapis.com/oauth2/v4/token";
        this.tokenMachineConfig.apiHost =  "www.googleapis.com";
        this.tokenMachineConfig.apiPath = "/oauth2/v4/token";
    }
}