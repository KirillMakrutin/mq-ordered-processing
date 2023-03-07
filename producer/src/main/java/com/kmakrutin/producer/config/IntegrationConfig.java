package com.kmakrutin.producer.config;

import com.kmakrutin.producer.service.PipeSyncSender;
import com.kmakrutin.producer.service.Sender;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.dsl.MessageChannels;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.GenericMessage;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@EnableIntegration
@Configuration
public class IntegrationConfig {
    private static final String SENDER_CHANNEL = "sender";

    private final AtomicInteger numProducer = new AtomicInteger();

    @Bean
    MessageChannel sender() {
        return MessageChannels.direct(SENDER_CHANNEL).get();
    }

    @Bean
    IntegrationFlow sequenceGenerator() {
        return IntegrationFlows.from(
                        () -> new GenericMessage<>(numProducer.incrementAndGet()),
                        sourceSpec -> sourceSpec.poller(poller -> poller.fixedRate(100, TimeUnit.MILLISECONDS)))
                .channel(SENDER_CHANNEL)
                .get();
    }

    @Bean
    IntegrationFlow senderFlow(Sender sender) {
        return IntegrationFlows.from(SENDER_CHANNEL)
                .<Integer>handle((p, h) -> {
                    sender.send(p);

                    return null;
                })
                .get();
    }
}
