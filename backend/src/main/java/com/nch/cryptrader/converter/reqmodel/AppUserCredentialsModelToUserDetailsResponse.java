package com.nch.cryptrader.converter.reqmodel;

import com.nch.cryptrader.controller.user.reqresbody.UserDetailsResponse;
import com.nch.cryptrader.converter.BaseConverter;
import com.nch.cryptrader.model.AppUserCredentialsModel;
import org.springframework.stereotype.Component;

@Component
public class AppUserCredentialsModelToUserDetailsResponse
    extends BaseConverter<AppUserCredentialsModel, UserDetailsResponse> {
    @Override
    public UserDetailsResponse convert(AppUserCredentialsModel source) {
        return UserDetailsResponse.builder()
                .email(source.getEmail())
                .username(source.getUsername())
                .balance(source.getBalance().doubleValue())
                .build();
    }
}
