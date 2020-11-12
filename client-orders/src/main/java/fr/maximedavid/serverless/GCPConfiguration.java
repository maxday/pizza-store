package fr.maximedavid.serverless;

import io.quarkus.arc.config.ConfigProperties;

@ConfigProperties(prefix = "gcp")
public interface GCPConfiguration {
    String getApiHost();
    String getApiToken();
    String getPubsubTopicPublishUrl();
    String getServiceAccount();
}
