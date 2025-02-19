package com.nch.cryptrader.controller.user;

import com.nch.cryptrader.controller.user.reqresbody.*;
import com.nch.cryptrader.model.respModel.RespModelImpl;
import com.nch.cryptrader.service.UserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.ConversionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
@RestController
@ControllerAdvice
@RequestMapping("/user")
@AllArgsConstructor
public class UserController {
    private final UserService userService;
    private final ConversionService conversionService;

    @GetMapping
    public Mono<ResponseEntity<RespModelImpl<UserDetailsResponse>>> getUserDetails() {
        return userService.getUserDetails()
                .mapNotNull(userCredentialsModel ->
                        conversionService.convert(userCredentialsModel, UserDetailsResponse.class)
                )
                .map(RespModelImpl::of)
                .onErrorReturn(RespModelImpl.serviceUnavailableError())
                .map(ResponseEntity::ok);
    }

    @GetMapping(value = "/assets", params = "symbol")
    public Mono<ResponseEntity<RespModelImpl<List<UserAssetResponse>>>> getUserAsset(@RequestParam String symbol) {
        return userService.getUserAssets()
                .filter(userAssetModel -> userAssetModel.getSymbol().equals(symbol))
                .take(1)
                .map(userAssetModel ->
                        conversionService.convert(userAssetModel, UserAssetResponse.class)
                ).collectList()
                .map(RespModelImpl::of)
                .map(ResponseEntity::ok);
    }

    @GetMapping("/assets")
    public Mono<ResponseEntity<RespModelImpl<List<UserAssetResponse>>>> getUserAssets() {
        return userService.getUserAssets()
                .map(userAssetModel ->
                        conversionService.convert(userAssetModel, UserAssetResponse.class)
                ).collectList()
                .map(RespModelImpl::of)
                .map(ResponseEntity::ok);
    }

    @GetMapping("/trade-history")
    public Mono<ResponseEntity<RespModelImpl<List<UserHistoryTradeResponse>>>> getUserTradeHistory() {
        return userService.getUserTradeHistory()
                .map(userHistoryTradeModel ->
                        conversionService.convert(userHistoryTradeModel, UserHistoryTradeResponse.class)
                ).collectList()
                .map(RespModelImpl::of)
                .map(ResponseEntity::ok);
    }

    @PostMapping("/buy")
    public Mono<ResponseEntity<RespModelImpl<Boolean>>> buyAsset(@Valid @RequestBody BuyAssetRequest buyAssetRequest) {
        return userService.buyAsset(buyAssetRequest.getSymbol(), buyAssetRequest.getQuantity())
                .map(RespModelImpl::of)
                .map(ResponseEntity::ok);
    }

    @PostMapping("/sell")
    public Mono<ResponseEntity<RespModelImpl<Boolean>>> sellAsset(@Valid @RequestBody SellAssetRequest sellAssetRequest) {
        return userService.sellAsset(sellAssetRequest.getSymbol(), sellAssetRequest.getQuantity())
                .map(RespModelImpl::of)
                .map(ResponseEntity::ok);
    }

    @PostMapping("/reset")
    public Mono<ResponseEntity<RespModelImpl<Boolean>>> resetUser() {
        return userService.resetAccount()
                .map(RespModelImpl::of)
                .map(ResponseEntity::ok);
    }
}
