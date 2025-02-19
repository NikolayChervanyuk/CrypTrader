package com.nch.cryptrader.repository;

import com.nch.cryptrader.entity.Asset;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
public interface AssetRepository extends R2dbcRepository<Asset, UUID> {

    Mono<Asset> getById(UUID id);

    Mono<Asset> getBySymbol(String symbol);


}
