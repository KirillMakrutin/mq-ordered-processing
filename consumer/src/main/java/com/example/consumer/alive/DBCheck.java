package com.example.consumer.alive;

import com.example.consumer.repo.FooRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Slf4j
@AllArgsConstructor
@Component
public class DBCheck implements CommandLineRunner {
    private final FooRepository repository;

    @Override
    public void run(String... args) {
        repository.deleteAll();
    }
}
