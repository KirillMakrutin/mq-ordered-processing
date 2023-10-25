package com.example.consumer.repo;

import com.example.consumer.entity.Foo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FooRepository extends JpaRepository<Foo, Long> {
    Optional<Foo> findFirstByPropertyCodeOrderByNumDesc(String propertyCode);
}
