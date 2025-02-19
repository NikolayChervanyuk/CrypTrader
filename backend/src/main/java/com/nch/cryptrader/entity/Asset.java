package com.nch.cryptrader.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(indexes = @Index(columnList = "symbol"))
public class Asset extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String symbol;
}
