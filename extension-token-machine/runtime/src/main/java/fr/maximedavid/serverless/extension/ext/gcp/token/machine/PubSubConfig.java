package fr.maximedavid.serverless.extension.ext.gcp.token.machine;

import io.quarkus.runtime.annotations.ConfigItem;
import io.quarkus.runtime.annotations.ConfigPhase;
import io.quarkus.runtime.annotations.ConfigRoot;

@ConfigRoot(name = "pub-sub", phase = ConfigPhase.RUN_TIME)
public class PubSubConfig {

    /**
     * Pub sub host.
     */
    @ConfigItem(defaultValue = "pubsub.googleapis.com")
    public String host;

    /**
     * Pub sub port.
     */
    @ConfigItem(defaultValue = "443")
    public Integer port;

}