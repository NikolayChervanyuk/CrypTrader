package com.nch.cryptrader.converter.modelentity;

import com.nch.cryptrader.converter.BaseConverter;
import com.nch.cryptrader.entity.AppUser;
import com.nch.cryptrader.model.AppUserModel;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@AllArgsConstructor
public class AppUserModelToAppUser extends BaseConverter<AppUserModel, AppUser> {

    private final PasswordEncoder passwordEncoder;

    @Override
    public AppUser convert(AppUserModel source) {
        var appUser = new AppUser();
        if(source.getId() != null) appUser.setId(UUID.fromString(source.getId()));
        appUser.setEmail(source.getEmail());
        appUser.setUsername(source.getUsername());
        appUser.setPassword(passwordEncoder.encode(source.getPassword()));
        appUser.setBalance(source.getBalance());
        appUser.setLastIssuedTokenRevocation(source.getLastIssuedTokenRevocation());

        return appUser;
    }
}
