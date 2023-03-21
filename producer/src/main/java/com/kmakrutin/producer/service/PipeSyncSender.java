package com.kmakrutin.producer.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Slf4j
@Component
public class PipeSyncSender implements Sender {
    private static final String PIPE_CHANNEL = "pipeline";

    private final RabbitTemplate rabbitTemplate;

    @Override
    public void send(Integer num, String propertyCode) {
        Object response = rabbitTemplate.convertSendAndReceive(PIPE_CHANNEL, num, message -> {
            message.getMessageProperties().setHeader(Sender.PROPERTY_CODE_HEADER, propertyCode);
            return message;
        });
        log.info(">>> Produced {} and got a response back: {}", num, response);
    }
}
