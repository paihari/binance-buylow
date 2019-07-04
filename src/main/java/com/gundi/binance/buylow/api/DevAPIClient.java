package com.gundi.binance.buylow.api;

import com.binance.api.client.BinanceApiCallback;
import com.binance.api.client.domain.account.AssetBalance;
import com.binance.api.client.domain.account.NewOrder;
import com.binance.api.client.domain.account.Order;
import com.binance.api.client.domain.account.Trade;
import com.binance.api.client.domain.account.request.CancelOrderRequest;
import com.binance.api.client.domain.account.request.OrderRequest;
import com.binance.api.client.domain.event.AggTradeEvent;
import com.binance.api.client.domain.general.ExchangeInfo;
import com.binance.api.client.domain.market.TickerStatistics;
import com.gundi.binance.buylow.config.APIKeyAndSecret;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.io.Closeable;
import java.util.List;

@Component
@Profile("dev")
public class DevAPIClient extends BaseAPIClient{



    @Override
    public void newOrder(NewOrder newOrder) {
        this.apiRestClient.newOrderTest(newOrder);
    }

    @Override
    public void cancelOrder(CancelOrderRequest cancelOrderRequest) {
        return;
    }


}
