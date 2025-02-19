package com.nch.cryptrader.controller.asset;

import com.nch.cryptrader.controller.asset.reqrespbody.AssetResponse;
import com.nch.cryptrader.model.respModel.RespModelImpl;
import com.nch.cryptrader.service.AssetService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.ConversionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Objects;

@Slf4j
@RestController
@ControllerAdvice
@RequestMapping("/assets")
@AllArgsConstructor
public class AssetController {

    private final AssetService assetService;
    private final ConversionService conversionService;

    @GetMapping("/top-20")
    public Mono<ResponseEntity<RespModelImpl<List<AssetResponse>>>> getTop20Assets() {
        return Mono.just(RespModelImpl.of(
                assetService.getTop20Assets().stream()
                        .map(assetModel -> conversionService.convert(assetModel, AssetResponse.class))
                        .toList()
        )).map(ResponseEntity::ok);
    }

    @GetMapping(params = "symbol")
    public Mono<ResponseEntity<RespModelImpl<AssetResponse>>> getAsset(@RequestParam String symbol) {
        return Mono.just(Objects.requireNonNull(
                        conversionService.convert(assetService.getAsset(symbol), AssetResponse.class)
                ))
                .map(RespModelImpl::of)
                .map(ResponseEntity::ok);
    }
}
