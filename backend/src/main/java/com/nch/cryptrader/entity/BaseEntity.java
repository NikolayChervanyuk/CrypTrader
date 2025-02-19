package com.nch.cryptrader.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.Instant;
import java.util.UUID;

@MappedSuperclass
@Getter
@Setter
public class BaseEntity {

    @Id
    @org.springframework.data.annotation.Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false)
    private UUID id = UUID.randomUUID();

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private Instant creationDate;

    @LastModifiedDate
    @Column(nullable = false)
    private Instant lastModifiedDate;

    @org.springframework.data.annotation.Version
    private Integer version;
}
