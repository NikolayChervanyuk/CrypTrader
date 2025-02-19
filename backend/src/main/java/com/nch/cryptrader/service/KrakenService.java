package com.nch.cryptrader.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.nch.cryptrader.exception.KrakenApiException;
import com.nch.cryptrader.model.AssetModel;
import com.nch.cryptrader.model.AssetPairModel;
import com.nch.cryptrader.util.KrakenObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class KrakenService {

    @Value("${kraken-api.get.asset-pairs}")
    private String ASSET_PAIRS_URL;

    @Value("${kraken-api.get.ticker-snapshot}")
    private String TICKER_SNAPSHOT_URL;

    private final KrakenObjectMapper objectMapper;

    public List<AssetPairModel> getAssetPairs() throws KrakenApiException {

        List<AssetPairModel> assetPairModels = new ArrayList<>();

        ResponseEntity<String> apiResult = krakenGetRequest(ASSET_PAIRS_URL);
        try {
            JsonNode jsonNode = objectMapper.readTree(apiResult.getBody());
            var pairsListNode = jsonNode.get("result");
            pairsListNode.forEach(pair -> {
                String webSocketPairName = pair.findValue("wsname").textValue();
                String[] baseAndQuota = webSocketPairName.split("/");
                assetPairModels.add(new AssetPairModel(webSocketPairName, baseAndQuota[0], baseAndQuota[1]));
            });

        } catch (JsonProcessingException | IndexOutOfBoundsException e) {
            throw new KrakenApiException(
                    "Reading JSON body from \"GET " + ASSET_PAIRS_URL + "\" failed. Can't get Asset Pairs"
            );
        }
        return assetPairModels;
    }

    public List<AssetModel> getAssetsTickerSnapshot() {
        List<AssetModel> assetModels = new ArrayList<>();

        ResponseEntity<String> apiResult = krakenGetRequest(TICKER_SNAPSHOT_URL);

        try {
            JsonNode jsonNode = objectMapper.readTree(apiResult.getBody());
            var pairsListMap = getPairsUsdFilteredMap(jsonNode.get("result"));

            pairsListMap.forEach((key, value) -> {
                var lastTradeClosedPrice = new BigDecimal(value.get("c").elements().next().textValue());
                assetModels.add(new AssetModel(
                        key.substring(0, key.length() - 3),
                        lastTradeClosedPrice
                ));
            });

        } catch (JsonProcessingException |
                 IndexOutOfBoundsException |
                 NoSuchElementException e
        ) {
            throw new KrakenApiException(
                    "Reading JSON body from \"GET " + TICKER_SNAPSHOT_URL + "\" failed. Can't get Assets"
            );
        }
        return assetModels;
    }

    private Map<String, JsonNode> getPairsUsdFilteredMap(JsonNode pairsListNode) {
        Map<String, JsonNode> pairsMap = new HashMap<>();

        var fields = pairsListNode.fields();
        while (fields.hasNext()) {
            var entry = fields.next();
            var entryKey = entry.getKey();
            log.debug("Snapshot entry: {}", entryKey);
            if (entryKey.endsWith("USD") &&
                    entryKey.charAt(entryKey.length() - 4) != 'Z'
            ) {
                pairsMap.put(entry.getKey(), entry.getValue());
            }
        }
        return pairsMap;
    }

    private ResponseEntity<String> krakenGetRequest(String url) {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        headers.add("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.99 Safari/537.36");

        HttpEntity<String> entity = new HttpEntity<>("parameters", headers);
        return restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
    }
}
