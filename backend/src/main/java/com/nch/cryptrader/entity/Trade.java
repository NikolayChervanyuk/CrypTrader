package com.nch.cryptrader.entity;

import com.nch.cryptrader.util.TradeType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(indexes = @Index(columnList = "userId"))
public class Trade extends BaseEntity {
    @Column(nullable = false)
    private UUID userId;

    @Column(nullable = false)
    private String symbol;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TradeType tradeType;

    @Column(nullable = false, precision = 10, scale = 8)
    private double quantity;

    @Column(nullable = false, precision = 18, scale = 2)
    private BigDecimal totalPrice;
}
