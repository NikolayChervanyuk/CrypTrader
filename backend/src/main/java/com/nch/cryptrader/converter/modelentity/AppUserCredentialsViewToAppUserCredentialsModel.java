package com.nch.cryptrader.converter.modelentity;

import com.nch.cryptrader.converter.BaseConverter;
import com.nch.cryptrader.model.AppUserCredentialsModel;
import com.nch.cryptrader.view.AppUserCredentialsView;
import org.springframework.stereotype.Component;

@Component
public class AppUserCredentialsViewToAppUserCredentialsModel
        extends BaseConverter<AppUserCredentialsView, AppUserCredentialsModel> {
    @Override
    public AppUserCredentialsModel convert(AppUserCredentialsView source) {
        return new AppUserCredentialsModel(
                source.getEmail(),
                source.getUsername(),
                source.getPassword(),
                source.getBalance()
        );
    }
}
