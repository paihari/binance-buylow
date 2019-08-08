package com.gundi.binance.buylow.controller;

import com.binance.api.client.domain.account.Trade;
import com.gundi.binance.buylow.api.APIClient;
import com.gundi.binance.buylow.config.CryptoPair;
import com.gundi.binance.buylow.service.AuditService;
import com.gundi.binance.buylow.service.CalculationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
        String message = msg + " from the" + env + System.lineSeparator();
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
        for (String auditLog : auditService.getAuditLogs()) {
            message = message.concat(auditLog);
        }
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
