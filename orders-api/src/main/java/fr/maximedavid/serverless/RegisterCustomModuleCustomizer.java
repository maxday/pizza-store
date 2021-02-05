package fr.maximedavid.serverless;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.DeserializationProblemHandler;
import io.quarkus.jackson.ObjectMapperCustomizer;
import org.jboss.logging.Logger;

import javax.inject.Singleton;
import java.io.IOException;

@Singleton
public class RegisterCustomModuleCustomizer implements ObjectMapperCustomizer {

    private static final Logger LOG = Logger.getLogger(RegisterCustomModuleCustomizer.class);


    public void customize(ObjectMapper mapper) {
        mapper.addHandler(new DeserializationProblemHandler() {

            @Override
            public boolean handleUnknownProperty(DeserializationContext ctxt,
                                                 JsonParser jp, JsonDeserializer<?> deserializer,
                                                 Object beanOrClass, String propertyName)
                    throws IOException, JsonProcessingException {

                String unknownField = String.format("Ignoring unknown property %s while deserializing %s", propertyName, beanOrClass);
                LOG.info(getClass().getSimpleName() +  unknownField);
                return true;
            }
        });
        }
}
