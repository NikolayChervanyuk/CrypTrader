package com.nch.cryptrader.bootstrap;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@AllArgsConstructor
public class AppBootstrap implements ApplicationListener<ApplicationReadyEvent> {

    private final CurrencyBootstrap currencyBootstrap;
    private final KrakenTickerBootstrap krakenTickerBootstrap;

    @Override
    public void onApplicationEvent(@NonNull ApplicationReadyEvent event) {
        log.info("Bootstrap: Application bootstrapping started.");
//        currencyBootstrap.run();
        //krakenTickerBootstrap.run();
        log.info("Bootstrap: Application bootstrapping completed.");
    }
}
