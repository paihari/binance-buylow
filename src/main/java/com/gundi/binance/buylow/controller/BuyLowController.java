package com.gundi.binance.buylow.controller;

import com.gundi.binance.buylow.config.CryptoPair;
import com.gundi.binance.buylow.service.CalculationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BuyLowController {

    @Value("${spring.profiles.active}")
    private String env;

    @Value("${msg}")
    private String msg;


    private CalculationService calculationService;

    @Autowired
    public BuyLowController(CalculationService calculationService) {
        this.calculationService = calculationService;
    }


    @RequestMapping("/invoke")
    public String invoke() {
        calculationService.invoke();
        String message = msg + " from " + env + System. lineSeparator();
        for(CryptoPair cryptoPair : CryptoPair.values()) {
            message = message + " Symbol " + cryptoPair.getPair() +
                    " Number of Trades " + calculationService.getNoOfTradesPerSymbol(cryptoPair.getPair()) +
                    " Average Price " + calculationService.getAveragePricePerSymbol(cryptoPair.getPair()) +
                    " Total Qty " + calculationService.getTotalQtyPerSymbol(cryptoPair.getPair()) +
                    System.lineSeparator();


        }

        return  message;
    }
}
