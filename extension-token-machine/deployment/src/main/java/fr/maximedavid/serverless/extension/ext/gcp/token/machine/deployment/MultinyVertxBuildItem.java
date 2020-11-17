package fr.maximedavid.serverless.extension.ext.gcp.token.machine.deployment;

import io.quarkus.builder.item.SimpleBuildItem;
import io.vertx.mutiny.core.Vertx;

import java.util.function.Supplier;

public class MultinyVertxBuildItem extends SimpleBuildItem {
    private final Supplier<Vertx> vertx;

    public MultinyVertxBuildItem(Supplier<Vertx> vertx) {
        this.vertx = vertx;
    }

    public Supplier<Vertx> getVertx() {
        return this.vertx;
    }
}