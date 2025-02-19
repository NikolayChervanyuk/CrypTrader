package com.nch.cryptrader.controller.asset.reqrespbody;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
public class AssetResponse {
    private String symbol;
    private BigDecimal price;
}
