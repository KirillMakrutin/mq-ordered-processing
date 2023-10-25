package com.example.consumer.handler;

import com.example.consumer.entity.Foo;
import com.example.consumer.repo.FooRepository;
import com.example.consumer.service.LockingService;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.springframework.integration.handler.GenericHandler;
import org.springframework.messaging.MessageHeaders;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Slf4j
@AllArgsConstructor
@Component
public class FooHandler implements GenericHandler<Integer> {
    private static final String PROPERTY_CODE_HEADER = "propertyCode";
    private static final Random NEXT = new Random();
    private final FooRepository repository;
    private final LockingService lockingService;

    @SneakyThrows
    @Override
    public Integer handle(Integer payload, MessageHeaders headers) {
        final String propertyCode = String.valueOf(headers.get(PROPERTY_CODE_HEADER));
        final Foo entity = new Foo(payload, propertyCode);
        final RLock lock = lockingService.getLock(propertyCode);

        try {
            if(!lock.tryLock(10_000, 10_000, TimeUnit.MILLISECONDS)){
                log.warn(">>> Lock didn't acquired");
            }

            final Optional<Foo> byPropertyCode = repository.findFirstByPropertyCodeOrderByNumDesc(propertyCode);

            if (byPropertyCode.isEmpty() || byPropertyCode.get().getNum() <= entity.getNum()) {
                save(payload, entity);
            } else {
                log.warn(">>> Get outdated {}", entity);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();

            log.error(">>> Failed to save {}", entity, e);
        } finally {
            lock.unlock();
        }


        return payload;
    }

    private void save(Integer payload, Foo entity) throws InterruptedException {
        TimeUnit.MILLISECONDS.sleep(Math.abs(NEXT.nextInt(3_000)));

//        log.info(">>> Consumed {}", payload);

        repository.save(entity);
    }
}
