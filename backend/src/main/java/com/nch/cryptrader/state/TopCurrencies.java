package com.nch.cryptrader.state;

import com.nch.cryptrader.model.AssetModel;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.HashMap;

@Getter
@Component
public class TopCurrencies {
    private final HashMap<String, AssetModel> assets = new HashMap<>(20);
}
