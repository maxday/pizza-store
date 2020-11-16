package fr.maximedavid.serverless.extension.ext.gcp.token.machine;

import io.quarkus.runtime.annotations.ConfigItem;
import io.quarkus.runtime.annotations.ConfigPhase;
import io.quarkus.runtime.annotations.ConfigRoot;

@ConfigRoot(name = "token-machine", phase = ConfigPhase.RUN_TIME)
public class TokenMachineConfig {

    /**
     * Service account (base64).
     */
    @ConfigItem
    public String serviceAccount;

    /**
     * Token audience.
     */
    @ConfigItem(defaultValue = "https://www.googleapis.com/oauth2/v4/token")
    public String audience;

    /**
     * Token scope.
     */
    @ConfigItem(defaultValue = "https://www.googleapis.com/auth/pubsub")
    public String scope;

    /**
     * Token lifetime in seconds.
     */
    @ConfigItem(defaultValue = "3600")
    public Integer expiryLength;

    /**
     * API host.
     */
    @ConfigItem(defaultValue = "www.googleapis.com")
    public String apiHost;

    /**
     * API path.
     */
    @ConfigItem(defaultValue = "/oauth2/v4/token")
    public String apiPath;

}