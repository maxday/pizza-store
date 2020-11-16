package fr.maximedavid.serverless.extension.ext.gcp.token.machine.deployment;


import fr.maximedavid.serverless.extension.ext.gcp.token.machine.TokenMachine;
import io.quarkus.builder.item.MultiBuildItem;
import io.quarkus.runtime.RuntimeValue;

public final class TokenMachineBuildItem extends MultiBuildItem {
    private final RuntimeValue<TokenMachine> tokenMachine;

    private final String name;

    public TokenMachineBuildItem(RuntimeValue<TokenMachine> tokenMachine, String name) {
        this.tokenMachine = tokenMachine;
        this.name = name;
    }

    public RuntimeValue<TokenMachine> getClient() {
        return tokenMachine;
    }

    public String getName() {
        return name;
    }
}