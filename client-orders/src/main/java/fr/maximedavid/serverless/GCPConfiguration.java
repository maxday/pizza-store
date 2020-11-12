package fr.maximedavid.serverless;

import io.quarkus.arc.config.ConfigProperties;

@ConfigProperties(prefix = "gcp")
public interface GCPConfiguration {
    String getServiceEmail();
    String getAudience();
    String getApiHost();
    String getApiTokenPath();
    Integer getTokenExpiryLength();
    String getTokenScope();
    String getPubsubApiHost();
    String getPubsubTopicPublishUrl();
    String getServiceAccount();
}