package fr.maximedavid.serverless;

import io.quarkus.arc.config.ConfigProperties;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@ConfigProperties(prefix = "gcp")
public interface GCPConfiguration { ;
    String getPubsubTopicPublishUrl();
    String getPubsubManagerTopicPublishUrl();

    @ConfigProperty(defaultValue = "pubsub.googleapis.com")
    String getPubsubApiHost();

    @ConfigProperty(defaultValue = "443")
    int getPubsubApiPort();
}