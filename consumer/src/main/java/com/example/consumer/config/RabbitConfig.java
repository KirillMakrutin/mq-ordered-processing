package com.example.consumer.config;

import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
public class RabbitConfig {

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory rabbitConnectionFactory) {
        return new RabbitTemplate(rabbitConnectionFactory);
    }

    @Bean
    public ConnectionFactory connectionFactory() {
        com.rabbitmq.client.ConnectionFactory rabbitConnectionFactory = new com.rabbitmq.client.ConnectionFactory();
        rabbitConnectionFactory.setAutomaticRecoveryEnabled(false);

        CachingConnectionFactory connectionFactory = new CachingConnectionFactory(rabbitConnectionFactory);
        connectionFactory.setUsername("guest");
        connectionFactory.setPassword("guest");
        connectionFactory.setVirtualHost("/");
        connectionFactory.setPublisherReturns(false);

        return connectionFactory;
    }

    @Bean
    public AmqpAdmin amqpAdmin(ConnectionFactory connectionFactory) {
        return new RabbitAdmin(connectionFactory);
    }

    @Bean
    public ThreadPoolTaskExecutor threadPoolTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setThreadNamePrefix("ConsumerExecutor-");
        executor.setCorePoolSize(5);
        executor.setKeepAliveSeconds(10);
        executor.setWaitForTasksToCompleteOnShutdown(true);

        return executor;
    }

}
