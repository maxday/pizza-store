package fr.maximedavid.serverless;

import io.quarkus.arc.config.ConfigProperties;

@ConfigProperties(prefix = "gcp")
public interface GCPConfiguration {
    String getApiHost();
    String getPubsubTopicPublishUrl();
    String getServiceAccount();
}
