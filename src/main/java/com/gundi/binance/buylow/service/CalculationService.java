package com.gundi.binance.buylow.service;

import com.binance.api.client.domain.account.Trade;
import com.binance.api.client.domain.general.ExchangeInfo;
import com.binance.api.client.domain.general.FilterType;
import com.binance.api.client.domain.general.SymbolFilter;
import com.gundi.binance.buylow.api.APIClient;
import com.gundi.binance.buylow.config.APIKeyAndSecret;
import com.gundi.binance.buylow.config.CryptoPair;
import org.decimal4j.util.DoubleRounder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CalculationService {

    Logger logger = LoggerFactory.getLogger(CalculationService.class);


    private APIClient apiClient;


    private Map<String, Double> totalQtyPerSymbol = new HashMap<String, Double>();
    private Map<String, Double> averagePricePerSymbol = new HashMap<String, Double>();
    private Map<String, Integer> roundDecimalPerSymbol = new HashMap<String, Integer>();



    private Map<String, LocalDateTime> lastTradeTimePerSymbol = new HashMap<String, LocalDateTime>();

    @Autowired
    public CalculationService(APIClient apiClient) {
        this.apiClient = apiClient;
    }

    public void invoke() {
        for(CryptoPair cryptoPair : CryptoPair.values()) {
            invoke(cryptoPair.getPair());
        }
    }

    public void invoke(String symbol) {

        List<Trade> allTradeList = apiClient.getMyTrades(symbol);

        Optional<Trade> lastTradedSellOrder = allTradeList.stream().filter(trade -> {
            return !trade.isBuyer();
        }).max((i, j) -> Long.valueOf(i.getTime()).compareTo(Long.valueOf(j.getTime())));

        long lastSellOrderTime = (lastTradedSellOrder.isPresent()) ? lastTradedSellOrder.get().getTime()  : 1561465294471L;

        List<Trade> activeBuytradeList = allTradeList.stream().filter(trade -> {
            return trade.isBuyer() && trade.getTime() > lastSellOrderTime;
        }).collect(Collectors.toList());



        totalQtyPerSymbol.put(symbol,
                activeBuytradeList.stream().mapToDouble(s -> Double.parseDouble(s.getQty())).sum());

        averagePricePerSymbol.put(symbol, calculateAveragePrice(symbol, activeBuytradeList));

        OptionalLong lastTradeTimeEpoch = activeBuytradeList.stream().mapToLong(s -> s.getTime()).max();
        LocalDateTime lastTradeTime =
                LocalDateTime.ofInstant(Instant.ofEpochMilli(lastTradeTimeEpoch.orElse(Long.MIN_VALUE)), ZoneId.systemDefault());
        lastTradeTimePerSymbol.put(symbol, lastTradeTime);

    }

    private Double calculateAveragePrice(String symbol, List<Trade> tradeList) {

        double totalPrice = tradeList.stream().mapToDouble(s -> {
            double price = Double.parseDouble(s.getPrice());
            double qty = Double.parseDouble(s.getQty());
            return  price * qty;
        }).sum();

        Double averagePricePerQty = totalPrice/totalQtyPerSymbol.get(symbol);
        return DoubleRounder.round(averagePricePerQty, roundDecimalPerSymbol.get(symbol));
    }

    @PostConstruct
    public void initRoundDecimalForSymbol() {

        for(CryptoPair cryptoPair : CryptoPair.values()) {
            ExchangeInfo exchangeInfo = apiClient.getExchangeInfo();
            List<SymbolFilter> symbolFilterList = exchangeInfo.getSymbolInfo(cryptoPair.getPair()).getFilters();

            for(SymbolFilter symbolFilter : symbolFilterList) {

                if(symbolFilter.getFilterType().compareTo(FilterType.PRICE_FILTER) == 0) {
                    roundDecimalPerSymbol.put(cryptoPair.getPair(),
                            symbolFilter.getTickSize().indexOf('1') -1);
                }
            }
        }


    }


    public Double getTotalQtyPerSymbol(String symbol) {
        return totalQtyPerSymbol.get(symbol);
    }

    public  Double getAveragePricePerSymbol(String symbol) {
        return averagePricePerSymbol.get(symbol);
    }

    public Integer getRoundDecimalPerSymbol(String symbol) {
        return roundDecimalPerSymbol.get(symbol);
    }

    public LocalDateTime getLastTradeTimePerSymbol(String symbol) {
        return lastTradeTimePerSymbol.get(symbol);
    }
}
