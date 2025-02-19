package com.nch.cryptrader.service;

import com.nch.cryptrader.entity.Asset;
import com.nch.cryptrader.model.AssetModel;
import com.nch.cryptrader.repository.AssetRepository;
import com.nch.cryptrader.state.TopCurrencies;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class AssetService {

    private final AssetRepository assetRepository;
    private final TopCurrencies topCurrencies;

    public Mono<Boolean> updateAssets(List<AssetModel> assetModels) {
        return assetRepository.findAll().collectList()
                .flatMap(assets -> {
                    if (assets.isEmpty()) {
                        return assetRepository.saveAll(assetModels.stream()
                                        .map(assetModel -> new Asset(assetModel.getSymbol()))
                                        .toList()
                                ).then()
                                .thenReturn(true);
                    }
                    return Mono.just(false);
                });
    }

    public List<AssetModel> getTop20Assets() {
        return topCurrencies.getAssets().values().stream().toList();
    }

    public AssetModel getAsset(String symbol) {
        return topCurrencies.getAssets().get(symbol);
    }
}
