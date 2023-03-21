package com.kmakrutin.producer.config;

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
                    sender.send(p, getPropertyCode(p));

                    return null;
                })
                .get();
    }

    private static String getPropertyCode(Integer p) {
        switch (p % 10) {
            case 0:
                return "ABC000";
            case 1:
                return "ABC001";
            case 2:
                return "ABC002";
            case 3:
                return "ABC003";
            case 4:
                return "ABC004";
            case 5:
                return "ABC005";
            case 6:
                return "ABC006";
            case 7:
                return "ABC007";
            case 8:
                return "ABC008";
            case 9:
                return "ABC009";
            default:
                return "UNKNOWN";
        }
    }
}
