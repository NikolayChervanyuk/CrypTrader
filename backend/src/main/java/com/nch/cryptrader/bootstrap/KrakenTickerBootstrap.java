package com.nch.cryptrader.bootstrap;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.nch.cryptrader.model.AssetModel;
import com.nch.cryptrader.service.KrakenService;
import com.nch.cryptrader.state.TopCurrencies;
import com.nch.cryptrader.state.WebSocketSessions;
import com.nch.cryptrader.util.KrakenObjectMapper;
import io.vertx.core.json.Json;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.client.ReactorNettyWebSocketClient;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.List;

@Slf4j
@Component
@AllArgsConstructor
public class KrakenTickerBootstrap implements ApplicationListener<ApplicationReadyEvent> {

    private final KrakenService krakenService;
    private final KrakenObjectMapper objectMapper;
    private final TopCurrencies topCurrencies;
    private final ReactorNettyWebSocketClient webSocketClient;
    private final WebSocketSessions clientWebSocketSessions;

    @Override
    public void onApplicationEvent(@NonNull ApplicationReadyEvent event) {
        log.info("Establishing websocket connections");
        var assetModels = krakenService.getAssetsTickerSnapshot();

        List<AssetModel> top20Assets = assetModels.stream()
                .sorted(AssetModel::compareTo)
                .skip(assetModels.size() - 20).toList()
                .reversed();

        var subscribeMsg = Json.encode(
                new SubscriptionMsg("subscribe", new SubscriptionParams(
                        top20Assets.stream()
                                .map(asset -> asset.getSymbol() + "/USD").toList()
                )));

        log.debug("Kraken subscribe msg: {}", subscribeMsg);
        webSocketClient.execute(URI.create("wss://ws.kraken.com/v2"), session ->
                session.send(Mono.just(session.textMessage(subscribeMsg))).thenMany(session.receive()
                        .map(WebSocketMessage::getPayloadAsText)
                        .doOnNext(msg -> {
                            try {
                                JsonNode krakenMsg = objectMapper.readTree(msg);
                                var firstEntry = krakenMsg.fields().next();
                                if (!firstEntry.getKey().equals("channel") ||
                                        !firstEntry.getValue().textValue().equals("ticker")
                                ) return;

                                var type = krakenMsg.get("type").textValue();

                                var updateData = krakenMsg.get("data").elements().next();

                                var symbolPair = updateData.get("symbol").textValue();
                                var baseSymbol = symbolPair.split("/")[0];
                                var price = updateData.get("ask").decimalValue();

                                var updatedAsset = new AssetModel(baseSymbol, price);

                                topCurrencies.getAssets()
                                        .put(baseSymbol, updatedAsset);
                                //log.debug("Kraken update msg for {}: {}", baseSymbol, subscribeMsg);
                                clientWebSocketSessions.getSessions()
                                        .forEach((id, clientSession) ->
                                                clientSession.send(Mono.just(
                                                        clientSession.textMessage(
                                                                Json.encode(new ClientUpdateMessage(type, updatedAsset))
                                                        )
                                                )).subscribe()
                                        );
                            } catch (JsonProcessingException e) {
                                throw new RuntimeException(e);
                            }
                        })
                        .doOnError(Throwable::printStackTrace)
                ).then().doOnError(Throwable::printStackTrace)
        ).subscribe();
    }

    @Getter
    @Setter
    public static class ClientUpdateMessage {

        private String type;
        private AssetModel asset;

        public ClientUpdateMessage(String type, AssetModel asset) {
            this.type = type;
            this.asset = asset;
        }

    }

    @Getter
    @Setter
    public static class SubscriptionMsg {

        private String method;
        private SubscriptionParams params;

        public SubscriptionMsg(String method, SubscriptionParams params) {
            this.method = method;
            this.params = params;
        }
    }

    @Getter
    @Setter
    public static class SubscriptionParams {
        private String channel = "ticker";
        private List<String> symbol;

        public SubscriptionParams(List<String> symbols) {
            this.symbol = symbols;
        }
    }
}
