package fr.maximedavid.serverless;

import io.quarkus.arc.config.ConfigProperties;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@ConfigProperties(prefix = "gcp")
public interface GCPConfiguration { ;
    String getProjectName();
    String getDatabaseName();

    @ConfigProperty(defaultValue = "firestore.googleapis.com")
    String getFirestoreApiHost();

    @ConfigProperty(defaultValue = "/v1/projects/")
    String getApiPath();
}