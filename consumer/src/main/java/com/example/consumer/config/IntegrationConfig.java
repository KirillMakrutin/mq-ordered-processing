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
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;


@Slf4j
@EnableIntegration
@Configuration
public class IntegrationConfig {
    private static final String PIPE_QUEUE = "pipeline";

    @Bean
    public Queue pipeQueue() {
        return new Queue(PIPE_QUEUE, true, false, false);
    }

    @Bean
    IntegrationFlow pipeFlow(FooHandler handler, ConnectionFactory connectionFactory, ThreadPoolTaskExecutor threadPoolTaskExecutor) {
        return IntegrationFlows.from(
                        Amqp.inboundGateway(connectionFactory, PIPE_QUEUE)
                                .defaultReplyTo("nullChannel")
                                .configureContainer(c -> c
                                        .concurrentConsumers(5)
                                        .prefetchCount(5)
                                        .taskExecutor(threadPoolTaskExecutor)))
                .handle(handler)
                .get();
    }

}
