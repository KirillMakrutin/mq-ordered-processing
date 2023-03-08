package com.example.consumer.handler;

import com.example.consumer.entity.Foo;
import com.example.consumer.repo.FooRepository;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.integration.handler.GenericHandler;
import org.springframework.messaging.MessageHeaders;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.*;

@Slf4j
@Component
public class FooHandler implements GenericHandler<Integer> {
    private static final String GROUP_HEADER = "groupId";
    private static final Random NEXT = new Random();

    private final Map<String, BlockingQueue<Integer>> messageBuffers = new ConcurrentHashMap<>();
    private final FooRepository repository;
    private final ThreadPoolTaskExecutor threadPoolTaskExecutor;

    public FooHandler(FooRepository repository,
                      ThreadPoolTaskExecutor threadPoolTaskExecutor) {
        this.repository = repository;
        this.threadPoolTaskExecutor = threadPoolTaskExecutor;
    }

    @SneakyThrows
    @Override
    public Integer handle(Integer num, MessageHeaders headers) {


        String groupId = extractGroupIdFromMessage(headers);
        if (groupId != null) {
            BlockingQueue<Integer> buffer = messageBuffers.computeIfAbsent(groupId, k -> new LinkedBlockingQueue<>(1));
            buffer.put(num);

            threadPoolTaskExecutor.execute(() -> {
                try {
                    TimeUnit.MILLISECONDS.sleep(Math.abs(NEXT.nextInt(1000)));

                    log.info(">>> Consumed {}", num);

                    repository.save(new Foo(num));


                    buffer.take();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            });
        } else {
            log.info(">>> Consumed {}", num);

            repository.save(new Foo(num));
        }

        return num;
    }

    private String extractGroupIdFromMessage(MessageHeaders headers) {
        // extract the group ID header from the message
        return Optional.ofNullable(headers.get(GROUP_HEADER)).map(String::valueOf).orElse(null);
    }
}
