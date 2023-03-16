package com.example.consumer.entity;

import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;


@Data
@EntityListeners(AuditingEntityListener.class)
@Entity
public class Foo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long id;

    @Column
    private Integer num;

    @Column
    private String groupId;

    @CreatedDate
    @Column
    private LocalDateTime created;

    @LastModifiedDate
    @Column
    private LocalDateTime updated;

    public Foo() {
    }

    public Foo(int num, String groupId) {
        this.num = num;
        this.groupId = groupId;
    }
}
