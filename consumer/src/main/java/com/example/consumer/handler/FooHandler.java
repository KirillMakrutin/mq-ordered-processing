package com.example.consumer.handler;

import com.example.consumer.entity.Foo;
import com.example.consumer.repo.FooRepository;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.integration.handler.GenericHandler;
import org.springframework.messaging.MessageHeaders;
import org.springframework.stereotype.Component;

import java.util.Random;
import java.util.concurrent.TimeUnit;

@Slf4j
@AllArgsConstructor
@Component
public class FooHandler implements GenericHandler<Integer> {
    private static final String PROPERTY_CODE_HEADER = "propertyCode";
    private static final Random NEXT = new Random();
    private final FooRepository repository;

    @SneakyThrows
    @Override
    public Integer handle(Integer payload, MessageHeaders headers) {
        TimeUnit.MILLISECONDS.sleep(Math.abs(NEXT.nextInt(10_000)));

        log.info(">>> Consumed {}", payload);

        repository.save(new Foo(payload, String.valueOf(headers.get(PROPERTY_CODE_HEADER))));

        return payload;
    }
}
