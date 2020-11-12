package fr.maximedavid.serverless;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped

public class TokenService {

    private String accessToken;

    public TokenService() {

    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getAccessToken() {
        return this.accessToken;
    }
}
