package com.nch.cryptrader.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
@Builder
public class AssetPairModel {
    private String webSocketName;
    private String base;
    private String quota;

}
