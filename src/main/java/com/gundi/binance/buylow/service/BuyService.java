package com.gundi.binance.buylow.service;

import com.binance.api.client.domain.account.NewOrder;
import com.binance.api.client.domain.event.AggTradeEvent;
import com.gundi.binance.buylow.config.CryptoPair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Service
public class BuyService {

    private AuthenticationService authenticationService;

    private LocalDateTime lastTradeTime = null;

    @Autowired
    public BuyService(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    public void invoke(AggTradeEvent aggTradeEvent) {

        CryptoPair cryptoPair = CryptoPair.valueOf(aggTradeEvent.getSymbol());
        String lastPrice = authenticationService.getApiRestClient().get24HrPriceStatistics(aggTradeEvent.getSymbol()).getLastPrice();
        String lowPrice = authenticationService.getApiRestClient().get24HrPriceStatistics(aggTradeEvent.getSymbol()).getLowPrice();
        //System.out.println("The Last Price " + lastPrice + " The Low Price " + lowPrice );
        boolean tradeAble = false;
        if(lastTradeTime == null || lastTradeTime.plus(5, ChronoUnit.MINUTES).isBefore(LocalDateTime.now())) {
            tradeAble = true;
        }

        if (tradeAble && lastPrice.equals(lowPrice)) {
            BigDecimal d_lastPrice = new BigDecimal(lastPrice);
            BigDecimal amountNeededForBuyTrade = d_lastPrice.multiply(new BigDecimal(cryptoPair.getQuantity()));

            String baseCurrencyAssetBalanceStr = authenticationService.getApiRestClient().getAccount().getAssetBalance(cryptoPair.getBaseCurrency()).getFree();
            BigDecimal amountInTheAccount = new BigDecimal(baseCurrencyAssetBalanceStr);
            if(amountInTheAccount.compareTo(amountNeededForBuyTrade) == 1) {
                authenticationService.getApiRestClient().newOrderTest(NewOrder.marketBuy(aggTradeEvent.getSymbol(), cryptoPair.getQuantity()));
                lastTradeTime = LocalDateTime.now();
                //sellService.createSellOrder(aggTradeEvent.getSymbol());
            }
        }
    }
}
