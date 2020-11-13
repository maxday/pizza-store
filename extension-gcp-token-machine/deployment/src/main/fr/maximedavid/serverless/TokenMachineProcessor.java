package fr.maximedavid.serverless;
import io.quarkus.arc.deployment.AdditionalBeanBuildItem;
import io.quarkus.arc.deployment.BeanContainerBuildItem;
import io.quarkus.arc.deployment.BeanContainerListenerBuildItem;
import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.annotations.ExecutionTime;
import io.quarkus.deployment.annotations.Record;
import io.quarkus.deployment.builditem.FeatureBuildItem;
import io.quarkus.vertx.deployment.VertxBuildItem;
import org.acme.quarkus.greeting.*;
import org.jboss.logging.Logger;

public class TokenMachineProcessor {

    private static final Logger LOG = Logger.getLogger(TokenMachineProcessor.class);
    private TokenMachineConfig tokenMachineConfig;

    @Record(ExecutionTime.STATIC_INIT)
    @BuildStep
    void build(BuildProducer<AdditionalBeanBuildItem> additionalBeanProducer,
               BuildProducer<FeatureBuildItem> featureProducer,
               TokenMachineRecorder recorder,
               BuildProducer<BeanContainerListenerBuildItem> containerListenerProducer) {
        featureProducer.produce(new FeatureBuildItem("tokenMachine"));
        AdditionalBeanBuildItem unremovableProducer = AdditionalBeanBuildItem.unremovableOf(TokenMachineProducer.class);
        additionalBeanProducer.produce(unremovableProducer);
        containerListenerProducer.produce(
                new BeanContainerListenerBuildItem(recorder.setTokenMachineConfig(tokenMachineConfig)));
    }

    @Record(ExecutionTime.RUNTIME_INIT)
    @BuildStep
    void setAccessToken(TokenMachineRecorder recorder, BeanContainerBuildItem beanContainer, VertxBuildItem vertx)  {
        LOG.info("Setting the access token");
        recorder.setAccessToken(vertx.getVertx(), beanContainer.getValue());
    }


}