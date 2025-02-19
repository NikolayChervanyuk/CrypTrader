package com.nch.cryptrader.repository;

import com.nch.cryptrader.entity.AppUser;
import com.nch.cryptrader.view.AppUserCredentialsView;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
public interface UserRepository extends R2dbcRepository<AppUser, UUID> {

    Mono<AppUser> getByUsername(String username);

    Mono<AppUserCredentialsView> getById(UUID userId);

    @Query("SELECT * FROM app_user As u WHERE u.id = $1")
    Mono<AppUser> getAppUserById(UUID userId);

    @Query("SELECT * FROM app_user AS u WHERE u.username = $1")
    Mono<AppUserCredentialsView> getUserCredentialsByUsername(String username);

    @Query("SELECT * FROM app_user AS u WHERE u.email = $1")
    Mono<AppUserCredentialsView> getUserCredentialsByEmail(String email);

    @Modifying
    @Query("UPDATE app_user SET last_issued_token_revocation = current_timestamp WHERE username = $1")
    Mono<Boolean> issueTokenRevocationForUser(String username);
}
