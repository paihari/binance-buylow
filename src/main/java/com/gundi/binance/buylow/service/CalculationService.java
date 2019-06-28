package com.gundi.binance.buylow.service;

import com.binance.api.client.domain.account.Trade;
import com.binance.api.client.domain.general.ExchangeInfo;
import com.binance.api.client.domain.general.FilterType;
import com.binance.api.client.domain.general.SymbolFilter;
import com.gundi.binance.buylow.config.APIKeyAndSecret;
import com.gundi.binance.buylow.config.CryptoPair;
import org.decimal4j.util.DoubleRounder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CalculationService {


    private AuthenticationService authenticationService;


    private Map<String, Integer> noOfTradesPerSymbol = Collections.emptyMap();
    private Map<String, Double> totalQtyPerSymbol = Collections.emptyMap();
    private Map<String, Double> averagePricePerSymbol = Collections.emptyMap();


    public CalculationService(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    public void invoke() {
        for(CryptoPair cryptoPair : CryptoPair.values()) {
            invoke(cryptoPair.getPair());
        }
    }

    public void invoke(String symbol) {

        List<Trade> allTradeList = authenticationService.getApiRestClient().getMyTrades(symbol);
        Optional<Trade> lastTradedSellOrder = allTradeList.stream().filter(trade -> {
            return trade.isMaker();
        }).max((i, j) -> Long.valueOf(i.getTime()).compareTo(Long.valueOf(j.getTime())));

       long lastSellOrderTime = (lastTradedSellOrder.isPresent()) ? lastTradedSellOrder.get().getTime()  : 1561465294471L;

        List<Trade> activeBuytradeList = allTradeList.stream().filter(trade -> {
            return trade.isBuyer() && trade.getTime() > lastSellOrderTime;
        }).collect(Collectors.toList());
        noOfTradesPerSymbol.put(symbol,
                activeBuytradeList.size());
        totalQtyPerSymbol.put(symbol,
                activeBuytradeList.stream().mapToDouble(s -> Double.parseDouble(s.getQty())).sum());
        averagePricePerSymbol.put(symbol, calculateAveragePrice(symbol, activeBuytradeList));

    }

    private Double calculateAveragePrice(String symbol, List<Trade> tradeList) {

        double totalPrice = tradeList.stream().mapToDouble(s -> {
            double price = Double.parseDouble(s.getPrice());
            double qty = Double.parseDouble(s.getQty());
            return  price * qty;
        }).sum();

        Double averagePricePerQty = totalPrice/totalQtyPerSymbol.get(symbol);
        return DoubleRounder.round(averagePricePerQty, getRoundDecimalForSymbol(symbol));
    }

    public int getRoundDecimalForSymbol(String symbol) {

        ExchangeInfo exchangeInfo = authenticationService.getApiRestClient().getExchangeInfo();
        List<SymbolFilter> symbolFilterList = exchangeInfo.getSymbolInfo(symbol).getFilters();

        for(SymbolFilter symbolFilter : symbolFilterList) {

            if(symbolFilter.getFilterType().compareTo(FilterType.PRICE_FILTER) == 0) {
                return symbolFilter.getTickSize().indexOf('1') -1;
            }
        }
        return Integer.MIN_VALUE;
    }

    public Integer getNoOfTradesPerSymbol(String symbol) {
        return noOfTradesPerSymbol.get(symbol);
    }

    public Double getTotalQtyPerSymbol(String symbol) {
        return totalQtyPerSymbol.get(symbol);
    }

    public  Double getAveragePricePerSymbol(String symbol) {
        return averagePricePerSymbol.get(symbol);
    }





}
