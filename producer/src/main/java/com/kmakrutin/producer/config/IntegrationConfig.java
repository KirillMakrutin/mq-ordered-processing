package com.kmakrutin.producer.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.amqp.dsl.Amqp;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.dsl.MessageChannels;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.GenericMessage;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@EnableIntegration
@Configuration
public class IntegrationConfig {
    private static final String SENDER_CHANNEL = "sender";
    private static final String PIPE_EXCHANGE = "pipeline-ex";
    private static final String GROUP_HEADER = "groupId";

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
    IntegrationFlow senderFlow(RabbitTemplate rabbitTemplate) {
        return IntegrationFlows.from(SENDER_CHANNEL)
                .enrichHeaders(h -> h.headerFunction(GROUP_HEADER, message -> {
                    switch ((int) message.getPayload() % 10) {
                        case 0:
                            return "group0";
                        case 1:
                            return "group1";
                        case 2:
                            return "group2";
                        case 3:
                            return "group3";
                        case 4:
                            return "group4";
                        case 5:
                            return "group5";
                        case 6:
                            return "group6";
                        case 7:
                            return "group7";
                        case 8:
                            return "group8";
                        case 9:
                            return "group9";
                        default:
                            return "common";
                    }
                }))
                .log(Message::getPayload)
                .handle(Amqp
                        .outboundAdapter(rabbitTemplate)
                        .exchangeName(PIPE_EXCHANGE)
                        .routingKeyExpression("headers.groupId"))
                .get();
    }
}
