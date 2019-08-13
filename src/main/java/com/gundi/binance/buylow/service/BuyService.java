package com.gundi.binance.buylow.service;

import com.binance.api.client.domain.account.NewOrder;
import com.binance.api.client.domain.account.Trade;
import com.binance.api.client.domain.event.AggTradeEvent;
import com.binance.api.client.domain.market.TickerStatistics;
import com.gundi.binance.buylow.api.APIClient;
import com.gundi.binance.buylow.config.CryptoPair;
import com.gundi.binance.buylow.model.TradeInfo;
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

    @Value("${buy.cool.off.time}")
    Integer buyCoolOffTime;

    private APIClient apiClient;
    private CalculationService calculationService;
    private AuditService auditService;

    @Autowired
    public BuyService(APIClient apiClient,
                      CalculationService calculationService,
                      AuditService auditService) {
        this.apiClient = apiClient;
        this.calculationService = calculationService;
        this.auditService = auditService;
    }

    public void tradeIt(String symbol) {
        Long serverTime = apiClient.getServerTime();
        Double buyPrice = Double.parseDouble(apiClient.get24HrPriceStatistics(symbol).getAskPrice());
        TradeInfo tradeInfo = new TradeInfo(serverTime, true, buyPrice);
        auditService.addTradeLogs(tradeInfo);


    }


    public void invoke(AggTradeEvent aggTradeEvent) {

        CryptoPair cryptoPair = CryptoPair.valueOf(aggTradeEvent.getSymbol());
        TickerStatistics  tickerStatistics =  apiClient.get24HrPriceStatistics(aggTradeEvent.getSymbol());
        String lastPrice_str = tickerStatistics.getLastPrice();
        Double lastPrice = DoubleRounder.round(Double.parseDouble(lastPrice_str),
                calculationService.getRoundDecimalPerSymbol(aggTradeEvent.getSymbol()));

        String lowPrice_str = tickerStatistics.getLowPrice();

        LocalDateTime lastTradeTime = calculationService.getLastTradeTimePerSymbol(aggTradeEvent.getSymbol());


        boolean tradeAble = false;
        if(lastTradeTime == null || lastTradeTime.plus(buyCoolOffTime, ChronoUnit.MINUTES).isBefore(LocalDateTime.now())) {
            tradeAble = true;
        }

        if (tradeAble  && lastPrice_str.compareTo(lowPrice_str) == 0 && cryptoPair.isKeepOnBuying()) {
            String auditLog = " Buy Event Occured for Symbol " + aggTradeEvent.getSymbol() + " Last Price "
                    + lastPrice_str + " Time " + LocalDateTime.now() + System.lineSeparator();
            auditService.addAuditLogs(auditLog);

            Double amountNeededForBuyTrade = lastPrice * Double.parseDouble(cryptoPair.getQuantity());

            String baseCurrencyAssetBalanceStr =
                    apiClient.getAssetBalance(
                            cryptoPair.getBaseCurrency()).getFree();
            Double amountInTheAccount = Double.parseDouble(baseCurrencyAssetBalanceStr);

            if(amountInTheAccount.compareTo(amountNeededForBuyTrade) == 1) {
                apiClient.newOrder(NewOrder.marketBuy(aggTradeEvent.getSymbol(), cryptoPair.getQuantity()));
                logger.info("TradeInfo Created for Symbol " + aggTradeEvent.getSymbol()  + " Quantity " + cryptoPair.getQuantity());

            }
        }
    }
}
