package com.gundi.binance.buylow.service;

import com.binance.api.client.BinanceApiCallback;
import com.binance.api.client.domain.account.Trade;
import com.binance.api.client.domain.event.AggTradeEvent;
import com.gundi.binance.buylow.config.CryptoPair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class BuyLowPrimeService {

    private AuthenticationService authenticationService;

    Logger logger = LoggerFactory.getLogger(BuyLowPrimeService.class);

    private SellService sellService;
    private BuyService buyService;

    @Autowired
    public BuyLowPrimeService(AuthenticationService authenticationService,
                              SellService sellService,
                              BuyService buyService) {
        this.authenticationService = authenticationService;
        this.sellService = sellService;
        this.buyService = buyService;
    }

    public void invoke() {

        String pair = CryptoPair.getPairsAsString();
        authenticationService.getBinanceApiWebSocketClient().onAggTradeEvent(pair.toLowerCase(),
                new BinanceApiCallback<AggTradeEvent>() {
            @Override
            public void onResponse(final AggTradeEvent aggTradeEvent) {
                logger.info("Web Socket Active " + LocalDateTime.now());
                authenticationService.getApiRestClient().ping();
                sellService.invoke(aggTradeEvent);
                buyService.invoke(aggTradeEvent);
            }
            public void onFailure(final Throwable cause) {
                logger.error("Web Socket Failed " + LocalDateTime.now());
                cause.printStackTrace();
                invoke();
            }
        });

    }
}