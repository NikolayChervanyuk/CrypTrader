package com.nch.cryptrader.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class UserAssetModel {
    private String symbol;
    private double totalProfit;
    private double quantity;
    private double currentPrice;
}
