package com.nch.cryptrader.converter.reqmodel;

import com.nch.cryptrader.controller.asset.reqrespbody.AssetResponse;
import com.nch.cryptrader.converter.BaseConverter;
import com.nch.cryptrader.model.AssetModel;
import lombok.NonNull;
import org.springframework.stereotype.Component;

@Component
public class AssetModelToAssetResponse extends BaseConverter<AssetModel, AssetResponse> {
    @Override
    public AssetResponse convert(@NonNull AssetModel source) {
        return new AssetResponse(source.getSymbol(), source.getPrice());
    }
}
