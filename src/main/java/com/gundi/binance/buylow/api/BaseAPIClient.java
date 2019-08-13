package com.gundi.binance.buylow.api;

import com.binance.api.client.BinanceApiCallback;
import com.binance.api.client.BinanceApiClientFactory;
import com.binance.api.client.BinanceApiRestClient;
import com.binance.api.client.BinanceApiWebSocketClient;
import com.binance.api.client.domain.account.AssetBalance;
import com.binance.api.client.domain.account.Order;
import com.binance.api.client.domain.account.Trade;
import com.binance.api.client.domain.account.request.CancelOrderRequest;
import com.binance.api.client.domain.account.request.OrderRequest;
import com.binance.api.client.domain.event.AggTradeEvent;
import com.binance.api.client.domain.general.ExchangeInfo;
import com.binance.api.client.domain.market.Candlestick;
import com.binance.api.client.domain.market.CandlestickInterval;
import com.binance.api.client.domain.market.TickerStatistics;
import com.gundi.binance.buylow.config.APIKeyAndSecret;
import com.gundi.binance.buylow.service.AnalyticsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.io.Closeable;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.List;

public abstract class BaseAPIClient implements APIClient{

    Logger logger = LoggerFactory.getLogger(BaseAPIClient.class);

    @Autowired
    private APIKeyAndSecret apiKeyAndSecret;

    private BinanceApiClientFactory factory;

    private BinanceApiWebSocketClient binanceApiWebSocketClient;
    protected BinanceApiRestClient apiRestClient;


    @PostConstruct
    private void postConstruct() {
        factory = BinanceApiClientFactory.newInstance(
                apiKeyAndSecret.getApiKey(), apiKeyAndSecret.getApiSecret());
        binanceApiWebSocketClient = factory.newWebSocketClient();
        apiRestClient = factory.newRestClient();
    }


    @Override
    public void ping() {
        this.apiRestClient.ping();
    }

    @Override
    public void cancelOrder(CancelOrderRequest cancelOrderRequest) {
        this.apiRestClient.cancelOrder(cancelOrderRequest);
    }

    @Override
    public Closeable onAggTradeEvent(String symbols, BinanceApiCallback<AggTradeEvent> callback) {
        return this.binanceApiWebSocketClient.onAggTradeEvent(symbols,callback);
    }

    @Override
    public TickerStatistics get24HrPriceStatistics(String symbol) {
        return this.apiRestClient.get24HrPriceStatistics(symbol);
    }

    @Override
    public AssetBalance getAssetBalance(String baseCurrency) {
        return this.apiRestClient.getAccount().getAssetBalance(baseCurrency);
    }

    @Override
    public List<Trade> getMyTrades(String symbol) {

        return this.apiRestClient.getMyTrades(symbol);
    }

    @Override
    public ExchangeInfo getExchangeInfo() {
        return this.apiRestClient.getExchangeInfo();
    }

    @Override
    public List<Order> getOpenOrders(OrderRequest orderRequest) {
        return this.apiRestClient.getOpenOrders(orderRequest);
    }

    public List<Candlestick> getPastFiveDaysCandlestickBars(String symbol, CandlestickInterval candlestickInterval) {
        Long serverTime = apiRestClient.getServerTime();

        //Long timeThreeDaysBack = serverTime - 120 * 60 * 1000;

        Long timeThreeDaysBack = serverTime - 1 * 24 * 60 * 60 * 1000;

        return this.apiRestClient.getCandlestickBars(symbol, candlestickInterval,
                500, timeThreeDaysBack,
                serverTime);
    }

    public Long getServerTime() {
        return apiRestClient.getServerTime();
    }
}
