package com.nch.cryptrader.repository;

import com.nch.cryptrader.entity.Trade;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
public interface TradeRepository extends R2dbcRepository<Trade, UUID> {

    Flux<Trade> getByUserId(UUID userId);

    Mono<Boolean> deleteByUserId(UUID userId);

}
