package com.nch.cryptrader.controller.user.reqresbody;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class UserAssetResponse {
    private String symbol;
    private double totalProfit;
    private double quantity;
    private double currentPrice;
}
