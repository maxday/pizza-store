package fr.maximedavid.serverless;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.algorithms.Algorithm;
import com.google.auth.oauth2.ServiceAccountCredentials;
import io.quarkus.runtime.ShutdownEvent;
import io.quarkus.runtime.StartupEvent;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClientOptions;
import io.vertx.ext.web.multipart.MultipartForm;
import io.vertx.mutiny.core.Vertx;
import io.vertx.mutiny.core.buffer.Buffer;
import io.vertx.mutiny.ext.web.client.HttpResponse;
import io.vertx.mutiny.ext.web.client.WebClient;
import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.interfaces.RSAPrivateKey;
import java.util.Base64;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@ApplicationScoped
public class AppLifecycleBean {

    private static final Logger LOGGER = Logger.getLogger("ListenerBean");

    @Inject
    GCPConfiguration configuration;

    @Inject
    Vertx vertx;

    @Inject
    TokenService tokenService;

    private WebClient webclient;

    void onStart(@Observes StartupEvent ev) {
        LOGGER.info("The application is starting..." + configuration.getServiceAccount());
        try {
            String jwt = generateJwt(configuration.getServiceEmail(),
                    configuration.getAudience(), configuration.getTokenExpiryLength());
            this.webclient = WebClient.create(vertx,
                    new WebClientOptions().setDefaultHost(configuration.getApiHost()).setDefaultPort(443).setSsl(true));
            HttpResponse<Buffer> res = this.webclient
                    .post(configuration.getApiTokenPath())
                    .sendMultipartForm(io.vertx.mutiny.ext.web.multipart.MultipartForm.newInstance(
                            MultipartForm.create()
                                    .attribute("grant_type", "urn:ietf:params:oauth:grant-type:jwt-bearer")
                                    .attribute("assertion", jwt)
                    ))
                    .await().indefinitely();
            String accesToken = new JsonObject(res.bodyAsString()).getString("access_token");
            System.out.println(accesToken);
            tokenService.setAccessToken(accesToken);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void onStop(@Observes ShutdownEvent ev) {
        LOGGER.info("The application is stopping...");
    }

    private String generateJwt(final String saEmail,
                               final String audience, final int expiryLength) throws IOException {

        Date now = new Date();
        Date expTime = new Date(System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(expiryLength));

        JWTCreator.Builder token = JWT.create()
                .withIssuedAt(now)
                .withExpiresAt(expTime)
                .withClaim("scope", configuration.getTokenScope())
                .withIssuer(saEmail)
                .withAudience(audience)
                .withSubject(saEmail)
                .withClaim("email", saEmail);

        byte[] res = Base64.getDecoder().decode(configuration.getServiceAccount());
        InputStream stream = new ByteArrayInputStream(res);

        ServiceAccountCredentials cred = ServiceAccountCredentials.fromStream(stream);
        RSAPrivateKey key = (RSAPrivateKey) cred.getPrivateKey();

        Algorithm algorithm = Algorithm.RSA256(null, key);
        return token.sign(algorithm);
    }

}