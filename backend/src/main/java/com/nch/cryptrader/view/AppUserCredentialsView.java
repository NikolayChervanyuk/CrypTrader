package com.nch.cryptrader.view;

import java.math.BigDecimal;

public interface AppUserCredentialsView {
    String getEmail();
    String getUsername();
    String getPassword();
    BigDecimal getBalance();

}
