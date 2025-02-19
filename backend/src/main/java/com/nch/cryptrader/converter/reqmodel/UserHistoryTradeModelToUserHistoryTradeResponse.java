package com.nch.cryptrader.converter.reqmodel;

import com.nch.cryptrader.controller.user.reqresbody.UserHistoryTradeResponse;
import com.nch.cryptrader.converter.BaseConverter;
import com.nch.cryptrader.model.UserHistoryTradeModel;
import org.springframework.stereotype.Component;

@Component
public class UserHistoryTradeModelToUserHistoryTradeResponse
        extends BaseConverter<UserHistoryTradeModel, UserHistoryTradeResponse> {

    @Override
    public UserHistoryTradeResponse convert(UserHistoryTradeModel source) {
        return UserHistoryTradeResponse.builder()
                .tradeType(source.getTradeType().toString())
                .symbol(source.getSymbol())
                .quantity(source.getQuantity())
                .price(source.getPrice())
                .date(source.getDate())
                .build();
    }
}
