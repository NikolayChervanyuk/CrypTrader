package com.nch.cryptrader.model;

import com.nch.cryptrader.util.TradeType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@AllArgsConstructor
public class UserHistoryTradeModel {
    private TradeType tradeType;
    private String symbol;
    private double quantity;
    private double price;
    private Instant date;
}
