package fr.maximedavid.serverless.extension.ext.gcp.token.machine;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.algorithms.Algorithm;
import com.google.auth.oauth2.ServiceAccountCredentials;

import io.smallrye.mutiny.Uni;

import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClientOptions;
import io.vertx.mutiny.core.Vertx;
import io.vertx.mutiny.ext.web.client.WebClient;

import io.vertx.mutiny.ext.web.multipart.MultipartForm;
import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.security.interfaces.RSAPrivateKey;
import java.util.Base64;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@ApplicationScoped
public class TokenMachine {

    private final Long GRACE_PERIOD_IN_MILLIS = 5 * 60 * 1000L;

    protected String audience;
    protected Integer expiryLength;
    public String apiHost;
    protected String apiPath;
    protected String scope;
    protected String serviceAccount;
    private String accessToken;
    private Date expTime;

    private static final Logger LOG = Logger.getLogger(TokenMachine.class);

    public TokenMachine(TokenMachineConfig config) {
        this.audience = config.audience;
        this.expiryLength = config.expiryLength;
        this.apiHost = config.apiHost;
        this.apiPath = config.apiPath;
        this.scope = config.scope;
        this.serviceAccount = config.serviceAccount;
    }

    private String generateJwt() {

        Date now = new Date();
        Date expTime = new Date(System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(this.expiryLength));

        try {
            byte[] res = Base64.getDecoder().decode(this.serviceAccount);
            InputStream stream = new ByteArrayInputStream(res);
            ServiceAccountCredentials cred = ServiceAccountCredentials.fromStream(stream);
            RSAPrivateKey key = (RSAPrivateKey) cred.getPrivateKey();

            Algorithm algorithm = Algorithm.RSA256(null, key);

            JWTCreator.Builder token = JWT.create()
                    .withIssuedAt(now)
                    .withExpiresAt(expTime)
                    .withClaim("scope", this.scope)
                    .withIssuer(cred.getClientEmail())
                    .withAudience(this.audience)
                    .withSubject(cred.getClientEmail())
                    .withClaim("email", cred.getClientEmail());

            return token.sign(algorithm);
        } catch (Exception e) {
            LOG.error("Impossible to generate the JWT");
            LOG.error(e);
            return "";
        }
    }

    public Uni<String> getAccessToken(Vertx vertx) {
        if(null == this.accessToken) {
            LOG.info("Token is null, fetching a new one");
            return this.setAccessToken(vertx).flatMap(res -> Uni.createFrom().item(this.accessToken));
        } else {
            LOG.info("Token is already set");
            if(new Date(System.currentTimeMillis()).after(this.expTime)) {
                LOG.info("Token is about or has expired, getting a new one");
                return this.setAccessToken(vertx).flatMap(res -> Uni.createFrom().item(this.accessToken));
            } else {
                return Uni.createFrom().item(this.accessToken);
            }
        }
    }

    private Uni<Boolean> setAccessToken(Vertx vertx) {
        String jwt = generateJwt();
        WebClient webclient = WebClient.create(vertx,
                new WebClientOptions()
                        .setDefaultHost(this.apiHost)
                        .setDefaultPort(443)
                        .setSsl(true));
        return webclient
                .post(this.apiPath)
                .sendMultipartForm(
                        MultipartForm.create()
                                .attribute("grant_type", "urn:ietf:params:oauth:grant-type:jwt-bearer")
                                .attribute("assertion", jwt))
                .onItem().transform(resp -> {
                    if (resp.statusCode() == 200) {
                        JsonObject jsonResult = resp.bodyAsJsonObject();
                        String accessToken = jsonResult.getString("access_token");
                        if (null != accessToken) {
                            this.accessToken = accessToken;
                            this.expTime = new Date(System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(this.expiryLength) - GRACE_PERIOD_IN_MILLIS) ;
                            LOG.info("Successfully set the access_token");
                            return true;
                        } else {
                            LOG.error("Error while getting an access_token");
                            return false;
                        }
                    } else {
                        LOG.error("error = " + resp.bodyAsString());
                        return false;
                    }
                });
    }
}