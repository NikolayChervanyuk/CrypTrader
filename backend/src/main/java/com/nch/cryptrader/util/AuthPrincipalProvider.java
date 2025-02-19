package com.nch.cryptrader.util;

import com.nch.cryptrader.configuration.security.AuthPrincipal;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import reactor.core.publisher.Mono;

import java.util.UUID;

public class AuthPrincipalProvider {

    public static Mono<AuthPrincipal> getAuthenticatedUserMono() {
        return ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)
                .filter(Authentication::isAuthenticated)
                .map(Authentication::getPrincipal)
                .cast(AuthPrincipal.class)
                .doOnError(Throwable::printStackTrace);
    }

    public static Mono<String> getAuthenticatedUserIdMono() {
        return getAuthenticatedUserMono().map(AuthPrincipal::getId);
    }

    public static Mono<UUID> getAuthenticatedUserUUIDMono() {
        return getAuthenticatedUserMono().map(AuthPrincipal::getId)
                .map(UUID::fromString);
    }
}
