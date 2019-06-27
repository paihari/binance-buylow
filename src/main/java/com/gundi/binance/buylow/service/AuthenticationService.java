package com.gundi.binance.buylow.service;

import com.binance.api.client.BinanceApiClientFactory;
import com.binance.api.client.BinanceApiRestClient;
import com.binance.api.client.BinanceApiWebSocketClient;
import com.gundi.binance.buylow.config.APIKeyAndSecret;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {

    private APIKeyAndSecret apiKeyAndSecret;

    private BinanceApiClientFactory factory;

    private BinanceApiWebSocketClient binanceApiWebSocketClient;
    private BinanceApiRestClient apiRestClient;


    @Autowired
    public AuthenticationService(APIKeyAndSecret apiKeyAndSecret) {
        this.apiKeyAndSecret = apiKeyAndSecret;
        factory = BinanceApiClientFactory.newInstance(
                apiKeyAndSecret.getApiKey(), apiKeyAndSecret.getApiSecret());
        binanceApiWebSocketClient = factory.newWebSocketClient();
        apiRestClient = factory.newRestClient();
    }


    public BinanceApiWebSocketClient getBinanceApiWebSocketClient() {
        return binanceApiWebSocketClient;
    }

    public BinanceApiRestClient getApiRestClient() {
        return apiRestClient;
    }
}
