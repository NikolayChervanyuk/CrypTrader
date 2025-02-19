package com.nch.cryptrader.controller.user.reqresbody;

import com.nch.cryptrader.validator.ValidAssetAmount;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SellAssetRequest {
    private String symbol;

    @ValidAssetAmount
    private double quantity;
}
