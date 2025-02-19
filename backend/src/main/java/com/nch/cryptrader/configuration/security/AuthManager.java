package com.nch.cryptrader.configuration.security;

import com.nch.cryptrader.repository.UserRepository;
import com.nch.cryptrader.service.JwtService;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@AllArgsConstructor
public class AuthManager implements ReactiveAuthenticationManager {

    private final JwtService jwtService;
    private final UserRepository userRepository;

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        return Mono
                .justOrEmpty(authentication)
                .cast(BearerToken.class)
                .flatMap(auth -> userRepository
                        .getByUsername(jwtService.extractUsername(auth.getCredentials()))
                        .flatMap(foundUser -> {
                            if (jwtService.isTokenValid(auth.getCredentials(), foundUser)) {
                                return Mono.just(
                                        new UsernamePasswordAuthenticationToken(
                                                new AuthPrincipal(
                                                        foundUser.getId().toString(),
                                                        foundUser.getUsername(),
                                                        foundUser.getPassword()
                                                ),
                                                foundUser.getPassword(),
                                                foundUser.getAuthorities()
                                        )
                                );
                            }
                            return Mono.error(new IllegalArgumentException("Invalid token"));
                        }).doOnError(Throwable::printStackTrace)
                );
    }
}
