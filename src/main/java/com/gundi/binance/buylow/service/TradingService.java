package com.gundi.binance.buylow.service;

import com.binance.api.client.BinanceApiCallback;
import com.binance.api.client.BinanceApiClientFactory;
import com.binance.api.client.BinanceApiRestClient;
import com.binance.api.client.BinanceApiWebSocketClient;
import com.binance.api.client.domain.event.AggTradeEvent;
import org.springframework.stereotype.Service;

@Service
public class TradingService {

    public void doIt() {

        BinanceApiClientFactory factory = BinanceApiClientFactory.newInstance();
        BinanceApiWebSocketClient binanceApiWebSocketClient = factory.newWebSocketClient();
        BinanceApiRestClient apiRestClient = factory.newRestClient();

        System.out.println(binanceApiWebSocketClient);
        binanceApiWebSocketClient.onAggTradeEvent("xrpusdt", new BinanceApiCallback<AggTradeEvent>() {

            @Override
            public void onResponse(AggTradeEvent aggTradeEvent) {
                String lastPrice = apiRestClient.get24HrPriceStatistics(aggTradeEvent.getSymbol()).getLastPrice();
                System.out.println("Traded done " + lastPrice);
            }
        });


    }
}
