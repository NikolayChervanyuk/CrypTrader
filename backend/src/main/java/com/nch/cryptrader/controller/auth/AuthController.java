package com.nch.cryptrader.controller.auth;

import com.nch.cryptrader.controller.auth.reqrespbody.LoginRequest;
import com.nch.cryptrader.controller.auth.reqrespbody.LoginResponse;
import com.nch.cryptrader.controller.auth.reqrespbody.RegisterRequest;
import com.nch.cryptrader.model.AppUserModel;
import com.nch.cryptrader.model.respModel.RespModelImpl;
import com.nch.cryptrader.service.UserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.ConversionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@ControllerAdvice
@RequestMapping("/auth")
@AllArgsConstructor
public class AuthController {
    private final UserService userService;
    private final ConversionService conversionService;

    @PostMapping(value = "/login", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<RespModelImpl<LoginResponse>>> authenticate(
            @Valid @RequestBody LoginRequest loginRequest
    ) {
        return userService
                .getUserCredentialsByIdentifier(loginRequest.getIdentifier(), loginRequest.getPassword()).map(
                        user -> ResponseEntity.ok(
                                RespModelImpl.of(conversionService.convert(user, LoginResponse.class))
                        )
                ).defaultIfEmpty(new ResponseEntity<>(
                        RespModelImpl.ofError("Invalid credentials"), HttpStatus.NOT_FOUND
                ));
    }

    @PostMapping("/register")
    public Mono<ResponseEntity<RespModelImpl<Boolean>>> register(
            @Valid @RequestBody RegisterRequest registerRequest
    ) {
        var userModel = conversionService.convert(registerRequest, AppUserModel.class);

        assert userModel != null;
        return userService.saveUser(userModel).mapNotNull(persistenceStatus ->
                switch (persistenceStatus) {
                    case SUCCESS -> ResponseEntity.ok(RespModelImpl.of(true));
                    case USERNAME_EXISTS, EMAIL_EXISTS, INTERNAL_ERROR -> new ResponseEntity<>(
                            RespModelImpl.ofError(persistenceStatus.getStatusMessage()),
                            HttpStatus.CONFLICT
                    );
                }
        );
    }

    @PostMapping("/logout")
    public Mono<ResponseEntity<RespModelImpl<Boolean>>> logout() {
        return userService.issueTokenRevocation()
                .map(user -> ResponseEntity.ok(RespModelImpl.of(true)))
                .onErrorReturn(new ResponseEntity<>(
                                RespModelImpl.serviceUnavailableError(),
                                HttpStatus.INTERNAL_SERVER_ERROR
                        )
                );
    }
}
