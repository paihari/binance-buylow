package com.gundi.binance.buylow.service;

import com.binance.api.client.BinanceApiCallback;
import com.binance.api.client.domain.TimeInForce;
import com.binance.api.client.domain.account.NewOrder;
import com.binance.api.client.domain.account.NewOrderResponse;
import com.binance.api.client.domain.account.Trade;
import com.binance.api.client.domain.account.request.OrderRequest;
import com.binance.api.client.domain.event.AggTradeEvent;
import com.gundi.binance.buylow.api.APIClient;
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

    private APIClient apiClient;

    Logger logger = LoggerFactory.getLogger(BuyLowPrimeService.class);

    private SellService sellService;
    private BuyService buyService;
    private  AnalyticsService analyticsService;


    @Autowired
    public BuyLowPrimeService(APIClient apiClient,
                              SellService sellService,
                              BuyService buyService,
                              AnalyticsService analyticsService) {
        this.apiClient = apiClient;
        this.sellService = sellService;
        this.buyService = buyService;
        this.analyticsService = analyticsService;
    }


    public void invoke() {
    }
}
