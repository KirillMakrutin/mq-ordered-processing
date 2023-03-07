package com.kmakrutin.producer.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Slf4j
@AllArgsConstructor
@Component
@Primary
@Profile("async")
public class PipeAsyncSender implements Sender {
    private static final String PIPE_CHANNEL = "pipeline";

    private final RabbitTemplate rabbitTemplate;

    @Override
    public void send(Integer num) {
        rabbitTemplate.convertAndSend(PIPE_CHANNEL, num);
        log.info(">>> Produced {}", num);
    }
}
