package com.nch.cryptrader.converter.reqmodel;

import com.nch.cryptrader.controller.auth.reqrespbody.LoginResponse;
import com.nch.cryptrader.converter.BaseConverter;
import com.nch.cryptrader.model.AppUserCredentialsModel;
import com.nch.cryptrader.service.JwtService;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class AppUserCredentialsModelToLoginResponse extends BaseConverter<AppUserCredentialsModel, LoginResponse> {

    private final JwtService jwtService;

    @Override
    public LoginResponse convert(@NonNull AppUserCredentialsModel source) {
        return LoginResponse.builder()
                .refreshToken(jwtService.generateRefreshToken(source))
                .accessToken(jwtService.generateAccessToken(source))
                .build();
    }
}