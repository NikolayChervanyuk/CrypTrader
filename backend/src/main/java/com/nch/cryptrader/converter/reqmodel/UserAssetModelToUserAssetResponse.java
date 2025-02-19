package com.nch.cryptrader.converter.reqmodel;

import com.nch.cryptrader.controller.user.reqresbody.UserAssetResponse;
import com.nch.cryptrader.converter.BaseConverter;
import com.nch.cryptrader.model.UserAssetModel;
import org.springframework.stereotype.Component;

@Component
public class UserAssetModelToUserAssetResponse extends BaseConverter<UserAssetModel, UserAssetResponse> {
    @Override
    public UserAssetResponse convert(UserAssetModel source) {
        return UserAssetResponse.builder()
                .symbol(source.getSymbol())
                .quantity(source.getQuantity())
                .totalProfit(source.getTotalProfit())
                .currentPrice(source.getCurrentPrice())
                .build();
    }
}
