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

import java.io.Closeable;
import java.util.List;

public interface APIClient {

     void ping();
     void newOrder(NewOrder newOrder);
     void cancelOrder(CancelOrderRequest cancelOrderRequest);
     Closeable onAggTradeEvent(String symbols, BinanceApiCallback<AggTradeEvent> callback);
     TickerStatistics get24HrPriceStatistics(String symbol);
     AssetBalance getAssetBalance(String baseCurrency);

    List<Trade> getMyTrades(String symbol);

    ExchangeInfo getExchangeInfo();

    List<Order> getOpenOrders(OrderRequest orderRequest);

    //getApiRestClient().getAccount()

}
