package com.nch.cryptrader.bootstrap;

import com.nch.cryptrader.model.AssetModel;
import com.nch.cryptrader.model.AssetPairModel;
import com.nch.cryptrader.service.AssetService;
import com.nch.cryptrader.service.KrakenService;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@AllArgsConstructor
public class CurrencyBootstrap implements ApplicationListener<ApplicationReadyEvent> {

    private final KrakenService krakenService;
    private final AssetService assetService;

    @Override
    public void onApplicationEvent(@NonNull ApplicationReadyEvent event) {
        log.info("Updating available currencies");

        List<AssetPairModel> assetPairModels = krakenService.getAssetPairs();

        List<AssetModel> assetModels = assetPairModels.stream()
                .filter(pair -> pair.getQuota().equals("USD"))
                .map(pair -> AssetModel.builder()
                        .symbol(pair.getBase())
                        .build()
                )
                .toList();
        assetService.updateAssets(assetModels)
                .doOnNext(status -> {
                    if (status) {
                        log.info("Currencies updated successfully");
                    } else log.info("Updating currencies aborted. Assets already exist");
                }).then().subscribe();
    }
}
