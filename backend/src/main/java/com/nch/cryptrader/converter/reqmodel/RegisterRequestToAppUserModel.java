package com.nch.cryptrader.converter.reqmodel;

import com.nch.cryptrader.controller.auth.reqrespbody.RegisterRequest;
import com.nch.cryptrader.converter.BaseConverter;
import com.nch.cryptrader.model.AppUserModel;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class RegisterRequestToAppUserModel extends BaseConverter<RegisterRequest, AppUserModel> {
    @Override
    public AppUserModel convert(RegisterRequest source) {
        return AppUserModel.builder()
                .email(source.getEmail())
                .username(source.getUsername().toLowerCase())
                .password(source.getPassword())
                .lastIssuedTokenRevocation(Instant.now().minusSeconds(120))
                .build();
    }
}
