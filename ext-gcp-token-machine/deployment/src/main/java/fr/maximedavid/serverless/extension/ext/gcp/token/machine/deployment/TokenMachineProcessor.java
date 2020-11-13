package fr.maximedavid.serverless.extension.ext.gcp.token.machine.deployment;

import fr.maximedavid.serverless.extension.ext.gcp.token.machine.TokenMachineConfig;
import fr.maximedavid.serverless.extension.ext.gcp.token.machine.TokenMachineProducer;
import fr.maximedavid.serverless.extension.ext.gcp.token.machine.TokenMachineRecorder;
import io.quarkus.arc.deployment.AdditionalBeanBuildItem;
import io.quarkus.arc.deployment.BeanContainerBuildItem;
import io.quarkus.arc.deployment.BeanContainerListenerBuildItem;
import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.annotations.ExecutionTime;
import io.quarkus.deployment.annotations.Record;
import io.quarkus.deployment.builditem.FeatureBuildItem;
import io.quarkus.vertx.deployment.VertxBuildItem;
import org.eclipse.microprofile.config.ConfigProvider;
import org.jboss.logging.Logger;

public class TokenMachineProcessor {

    private static final Logger LOG = Logger.getLogger(TokenMachineProcessor.class);


    @Record(ExecutionTime.STATIC_INIT)
    @BuildStep
    void build(BuildProducer<AdditionalBeanBuildItem> additionalBeanProducer,
               BuildProducer<FeatureBuildItem> featureProducer,
               TokenMachineRecorder recorder,
               BuildProducer<BeanContainerListenerBuildItem> containerListenerProducer) {
        featureProducer.produce(new FeatureBuildItem("tokenMachine"));
        AdditionalBeanBuildItem unremovableProducer = AdditionalBeanBuildItem.unremovableOf(TokenMachineProducer.class);
        additionalBeanProducer.produce(unremovableProducer);
        String config = ConfigProvider.getConfig().getOptionalValue("quarkus.token-machine.service-account", String.class).orElse("toto");
        containerListenerProducer.produce(
                new BeanContainerListenerBuildItem(recorder.setTokenMachineConfig(config)));
    }

    @Record(ExecutionTime.RUNTIME_INIT)
    @BuildStep
    void setAccessToken(TokenMachineRecorder recorder, BeanContainerBuildItem beanContainer, VertxBuildItem vertx)  {
        LOG.info("Setting the access token");
        recorder.setAccessToken(vertx.getVertx(), beanContainer.getValue());
    }


}