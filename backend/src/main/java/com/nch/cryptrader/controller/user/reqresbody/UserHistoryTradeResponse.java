package com.nch.cryptrader.controller.user.reqresbody;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@Builder
public class UserHistoryTradeResponse {
    public String tradeType;
    public String symbol;
    public double quantity;
    public double price;
    public Instant date;
}
