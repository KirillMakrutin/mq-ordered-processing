package com.example.consumer.config;

import com.example.consumer.handler.FooHandler;
import com.rabbitmq.http.client.Client;
import com.rabbitmq.http.client.domain.ShovelDetails;
import com.rabbitmq.http.client.domain.ShovelInfo;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.amqp.dsl.Amqp;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.UUID;


@Slf4j
@EnableIntegration
@Configuration
public class IntegrationConfig {
    private static final String PIPE_EXCHANGE = "pipeline-ex";
    private static final String PIPE_QUEUE = "pipeline-" + UUID.randomUUID();
    private static final int NUM_THREADS = 5;

    private static final String GROUP_HEADER = "groupId";

    @Bean
    public Exchange pipeExchange() {

        return new CustomExchange(PIPE_EXCHANGE, "x-consistent-hash", true, false, Map.of("hash-header", GROUP_HEADER));
    }

    @Bean
    public Binding pipeChannelBinding(Exchange pipeExchange, Queue pipeQueue) {
        return BindingBuilder
                .bind(pipeQueue)
                .to(pipeExchange)
                .with("1")
                .noargs();
    }

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
                                .configureContainer(c -> c.concurrentConsumers(NUM_THREADS)
                                        .prefetchCount(1)))
                .handle(handler)
                .get();
    }

    @SneakyThrows
    @Bean
    public Client rabbitManagementClient() {
        return new Client(new URL("http://localhost:15672/api"), "guest", "guest");
    }

    @Bean
    public DisposableBean rerouteMessages(AmqpAdmin amqpAdmin,
                                          Binding binding,
                                          Client rabbitClient,
                                          ThreadPoolTaskExecutor threadPoolTaskExecutor) {
        return () -> {
            amqpAdmin.removeBinding(binding);

            ShovelDetails details = new ShovelDetails();
            List<String> uris = List.of("amqp://");
            details.setSourceURIs(uris);
            details.setSourceQueue(PIPE_QUEUE);
            details.setDestinationURIs(uris);
            details.setDestinationExchange(PIPE_EXCHANGE);
            details.setSourceDeleteAfter("queue-length");

            try {
                String name = "moveBackToExchange" + UUID.randomUUID();
                rabbitClient.declareShovel("/", new ShovelInfo(name, details));
                List<ShovelInfo> shovelInfos;
                do {
                    shovelInfos = rabbitClient.getShovels("/");
                } while (shovelInfos.stream().noneMatch(shovelInfo -> name.equals(shovelInfo.getName())));
                rabbitClient.getShovels("/");

                amqpAdmin.deleteQueue(PIPE_QUEUE, false, false);
            } catch (Exception e) {
                log.error("Failed to move messages from queue {} back to exchange {}", PIPE_QUEUE, PIPE_EXCHANGE, e);
            }

            threadPoolTaskExecutor.shutdown();
        };
    }

}
