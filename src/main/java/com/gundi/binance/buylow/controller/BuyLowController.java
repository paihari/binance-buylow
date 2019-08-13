package com.gundi.binance.buylow.controller;

import com.binance.api.client.domain.account.Trade;
import com.gundi.binance.buylow.api.APIClient;
import com.gundi.binance.buylow.config.CryptoPair;
import com.gundi.binance.buylow.model.TradeLog;
import com.gundi.binance.buylow.service.AuditService;
import com.gundi.binance.buylow.service.CalculationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class BuyLowController {

    @Value("${spring.profiles.active}")
    private String env;

    @Value("${msg}")
    private String msg;


    private CalculationService calculationService;

    private AuditService auditService;

    private APIClient apiClient;

    @Autowired
    public BuyLowController(CalculationService calculationService,
                            AuditService auditService,
                            APIClient apiClient
    ) {
        this.calculationService = calculationService;
        this.auditService = auditService;
        this.apiClient = apiClient;
    }


    @RequestMapping("/invoke")
    public String invoke() {
        calculationService.invoke();
        String message = msg + " from the system" + env + System.lineSeparator();
        for (CryptoPair cryptoPair : CryptoPair.values()) {
            message = message + " Symbol " + cryptoPair.getPair() +

                    " Average Price " + calculationService.getAveragePricePerSymbol(cryptoPair.getPair()) +
                    " Total Qty " + calculationService.getTotalQtyPerSymbol(cryptoPair.getPair()) +
                    System.lineSeparator();


        }
        return message;
    }

    @RequestMapping("/audit")
    public String audit() {
        String message = "";

        Long serverTime = apiClient.getServerTime();
        LocalDateTime localDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(serverTime),
                ZoneId.systemDefault());
        message = message.concat("The Exchange Server Time " + localDateTime + System.lineSeparator() );

        LocalDateTime localDate = LocalDateTime.now();
        message = message.concat("The Server Time " + localDate + System.lineSeparator());

        message = message.concat("Average Drop of Red Candles " + auditService.getAverageDropOfRedCandles() + System.lineSeparator());
        message = message.concat("Average Volume of Red Candles " + auditService.getAverageVolumeOfRedCandles() + System.lineSeparator());



        Integer noOfBuyTrade = auditService.getTradeLogs().size();
        message = message.concat("No Of Trades " + noOfBuyTrade + System.lineSeparator()) ;

        Double buyPrice = new Double(0);

        for(TradeLog tradeLog : auditService.getTradeLogs()) {
            if(tradeLog.getBuyTrade()) {
                LocalDateTime lastTradeTime =
                        LocalDateTime.ofInstant(Instant.ofEpochMilli(tradeLog.getTradeTime()),
                                ZoneId.systemDefault());

                message = message.concat("Trade Time " + lastTradeTime + " Price " + tradeLog.getTradePrice() + System.lineSeparator());
                buyPrice = buyPrice + tradeLog.getTradePrice();
            }

        }
        message = message.concat(" Average Price " + buyPrice/noOfBuyTrade);

        return message;
    }

    @RequestMapping("/calculate")
    public String calculate() {
        for (CryptoPair cryptoPair : CryptoPair.values()) {
            List<Trade> allTrades = apiClient.getMyTrades(cryptoPair.getPair()).stream().filter(trade -> {
                return trade.getTime() > 1561465294471L;
            }).collect(Collectors.toList());


            Double buyValue = allTrades.stream().filter(trade -> {
                return trade.isBuyer();
            }).collect(Collectors.summingDouble(trade -> {
                return Double.parseDouble(trade.getQty()) * Double.parseDouble(trade.getPrice());
            }));

            Double sellValue = allTrades.stream().filter(trade -> {
                return !trade.isBuyer();
            }).collect(Collectors.summingDouble(trade -> {
                return Double.parseDouble(trade.getQty()) * Double.parseDouble(trade.getPrice());
            }));

            System.out.println("Symbol " + cryptoPair.getPair() + " Buy Value " + buyValue + " Sell Value " + sellValue);
            System.out.println("Profit/Loss " + (sellValue - buyValue));


        }
        return "";

    }

}
