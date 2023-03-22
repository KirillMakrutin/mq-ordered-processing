package com.example.consumer.config;

import com.example.consumer.handler.FooHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.amqp.dsl.Amqp;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;


@Slf4j
@EnableIntegration
@Configuration
public class IntegrationConfig {
    private static final String PIPE_QUEUE = "pipeline";
    private static final int NUM_THREADS = 5;

    @Bean
    public Queue pipeQueue() {
        return new Queue(PIPE_QUEUE, true, false, false);
    }

    @Bean
    IntegrationFlow pipeFlow(FooHandler handler,
                             ConnectionFactory connectionFactory) {
        return IntegrationFlows.from(
                        Amqp.inboundGateway(connectionFactory, PIPE_QUEUE)
                                .defaultReplyTo("nullChannel")
                                .configureContainer(c -> c.concurrentConsumers(NUM_THREADS).prefetchCount(1)))
                .handle(handler)
                .get();
    }

}
