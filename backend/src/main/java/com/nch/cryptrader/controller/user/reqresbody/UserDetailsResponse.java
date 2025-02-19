package com.nch.cryptrader.controller.user.reqresbody;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class UserDetailsResponse {
    private String email;
    private String username;
    private double balance;
}
