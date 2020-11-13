package fr.maximedavid.extension.runtime;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.algorithms.Algorithm;
import com.google.auth.oauth2.ServiceAccountCredentials;
import io.quarkus.arc.runtime.BeanContainer;
import io.quarkus.arc.runtime.BeanContainerListener;
import io.quarkus.runtime.RuntimeValue;
import io.quarkus.runtime.annotations.Recorder;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.client.WebClientOptions;
import io.vertx.ext.web.multipart.MultipartForm;
import io.vertx.core.Vertx;
import org.jboss.logging.Logger;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.interfaces.RSAPrivateKey;
import java.util.Base64;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@Recorder
public class TokenMachineRecorder {

    private static final Logger LOG = Logger.getLogger(TokenMachineRecorder.class);

    public BeanContainerListener setTokenMachineConfig(TokenMachineConfig tokenMachineConfig) {
        return beanContainer -> {
            TokenMachineProducer producer = beanContainer.instance(TokenMachineProducer.class);
            producer.setTokenMachineConfig(tokenMachineConfig);
        };
    }

    private String generateJwt(TokenMachine tokenMachine)
            throws IOException {

        Date now = new Date();
        Date expTime = new Date(System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(tokenMachine.expiryLength));

        byte[] res = Base64.getDecoder().decode(tokenMachine.serviceAccount);
        InputStream stream = new ByteArrayInputStream(res);

        ServiceAccountCredentials cred = ServiceAccountCredentials.fromStream(stream);
        RSAPrivateKey key = (RSAPrivateKey) cred.getPrivateKey();

        Algorithm algorithm = Algorithm.RSA256(null, key);

        JWTCreator.Builder token = JWT.create()
                .withIssuedAt(now)
                .withExpiresAt(expTime)
                .withClaim("scope", tokenMachine.scope)
                .withIssuer(cred.getClientEmail())
                .withAudience(tokenMachine.audience)
                .withSubject(cred.getClientEmail())
                .withClaim("email", cred.getClientEmail());

        return token.sign(algorithm);
    }

    public void setAccessToken(RuntimeValue<Vertx> vertx, BeanContainer container) {
        TokenMachine tokenMachine = container.instance(TokenMachine.class);

        try {
            String jwt = generateJwt(tokenMachine);
            WebClient webClient =  WebClient.create(vertx.getValue(),
                    new WebClientOptions()
                            .setDefaultHost(tokenMachine.apiHost)
                            .setDefaultPort(443)
                            .setSsl(true));
            webClient
                    .post(tokenMachine.apiPath)
                    .sendMultipartForm(
                            MultipartForm.create()
                                    .attribute("grant_type", "urn:ietf:params:oauth:grant-type:jwt-bearer")
                                    .attribute("assertion", jwt),
                            (result -> {
                                if(result.succeeded()) {
                                    JsonObject jsonResult = result.result().bodyAsJsonObject();
                                    String accessToken = jsonResult.getString("access_token");
                                    tokenMachine.setAccessToken(accessToken);
                                    LOG.info("Successfully set the access_token");
                                } else {
                                    LOG.error("Error while getting an access_token");
                                }
                            })
                    );
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}