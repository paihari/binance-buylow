package com.gundi.binance.buylow.service;

import com.binance.api.client.domain.account.NewOrder;
import com.binance.api.client.domain.event.AggTradeEvent;
import com.binance.api.client.domain.market.TickerStatistics;
import com.gundi.binance.buylow.api.APIClient;
import com.gundi.binance.buylow.config.CryptoPair;
import org.decimal4j.util.DoubleRounder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Service
public class BuyService {

    Logger logger = LoggerFactory.getLogger(BuyService.class);

    private APIClient apiClient;
    private CalculationService calculationService;


    @Autowired
    public BuyService(APIClient apiClient,
                      CalculationService calculationService
                      ) {
        this.apiClient = apiClient;
        this.calculationService = calculationService;

    }

    public boolean tradeIt(String symbol) {
        boolean tradeDone = false;
        CryptoPair cryptoPair = CryptoPair.valueOf(symbol);

        Double buyPrice = DoubleRounder.round(Double.parseDouble(apiClient.get24HrPriceStatistics(symbol).getAskPrice()),
                calculationService.getRoundDecimalPerSymbol(symbol));

        Double amountNeededForBuyTrade = buyPrice * Double.parseDouble(cryptoPair.getQuantity());

        String baseCurrencyAssetBalanceStr =
                apiClient.getAssetBalance(
                        cryptoPair.getBaseCurrency()).getFree();

        Double amountInTheAccount = Double.parseDouble(baseCurrencyAssetBalanceStr);
        if(amountInTheAccount.compareTo(amountNeededForBuyTrade) == 1) {
            apiClient.newOrder(NewOrder.marketBuy(symbol, cryptoPair.getQuantity()));
            tradeDone = true;
        }
        return tradeDone;

    }
}
