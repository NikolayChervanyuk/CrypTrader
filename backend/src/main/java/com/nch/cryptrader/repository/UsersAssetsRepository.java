package com.nch.cryptrader.repository;

import com.nch.cryptrader.entity.UsersAssets;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
public interface UsersAssetsRepository extends R2dbcRepository<UsersAssets, UUID> {

    Flux<UsersAssets> getByUserId(UUID userId);

    Mono<Boolean> deleteByUserId(UUID userId);

    @Query("SELECT * FROM users_assets WHERE user_id = $1 AND asset_id = $2")
    Mono<UsersAssets> getByUserIdAndAssetId(UUID userId, UUID assetId);
}
