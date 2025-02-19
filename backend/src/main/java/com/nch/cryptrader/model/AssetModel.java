package com.nch.cryptrader.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@AllArgsConstructor
@Getter
@Setter
@Builder
public class AssetModel implements Comparable<AssetModel> {
    private String symbol;
    private BigDecimal price;

    @Override
    public int compareTo(AssetModel other) {
        return this.price.compareTo(other.price);
    }
}
