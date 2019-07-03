package com.gundi.binance.buylow.service;

import com.binance.api.client.domain.account.NewOrder;
import com.binance.api.client.domain.event.AggTradeEvent;
import com.binance.api.client.domain.market.TickerStatistics;
import com.gundi.binance.buylow.config.CryptoPair;
import org.decimal4j.util.DoubleRounder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Service
public class BuyService {

    Logger logger = LoggerFactory.getLogger(BuyService.class);

    private AuthenticationService authenticationService;
    private CalculationService calculationService;



    @Autowired
    public BuyService(AuthenticationService authenticationService,
                      CalculationService calculationService) {
        this.authenticationService = authenticationService;
        this.calculationService = calculationService;
    }


    public void invoke(AggTradeEvent aggTradeEvent) {

        CryptoPair cryptoPair = CryptoPair.valueOf(aggTradeEvent.getSymbol());
        TickerStatistics  tickerStatistics =  authenticationService.getApiRestClient().get24HrPriceStatistics(aggTradeEvent.getSymbol());
        String lastPrice_str = tickerStatistics.getLastPrice();
        Double lastPrice = DoubleRounder.round(Double.parseDouble(lastPrice_str),
                calculationService.getRoundDecimalPerSymbol(aggTradeEvent.getSymbol()));

        String lowPrice_str = tickerStatistics.getLowPrice();
        Double lowPrice = DoubleRounder.round(Double.parseDouble(lowPrice_str),
                calculationService.getRoundDecimalPerSymbol(aggTradeEvent.getSymbol()));
        LocalDateTime lastTradeTime = calculationService.getLastTradeTimePerSymbol(aggTradeEvent.getSymbol());


        boolean tradeAble = false;
        if(lastTradeTime == null || lastTradeTime.plus(15, ChronoUnit.MINUTES).isBefore(LocalDateTime.now())) {
            tradeAble = true;
        }

        if (tradeAble && lastPrice_str.compareTo(lowPrice_str) == 0) {
            logger.trace("Event Occured " + aggTradeEvent + " Price " + lastPrice_str);

            Double amountNeededForBuyTrade = lastPrice * Double.parseDouble(cryptoPair.getQuantity());

            String baseCurrencyAssetBalanceStr =
                    authenticationService.getApiRestClient().getAccount().getAssetBalance(
                            cryptoPair.getBaseCurrency()).getFree();
            Double amountInTheAccount = Double.parseDouble(baseCurrencyAssetBalanceStr);

            if(amountInTheAccount.compareTo(amountNeededForBuyTrade) == 1) {
                authenticationService.getApiRestClient().newOrder(NewOrder.marketBuy(aggTradeEvent.getSymbol(), cryptoPair.getQuantity()));
                lastTradeTime = LocalDateTime.now();
                logger.info("Trade Created for Symbol " + aggTradeEvent.getSymbol()  + " Quantity " + cryptoPair.getQuantity());

            }
        }
    }
}
