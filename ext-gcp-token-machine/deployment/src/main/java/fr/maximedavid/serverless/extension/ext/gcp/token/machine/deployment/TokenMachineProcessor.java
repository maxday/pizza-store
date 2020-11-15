package fr.maximedavid.serverless.extension.ext.gcp.token.machine.deployment;

import com.google.inject.Singleton;
import fr.maximedavid.serverless.extension.ext.gcp.token.machine.TokenMachine;
import fr.maximedavid.serverless.extension.ext.gcp.token.machine.TokenMachineConfig;
import fr.maximedavid.serverless.extension.ext.gcp.token.machine.TokenMachineRecorder;
import io.quarkus.arc.deployment.*;
import io.quarkus.deployment.annotations.*;
import org.jboss.logging.Logger;

import static io.quarkus.deployment.annotations.ExecutionTime.RUNTIME_INIT;
import static io.quarkus.deployment.annotations.ExecutionTime.STATIC_INIT;

public class TokenMachineProcessor {

    private static final Logger LOG = Logger.getLogger(TokenMachineProcessor.class);

    @BuildStep
    @Record(STATIC_INIT)
    SyntheticBeanBuildItem syntheticBean(TokenMachineRecorder recorder) {
        return SyntheticBeanBuildItem.configure(TokenMachine.class).scope(Singleton.class)
                .runtimeValue(recorder.getTokenMachine())
                .done();
    }

    @Record(RUNTIME_INIT)
    @BuildStep
    void generateClientBeans(TokenMachineRecorder recorder,
                             TokenMachineConfig tokenMachineConfig
                             ) {
        recorder.setConfig(tokenMachineConfig);
    }

    /*
    private SyntheticBeanBuildItem createBlockingSyntheticBean(TokenMachineRecorder recorder,
                                                               TokenMachineConfig tokenMachineConfig) {

        SyntheticBeanBuildItem.ExtendedBeanConfigurator configurator = SyntheticBeanBuildItem
                .configure(TokenMachine.class)
                .scope(Singleton.class)
                .supplier(recorder.tokenMachineSupplier(tokenMachineConfig))
                .unremovable()
                .setRuntimeInit();
        return  configurator.done();
    }

    @Record(RUNTIME_INIT)
    @BuildStep
    void generateClientBeans(TokenMachineRecorder recorder,
                             TokenMachineConfig tokenMachineConfig,
                             BuildProducer<AdditionalBeanBuildItem> additionalBeans,
                             BuildProducer<SyntheticBeanBuildItem> syntheticBeanBuildItemBuildProducer) {

        syntheticBeanBuildItemBuildProducer.produce(createBlockingSyntheticBean(recorder, tokenMachineConfig));
    }

    @BuildStep
    @Record(value = RUNTIME_INIT, optional = true)
    TokenMachineBuildItem tokenMachine(TokenMachineRecorder recorder,
                                       BeanContainerBuildItem beanContainer) {
        return new TokenMachineBuildItem(recorder.getTokenMachine("tokenMachine"), "tokenMachine");
    }

    @BuildStep
    @Weak
    TokenMachineUnremovableBuildItem unremovable(@SuppressWarnings("unused") BuildProducer<TokenMachineBuildItem> producer) {
        return new TokenMachineUnremovableBuildItem();
    }




    /*
    @BuildStep
    @Record(ExecutionTime.RUNTIME_INIT)
    void build(BuildProducer<AdditionalBeanBuildItem> additionalBeanProducer,
               BuildProducer<FeatureBuildItem> featureProducer,
               TokenMachineRecorder recorder,
               TokenMachineConfig runtimeConfig) {
        featureProducer.produce(new FeatureBuildItem("tokenMachine"));
        recorder.configureRuntimeConfig(runtimeConfig);
        AdditionalBeanBuildItem unremovableProducer = AdditionalBeanBuildItem.unremovableOf(TokenMachineProducer.class);
        additionalBeanProducer.produce(unremovableProducer);
    }


    @Record(ExecutionTime.RUNTIME_INIT)
    @BuildStep
    void setAccessToken(TokenMachineRecorder recorder, BeanContainerBuildItem beanContainer, VertxBuildItem vertx)  {
        LOG.info("Setting the access token");
        recorder.setAccessToken(vertx.getVertx(), beanContainer.getValue());
    }


*/

}