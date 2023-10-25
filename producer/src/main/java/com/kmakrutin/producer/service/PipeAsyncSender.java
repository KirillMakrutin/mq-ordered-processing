package com.kmakrutin.producer.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Slf4j
@AllArgsConstructor
@Component
@Primary
public class PipeAsyncSender implements Sender {
    private static final String PIPE_CHANNEL = "pipeline";

    private final RabbitTemplate rabbitTemplate;

    @Override
    public void send(Integer num, String propertyCode) {
        rabbitTemplate.convertAndSend(PIPE_CHANNEL, num, message -> {
            message.getMessageProperties().setHeader(Sender.PROPERTY_CODE_HEADER, propertyCode);
            return message;
        });
        log.info(">>> Produced {}", num);
    }
}
