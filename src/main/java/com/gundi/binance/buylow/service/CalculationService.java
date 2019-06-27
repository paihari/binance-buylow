package com.gundi.binance.buylow.service;

import com.binance.api.client.domain.account.Trade;
import com.binance.api.client.domain.general.ExchangeInfo;
import com.binance.api.client.domain.general.FilterType;
import com.binance.api.client.domain.general.SymbolFilter;
import com.gundi.binance.buylow.config.APIKeyAndSecret;
import org.decimal4j.util.DoubleRounder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CalculationService {


    private AuthenticationService authenticationService;

    private Integer noOfTrades = 0;

    private Double totalQty = Double.NaN;

    private Double averagePrice = Double.NaN;

    public CalculationService(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
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
        noOfTrades = activeBuytradeList.size();
        totalQty = activeBuytradeList.stream().mapToDouble(s -> Double.parseDouble(s.getQty())).sum();
        averagePrice = calculateAveragePrice(symbol, activeBuytradeList);
    }

    private Double calculateAveragePrice(String symbol, List<Trade> tradeList) {

        double totalPrice = tradeList.stream().mapToDouble(s -> {
            double price = Double.parseDouble(s.getPrice());
            double qty = Double.parseDouble(s.getQty());
            return  price * qty;
        }).sum();

        Double averagePricePerQty = totalPrice/totalQty;
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

    public Integer getNoOfTrades() {
        return noOfTrades;
    }

    public void setNoOfTrades(Integer noOfTrades) {
        this.noOfTrades = noOfTrades;
    }

    public Double getTotalQty() {
        return totalQty;
    }

    public void setTotalQty(Double totalQty) {
        this.totalQty = totalQty;
    }

    public Double getAveragePrice() {
        return averagePrice;
    }

    public void setAveragePrice(Double averagePrice) {
        this.averagePrice = averagePrice;
    }
}
