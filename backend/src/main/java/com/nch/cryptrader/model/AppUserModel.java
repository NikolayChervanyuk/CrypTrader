package com.nch.cryptrader.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;

@AllArgsConstructor
@Getter
@Setter
@Builder
public class AppUserModel {

    private String id;

    private String email;

    private String username;

    private String password;

    private BigDecimal balance;

    private Instant lastIssuedTokenRevocation;
}
