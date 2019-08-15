package com.gundi.binance.buylow.api;

import com.binance.api.client.BinanceApiCallback;
import com.binance.api.client.domain.account.*;
import com.binance.api.client.domain.account.request.CancelOrderRequest;
import com.binance.api.client.domain.account.request.OrderRequest;
import com.binance.api.client.domain.event.AggTradeEvent;
import com.binance.api.client.domain.general.ExchangeInfo;
import com.binance.api.client.domain.market.Candlestick;
import com.binance.api.client.domain.market.CandlestickInterval;
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

     List<Candlestick> getPastFiveDaysCandlestickBars(String symbol, CandlestickInterval candlestickInterval);
     Long getServerTime();

    //getApiRestClient().getAccount()

}
