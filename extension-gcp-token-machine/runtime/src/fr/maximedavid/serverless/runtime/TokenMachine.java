package fr.maximedavid.serverless.runtime;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class TokenMachine {

    protected String audience;
    protected Integer expiryLength;
    protected String apiHost;
    protected String apiPath;
    protected String scope;
    protected String serviceAccount;

    private String accessToken;

    public TokenMachine(String audience,
                        Integer expiryLength, String apiHost,
                        String apiPath, String scope, String serviceAccount) {
        this.audience = audience;
        this.expiryLength = expiryLength;
        this.apiHost = apiHost;
        this.apiPath = apiPath;
        this.scope = scope;
        this.serviceAccount = serviceAccount;
    }

    public void setAccessToken(String accesToken) {
        this.accessToken = accesToken;
    }

    public String getAccessToken() {
        return accessToken;
    }
}